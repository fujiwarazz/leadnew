package com.heima.search.service.Impl;

import com.heima.common.common.dtos.ResponseResult;
import com.heima.common.common.enums.AppHttpCodeEnum;
import com.heima.model.search.entity.mongo.ApUserSearch;
import com.heima.search.service.ApUserSearchService;
import com.heima.utils.common.ApUserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author peelsannaw
 * @create 31/12/2022 18:11
 */
@Service
@Slf4j
public class ApUserSearchServiceImpl implements ApUserSearchService {

    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    @Async
    public void insertSearchHistory(String keyWord, Integer apUserId) {
        //查询mongo的关键词
        ApUserSearch search = mongoTemplate.findOne(Query
                .query(Criteria.where("userId").is(apUserId)
                        .and("keyword").is(keyWord)), ApUserSearch.class);
        //存在更新时间
        if (search != null) {
            search.setCreatedTime(new Date());
            mongoTemplate.save(search);
        } else {
            //不存在插入
            search = new ApUserSearch();
            search.setUserId(apUserId);
            search.setKeyword(keyWord);
            search.setCreatedTime(new Date());
            //如果超过10条,删除最早的一条
            List<ApUserSearch> apUserSearches = mongoTemplate
                    .find(Query.query(Criteria
                                    .where("userId").is(apUserId))
                            .with(Sort.by(Sort.Direction.DESC, "createdTime")), ApUserSearch.class);

            if (apUserSearches.size() < 10) {
                mongoTemplate.save(search);
            } else {
                ApUserSearch lastSearch = apUserSearches.get(apUserSearches.size() - 1);
                System.out.println(lastSearch);
                mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(lastSearch.getId())), ApUserSearch.class);
                mongoTemplate.save(search);
            }
        }
    }

    @Override
    public ResponseResult<?> getUserSearchHistory() {
        Integer apUserId = ApUserThreadLocal.getUser().getId();
        try {
            List<ApUserSearch> apUserSearches = mongoTemplate
                    .find(Query.query(Criteria
                                    .where("userId").is(apUserId))
                            .with(Sort.by(Sort.Direction.DESC, "createdTime")), ApUserSearch.class);
            return ResponseResult.okResult(apUserSearches);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public ResponseResult<?> delUserSearchHistory(Map request) {
        String  id = (String) request.get("id");
        Integer apUserId = ApUserThreadLocal.getUser().getId();
        mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id).and("userId").is(apUserId)),ApUserSearch.class);
        return ResponseResult.okResult();
    }
}
