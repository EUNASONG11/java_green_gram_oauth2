package com.green.greengramver.user;

import com.green.greengramver.config.security.SignInProviderType;
import com.green.greengramver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

// <연결할 entity, PK 타입>
// 상속받은 것으로도 빈 등록이 가능
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUidAndProviderType(String uid, SignInProviderType signInProviderType); //메소드 쿼리
}
