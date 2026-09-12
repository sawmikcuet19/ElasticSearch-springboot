package com.sawmik.elastic_search.controller.article;

import com.sawmik.elastic_search.entity.Article;
import com.sawmik.elastic_search.service.article.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Article> create(@RequestBody Article article) {
        return ResponseEntity.ok(articleService.save(article));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getById(@PathVariable String id) {
        return articleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Article>> getAll() {
        return ResponseEntity.ok(articleService.findAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Article> update(@PathVariable String id, @RequestBody Article article) {
        article.setId(id);
        return ResponseEntity.ok(articleService.save(article));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        articleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(articleService.count());
    }

    @GetMapping("/author/{author}")
    public ResponseEntity<List<Article>> findByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(articleService.findByAuthor(author));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Article>> findByStatus(@PathVariable String status) {
        return ResponseEntity.ok(articleService.findByStatus(status));
    }

    @GetMapping("/published/{published}")
    public ResponseEntity<List<Article>> findByPublished(@PathVariable boolean published) {
        return ResponseEntity.ok(articleService.findByPublished(published));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Article>> bulkSave(@RequestBody List<Article> articles) {
        return ResponseEntity.ok(articleService.bulkSave(articles));
    }
}
