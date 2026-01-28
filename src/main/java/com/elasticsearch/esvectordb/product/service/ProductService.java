package com.elasticsearch.esvectordb.product.service;

import com.elasticsearch.esvectordb.product.entity.Product;
import com.elasticsearch.esvectordb.product.repository.ProductRepository;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Score;
import org.springframework.data.domain.ScoringFunction;
import org.springframework.data.domain.Vector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private EmbeddingModel embeddingModel;

    @Transactional
    public Product create(String name, List<String> keywords) {
        Product product = new Product();
        product.setName(name);
        keywords.forEach(product::addKeyword);

        if (!keywords.isEmpty()) {
            List<float[]> embeddings = embeddingModel.embed(keywords);
            product.setEmbedding(calculateAverage(embeddings));
        }

        return productRepository.save(product);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> findAll() {
        return StreamSupport.stream(productRepository.findAll().spliterator(), false).toList();
    }

    @Transactional
    public Product update(Long id, String name, List<String> keywords) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));

        product.setName(name);
        product.getKeywords().clear();
        keywords.forEach(product::addKeyword);

        if (!keywords.isEmpty()) {
            List<float[]> embeddings = embeddingModel.embed(keywords);
            product.setEmbedding(calculateAverage(embeddings));
        } else {
            product.setEmbedding(null);
        }

        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * Vector.of(): float 배열을 Vector 객체로 변
     * Score.of(maxScore, scoringFunction): 최대 점수와 스코어링 함수 설정
     * ScoringFunction.euclidean(): 유클리드 거리 기반 유사도 계산
     * Limit.of(k): 상위 k개 결과만 반환
     */
    public List<Product> knnSearch(List<String> keywords, int k) {
        if (keywords == null || keywords.isEmpty()) {
            return List.of();
        }

        List<float[]> embeddings = embeddingModel.embed(keywords);
        float[] queryVector = calculateAverage(embeddings);

        return productRepository.searchByEmbeddingNear(
                        Vector.of(queryVector), // 검색 쿼리 벡터
                        Score.of(Double.MAX_VALUE, ScoringFunction.euclidean()), // 유클리드 거리 기반 스코어링
                        Limit.of(k)) // 최대 k개 결과 반환
                .stream()
                .map(result -> result.getContent())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Product> findSimilarProducts(Long productId, int k) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        List<String> keywords = product.getKeywords().stream()
                .map(pk -> pk.getKeyword())
                .toList();

        // 자기 자신을 제외하기 위해 k+1개를 검색한 후 필터링
        return knnSearch(keywords, k + 1).stream()
                .filter(p -> !p.getId().equals(productId))
                .limit(k)
                .toList();
    }

    private float[] calculateAverage(List<float[]> vectors) {
        if (vectors.isEmpty()) return new float[0];

        int dimension = vectors.get(0).length;
        float[] sum = new float[dimension];

        for (float[] vector : vectors) {
            for (int i = 0; i < dimension; i++) {
                sum[i] += vector[i];
            }
        }

        for (int i = 0; i < dimension; i++) {
            sum[i] /= vectors.size();
        }

        return sum;
    }
}

/**
 * 주요 변경사항:
 * ProductDocumentRepository 제거 - 더 이상 별도 저장소 불필요
 * ElasticsearchTemplate 제거 - Spring Data JPA 기본 기능 사용
 * create(): embedding을 Product 엔티티에 직접 저장
 * update(): 키워드가 비어있으면 embedding을 null로 설정
 * delete(): Elasticsearch 문서 삭제 로직 제거 (자동으로 함께 삭제)
 * knnSearch(): Spring Data의 searchByEmbeddingNear() 메서드 사용
 * knnSearchByVector(), toFloatList() 메서드 제거 - 더 이상 불필요
 * findDocumentById() 메서드 제거
 */