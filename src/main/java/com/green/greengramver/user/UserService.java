package com.green.greengramver.user;

import com.green.greengramver.common.MyFileUtils;
import com.green.greengramver.user.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper mapper;
    private final MyFileUtils myFileUtils;

    public int postUser(MultipartFile pic, UserSignUpReq p){
        String savedPicName = pic != null ? myFileUtils.makeRandomFileName(pic) : null;
        String hashedPassword = BCrypt.hashpw(p.getUpw(), BCrypt.gensalt());
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

    public UserSignInRes postSignIn(UserSignInReq p){
        UserSignInRes res = mapper.selUserByUid(p.getUid());
        if (res == null) {
            res = new UserSignInRes();
            res.setMessage("아이디를 확인해 주세요.");
        } else if (!BCrypt.checkpw(p.getUpw(), res.getUpw())) {
            res = new UserSignInRes();
            res.setMessage("비밀번호를 확인해 주세요.");
        } else {
            res.setMessage("로그인 성공");
        }
        return res;
    }

    public UserInfoGetRes getUserInfo(UserInfoGetReq p) {
        return mapper.selUserInfo(p);
    }

    @Transactional
    public String patchUserPic(UserPicPatchReq p) {
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
