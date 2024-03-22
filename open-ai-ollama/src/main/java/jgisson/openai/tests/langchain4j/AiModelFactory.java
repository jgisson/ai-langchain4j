package jgisson.openai.tests.langchain4j;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class AiModelFactory {

    private AiModelFactory() {};

    public static ChatLanguageModel createChatLanguageModel(AiModel modelType) {

        return switch (modelType) {
            case AiModel.OPENAI -> {
                final String openAiKey = System.getenv("OPENAI_API_KEY");
                yield OpenAiChatModel.withApiKey(openAiKey);
            }
            case AiModel.OPENAI_DEMO -> OpenAiChatModel.withApiKey("demo");
            case AiModel.LLAMA2 -> OllamaChatModel.builder()
                    .baseUrl("http://localhost:11434")
                    .modelName("llama2")
                    .build();
        };
    }

}
