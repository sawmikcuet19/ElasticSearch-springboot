package com.sawmik.elastic_search.service.log;

import com.sawmik.elastic_search.entity.LogEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public SearchHits<LogEntry> searchWithBoolQuery(String level, String service, int minResponseCode) {
        NativeQuery query = new NativeQueryBuilder()
                .withQuery(q -> q.bool(b -> b
                        .must(m -> m.term(t -> t.field("level").value(level)))
                        .filter(f -> f.term(t -> t.field("service").value(service)))
                        .filter(f2 -> f2.range(r -> r
                                .number(n -> n
                                        .field("responseCode")
                                        .gte((double) minResponseCode)
                                )
                        ))
                ))
                .build();

        return elasticsearchOperations.search(query, LogEntry.class);
    }

    public SearchHits<LogEntry> searchWithAggregations() {
        NativeQuery query = new NativeQueryBuilder()
                .withAggregation("by_level", co.elastic.clients.elasticsearch._types.aggregations.Aggregation.of(a -> a
                        .terms(t -> t.field("level").size(10))
                ))
                .withAggregation("by_service", co.elastic.clients.elasticsearch._types.aggregations.Aggregation.of(a -> a
                        .terms(t -> t.field("service").size(10))
                ))
                .withAggregation("avg_duration", co.elastic.clients.elasticsearch._types.aggregations.Aggregation.of(a -> a
                        .avg(avg -> avg.field("durationMs"))
                ))
                .withAggregation("max_duration", co.elastic.clients.elasticsearch._types.aggregations.Aggregation.of(a -> a
                        .max(max -> max.field("durationMs"))
                ))
                .withPageable(PageRequest.of(0, 1))
                .build();

        return elasticsearchOperations.search(query, LogEntry.class);
    }

    public SearchHits<LogEntry> searchByDateHistogram() {
        NativeQuery query = new NativeQueryBuilder()
                .withAggregation("logs_over_time", co.elastic.clients.elasticsearch._types.aggregations.Aggregation.of(a -> a
                        .dateHistogram(dh -> dh
                                .field("timestamp")
                                .calendarInterval(co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval.Day)
                        )
                ))
                .withPageable(PageRequest.of(0, 1))
                .build();

        return elasticsearchOperations.search(query, LogEntry.class);
    }
}
