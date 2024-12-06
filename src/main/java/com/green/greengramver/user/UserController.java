package com.green.greengramver.user;

import com.green.greengramver.common.model.ResultResponse;
import com.green.greengramver.user.model.UserSignInReq;
import com.green.greengramver.user.model.UserSignInRes;
import com.green.greengramver.user.model.UserSignUpReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {
    private final UserService service;

    @PostMapping("sign-up")
    public ResultResponse<Integer> postUser(@RequestPart(required = false) MultipartFile pic, @RequestPart UserSignUpReq p) {
        int result = service.postUser(pic, p);
        return ResultResponse.<Integer>builder()
                .resultMessage("회원가입 완료")
                .resultData(result)
                .build();
    }

    @PostMapping("sign-in")
    public ResultResponse<UserSignInRes> postUserSignIn(@RequestBody UserSignInReq req) {
        UserSignInRes res = service.postSignIn(req);
        return ResultResponse.<UserSignInRes>builder()
                .resultMessage(res.getMessage())
                .resultData(res)
                .build();
    }
}
