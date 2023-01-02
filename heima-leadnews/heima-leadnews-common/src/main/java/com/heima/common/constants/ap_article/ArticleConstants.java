package com.heima.common.constants.ap_article;

/**
 * @Author peelsannaw
 * @create 9/11/2022 下午7:04
 */
public class ArticleConstants {
    public static final Short LOAD_TYPE_LOAD_MORE = 1;
    public static final Short LOAD_TYPE_LOAD_NEW = 2;
    public static final String DEFAULT_TAG = "__all__";

    /**
     * 审核相关
     */
    public static final String VERIFICATION_BLOCKED = "block";
    public static final String VERIFICATION_PASSED = "pass";
    public static final String VERIFICATION_REVIEW = "review";

    public static final String CONTAIN_VIOLATION = "存在违规内容!";
    public static final String CONTAIN_UNCERTAIN = "内容可能涉嫌,正在等待进一步审核";
    public static final String ARTICLE_ES_SYNC_TOPIC = "article.es.sync.topic";


    /**
     * 行为相关
     */
    public static final String ARTICLE_LIKE_PREFIX = "article_user_likes:";
    public static final String COMMENT_LIKE_PREFIX = "comment_user_likes:";

    public static final String ARTICLE_LIKE_CHANGE_TOPIC = "article.behavior.like.topic";
    public static final String ARTICLE_READ_PREFIX ="article_user_read:" ;
    public static final String ARTICLE_READ_INCRE = "article.behavior.read.topic";
    public static final String ARTICLE_UNLIKE_PREFIX ="article_user_dislikes:" ;
    public static final String ARTICLE_DISLIKE_CHANGE ="article.behavior.dislike.topic" ;
}