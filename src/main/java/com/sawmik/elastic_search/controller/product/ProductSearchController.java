package com.sawmik.elastic_search.controller.product;

import com.sawmik.elastic_search.entity.Product;
import com.sawmik.elastic_search.service.product.ProductSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products/search")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Product>> findByName(@PathVariable String name) {
        return ResponseEntity.ok(productSearchService.findByName(name));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> findByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productSearchService.findByCategory(category));
    }

    @GetMapping("/price")
    public ResponseEntity<List<Product>> findByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return ResponseEntity.ok(productSearchService.findByPriceRange(min, max));
    }

    @GetMapping("/fuzzy/{name}")
    public ResponseEntity<List<Product>> findByFuzzyName(@PathVariable String name) {
        return ResponseEntity.ok(productSearchService.findByFuzzyName(name));
    }

    @GetMapping("/wildcard/{pattern}")
    public ResponseEntity<List<Product>> findByWildcardName(@PathVariable String pattern) {
        return ResponseEntity.ok(productSearchService.findByWildcardName(pattern));
    }

    @GetMapping("/category/{category}/page")
    public ResponseEntity<Page<Product>> findByCategoryPaged(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productSearchService.findByCategoryPaged(category, page, size));
    }

    @GetMapping("/sort")
    public ResponseEntity<Page<Product>> searchPaged(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        org.springframework.data.domain.Sort.Direction dir =
                "DESC".equalsIgnoreCase(direction) ? org.springframework.data.domain.Sort.Direction.DESC : org.springframework.data.domain.Sort.Direction.ASC;
        return ResponseEntity.ok(productSearchService.searchPaged(category, page, size, sortBy, dir));
    }

    @GetMapping
    public ResponseEntity<List<Product>> search(@RequestParam String q) {
        return ResponseEntity.ok(productSearchService.searchByNameOrDescription(q));
    }

    @GetMapping("/highlight/{query}")
    public ResponseEntity<SearchHits<Product>> searchWithHighlight(@PathVariable String query) {
        return ResponseEntity.ok(productSearchService.searchWithHighlight(query));
    }

    @GetMapping("/bool")
    public ResponseEntity<SearchHits<Product>> searchBool(
            @RequestParam String name,
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        return ResponseEntity.ok(productSearchService.searchWithBoolQuery(name, minPrice, maxPrice));
    }

    @GetMapping("/aggregations")
    public ResponseEntity<SearchHits<Product>> searchAggregations() {
        return ResponseEntity.ok(productSearchService.searchWithAggregations());
    }

    @GetMapping("/terms-aggregation")
    public ResponseEntity<SearchHits<Product>> searchTermsAggregation() {
        return ResponseEntity.ok(productSearchService.searchWithTermsAggregation());
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<Product>> findNearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "10km") String distance) {
        return ResponseEntity.ok(productSearchService.findByLocationNear(lat, lon, distance));
    }

    @PostMapping("/tags")
    public ResponseEntity<List<Product>> findByTags(@RequestBody List<String> tags) {
        return ResponseEntity.ok(productSearchService.findByTags(tags));
    }
}
