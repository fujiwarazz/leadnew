package com.heima.wemedia.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.heima.apis.article.IArticleClient;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.common.annotation.LogEnhance;
import com.heima.common.constants.ap_article.ArticleConstants;
import com.heima.common.exception.CustomException;
import com.heima.common.tess4j.Tess4jClient;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dto.ArticleDto;
import com.heima.common.common.dtos.ResponseResult;
import com.heima.common.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.entity.WmChannel;
import com.heima.model.wemedia.entity.WmNews;
import com.heima.model.wemedia.entity.WmUser;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewAutoVerification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @Author peelsannaw
 * @create 15/11/2022 上午10:56
 */


@Service
@Slf4j
@SuppressWarnings("all")
public class WmNewAutoVerificationImpl implements WmNewAutoVerification {

    @Resource
    private WmNewsMapper wmNewsMapper;

    @Resource
    private GreenTextScan greenTextScan;

    @Resource
    private GreenImageScan greenImageScan;

    @Resource
    private WmChannelMapper wmChannelMapper;

    @Resource
    private WmSensitiveMapper wmSensitiveMapper;

    @Resource
    private WmUserMapper wmUserMapper;

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private IArticleClient articleClient;

    @Resource
    private Tess4jClient tess4jClient;

    /**
     * 异步自动审核
     *
     * @param id
     */
    @Override
    @Async
    @Transactional
    @LogEnhance(BusinessName = "自动审核")
    public void autoVerification(Integer id) {
        //1.查询文章 自媒体
        WmNews wmNews = wmNewsMapper.selectById(id);
        if (wmNews == null) {
            throw new CustomException(AppHttpCodeEnum.VERIFICATION_ARTICLE_ERROR);
        }
        //待审核
        if (wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {
            //2. 抽取文章内容
            Map<String, Object> contentAndImages = handleContentAndImages(wmNews);
            boolean isSensitive = handleSensitiveWords(wmNews, (String) contentAndImages.get("content"));
            if (!isSensitive) {
                return;
            }
            //3. 审核文本内容 阿里云contentAndImages = {HashMap@11688}  size = 2
            boolean isContentPass = handleVerificationContent(wmNews, (String) contentAndImages.get("content"));
            if (!isContentPass) {
                return;
            }
            //4. 审核图片内容 阿里云
            boolean isImagesPass = handleVerificationImages(wmNews, (List<String>) contentAndImages.get("images"));
            if (!isImagesPass) {
                return;
            }

            log.info("文本状态:  " + isContentPass + " 图片状态" + isImagesPass);

            //5. 审核成功 保存app端文章记录
            ResponseResult<?> responseResult = saveAppArticle(wmNews);

            if (!responseResult.getCode().equals(200)) {
                throw new CustomException(AppHttpCodeEnum.SAVE_ARTICLE_ERROR);
            }
            wmNews.setArticleId((Long) responseResult.getData());
            updateWmNews(wmNews, WmNews.Status.PUBLISHED.getCode(), ArticleConstants.VERIFICATION_PASSED);
        }
    }

    private boolean handleSensitiveWords(WmNews wmNews, String content) {
        Map<String, Integer> map = SensitiveWordUtil.matchWords(content);
        if (map.size() > 0) {
            updateWmNews(wmNews, WmNews.Status.FAIL.getCode(), ArticleConstants.CONTAIN_VIOLATION);
            return false;
        }
        return true;
    }

    /**
     * 保存文章数据
     *
     * @param wmNews
     */
    private ResponseResult<?> saveAppArticle(WmNews wmNews) {
        ArticleDto dto = new ArticleDto();
        //属性的拷贝
        BeanUtil.copyProperties(wmNews, dto);
        dto.setId(null);
        //文章的布局
        dto.setLayout(wmNews.getType());
        //频道
        WmChannel wmChannel = wmChannelMapper.selectById(wmNews.getChannelId());
        if (wmChannel != null) {
            dto.setChannelName(wmChannel.getName());
        }

        //作者
        dto.setAuthorId(wmNews.getUserId().longValue());
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if (wmUser != null) {
            dto.setAuthorName(wmUser.getName());
        }

        //设置文章id
        if (wmNews.getArticleId() != null) {
            dto.setId(wmNews.getArticleId());
        }

        ResponseResult responseResult = articleClient.saveArticle(dto);
        log.warn("articleClinetStatus: {}", responseResult.getCode());
        System.out.println(JSON.toJSONString(responseResult));
        return responseResult;
    }

    private boolean handleVerificationImages(WmNews wmNews, List<String> images) {
        System.out.println("images size--------------" + images.size());
        boolean flag = true;
        if (images == null || images.size() == 0) {
            return true;
        }
        if (images.size() == 1) {
            System.out.println(images.get(0));
        }
        AtomicBoolean err = new AtomicBoolean(false);
        //下载图片
        List<byte[]> imagesByte = images.stream().distinct().map(item -> {
            byte[] bytes = fileStorageService.downLoadFile(item);
            return bytes;
        }).peek(item -> {
            //图片识别
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(item);
                BufferedImage read = ImageIO.read(inputStream);
                String result = tess4jClient.doOcr(read);
                boolean sensitiveWordsInImage = handleSensitiveWords(wmNews, result);
                if (sensitiveWordsInImage) {
                    err.set(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).collect(Collectors.toList());
        if (!err.get()) {
            return false;
        }
        try {
            Map map = greenImageScan.imageScan(imagesByte);
            System.out.println("--------------------------");
            System.out.println(map.get("suggestion"));
            System.out.println("--------------------------");
            if (map != null) {
                if (map.get("suggestion").equals(ArticleConstants.VERIFICATION_BLOCKED)) {
                    updateWmNews(wmNews, WmNews.Status.FAIL.getCode(), ArticleConstants.CONTAIN_VIOLATION);
                    flag = false;
                } else if (map.get("suggestion").equals(ArticleConstants.VERIFICATION_REVIEW)) {
                    updateWmNews(wmNews, WmNews.Status.ADMIN_AUTH.getCode(), ArticleConstants.CONTAIN_UNCERTAIN);
                    flag = false;
                }
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();

        }
        return flag;
    }

    private boolean handleVerificationContent(WmNews wmNews, String content) {
        boolean flag = true;

        if ((wmNews.getTitle() + content).isEmpty()) {
            return flag;
        }

        try {
            Map map = greenTextScan.greeTextScan(content + "\n" + wmNews.getTitle());
            if (map != null) {
                if (map.get("suggestion").equals(ArticleConstants.VERIFICATION_BLOCKED)) {
                    updateWmNews(wmNews, WmNews.Status.FAIL.getCode(), ArticleConstants.CONTAIN_VIOLATION);
                    flag = false;
                } else if (map.get("suggestion").equals(ArticleConstants.VERIFICATION_REVIEW)) {
                    updateWmNews(wmNews, WmNews.Status.ADMIN_AUTH.getCode(), ArticleConstants.CONTAIN_UNCERTAIN);
                    flag = false;
                }
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    private void updateWmNews(WmNews wmNews, short type, String reason) {
        wmNews.setStatus(type);
        wmNews.setReason(reason);
        wmNewsMapper.updateById(wmNews);
    }

    /**
     * 从文章中提取文章和图片
     * 提取封面图片
     *
     * @param wmNews 自媒体文章
     * @return
     */
    @SuppressWarnings("all")
    private Map<String, Object> handleContentAndImages(WmNews wmNews) {
        String content = wmNews.getContent();
        if (content == null || content.isEmpty()) {
            throw new CustomException(AppHttpCodeEnum.VERIFICATION_ARTICLE_ERROR);
        }
        StringBuilder textContent = new StringBuilder();
        List<String> contentImages;
        List<Map> maps = JSON.parseArray(content, Map.class);

        maps.stream()
                .filter(item -> "text".equals(item.get("type")))
                .map(item -> (String) item.get("value"))
                .forEach(item -> textContent.append(item));

        contentImages = maps.stream()
                .filter(item -> "image".equals(item.get("type")))
                .map(item -> (String) item.get("value"))
                .collect(Collectors.toList());

        System.out.println("---------------contentImages`s size");


        if (StrUtil.isNotBlank(wmNews.getImages()) && !wmNews.getImages().equals("[]")) {
            String[] split = wmNews.getImages().split(",");
            contentImages.addAll(Arrays.asList(split));
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("content", textContent.toString());
        resultMap.put("images", contentImages);
        return resultMap;
    }
}
