package com.shop.repository.order;

import com.shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order,Long> ,OrderRepositoryCustom{
  
}
