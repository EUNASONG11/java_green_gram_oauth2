package com.green.greengramver.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.green.greengramver.common.model.Paging;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.BindParam;

@Slf4j
@Getter
@ToString(callSuper = true) //부모 클래스의 필드들도 포함해서 toString() 메소드를 생성
public class FeedGetReq extends Paging {
    @JsonIgnore
    private long signedUserId;

    @Positive // 1 이상의 정수(양수)여야 함
    @Schema(title="프로필 유저 PK", name="profile_user_id", example = "2", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long profileUserId;

    //@ConstructorProperties({"page", "size", "signed_user_id"})
    public FeedGetReq(Integer page, Integer size, @BindParam("profile_user_id") Long profileUserId) {
        super(page, size);
        this.profileUserId = profileUserId;
    }

    public void setSignedUserId(long signedUserId) {
        this.signedUserId = signedUserId;
    }
}
