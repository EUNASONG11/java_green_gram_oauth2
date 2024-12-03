package com.green.greengramver2.feed.comment;

import com.green.greengramver2.common.model.ResultResponse;
import com.green.greengramver2.feed.comment.model.FeedCommentGetReq;
import com.green.greengramver2.feed.comment.model.FeedCommentGetRes;
import com.green.greengramver2.feed.comment.model.FeedCommentPostReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("feed/comment")
public class FeedCommentController {
    private final FeedCommentService service;

    @PostMapping
    public ResultResponse<Long> postFeedComment(@RequestBody FeedCommentPostReq p) {
        long result = service.postFeedComment(p);
        return ResultResponse.<Long>builder()
                .resultMessage("댓글 등록")
                .resultData(result)
                .build();
    }

    @GetMapping
    public ResultResponse<FeedCommentGetRes> getFeedComment(@ParameterObject @ModelAttribute FeedCommentGetReq p) {
//      FeedCommentGetReq p = new FeedCommentGetReq();
//
//      p.setPage(page);
//      p.setFeedId(feedId);

      FeedCommentGetRes res = service.getFeedComment(p);
      return ResultResponse.<FeedCommentGetRes>builder()
              .resultMessage(String.format("%d rows", res.getCommentList().size()))
              .resultData(res)
              .build();
    }

    @GetMapping("/ver2")
    public ResultResponse<FeedCommentGetRes> getFeedComment2(@RequestParam("feed_id") long feedId, @RequestParam int page) {
        FeedCommentGetReq p = new FeedCommentGetReq(feedId, page);
        FeedCommentGetRes res = service.getFeedComment(p);

        return ResultResponse.<FeedCommentGetRes>builder()
                .resultMessage(String.format("%d rows", res.getCommentList().size()))
                .resultData(res)
                .build();
    }

    @DeleteMapping
    //feed_comment_id, signed_user_id
    //FE - data 전달방식 : Query-String
    public ResultResponse<Integer> delFeedComment () {
        return null;
    }
}
