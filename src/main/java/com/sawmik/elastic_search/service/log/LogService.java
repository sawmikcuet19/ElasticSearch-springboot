package com.sawmik.elastic_search.service.log;

import com.sawmik.elastic_search.entity.LogEntry;
import com.sawmik.elastic_search.repository.LogEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class LogService {

    private final LogEntryRepository logEntryRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public LogEntry save(LogEntry logEntry) {
        if (logEntry.getTimestamp() == null) {
            logEntry.setTimestamp(LocalDateTime.now());
        }
        return logEntryRepository.save(logEntry);
    }

    public Optional<LogEntry> findById(String id) {
        return logEntryRepository.findById(id);
    }

    public List<LogEntry> findAll() {
        List<LogEntry> logs = new ArrayList<>();
        logEntryRepository.findAll().forEach(logs::add);
        return logs;
    }

    public void deleteById(String id) {
        logEntryRepository.deleteById(id);
    }

    public void deleteAll() {
        logEntryRepository.deleteAll();
    }

    public List<LogEntry> findByLevel(String level) {
        return logEntryRepository.findByLevel(level);
    }

    public List<LogEntry> findByService(String service) {
        return logEntryRepository.findByService(service);
    }

    public Page<LogEntry> findByLevelPaged(String level, int page, int size) {
        return logEntryRepository.findByLevel(level, PageRequest.of(page, size));
    }

    public List<LogEntry> searchByMessage(String query) {
        return logEntryRepository.searchByMessage(query);
    }

    public List<LogEntry> findErrorResponses() {
        return logEntryRepository.findErrorResponses();
    }

    public List<LogEntry> findSlowRequests(long minDurationMs) {
        return logEntryRepository.findSlowRequests(minDurationMs);
    }

    public List<LogEntry> findByExactPhrase(String phrase) {
        return logEntryRepository.findByExactMessagePhrase(phrase);
    }

    public List<LogEntry> bulkSave(List<LogEntry> logs) {
        List<IndexQuery> queries = logs.stream()
                .map(log -> new IndexQueryBuilder()
                        .withId(log.getId())
                        .withObject(log)
                        .build())
                .toList();

        elasticsearchOperations.bulkIndex(queries, IndexCoordinates.of("logs"));
        return logs;
    }
}
