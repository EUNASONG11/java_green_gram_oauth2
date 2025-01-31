package com.green.greengramver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass //Entity 부모 역할
@EntityListeners(AuditingEntityListener.class) //@EntityListeners Listeners가 있으면 이벤트 연결(binding), insert가 될 때 현재 일시 값을 넣자.
public class CreatedAt {
    @CreatedDate //insert가 되었을 때 현재 일시 값을 넣는다. 이 애노테이션이 작동하려면 @EntityListeners(AuditingEntityListener.class)이 세팅되어 있어야 한다.
    @Column(nullable = false) //빼도 자동으로 @Column 들어감, 설정을 좀 더 해주고 싶다면 명시적으로 붙여줘야 한다. nullable = false 는 not null 이라는 뜻
    private LocalDateTime createdAt;
}
