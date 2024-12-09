package com.green.greengramver.user;

import com.green.greengramver.user.model.UserInfoGetReq;
import com.green.greengramver.user.model.UserInfoGetRes;
import com.green.greengramver.user.model.UserSignInRes;
import com.green.greengramver.user.model.UserSignUpReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    int insUser(UserSignUpReq p);
    UserSignInRes selUserByUid(String uid);
    UserInfoGetRes selUserInfo(UserInfoGetReq p);
}
