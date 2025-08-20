package com.shop.repository.order;

import com.shop.entity.Order;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderRepositoryCustom {
    List<Order> findOrders(String email, Pageable pageable);
    Long countOrder(String email);
}
