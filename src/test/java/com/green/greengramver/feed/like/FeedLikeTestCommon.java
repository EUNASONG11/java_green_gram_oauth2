package com.green.greengramver.feed.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengramver.common.model.ResultResponse;
import com.green.greengramver.feed.like.model.FeedLikeReq;
import lombok.RequiredArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RequiredArgsConstructor
public class FeedLikeTestCommon {
    private final ObjectMapper objectMapper;

    public MultiValueMap<String, String> getParameter(long feedId) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>(1);
        queryParams.add("feedId", String.valueOf(feedId));
        return queryParams;
    }

    public FeedLikeReq getGivenParam(long feedId) {
        FeedLikeReq givenParam = new FeedLikeReq();
        givenParam.setFeedId(feedId);
        return givenParam;
    }

    public String getExpectedResJson(int result) throws Exception {
        ResultResponse expectedRes = ResultResponse.<Integer>builder()
                .resultMessage(result == 0 ? "좋아요 취소" : "좋아요 등록")
                .resultData(result)
                .build();
        return objectMapper.writeValueAsString(expectedRes);
    }
}
