package com.auction.onlineauction.repository;

import com.auction.onlineauction.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}