package com.whl.wako.statemachine.dao.repository;


import com.whl.wako.statemachine.dao.entity.Order;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository {

    public int save(Order order);

}
