package com.sawmik.elastic_search.service.article;

import com.sawmik.elastic_search.entity.Article;
import com.sawmik.elastic_search.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public Article save(Article article) {
        if (article.getCreatedAt() == null) {
            article.setCreatedAt(LocalDateTime.now());
        }
        article.setUpdatedAt(LocalDateTime.now());
        return articleRepository.save(article);
    }

    public Optional<Article> findById(String id) {
        return articleRepository.findById(id);
    }

    public List<Article> findAll() {
        List<Article> articles = new ArrayList<>();
        articleRepository.findAll().forEach(articles::add);
        return articles;
    }

    public void deleteById(String id) {
        articleRepository.deleteById(id);
    }

    public void deleteAll() {
        articleRepository.deleteAll();
    }

    public long count() {
        return articleRepository.count();
    }

    public List<Article> findByAuthor(String author) {
        return articleRepository.findByAuthor(author);
    }

    public List<Article> findByStatus(String status) {
        return articleRepository.findByStatus(status);
    }

    public List<Article> findByPublished(boolean published) {
        return articleRepository.findByPublished(published);
    }

    public List<Article> bulkSave(List<Article> articles) {
        List<IndexQuery> queries = articles.stream()
                .map(article -> new IndexQueryBuilder()
                        .withId(article.getId())
                        .withObject(article)
                        .build())
                .toList();

        elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of("articles"));
        return articles;
    }
}
