package com.szcinda.express.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface ClientRepository extends JpaRepository<Client,String> {
    Client findFirstById(String id);

    List<Client> findByNameIn(Collection<String> names);

    @Modifying
    @Transactional
    @Query("delete from Client where id = ?1")
    void deleteById(String id);
}
