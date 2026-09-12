package com.sawmik.elastic_search.service.article;

import com.sawmik.elastic_search.entity.Article;
import com.sawmik.elastic_search.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleSearchService {

    private final ArticleRepository articleRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public List<Article> fullTextSearch(String query) {
        return articleRepository.fullTextSearch(query);
    }

    public List<Article> searchByTitle(String query) {
        return articleRepository.searchByTitle(query);
    }

    public List<Article> searchByContent(String query) {
        return articleRepository.searchByContent(query);
    }

    public Page<Article> findByPublishedPaged(boolean published, int page, int size) {
        return articleRepository.findByPublished(published, PageRequest.of(page, size));
    }

    public SearchHits<Article> searchWithHighlight(String query) {
        HighlightParameters params = HighlightParameters.builder()
                .withPreTags("<em>")
                .withPostTags("</em>")
                .build();
        Highlight highlight = new Highlight(params, List.of(
                new HighlightField("title"),
                new HighlightField("content")
        ));

        NativeQuery nativeQuery = new NativeQueryBuilder()
                .withQuery(q -> q.multiMatch(mm -> mm
                        .fields("title^3", "content", "tags^2")
                        .query(query)
                ))
                .withHighlightQuery(new HighlightQuery(highlight, Article.class))
                .withPageable(PageRequest.of(0, 10))
                .build();

        return elasticsearchOperations.search(nativeQuery, Article.class);
    }

    public Page<Article> searchPaged(String query, int page, int size, Sort.Direction direction, String sortBy) {
        Sort sort = Sort.by(direction, sortBy);
        PageRequest pageable = PageRequest.of(page, size, sort);

        NativeQuery nativeQuery = new NativeQueryBuilder()
                .withQuery(q -> q.multiMatch(mm -> mm
                        .fields("title", "content")
                        .query(query)
                ))
                .withPageable(pageable)
                .build();

        SearchHits<Article> searchHits = elasticsearchOperations.search(nativeQuery, Article.class);
        List<Article> articles = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();
        return new PageImpl<>(articles, pageable, searchHits.getTotalHits());
    }

    public SearchHits<Article> searchWithBoolMust(String title, String author) {
        NativeQuery query = new NativeQueryBuilder()
                .withQuery(q -> q.bool(b -> b
                        .must(m -> m.match(mt -> mt.field("title").query(title)))
                        .must(m2 -> m2.match(mt2 -> mt2.field("author").query(author)))
                ))
                .build();

        return elasticsearchOperations.search(query, Article.class);
    }

    public SearchHits<Article> searchWithBoolMustNot(String author) {
        NativeQuery query = new NativeQueryBuilder()
                .withQuery(q -> q.bool(b -> b
                        .mustNot(mn -> mn.match(mt -> mt.field("author").query(author)))
                ))
                .build();

        return elasticsearchOperations.search(query, Article.class);
    }

    public SearchHits<Article> searchWithTermsFilter(List<String> categories) {
        NativeQuery query = new NativeQueryBuilder()
                .withQuery(q -> q.bool(b -> b
                        .filter(f -> f.terms(t -> t
                                .field("categories")
                                .terms(tv -> tv.value(categories.stream()
                                        .map(co.elastic.clients.elasticsearch._types.FieldValue::of)
                                        .toList()))
                        ))
                ))
                .build();

        return elasticsearchOperations.search(query, Article.class);
    }
}
