package com.elasticsearch.esvectordb.product.repository;

import com.elasticsearch.esvectordb.product.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument,Long> {
}