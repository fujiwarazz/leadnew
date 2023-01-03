package com.heima.article.stream;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.heima.common.constants.ap_article.ArticleConstants;
import com.heima.common.constants.mess.HotArticleConstants;
import com.heima.model.message.ArticleVisitStreamMess;
import com.heima.model.message.UpdateArticleMess;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.checkerframework.checker.units.qual.A;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @Author peelsannaw
 * @create 03/01/2023 15:47
 */
@Configuration
@Slf4j
public class HotArticleStreamHandler {

    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {
        //接受
        KStream<String, String> stream = streamsBuilder.stream(ArticleConstants.HOT_ARTICLE_SCORE_TOPIC);
        //处理
        stream.map((k, v) -> {
                    UpdateArticleMess updateArticleMess = JSON.parseObject(v, UpdateArticleMess.class);
                    //重置 k,v
                    return new KeyValue<>(updateArticleMess.getArticleId().toString(),
                            updateArticleMess.getType().name() + ":" + updateArticleMess.getAdd());
                })
                //按照id聚合
                .groupBy((key, value) -> key)
                //时间窗口
                .windowedBy(TimeWindows.of(Duration.ofSeconds(10)))
                //完成聚合计算:
                .aggregate(new Initializer<String>() {
                    /**
                     * 初始方法
                     * @return 消息value
                     */
                    @Override
                    public String apply() {
                        return "COLLECTION:0,COMMENT:0,LIKES:0,VIEWS:0";
                    }
                }, new Aggregator<String, String, String>() {
                    /**
                     * 聚合操作
                     * @param k key 文章id
                     * @param v value type + add
                     * @param before 上一步处理的value
                     * @return 真正的value
                     */
                    @Override
                    public String apply(String k, String v, String before) {
                        if (StrUtil.isBlank(v)) {
                            return before;
                        } else {
                            //处理上一步的值,也是时间窗口内计算的值
                            String[] status = before.split(",");
                            int totLikes = 0, totCollection = 0, totViews = 0, totComments = 0;
                            for (String agg : status) {
                                String[] split = agg.split(":");
                                switch (UpdateArticleMess.UpdateArticleType.valueOf(split[0])) {
                                    case COLLECTION:
                                        totCollection = Integer.parseInt(split[1]);
                                        break;
                                    case COMMENT:
                                        totComments = Integer.parseInt(split[1]);
                                        break;
                                    case VIEWS:
                                        totViews = Integer.parseInt(split[1]);
                                        break;
                                    case LIKES:
                                        totLikes = Integer.parseInt(split[1]);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            //处理现在的 value : type +":"+add 累加
                            String[] splitV = v.split(":");
                            switch (UpdateArticleMess.UpdateArticleType.valueOf(splitV[0])) {
                                case COLLECTION:
                                    totCollection += Integer.parseInt(splitV[1]);
                                    break;
                                case COMMENT:
                                    totComments += Integer.parseInt(splitV[1]);
                                    break;
                                case VIEWS:
                                    totViews += Integer.parseInt(splitV[1]);
                                    break;
                                case LIKES:
                                    totLikes += Integer.parseInt(splitV[1]);
                                    break;
                                default:
                                    break;
                            }
                            String formatStr = String
                                    .format("COLLECTION:%d,COMMENT:%d,LIKES:%d,VIEWS:%d", totCollection, totComments, totLikes, totViews);
                            System.out.println("-----------");
                            System.out.println("当前文章id: " + k);
                            System.out.println("当前时间窗口内的消息处理" + formatStr);
                            System.out.println("-----------");
                            return formatStr;
                        }
                    }
                }, Materialized.as("hot-article-stream-count-001"))
                //发送
                .toStream()
                .map((k, v) -> {
                    return new KeyValue<>(k.key(),formatObj(k.key(),v));
                })
                .to(ArticleConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC);
        return stream;
    }

    /**
     * 格式化value数据
     *
     * @param articleId
     * @param articleValue
     * @return
     */
    public String formatObj(String articleId, String articleValue) {
        //：COLLECTION:0,COMMENT:0,LIKES:2,VIEWS:0
        ArticleVisitStreamMess articleVisitStreamMess = new ArticleVisitStreamMess();
        articleVisitStreamMess.setArticleId(Long.parseLong(articleId));
        String[] split = articleValue.split(",");
        for (String s : split) {
            String[] kv = s.split(":");
            switch (UpdateArticleMess.UpdateArticleType.valueOf(kv[0])) {
                case COLLECTION:
                    articleVisitStreamMess.setCollect(Integer.parseInt(kv[1]));
                    break;
                case COMMENT:
                    articleVisitStreamMess.setComment(Integer.parseInt(kv[1]));
                    break;
                case VIEWS:
                    articleVisitStreamMess.setView(Integer.parseInt(kv[1]));
                    break;
                case LIKES:
                    articleVisitStreamMess.setLike(Integer.parseInt(kv[1]));
                    break;
                default:
                    break;
            }
        }
        log.info("聚合消息处理完成: {}",JSON.toJSONString(articleVisitStreamMess));
        return JSON.toJSONString(articleVisitStreamMess);
    }
}

