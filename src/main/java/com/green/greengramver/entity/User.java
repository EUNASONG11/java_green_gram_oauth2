package com.green.greengramver.entity;

import com.green.greengramver.config.security.SignInProviderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity // 테이블을 만들고 DML 때 사용
@Getter
@Setter
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"uid", "provider_type"}
                )
        }
)
public class User extends UpdatedAt{
    @Id //PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto_increment
    private Long userId;

    @Column(nullable = false)
    private SignInProviderType providerType;

    @Column(nullable = false, length = 30) //length = 30 은 VARCHAR(30)와 동일
    private String uid;

    @Column(nullable = false, length = 100)
    private String upw;

    @Column(length = 30)
    private String nickName;

    @Column(length = 255)
    private String pic;
}
