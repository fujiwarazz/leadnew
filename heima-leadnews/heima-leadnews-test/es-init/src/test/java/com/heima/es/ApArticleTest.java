package com.heima.es;

import com.alibaba.fastjson.JSON;
import com.heima.es.mapper.ApArticleMapper;
import com.heima.es.pojo.SearchArticleVo;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ApArticleTest {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Resource
    ApArticleMapper apArticleMapper;
    /**
     * 注意：数据量的导入，如果数据量过大，需要分页导入
     * @throws Exception
     */
    @Test
    public void init() throws Exception {
        List<SearchArticleVo> searchArticleVos = apArticleMapper.loadArticleList();
        BulkRequest bulkRequest = new BulkRequest();
        searchArticleVos.forEach(item -> {
            bulkRequest.add(new IndexRequest("app_info_article")
                    .id(item.getId().toString())
                    .source(JSON.toJSONString(item), XContentType.JSON));
        });
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

}