package com.szcinda.express.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface CarrierRepository extends JpaRepository<Carrier,String> {
    Carrier findFirstById(String id);

    List<Carrier> findByNameIn(Collection<String> names);

    @Modifying
    @Transactional
    @Query("delete from Carrier where id = ?1")
    void deleteById(String id);
}
