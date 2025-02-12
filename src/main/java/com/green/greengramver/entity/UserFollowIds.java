package com.green.greengramver.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class UserFollowIds implements Serializable {
    private Long fromUserId; // 여기서는 데이터베이스 관점으로 타입 설정
    private Long toUserId;
}
