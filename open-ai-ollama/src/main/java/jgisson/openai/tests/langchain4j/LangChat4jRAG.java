package jgisson.openai.tests.langchain4j;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class LangChat4jRAG {

    public static void main(String[] args) {

        // Embedding and Store document
        DocumentSplitter splitter = DocumentSplitters.recursive(600, 0);
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore;
        embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        // Load document
        Document document = FileSystemDocumentLoader.loadDocument(toPath("/siva.txt"), new TextDocumentParser());
        ingestor.ingest(document);
        System.out.println("Document siva.txt loaded and stored successfully");
        // Load document
        Document documentCuriosity = FileSystemDocumentLoader.loadDocument(toPath("/curiosity-rover.txt"), new TextDocumentParser());
        ingestor.ingest(documentCuriosity);
        System.out.println("Document curiosity-rover.txt loaded and stored successfully");

        // Create LLM Model and prompt template
        ChatLanguageModel model = AiModelFactory.createChatLanguageModel(AiModel.OPENAI_DEMO);
        PromptTemplate promptTemplate = PromptTemplate.from("""
                        Tell me about {{name}}?
                                                
                        Use the information to answer the question:
                        {{information}}
                        """);

        // Ask and Query Embedding store about Siva
        String answer = tellAbout(embeddingModel, embeddingStore, promptTemplate, model, "Siva");
        System.out.println("\nAnswer:\n" + answer);

        // Ask and Query Embedding store about Curiosity
        System.out.println("\n\n **** Ask about Curiosity *****\n");
        String answerCuriosity = tellAbout(embeddingModel, embeddingStore, promptTemplate, model, "Curiosity");
        System.out.println("\nAnswer:\n" + answerCuriosity);

        // Use LLM to answer with ContentRetriever
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(1)
                //.minScore(0.5)
                .build();
        PersonDataExtractor extractor = AiServices.builder(PersonDataExtractor.class)
                .chatLanguageModel(model)
                .contentRetriever(contentRetriever)
                .build();
        Person person = extractor.getInfoAbout("Siva");
        System.out.println("Person:\n" + person);
    }

    private static String tellAbout(EmbeddingModel embeddingModel, EmbeddingStore<TextSegment> embeddingStore,
                                    PromptTemplate promptTemplate, ChatLanguageModel model,
                                    String aboutName) {
        Embedding queryEmbedding = embeddingModel.embed("Tell me about " + aboutName + "?").content();
        List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 2);
        EmbeddingMatch<TextSegment> embeddingMatch = relevant.getFirst();
        String information = embeddingMatch.embedded().text();
        System.out.println("Relevant Information:\n" + information);
        // Use LLM to answer with RAG
        Prompt prompt = promptTemplate.apply(Map.of("name", aboutName, "information", information));
        return model.generate(prompt.toUserMessage()).content().text();
    }

    interface PersonDataExtractor {
        @UserMessage("Get information about {{it}} as of {{current_date}}")
        Person getInfoAbout(String name);
    }

    record Person(String name,
                  LocalDate dateOfBirth,
                  int experienceInYears,
                  List<String> books) {
    }

    private static Path toPath(String fileName) {
        try {
            URL fileUrl = LangChat4jRAG.class.getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
