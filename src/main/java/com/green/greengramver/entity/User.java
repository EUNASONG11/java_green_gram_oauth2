package com.green.greengramver.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity // 테이블을 만들고 DML 때 사용
@Getter
@Setter
public class User extends UpdatedAt{
    @Id //PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto_increment
    private Long userId;

    @Column(nullable = false, length = 30) //length = 30 은 VARCHAR(30)와 동일
    private String uid;

    @Column(nullable = false, length = 100)
    private String upw;

    @Column(length = 30)
    private String nickName;

    @Column(length = 50)
    private String pic;
}
