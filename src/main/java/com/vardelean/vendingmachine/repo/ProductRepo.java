package com.vardelean.vendingmachine.repo;

import com.vardelean.vendingmachine.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Long> {}
