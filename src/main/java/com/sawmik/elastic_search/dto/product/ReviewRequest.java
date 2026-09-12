package com.sawmik.elastic_search.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    private String reviewer;
    private String comment;
    private Integer rating;
}
