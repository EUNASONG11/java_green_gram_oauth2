package com.green.greengramver.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserFollow extends CreatedAt {
    @EmbeddedId
    private UserFollowIds userFollowIds;

    @ManyToOne // UserFollow 가 Many User 가 One
    @MapsId("fromUserId") //UserFollowIds class에 있는 필드와 연결
    @JoinColumn(name = "from_user_id") //@ManyToOne 처럼 관계 설정한 컬럼은 @Column이 아니라 @JoinColumn
    private User fromUser;

    @ManyToOne
    @MapsId("toUserId")
    @JoinColumn(name = "to_user_id")
    private User toUser;
}
