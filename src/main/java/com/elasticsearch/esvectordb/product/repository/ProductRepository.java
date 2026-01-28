package com.elasticsearch.esvectordb.product.repository;

import com.elasticsearch.esvectordb.product.entity.Product;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Score;
import org.springframework.data.domain.SearchResults;
import org.springframework.data.domain.Vector;
import org.springframework.data.repository.CrudRepository;

/**
 * SearchResults<Product>: Spring Data의 벡터 검색 결과 타입
 * searchByEmbeddingNear(): 메서드 이름 규칙 기반 벡터 근접 검색
 * searchBy + Embedding (필드명) + Near (근접 검색)
 * Vector: 검색할 쿼리 벡터
 * Score: 유사도 점수 기준 및 스코어링 함수
 * Limit: 반환할 최대 결과 수
 */
public interface ProductRepository extends CrudRepository<Product,Long> {
    SearchResults<Product> searchByEmbeddingNear(Vector vector, Score score, Limit limit);
}