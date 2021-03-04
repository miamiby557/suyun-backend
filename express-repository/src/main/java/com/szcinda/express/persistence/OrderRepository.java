package com.szcinda.express.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<TransportOrder, String>, JpaSpecificationExecutor<TransportOrder> {
    TransportOrder findFirstById(String orderId);
    TransportOrder findFirstByCindaNo(String cindaNo);
    List<TransportOrder> findByCindaNoIn(Collection<String> cindaNos);
    @Query(value = "select distinct clientName from TransportOrder where clientName is not null")
    List<String> findDistinctClientName();
    @Query(value = "select distinct transportChannel from TransportOrder where transportChannel is not null")
    List<String> findDistinctTransportChannel();
}
