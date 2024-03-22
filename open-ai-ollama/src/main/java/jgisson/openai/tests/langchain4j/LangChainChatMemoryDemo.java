package jgisson.openai.tests.langchain4j;

import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.time.LocalDate;
import java.util.Map;

public class LangChainChatMemoryDemo {

    public static void main(String[] args) {

        ChatLanguageModel model = AiModelFactory.createChatLanguageModel(AiModel.OPENAI_DEMO);

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);
        //ChatMemory chatMemory = TokenWindowChatMemory.withMaxTokens(300, new OpenAiTokenizer(GPT_3_5_TURBO));

        ConversationalChain chain = ConversationalChain.builder()
                .chatLanguageModel(model)
                .chatMemory(chatMemory)
                .build();

        String answer = chain.execute("What are all the movies directed by Quentin Tarantino?");
        System.out.println(answer); // Pulp Fiction, Kill Bill, etc.

        // answer = chain.execute("How old is he as of "+ LocalDate.now() + "?");
        Prompt prompt = PromptTemplate.from("How old is he as of {{current_date}}?").apply(Map.of());
        answer = chain.execute(prompt.text());
        System.out.println(answer); // Quentin Tarantino was born on March 27, 1963, so he is currently 58 years old.

        // Manage Memory manually
        //ChatLanguageModel model = OpenAiChatModel.withApiKey(openAiKey);
        //ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);
        //
        //chatMemory.add(UserMessage.userMessage("What are all the movies directed by Quentin Tarantino?"));
        //AiMessage answer = model.generate(chatMemory.messages()).content();
        //System.out.println(answer.text()); // Pulp Fiction, Kill Bill, etc.
        //chatMemory.add(answer);
        //
        //chatMemory.add(UserMessage.userMessage("How old is he?"));
        //AiMessage answer2 = model.generate(chatMemory.messages()).content();
        //System.out.println(answer2.text()); // Quentin Tarantino was born on March 27, 1963, so he is currently 58 years old.
        //chatMemory.add(answer2);
    }

}
