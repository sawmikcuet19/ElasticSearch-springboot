package com.sawmik.elastic_search.repository;

import com.sawmik.elastic_search.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleRepository extends ElasticsearchRepository<Article, String> {

    // Derived query methods
    List<Article> findByAuthor(String author);

    List<Article> findByTitle(String title);

    List<Article> findByPublished(boolean published);

    List<Article> findByStatus(String status);

    List<Article> findByCategoriesContaining(String category);

    List<Article> findByTagsContaining(String tag);

    Page<Article> findByPublished(boolean published, Pageable pageable);

    List<Article> findByAuthorAndPublished(String author, boolean published);

    long countByAuthor(String author);

    long countByStatus(String status);

    List<Article> findByPublishedAtBetween(LocalDateTime start, LocalDateTime end);

    // Custom Elasticsearch queries
    @Query("""
        {
          "bool": {
            "must": [
              { "match": { "title": "?0" } }
            ]
          }
        }
    """)
    List<Article> searchByTitle(String query);

    @Query("""
        {
          "bool": {
            "must": [
              { "match": { "content": "?0" } }
            ]
          }
        }
    """)
    List<Article> searchByContent(String query);

    @Query("""
        {
          "bool": {
            "must": [
              { "multi_match": {
                  "query": "?0",
                  "fields": ["title^3", "content", "tags^2"]
              }}
            ]
          }
        }
    """)
    List<Article> fullTextSearch(String query);

    @Query("""
        {
          "bool": {
            "must": [
              { "match": { "content": "?0" } }
            ],
            "should": [
              { "term": { "published": true } }
            ],
            "filter": [
              { "range": { "viewCount": { "gte": ?1 } } }
            ]
          }
        }
    """)
    List<Article> searchByContentWithMinViews(String query, int minViews);

    @Query("""
        {
          "bool": {
            "must_not": [
              { "term": { "status": "deleted" } }
            ],
            "filter": [
              { "term": { "published": true } }
            ]
          }
        }
    """)
    List<Article> findAllPublishedNotDeleted();

    @Query("""
        {
          "regexp": {
            "title.keyword": "?0"
          }
        }
    """)
    List<Article> findByTitleRegex(String regex);
}
