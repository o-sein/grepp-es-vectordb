package com.elasticsearch.esvectordb.product.repository;

import com.elasticsearch.esvectordb.product.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product,Long> {
}