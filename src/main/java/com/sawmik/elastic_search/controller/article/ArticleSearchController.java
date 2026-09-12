package com.sawmik.elastic_search.controller.article;

import com.sawmik.elastic_search.entity.Article;
import com.sawmik.elastic_search.service.article.ArticleSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles/search")
@RequiredArgsConstructor
public class ArticleSearchController {

    private final ArticleSearchService articleSearchService;

    @GetMapping("/fulltext/{query}")
    public ResponseEntity<List<Article>> fullTextSearch(@PathVariable String query) {
        return ResponseEntity.ok(articleSearchService.fullTextSearch(query));
    }

    @GetMapping("/title/{query}")
    public ResponseEntity<List<Article>> searchByTitle(@PathVariable String query) {
        return ResponseEntity.ok(articleSearchService.searchByTitle(query));
    }

    @GetMapping("/content/{query}")
    public ResponseEntity<List<Article>> searchByContent(@PathVariable String query) {
        return ResponseEntity.ok(articleSearchService.searchByContent(query));
    }

    @GetMapping("/highlight/{query}")
    public ResponseEntity<SearchHits<Article>> searchWithHighlight(@PathVariable String query) {
        return ResponseEntity.ok(articleSearchService.searchWithHighlight(query));
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<Article>> searchPaged(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        Sort.Direction dir = "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return ResponseEntity.ok(articleSearchService.searchPaged(q, page, size, dir, sortBy));
    }

    @GetMapping("/bool/must")
    public ResponseEntity<SearchHits<Article>> searchBoolMust(
            @RequestParam String title,
            @RequestParam String author) {
        return ResponseEntity.ok(articleSearchService.searchWithBoolMust(title, author));
    }

    @GetMapping("/bool/must-not/{author}")
    public ResponseEntity<SearchHits<Article>> searchBoolMustNot(@PathVariable String author) {
        return ResponseEntity.ok(articleSearchService.searchWithBoolMustNot(author));
    }

    @GetMapping("/bool/filter")
    public ResponseEntity<SearchHits<Article>> searchTermsFilter(@RequestBody List<String> categories) {
        return ResponseEntity.ok(articleSearchService.searchWithTermsFilter(categories));
    }

    @GetMapping("/published/{published}/page")
    public ResponseEntity<Page<Article>> findByPublishedPaged(
            @PathVariable boolean published,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(articleSearchService.findByPublishedPaged(published, page, size));
    }
}
