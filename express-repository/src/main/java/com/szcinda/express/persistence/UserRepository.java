package com.szcinda.express.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    User findFirstByAccount(String account);
    User findFirstById(String userId);

    @Modifying
    @Transactional
    @Query("delete from User where id = ?1")
    void deleteByUserId(String id);
}
