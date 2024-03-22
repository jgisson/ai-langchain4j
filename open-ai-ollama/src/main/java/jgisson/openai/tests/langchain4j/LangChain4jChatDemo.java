package jgisson.openai.tests.langchain4j;

import dev.langchain4j.model.chat.ChatLanguageModel;

public class LangChain4jChatDemo {

    public static void main(String[] args) {
        ChatLanguageModel chatModel = AiModelFactory.createChatLanguageModel(AiModel.LLAMA2);
        String answer = chatModel.generate("List all the movies directed by Quentin Tarantino");
        System.out.println("API response: " + answer);
    }

}
