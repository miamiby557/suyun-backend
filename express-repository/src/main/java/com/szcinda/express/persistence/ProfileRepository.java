package com.szcinda.express.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, String>, JpaSpecificationExecutor<Profile> {
    @Query(value = "select p from Profile p where p.name = :name or p.company = :name")
    List<Profile> findByName(@Param("name") String name);
    Profile findFirstById(String id);
}
