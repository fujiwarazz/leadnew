package com.heima.article.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.annotation.LogEnhance;
import com.heima.common.constants.ap_article.ArticleConstants;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dto.ArticleDto;
import com.heima.model.article.dto.ArticleHomeDto;
import com.heima.model.article.entity.ApArticle;
import com.heima.model.article.entity.ApArticleConfig;
import com.heima.model.article.entity.ApArticleContent;
import com.heima.common.common.dtos.ResponseResult;
import com.heima.common.common.enums.AppHttpCodeEnum;
import com.heima.model.article.vo.HotArticleVo;
import com.heima.model.message.ArticleVisitStreamMess;
import com.heima.model.search.vos.SearchArticleVo;
import com.heima.user.constants.UserConstants;
import com.heima.utils.common.ApUserThreadLocal;
import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author peelsannaw
 * @create 10/11/2022 上午9:16
 */

@Service
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    private final static short MAX_PAGE_SIZE = 50;

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    FileStorageService fileStorageService;
    @Resource
    private Configuration configuration;
    @Resource
    private ApArticleMapper apArticleMapper;
    @Resource
    private ApArticleConfigMapper apArticleConfigMapper;
    @Resource
    private ApArticleContentMapper apArticleContentMapper;
    @Resource
    private ArticleFreemarkerService articleFreemarkerService;
    @Resource
    private RestHighLevelClient restHighLevelClient;
    @Resource
    private KafkaTemplate<String, String> kafka;

    /**
     * 根据参数加载文章列表
     *
     * @param loadtype 1为加载更多  2为加载最新
     * @param dto
     * @return
     */
    @Override
    public ResponseResult<?> load(Short loadtype, ArticleHomeDto dto) {
        //1.校验参数
        Integer size = dto.getSize();
        if (size == null || size == 0) {
            size = 10;
        }
        size = Math.min(size, MAX_PAGE_SIZE);
        dto.setSize(size);

        //类型参数检验
        if (!loadtype.equals(ArticleConstants.LOAD_TYPE_LOAD_MORE) && !loadtype.equals(ArticleConstants.LOAD_TYPE_LOAD_NEW)) {
            loadtype = ArticleConstants.LOAD_TYPE_LOAD_MORE;
        }
        //文章频道校验
        if (StrUtil.isEmpty(dto.getTag())) {
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        //时间校验
        if (dto.getMaxBehotTime() == null) {
            dto.setMaxBehotTime(new Date());
        }
        if (dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date(0L));
        }
        //2.查询数据
        List<ApArticle> apArticles = apArticleMapper.loadArticleList(dto, loadtype);

        //加上redis热门的文章
        String key = ArticleConstants.HOT_ARTICLE_FIRST_PAGE + dto.getTag();
        String s = stringRedisTemplate.opsForValue().get(key);
        if (s != null && s.length() > 0) {
            List<HotArticleVo> hotArticleVos = JSON.parseArray(s, HotArticleVo.class);
        }


        //3.结果封装
        return ResponseResult.okResult(apArticles);
    }

    @Override
    public ResponseResult<?> load2(ArticleHomeDto dto, Short type, boolean firstPage) {
        if (firstPage) {
            String jsonStr = stringRedisTemplate.opsForValue().get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + dto.getTag());
            if (StrUtil.isNotBlank(jsonStr)) {
                List<HotArticleVo> hotArticleVoList = JSON.parseArray(jsonStr, HotArticleVo.class);
                return ResponseResult.okResult(hotArticleVoList);
            }
        }
        return load(type, dto);
    }

    @Override
    public ResponseResult<?> saveArticleDTO(ArticleDto dto) {
        System.out.println("进入方法");
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();
        BeanUtil.copyProperties(dto, apArticle);

        //2.判断是否存在id
        if (dto.getId() == null) {
            //2.1 不存在id  保存  文章  文章配置  文章内容

            //保存文章
            save(apArticle);

            //保存配置
            ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
            apArticleConfigMapper.insert(apArticleConfig);

            //保存 文章内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);
        } else {
            //2.2 存在id   修改  文章  文章内容
            System.out.println("else");
            //修改  文章
            updateById(apArticle);

            //修改文章内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, dto.getId()));
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }

        //异步调用 生成静态文件上传到minio中
        String s = articleFreemarkerService.buildArticleToStatic(apArticle, dto.getContent());
        //发送消息,创建esdoc
        createArticleDocToEs(apArticle, dto.getContent(), s);
        //3.结果返回  文章的id
        return ResponseResult.okResult(apArticle.getId());
    }


    @Override
    public ResponseResult<?> loadArticleBehavior(Map map) {
        String articleId = map.get("articleId").toString();
        String authorId = map.get("authorId").toString();
        String userId = ApUserThreadLocal.getUser().getId().toString();
        if (authorId == null || articleId == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        Map<String, Object> result = new Hashtable<>();
        String userDislike = ArticleConstants.ARTICLE_UNLIKE_PREFIX + userId;
        String userLike = ArticleConstants.ARTICLE_LIKE_PREFIX + userId;
        String userFollow = UserConstants.USER_FOLLOW_PREFIX + userId;
        String userCollect = UserConstants.USER_COLLECT_ARTICLE_PREFIX + userId;
        Set<String> members = stringRedisTemplate.opsForSet().members(userLike);
        Set<String> members2 = stringRedisTemplate.opsForSet().members(userDislike);
        Set<String> members3 = stringRedisTemplate.opsForSet().members(userFollow);
        Boolean aBoolean = stringRedisTemplate.opsForHash().hasKey(userCollect, articleId);
        boolean isLike = false, isDislike = false, isFollow = false, isCollect = false;
        if (members != null && members.contains(articleId)) {
            isLike = true;
        }
        if (members2 != null && members2.contains(articleId)) {
            isDislike = true;
        }
        if (members3 != null && members3.contains(authorId)) {
            isFollow = true;
        }
        if (aBoolean.equals(Boolean.TRUE)) {
            isCollect = true;
        }
        result.put("islike", isLike);
        result.put("isunlike", isDislike);
        result.put("isfollow", isFollow);
        result.put("iscollection", isCollect);
        return ResponseResult.okResult(result);
    }

    @Override
    @LogEnhance(BusinessName = "用户行为:收藏")
    public ResponseResult<?> userCollect(Map map) {
        try {
            String articleId = map.get("entryId").toString();
            String operation = map.get("operation").toString();
            Long publishedTime = (Long) map.get("publishedTime");
            String type = map.get("type").toString();
            String userId = ApUserThreadLocal.getUser().getId().toString();
            String key = null;
            if ("0".equals(type)) {
                key = UserConstants.USER_COLLECT_ARTICLE_PREFIX + userId;
            } else {
                key = UserConstants.USER_COLLECT_FEED_PREFIX + userId;
            }
            ApArticle article = apArticleMapper.selectById(articleId);
            Boolean member = stringRedisTemplate.opsForHash().hasKey(key, articleId);


            synchronized (this) {
                changeRedisAndDb(articleId, publishedTime, key, article, member);
            }

            apArticleMapper.updateById(article);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult();

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHotArticleScore(ArticleVisitStreamMess articleVisitStreamMess) {
        //更新文章信息
        ApArticle article = updateArticle(articleVisitStreamMess);
        //计算文章分值
        Integer score = computeArticleScore(article) * 3;

        //替换对应频道的热点数据
        String channelKey = ArticleConstants.HOT_ARTICLE_FIRST_PAGE+article.getChannelId();
        String recommendKey = ArticleConstants.HOT_ARTICLE_FIRST_PAGE+ArticleConstants.DEFAULT_TAG;
        String channelJson = stringRedisTemplate.opsForValue().get(channelKey);
        List<HotArticleVo> hotArticleVos = JSON.parseArray(recommendKey, HotArticleVo.class);
        List<HotArticleVo> hotArticleVos2 = JSON.parseArray(recommendKey, HotArticleVo.class);

        removeAndReCache(article, score, channelKey, hotArticleVos);
        //替换对应推荐的文庄数据
        removeAndReCache(article, score, recommendKey, hotArticleVos2);

    }

    private void removeAndReCache(ApArticle article, Integer score, String tag, List<HotArticleVo> hotArticleVos) {
        boolean isExist = false;
        for (HotArticleVo articleVo : hotArticleVos) {
            if(articleVo.getId().equals(article.getId())){
                articleVo.setScore(score);
                isExist = true;
                break;
            }
        }
        HotArticleVo hotArticleVo = BeanUtil.copyProperties(article, HotArticleVo.class);
        hotArticleVo.setScore(score);

        if(!isExist && hotArticleVos.size()>=30){
            //由于存入的时候已经排序了,所以此时只需要移除最后一个就行
            for (HotArticleVo articleVo : hotArticleVos) {
                if(articleVo.getScore()<=score){
                    articleVo = hotArticleVo;
                    break;
                }
            }
        }else if(!isExist) {
            hotArticleVos.add(hotArticleVo);
        }

        stringRedisTemplate.opsForValue().set(tag,JSON.toJSONString(hotArticleVos));
    }

    private Integer computeArticleScore(ApArticle item) {
        int score = 0;
        if (item.getLikes() != null) {
            score += item.getLikes() * ArticleConstants.ARTICLE_LIKE_WEIGHT;
        }
        if (item.getCollection() != null) {
            score += item.getCollection() * ArticleConstants.ARTICLE_COLLECT_WEIGHT;
        }
        if (item.getViews() != null) {
            score += item.getViews() * ArticleConstants.ARTICLE_VIEW_WEIGHT;
        }
        if (item.getComment() != null) {
            score += item.getComment() * ArticleConstants.ARTICLE_COMMENT_WEIGHT;
        }
        return score;
    }

    private ApArticle updateArticle(ArticleVisitStreamMess articleVisitStreamMess) {
        Long articleId = articleVisitStreamMess.getArticleId();
        ApArticle byId = this.getById(articleId);
        byId.setCollection(byId.getCollection() == null ? 0 : byId.getCollection() + articleVisitStreamMess.getCollect());
        byId.setLikes(byId.getLikes() == null ? 0 : byId.getLikes() + articleVisitStreamMess.getLike());
        byId.setViews(byId.getViews() == null ? 0 : byId.getViews() + articleVisitStreamMess.getView());
        byId.setComment(byId.getComment() == null ? 0 : byId.getComment() + articleVisitStreamMess.getComment());
        this.updateById(byId);
        return byId;
    }

    private void changeRedisAndDb(String articleId, Long publishedTime, String key, ApArticle article, Boolean member) {
        if (Boolean.FALSE.equals(member)) {
            stringRedisTemplate.opsForHash().put(key, articleId, publishedTime.toString());
            article.setCollection(article.getCollection() + 1);
        } else {
            stringRedisTemplate.opsForHash().delete(key, articleId);
            article.setCollection(article.getCollection() - 1);
        }
    }

    /**
     * 同步数据到elasticSearch
     * @param apArticle
     * @param content
     * @param path
     */
    private void createArticleDocToEs(ApArticle apArticle, String content, String path) {
        SearchArticleVo searchArticleVo = BeanUtil.copyProperties(apArticle, SearchArticleVo.class);
        searchArticleVo.setContent(content);
        searchArticleVo.setStaticUrl(path);
        log.info("kafka async发送信息--->begin");
        try {
            kafka.setProducerListener(new ProducerListener<String, String>() {
                @Override
                public void onError(ProducerRecord<String, String> producerRecord, RecordMetadata recordMetadata, Exception exception) {
                    if (producerRecord != null) {
                        System.out.println(producerRecord.toString());
                    }
                }
            });
            kafka.send(ArticleConstants.ARTICLE_ES_SYNC_TOPIC, JSON.toJSONString(searchArticleVo));
        } finally {
            log.info("kafka发送信息--->end");
        }

    }

}

