package com.sawmik.elastic_search.entity.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Field(type = FieldType.Text)
    private String reviewer;

    @Field(type = FieldType.Text)
    private String comment;

    @Field(type = FieldType.Integer)
    private Integer rating;
}
