package com.sawmik.elastic_search.repository;

import com.sawmik.elastic_search.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends ElasticsearchRepository<Product, String> {

    // Derived query methods
    List<Product> findByName(String name);

    List<Product> findByCategory(String category);

    List<Product> findByAvailable(boolean available);

    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);

    List<Product> findByCategoryAndAvailable(String category, boolean available);

    Page<Product> findByCategory(String category, Pageable pageable);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findByTagsContaining(String tag);

    long countByCategory(String category);

    long countByAvailable(boolean available);

    // Custom Elasticsearch queries
    @Query("""
        {
          "bool": {
            "must": [
              { "match": { "name": "?0" } }
            ],
            "filter": [
              { "range": { "price": { "gte": ?1, "lte": ?2 } } }
            ]
          }
        }
    """)
    List<Product> findByNameAndPriceRange(String name, BigDecimal min, BigDecimal max);

    @Query("""
        {
          "bool": {
            "should": [
              { "match": { "name": "?0" } },
              { "match": { "description": "?0" } }
            ],
            "minimum_should_match": 1
          }
        }
    """)
    List<Product> searchByNameOrDescription(String query);

    @Query("""
        {
          "nested": {
            "path": "reviews",
            "query": {
              "range": {
                "reviews.rating": { "gte": ?0 }
              }
            }
          }
        }
    """)
    List<Product> findByMinReviewRating(int minRating);

    @Query("""
        {
          "bool": {
            "must": [
              { "terms": { "tags": ?0 } }
            ]
          }
        }
    """)
    List<Product> findByAnyTag(List<String> tags);

    // Geo distance query
    @Query("""
        {
          "bool": {
            "filter": {
              "geo_distance": {
                "distance": "?0",
                "location": {
                  "lat": ?1,
                  "lon": ?2
                }
              }
            }
          }
        }
    """)
    List<Product> findByLocationNear(String distance, double lat, double lon);

    @Query("""
        {
          "bool": {
            "must": [
              { "wildcard": { "name.keyword": "?0" } }
            ]
          }
        }
    """)
    List<Product> findByWildcardName(String pattern);

    @Query("""
        {
          "match": {
            "name": {
              "query": "?0",
              "fuzziness": "AUTO"
            }
          }
        }
    """)
    List<Product> findByFuzzyName(String name);
}
