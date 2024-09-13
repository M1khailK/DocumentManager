import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DocumentManagerTest {

    private DocumentManager documentManager;

    @BeforeEach
    void setUp() {
        documentManager = new DocumentManager();
    }

    @Test
    void saveAndFindById_ShouldSaveAndReturnDocument() {
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Test Document")
                .content("Test content")
                .author(author)
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        Optional<DocumentManager.Document> foundDocument = documentManager.findById(savedDocument.getId());

        assertTrue(foundDocument.isPresent());
        assertEquals(savedDocument, foundDocument.get());
    }

    @Test
    void search_ShouldReturnCorrectDocuments() {
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        DocumentManager.Document document1 = DocumentManager.Document.builder()
                .title("First Document")
                .content("Content of the first document")
                .author(author)
                .build();

        DocumentManager.Document document2 = DocumentManager.Document.builder()
                .title("Second Document")
                .content("Content of the second document")
                .author(author)
                .build();

        documentManager.save(document1);
        documentManager.save(document2);

        DocumentManager.SearchRequest searchRequest = DocumentManager.SearchRequest.builder()
                .titlePrefixes(Collections.singletonList("First"))
                .build();

        List<DocumentManager.Document> searchResults = documentManager.search(searchRequest);

        assertEquals(1, searchResults.size());
        assertEquals(document1, searchResults.get(0));
    }

    @Test
    void search_ShouldReturnEmptyListIfNoDocumentsMatch() {
        DocumentManager.SearchRequest searchRequest = DocumentManager.SearchRequest.builder()
                .titlePrefixes(Collections.singletonList("Non-existent"))
                .build();

        List<DocumentManager.Document> searchResults = documentManager.search(searchRequest);

        assertTrue(searchResults.isEmpty());
    }

    @Test
    void save_ShouldUpdateExistingDocument() {
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Document to be updated")
                .content("Initial content")
                .author(author)
                .build();

        DocumentManager.Document savedDocument = documentManager.save(document);

        savedDocument.setContent("Updated content");

        DocumentManager.Document updatedDocument = documentManager.save(savedDocument);

        Optional<DocumentManager.Document> foundDocument = documentManager.findById(updatedDocument.getId());

        assertTrue(foundDocument.isPresent());
        assertEquals("Updated content", foundDocument.get().getContent());
        assertEquals(savedDocument.getCreated(), foundDocument.get().getCreated());
    }

    @Test
    void findById_ShouldReturnEmptyOptionalIfDocumentNotFound() {
        Optional<DocumentManager.Document> foundDocument = documentManager.findById("non-existent-id");

        assertTrue(foundDocument.isEmpty());
    }
}
