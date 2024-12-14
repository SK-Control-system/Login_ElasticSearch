//package com.example.final_jj.postgreSQL.repository;
//
//import com.example.final_jj.postgreSQL.entity.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface UserRepository extends JpaRepository<User, Long> {
//    /*
//        JPA는 데이터베이스와 관계형 매핑을 처리하는 API로, 하부 구현체로는 Hibernate가 기본적으로 사용되며,
//        PostgreSQL을 포함하여 다양한 관계형 데이터베이스에서 동작할 수 있음
//     */
//    User findByGoogleId(String googleId);
//
//    User findByAccessToken(String accessToken);
//}
//
