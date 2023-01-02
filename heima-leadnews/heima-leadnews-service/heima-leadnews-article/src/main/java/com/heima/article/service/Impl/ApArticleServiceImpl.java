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
import com.heima.common.constants.ap_article.ArticleConstants;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dto.ArticleDto;
import com.heima.model.article.dto.ArticleHomeDto;
import com.heima.model.article.entity.ApArticle;
import com.heima.model.article.entity.ApArticleConfig;
import com.heima.model.article.entity.ApArticleContent;
import com.heima.common.common.dtos.ResponseResult;
import com.heima.common.common.enums.AppHttpCodeEnum;
import com.heima.model.search.vos.SearchArticleVo;
import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author peelsannaw
 * @create 10/11/2022 上午9:16
 */

@Service
@Slf4j
public class ApArticleServiceImpl  extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    private final static short MAX_PAGE_SIZE = 50;

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
    private KafkaTemplate<String,String>kafka;
    /**
     * 根据参数加载文章列表
     * @param loadtype 1为加载更多  2为加载最新
     * @param dto
     * @return
     */
    @Override
    public ResponseResult<?> load(Short loadtype, ArticleHomeDto dto) {
        //1.校验参数
        Integer size = dto.getSize();
        if(size == null || size == 0){
            size = 10;
        }
        size = Math.min(size,MAX_PAGE_SIZE);
        dto.setSize(size);

        //类型参数检验
        if(!loadtype.equals(ArticleConstants.LOAD_TYPE_LOAD_MORE)&&!loadtype.equals(ArticleConstants.LOAD_TYPE_LOAD_NEW)){
            loadtype = ArticleConstants.LOAD_TYPE_LOAD_MORE;
        }
        //文章频道校验
        if(StrUtil.isEmpty(dto.getTag())){
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        //时间校验
        if(dto.getMaxBehotTime() == null){
            dto.setMaxBehotTime(new Date());
        }
        if(dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date());
        }
        //2.查询数据
        List<ApArticle> apArticles = apArticleMapper.loadArticleList(dto, loadtype);

        //3.结果封装
        return ResponseResult.okResult(apArticles);
    }

    @Override
    public ResponseResult<?> saveArticleDTO(ArticleDto dto) {
        System.out.println("进入方法");
        if(dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();
        BeanUtil.copyProperties(dto,apArticle);

        //2.判断是否存在id
        if(dto.getId() == null) {
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
        }else {
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
        createArticleDocToEs(apArticle,dto.getContent(),s);
        //3.结果返回  文章的id
        return ResponseResult.okResult(apArticle.getId());
    }

    private void createArticleDocToEs(ApArticle apArticle, String content,String path) {
        SearchArticleVo searchArticleVo = BeanUtil.copyProperties(apArticle, SearchArticleVo.class);
        searchArticleVo.setContent(content);
        searchArticleVo.setStaticUrl(path);
        log.info("kafka发送信息--->begin");
        try {
            kafka.send(ArticleConstants.ARTICLE_ES_SYNC_TOPIC, JSON.toJSONString(searchArticleVo));
        } finally {
            log.info("kafka发送信息--->end");
        }

    }
}