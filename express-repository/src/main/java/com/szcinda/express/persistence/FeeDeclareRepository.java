package com.szcinda.express.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FeeDeclareRepository extends JpaRepository<FeeDeclare,String>, JpaSpecificationExecutor<FeeDeclare> {
    FeeDeclare findFirstById(String id);
}
