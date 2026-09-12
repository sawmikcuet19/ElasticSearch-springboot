package com.sawmik.elastic_search.service.product;

import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import com.sawmik.elastic_search.entity.Product;
import com.sawmik.elastic_search.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsIterator;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public List<Product> findByName(String name) {
        return productRepository.findByName(name);
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        return productRepository.findByPriceBetween(min, max);
    }

    public Page<Product> findByCategoryPaged(String category, int page, int size) {
        return productRepository.findByCategory(category, PageRequest.of(page, size));
    }

    public List<Product> searchByNameOrDescription(String query) {
        return productRepository.searchByNameOrDescription(query);
    }

    public List<Product> findByWildcardName(String pattern) {
        return productRepository.findByWildcardName(pattern);
    }

    public List<Product> findByFuzzyName(String name) {
        return productRepository.findByFuzzyName(name);
    }

    public List<Product> findByTags(List<String> tags) {
        return productRepository.findByAnyTag(tags);
    }

    public Page<Product> searchPaged(String category, int page, int size, String sortBy, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return productRepository.findByCategory(category, pageable);
    }

    public List<Product> findByLocationNear(double lat, double lon, String distance) {
        return productRepository.findByLocationNear(distance, lat, lon);
    }

    public SearchHits<Product> searchWithBoolQuery(String name, BigDecimal minPrice, BigDecimal maxPrice) {
        NativeQuery query = new NativeQueryBuilder()
                .withQuery(q -> q.bool(b -> b
                        .must(m -> m.match(mt -> mt.field("name").query(name)))
                        .filter(f -> f.range(r -> r
                                .number(n -> n
                                        .field("price")
                                        .gte(minPrice.doubleValue())
                                        .lte(maxPrice.doubleValue())
                                )
                        ))
                ))
                .withSort(s -> s.field(f -> f.field("price").order(SortOrder.Asc)))
                .withPageable(PageRequest.of(0, 10))
                .build();

        return elasticsearchOperations.search(query, Product.class);
    }

    public SearchHits<Product> searchWithHighlight(String query) {
        HighlightParameters params = HighlightParameters.builder()
                .withPreTags("<em>")
                .withPostTags("</em>")
                .build();
        Highlight highlight = new Highlight(params, List.of(
                new HighlightField("name"),
                new HighlightField("description")
        ));

        NativeQuery nativeQuery = new NativeQueryBuilder()
                .withQuery(q -> q.multiMatch(mm -> mm
                        .fields("name", "description")
                        .query(query)
                ))
                .withHighlightQuery(new HighlightQuery(highlight, Product.class))
                .build();

        return elasticsearchOperations.search(nativeQuery, Product.class);
    }

    public SearchHits<Product> searchWithAggregations() {
        NativeQuery query = new NativeQueryBuilder()
                .withAggregation("avg_price", Aggregation.of(a -> a.avg(avg -> avg.field("price"))))
                .withAggregation("max_price", Aggregation.of(a -> a.max(max -> max.field("price"))))
                .withAggregation("min_price", Aggregation.of(a -> a.min(min -> min.field("price"))))
                .withAggregation("sum_stock", Aggregation.of(a -> a.sum(s -> s.field("stockQuantity"))))
                .withAggregation("doc_count", Aggregation.of(a -> a.valueCount(v -> v.field("price"))))
                .withPageable(PageRequest.of(0, 1))
                .build();

        return elasticsearchOperations.search(query, Product.class);
    }

    public SearchHits<Product> searchWithTermsAggregation() {
        NativeQuery query = new NativeQueryBuilder()
                .withAggregation("by_category", Aggregation.of(a -> a.terms(t -> t
                        .field("category")
                        .size(10)
                )))
                .withPageable(PageRequest.of(0, 1))
                .build();

        return elasticsearchOperations.search(query, Product.class);
    }

    public List<Product> searchWithScroll(String query, int batchSize) {
        NativeQuery nativeQuery = new NativeQueryBuilder()
                .withQuery(q -> q.match(m -> m.field("name").query(query)))
                .withPageable(PageRequest.of(0, batchSize))
                .build();

        List<Product> results = new ArrayList<>();
        try (SearchHitsIterator<Product> scroll = elasticsearchOperations.searchForStream(nativeQuery, Product.class)) {
            while (scroll.hasNext()) {
                results.add(scroll.next().getContent());
            }
        }
        return results;
    }
}
