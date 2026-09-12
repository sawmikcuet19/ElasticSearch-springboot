package com.sawmik.elastic_search.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductIndexService {

    private final ElasticsearchOperations elasticsearchOperations;

    public void createIndex(String indexName) {
        if (!elasticsearchOperations.indexOps(IndexCoordinates.of(indexName)).exists()) {
            elasticsearchOperations.indexOps(IndexCoordinates.of(indexName)).create();
        }
    }

    public boolean indexExists(String indexName) {
        return elasticsearchOperations.indexOps(IndexCoordinates.of(indexName)).exists();
    }

    public void deleteIndex(String indexName) {
        if (elasticsearchOperations.indexOps(IndexCoordinates.of(indexName)).exists()) {
            elasticsearchOperations.indexOps(IndexCoordinates.of(indexName)).delete();
        }
    }

    public void refreshIndex(String indexName) {
        elasticsearchOperations.indexOps(IndexCoordinates.of(indexName)).refresh();
    }
}
