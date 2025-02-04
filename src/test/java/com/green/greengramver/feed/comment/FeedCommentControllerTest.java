package com.green.greengramver.feed.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengramver.common.model.ResultResponse;
import com.green.greengramver.feed.comment.model.*;
import com.green.greengramver.feed.like.FeedLikeController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(
        controllers = FeedCommentController.class
        , excludeAutoConfiguration = SecurityAutoConfiguration.class
)
//@Import({MyFileUtils.class}) //진짜 객체를 사용하고 싶다면 @Import 사용
class FeedCommentControllerTest {
    @Autowired ObjectMapper objectMapper;
    @Autowired MockMvc mockMvc;
    @MockBean FeedCommentService feedCommentService; //@MockBean이 없으면 Controller가 객체화가 되지 않는다. 객체화가 되어야 Controller 테스트가 가능
                                                     // 그래서 Controller 안에 있는 가짜 객체를 빈 등록



    final long feedId_2 = 2L;
    final long feedCommentId_3 = 3L;
    final long writerUserId_4 = 4L;
    final int SIZE = 20;
    final String BASE_URL = "/api/feed/comment";

    @Test
    @DisplayName("피드 댓글 등록 리스트")
    void postFeedComment() throws Exception {
        FeedCommentPostReq givenParam = new FeedCommentPostReq();
        givenParam.setFeedId(feedId_2);
        givenParam.setComment("comment");

        given(feedCommentService.postFeedComment(givenParam)).willReturn(feedCommentId_3);

        String paramJson = objectMapper.writeValueAsString(givenParam);

        ResultActions resultActions = mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(paramJson));

        ResultResponse res = ResultResponse.<Long>builder()
                .resultMessage("댓글 등록")
                .resultData(feedCommentId_3)
                .build();

        String expectedResJson = objectMapper.writeValueAsString(res);

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResJson));

        verify(feedCommentService).postFeedComment(givenParam);

    }

    @Test
    @DisplayName("피드 댓글 리스트 테스트")
    void getFeedComment() throws Exception {
        //?key1=value1&key2=value2
        //?feed_id=2&start_idx=1 (size는 null이어도 됨)
        FeedCommentGetReq givenParam = new FeedCommentGetReq(feedId_2, 1, SIZE);

        FeedCommentDto feedCommentDto = new FeedCommentDto();
        feedCommentDto.setFeedId(feedId_2);
        feedCommentDto.setFeedCommentId(feedCommentId_3);
        feedCommentDto.setComment("comment");
        feedCommentDto.setWriterUserId(writerUserId_4);
        feedCommentDto.setWriterNm("작성자");
        feedCommentDto.setWriterPic("profile.jpg");

        FeedCommentGetRes expectedResult = new FeedCommentGetRes();
        expectedResult.setMoreComment(false);
        expectedResult.setCommentList(List.of(feedCommentDto));

        //service.getFeedComment에 임무 부여
        given(feedCommentService.getFeedComment(givenParam)).willReturn(expectedResult);

        ResultActions resultActions = mockMvc.perform(get(BASE_URL).queryParams(getParameter(givenParam)));
        String expectedResJson = getExpectedResJson(expectedResult);
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResJson));


        verify(feedCommentService).getFeedComment(givenParam);
    }

    private MultiValueMap<String, String> getParameter(FeedCommentGetReq givenParam) {
        MultiValueMap<String, String> getParameter = new LinkedMultiValueMap<>(3);
        getParameter.add("feed_id", String.valueOf(givenParam.getFeedId()));
        getParameter.add("start_idx", String.valueOf(givenParam.getStartIdx()));
        getParameter.add("size", String.valueOf(SIZE));

        return getParameter;
    }

    private String getExpectedResJson(FeedCommentGetRes feedCommentGetRes) throws Exception {
        ResultResponse expectedRes = ResultResponse.<FeedCommentGetRes>builder()
                .resultMessage(String.format("%d rows", feedCommentGetRes.getCommentList().size()))
                .resultData(feedCommentGetRes)
                .build();

        return objectMapper.writeValueAsString(expectedRes);
    }

    @Test
    @DisplayName("피드 댓글 삭제")
    void delFeedComment() throws Exception {
        final int RESULT = 3;
        FeedCommentDelReq givenParam = new FeedCommentDelReq(feedCommentId_3);

        //임무 부여
        //given(feedCommentService.delFeedComment(givenParam)).willReturn(RESULT);

        ResultActions resultActions = mockMvc.perform(delete(BASE_URL).queryParam("feed_comment_id", String.valueOf(feedCommentId_3)));

        String expectedResJson = objectMapper.writeValueAsString(ResultResponse.<Integer>builder()
                .resultMessage("삭제에 성공했습니다.")
                .resultData(RESULT)
                .build());
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResJson));

        verify(feedCommentService).delFeedComment(givenParam);
    }
}