package com.heima.wemedia.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.wm.WmConstants;
import com.heima.common.exception.CustomException;
import com.heima.model.admin.dtos.SensitiveWordsInsertDto;
import com.heima.model.admin.dtos.SensitiveWordsPageDto;
import com.heima.model.admin.vos.SensitiveWordsVo;
import com.heima.common.common.dtos.PageResponseResult;
import com.heima.common.common.dtos.ResponseResult;
import com.heima.common.common.entity.WmSensitive;
import com.heima.common.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dto.WmNewsDto;
import com.heima.model.wemedia.dto.WmNewsPageReqDto;
import com.heima.model.wemedia.dto.WmNewsUpdateDto;
import com.heima.model.wemedia.entity.WmMaterial;
import com.heima.model.wemedia.entity.WmNews;
import com.heima.model.wemedia.entity.WmNewsMaterial;
import com.heima.model.wemedia.entity.WmUser;
import com.heima.utils.common.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.WmNewAutoVerification;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.service.WmTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author peelsannaw
 */
@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Resource
    private WmNewsMaterialMapper wmNewsMaterialMapper;
    @Resource
    private WmMaterialMapper wmMaterialMapper;
    @Resource
    private WmNewAutoVerification autoVerification;
    @Resource
    private WmTaskService wmTaskService;
    @Resource
    private WmSensitiveMapper wmSensitiveMapper;
    @Resource
    private KafkaTemplate<String,String>kafka;
    @Override
    @SuppressWarnings("unchecked")
    public ResponseResult<?> findAll(WmNewsPageReqDto dto) {
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        dto.checkParam();
        WmUser user = WmThreadLocalUtil.getUser();
        LambdaQueryWrapper<WmNews> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WmNews::getUserId, user.getId());
        if (dto.getStatus() != null) {
            queryWrapper.eq(WmNews::getStatus, dto.getStatus());
        }
        //时间范围查询
        if (dto.getBeginPubDate() != null && dto.getEndPubDate() != null) {
            queryWrapper.between(WmNews::getPublishTime, dto.getBeginPubDate(), dto.getEndPubDate());
        }

        //关键字模糊查询
        if (StrUtil.isNotBlank(dto.getKeyword())) {
            queryWrapper.like(WmNews::getTitle, dto.getKeyword());
        }

        if (dto.getChannelId() != null) {
            queryWrapper.eq(WmNews::getChannelId, dto.getChannelId());
        }
        queryWrapper.orderByDesc(WmNews::getPublishTime);
        Page<WmNews> page = this.page(new Page<>(dto.getPage(), dto.getSize()), queryWrapper);
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());

        return responseResult;

    }

    public static void main(String[] args) {
        String s = "[{\"type\":\"text\",\"value\":\"1231231231\"},{\"type\":\"text\",\"value\":\"请在这里输入正文\"}]";
        List<Map> maps = JSON.parseArray(s, Map.class);
        List<String>urls = maps.stream()
                .distinct()
                .filter(item -> item.get("type").equals("image"))
                .map(item->item.get("value").toString())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ResponseResult<?> submitNews(WmNewsDto wmNewsDto) {

        //0.条件判断
        if(wmNewsDto == null || wmNewsDto.getContent() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //1.保存或修改文章
        WmNews wmNews = new WmNews();
        //属性拷贝 属性名词和类型相同才能拷贝
        BeanUtil.copyProperties(wmNewsDto,wmNews);
        //封面图片  list---> string
        if(wmNewsDto.getImages() != null && wmNewsDto.getImages().size() > 0){
            String imageStr = String.join( ",",wmNewsDto.getImages());
            wmNews.setImages(imageStr);
        }

        //如果当前封面类型为自动 -1
        if(wmNewsDto.getType().equals(WmConstants.THUMBNAIL_AUTO)){
            wmNews.setType(null);
        }

        if(wmNewsDto.getPublishTime()!=null){
            wmNews.setPublishTime(wmNewsDto.getPublishTime());
        }else{
            wmNews.setPublishTime(new Date());
        }

        saveOrUpdateWmNews(wmNews);

        //2.判断是否为草稿  如果为草稿结束当前方法
        if(wmNewsDto.getStatus().equals(WmNews.Status.NORMAL.getCode())){
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        log.info("审核状态:{}",wmNewsDto.getStatus());
        List<String> images = extractUrlInfo(wmNewsDto.getContent());

        //保存关系:文章内容图片和素材
        saveRelativeInfoForContent(images,wmNews.getId());

        //保存封面和素材的关系
        saveRelativeInfoForCover(wmNewsDto,images,wmNews);

        //添加到任务
        wmTaskService.addNewsToTask(wmNews.getId(),wmNews.getPublishTime());
        //autoVerification.autoVerification(wmNews.getId());

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }



    /**
     * 处理文章内容图片与素材的关系
     * @param materials
     * @param newsId
     */
    private void saveRelativeInfoForContent(List<String> materials, Integer newsId) {
        saveRelativeInfo(materials,newsId,WmConstants.WM_CONTENT_REFERENCE);
    }

    private void saveRelativeInfoForCover(WmNewsDto wmNewsDto, List<String> images, WmNews wmNews) {
        if(wmNewsDto.getType().equals(WmConstants.THUMBNAIL_AUTO)){
            if(images.size()>=WmConstants.THUMBNAIL_MANY){
                wmNews.setType(WmConstants.THUMBNAIL_MANY);
            }else if(images.size()>=WmConstants.THUMBNAIL_SINGLE){
                wmNews.setType(WmConstants.THUMBNAIL_SINGLE);
            }else{
                wmNews.setType(WmConstants.THUMBNAIL_NONE);
            }

            if(!wmNews.getType().equals(WmConstants.THUMBNAIL_NONE)){
                wmNews.setImages(String.join(",",images));
            }
            updateById(wmNews);
        }
        if(wmNewsDto.getImages()!=null && wmNewsDto.getImages().size()>0){
            saveRelativeInfo(images,wmNews.getId(),WmConstants.WM_COVER_REFERENCE);
        }
    }
    private void saveRelativeInfo(List<String> materials, Integer newsId, Short type) {
        if (materials != null && !materials.isEmpty()) {
            //通过图片的url查询素材的id
            List<WmMaterial> dbMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, materials));

            //判断素材是否有效
            if (dbMaterials == null || dbMaterials.size() == 0) {
                //手动抛出异常   第一个功能：能够提示调用者素材失效了，第二个功能，进行数据的回滚
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
            }

            List<Integer> idList = dbMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());

            //批量保存
            wmNewsMaterialMapper.saveRelations(idList, newsId, type);
        }
    }


    /**
     * 保存或修改文章
     * @param wmNews
     */
    private void saveOrUpdateWmNews(WmNews wmNews) {
        //补全属性
        wmNews.setUserId(WmThreadLocalUtil.getUser().getId());
        wmNews.setEnable((short)1);

        if(wmNews.getId() == null){
            //保存
            save(wmNews);
        }else {
            //修改
            //删除文章图片与素材的关系
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId,wmNews.getId()));
            updateById(wmNews);
        }

    }

    @SuppressWarnings("all")
    private List<String> extractUrlInfo(String content) {
        List<Map> maps = JSON.parseArray(content, Map.class);
        List<String>urls = maps.stream()
                                .distinct()
                                .filter(item -> item.get("type").equals("image"))
                                .map(item->item.get("value").toString())
                                .collect(Collectors.toList());
        return urls;
    }


    @Override
    public ResponseResult<?> downOrUp(WmNewsUpdateDto wmnewsUpdateDto) {
        if(wmnewsUpdateDto.getId()==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        Integer id = wmnewsUpdateDto.getId();
        WmNews wmNews = getById(id);
        if(wmNews.getArticleId()==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        if(!wmNews.getStatus().equals(WmNews.Status.PUBLISHED.getCode())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"文章未发布");
        }
        if(wmnewsUpdateDto.getEnable()!=null && wmnewsUpdateDto.getEnable()>-1 && wmnewsUpdateDto.getEnable()<2){

            update(Wrappers.lambdaUpdate(wmNews)
                    .set(WmNews::getEnable,wmnewsUpdateDto.getEnable()));
            //kafka发送消息,通知article修改配置
            Map<String ,Object>map = new Hashtable<>();
            map.put("articleId",wmNews.getArticleId());
            map.put("enable",wmnewsUpdateDto.getEnable());
            System.out.println(JSON.toJSONString(map));
            kafka.send(WmConstants.WM_NEWS_UP_OR_DOWN_TOPIC,JSON.toJSONString(map));
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult<?> deleteSensitiveWord(Integer id) {
        int i = wmSensitiveMapper.deleteById(id);
        if(i>0){
            return ResponseResult.okResult();
        }else {
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public ResponseResult<?> getList(SensitiveWordsPageDto pageDto) {
        SensitiveWordsVo sensitiveWordsVo = new SensitiveWordsVo();
        LambdaQueryWrapper<WmSensitive> wrappers = null;
        if(pageDto.getName()!=null && pageDto.getName().trim().length()>0){
            wrappers = Wrappers.lambdaQuery(WmSensitive.class).eq(WmSensitive::getSensitives, pageDto.getName());
        }
        Page<WmSensitive> wmSensitivePage = wmSensitiveMapper.selectPage(new Page<>(pageDto.getPage(), pageDto.getSize()),
                wrappers);
        sensitiveWordsVo.setData(wmSensitivePage.getRecords());
        sensitiveWordsVo.setTotal(wmSensitivePage.getTotal());
        sensitiveWordsVo.setCurrentPage(pageDto.getPage());
        sensitiveWordsVo.setSize(wmSensitivePage.getSize());
        return ResponseResult.okResult(sensitiveWordsVo);
    }

    @Override
    @Transactional
    public ResponseResult<?> saveSensitiveWord(SensitiveWordsInsertDto dto) {
        WmSensitive wmSensitive = BeanUtil.copyProperties(dto, WmSensitive.class);
        wmSensitive.setCreatedTime(new Date(dto.getCreatedTime()));
        if(dto.getId()==null){
            wmSensitiveMapper.insert(wmSensitive);
        }else{
            wmSensitiveMapper.updateById(wmSensitive);
        }
        return ResponseResult.okResult();
    }
}
