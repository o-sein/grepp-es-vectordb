package com.elasticsearch.esvectordb.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
public class ProductKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Setter
    @Getter
    @JsonIgnore // JSON 직렬화 시 순환 참조 방지
    private Product product;

    @Column
    @Getter
    @Setter
    private String keyword;

    public ProductKeyword(String keyword) {
        this.keyword = keyword;
    }
}