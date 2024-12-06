package com.green.greengramver.user.follow;

import com.green.greengramver.common.model.ResultResponse;
import com.green.greengramver.user.follow.model.UserFollowReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("user/follow")
public class UserFollowController {
    private final UserFollowService service;

    // 팔로우 신청
    // @requestBody, 요청을 보내는 자가 body에 JSON 형태의 데이터를 담아서 보낸다는 뜻
    @PostMapping
    public ResultResponse<Integer> postUserFollow(@RequestBody UserFollowReq p) {
        log.info("UserFollowController > postUserFollow > p: {}", p);
        int result = service.postUserFollow(p);
        return ResultResponse.<Integer>builder()
                .resultMessage("팔로우 성공")
                .resultData(result)
                .build();
    }

    // 팔로우 취소
    // @ParameterObject, 요청을 보내는 자가 Query-String 형태로 데이터를 보낸다는 뜻
    @DeleteMapping
    public ResultResponse<Integer> deleteUserFollow(@ParameterObject @ModelAttribute UserFollowReq p) {
        log.info("UserFollowController > deleteUserFollow > p: {}", p);
        int result = service.deleteUserFollow(p);
        return ResultResponse.<Integer>builder()
                .resultMessage("팔로우 취소")
                .resultData(result)
                .build();
    }
}
