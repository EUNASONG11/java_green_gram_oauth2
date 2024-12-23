package com.green.greengramver.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.greengramver.common.CookieUtils;
import com.green.greengramver.common.MyFileUtils;
import com.green.greengramver.common.exception.CustomException;
import com.green.greengramver.common.exception.UserErrorCode;
import com.green.greengramver.config.jwt.JwtProperties;
import com.green.greengramver.config.jwt.JwtUser;
import com.green.greengramver.config.jwt.TokenProvider;
import com.green.greengramver.config.security.AuthenticationFacade;
import com.green.greengramver.user.model.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.CookiesWithoutEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final MyFileUtils myFileUtils;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final CookieUtils cookieUtils;
    private final AuthenticationFacade authenticationFacade;

    public int postUser(MultipartFile pic, UserSignUpReq p){
        String savedPicName = pic != null ? myFileUtils.makeRandomFileName(pic) : null;
        String hashedPassword = passwordEncoder.encode(p.getUpw());
        p.setPic(savedPicName);
        p.setUpw(hashedPassword);

        int result = mapper.insUser(p);

        if (pic == null) {
            return result;
        }

        long userId = p.getUserId();
        String middlePath = String.format("user/%d", userId);
        myFileUtils.makeFolders(middlePath);

        String filePath = String.format("%s/%s", middlePath, savedPicName);
        try{
            myFileUtils.transferTo(pic, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public UserSignInRes postSignIn(UserSignInReq p, HttpServletResponse response){
        UserSignInRes res = mapper.selUserByUid(p.getUid());
        if (res == null || !passwordEncoder.matches(p.getUpw(), res.getUpw())) {
            throw new CustomException(UserErrorCode.INCORRECT_ID_PW);
        }

        /*
        JWT 토큰 생성 2개 - AccessToken(20분), RefreshToken(15일)
         */
        JwtUser jwtUser = new JwtUser();

        jwtUser.setSignedUserId(res.getUserId());

        List<String> roles = new ArrayList<>(2);
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
        jwtUser.setRoles(roles);

        String accessToken = tokenProvider.generateToken(jwtUser, Duration.ofSeconds(30));
        String refreshToken = tokenProvider.generateToken(jwtUser, Duration.ofDays(15));

        // refreshToken은 쿠키에 담는다.
        int maxAge = 1_296_000; // 15 * 24 * 60 * 60 > 15일의 초(second) 값
        cookieUtils.setCookie(response, "refreshToken", refreshToken, maxAge);

        res.setMessage("로그인 성공");
        res.setAccessToken(accessToken);
        return res;
    }

    public UserInfoGetRes getUserInfo(UserInfoGetReq p) {
        p.setSignedUserId(authenticationFacade.getSignedUserId());
        return mapper.selUserInfo(p);
    }

    public String getAccessToken (HttpServletRequest req) {
        Cookie cookie = cookieUtils.getCookie(req,"refreshToken");
        String refreshToken = cookie.getValue();
        log.info("refreshToken: {}", refreshToken);

        JwtUser jwtUser = tokenProvider.getJwtUserFromToken(refreshToken);
        String accessToken = tokenProvider.generateToken(jwtUser, Duration.ofMinutes(20));

        return accessToken;
    }

    @Transactional
    public String patchUserPic(UserPicPatchReq p) {
        p.setSignedUserId(authenticationFacade.getSignedUserId());
        // 저장할 파일명(랜덤한 파일명) 생성, 이때 확장자는 오리지널 파일명과 일치하게 한다.
        String savedPicName = p.getPic() != null ? myFileUtils.makeRandomFileName(p.getPic()) : null;

        //폴더 만들기
        String folderPath = String.format("user/%d", p.getSignedUserId());
        myFileUtils.makeFolders(folderPath);

        // 기존 파일을 삭제(방법 3가지 [1]: 폴더를 지운다. [2]: select 해서 기존 파일명을 얻어온다. [3]: 기존 파일명을 FE에서 받는다)
        String deletePath = String.format("%s/user/%s", myFileUtils.getUploadPath(), p.getSignedUserId());
        myFileUtils.deleteFolder(deletePath, false);

        p.setPicName(savedPicName);
        int result = mapper.updUserPic(p);

        if (p.getPic() == null) {
            return null;
        }
        // 원하는 위치에 저장할 파일명으로 파일을 이동(transferTo)
        String filePath = String.format("user/%d/%s", p.getSignedUserId(), savedPicName);
        try {
            myFileUtils.transferTo(p.getPic(), filePath);
        }catch (IOException e) {
            e.printStackTrace();
        }

        // DB에 튜플을 수정(Update)
        return savedPicName;
    }
}
