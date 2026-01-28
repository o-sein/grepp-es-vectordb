package com.elasticsearch.esvectordb.product.document;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

// @Document(indexName = "products"): Elasticsearch의 products 인덱스에 매핑
@Document(indexName = "products")
@Data
public class ProductDocument {
    @Id
    private Long id;

    // @Field(type = FieldType.Dense_Vector, dims = 384)
    @Field(type = FieldType.Dense_Vector, dims = 384)
    private float[] embedding;

}