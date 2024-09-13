import lombok.Builder;
import lombok.Data;

import java.util.UUID;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DocumentManager {

    private final Map<String, Document> storage = new HashMap<>();

    public Document save(Document document) {
        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId(UUID.randomUUID().toString());
            document.setCreated(Instant.now());
        } else if (storage.containsKey(document.getId())) {
            Document existingDocument = storage.get(document.getId());
            document.setCreated(existingDocument.getCreated());
        }

        storage.put(document.getId(), document);
        return document;
    }

    public List<Document> search(SearchRequest request) {
        return storage.values().stream()
                .filter(doc -> request.getTitlePrefixes() == null || request.getTitlePrefixes().stream().anyMatch(prefix -> doc.getTitle().startsWith(prefix)))
                .filter(doc -> request.getContainsContents() == null || request.getContainsContents().stream().anyMatch(content -> doc.getContent().contains(content)))
                .filter(doc -> request.getAuthorIds() == null || request.getAuthorIds().contains(doc.getAuthor().getId()))
                .filter(doc -> request.getCreatedFrom() == null || !doc.getCreated().isBefore(request.getCreatedFrom()))
                .filter(doc -> request.getCreatedTo() == null || !doc.getCreated().isAfter(request.getCreatedTo()))
                .collect(Collectors.toList());
    }

    public Optional<Document> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}
