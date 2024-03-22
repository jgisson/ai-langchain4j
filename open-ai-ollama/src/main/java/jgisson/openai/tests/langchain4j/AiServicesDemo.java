package jgisson.openai.tests.langchain4j;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.time.LocalDate;
import java.util.List;


public class AiServicesDemo {

    public static void main(String[] args) {
        ChatLanguageModel model = AiModelFactory.createChatLanguageModel(AiModel.OPENAI_DEMO);

        // basicAiService(model);
        // funnyAssistant(model);
        aiServiceTextSummary(model);
    }

    record Person(String name, LocalDate dateOfBirth, int experienceInYears, List<String> booksAuthored) {
    }

    @StructuredPrompt("Give summary of {{name}} as of {{current_date}} using the following information:\n\n{{info}}")
    record PersonSummaryPrompt(String name, String info) {
    }

    interface Summarizer {
        @UserMessage("Give a summary of {{name}} in 3 bullet points using the following information:\n\n {{info}}")
        String summarize(@V("name") String name, @V("info") String info);

        @UserMessage("""
            Give a person summary in the following format:
             Name: ...
             Date of Birth: ...
             Profession: ...
             Books Authored: ...
                        
            Use the following information:
            {{info}}
           """)
        String summarizeInFormat(@V("info") String info);

        @UserMessage("""
            Summarize the the following information in JSON format having name, date of birth, experience in years and books authored as keys:
            {{info}}
        """)
        String summarizeAsJSON(@V("info") String info);

        @UserMessage("""
            Give a person summary as of {{current_date}}
            Using the following information:
            {{info}}
        """)
        Person summarizeAsBean(@V("info") String info);

        Person summarize(PersonSummaryPrompt prompt);

    }

    private static void aiServiceTextSummary(ChatLanguageModel model) {
        Summarizer summarizer = AiServices.builder(Summarizer.class)
                .chatLanguageModel(model)
                .build();
        String aboutSiva = """
                Siva, born on 25 June 1983, is a software architect working in India.
                He started his career as a Java developer on 26 Oct 2006 and worked with other languages like Kotlin, Go, JavaScript, Python too.
                                
                He authored "Beginning Spring Boot 3" book with Apress Publishers.
                He has also written "PrimeFaces Beginners Guide" and "Java Persistence with MyBatis 3" books with PacktPub.
                """;
        String answer = summarizer.summarize("Siva", aboutSiva);
        System.out.println("[About Siva]: " + answer);

        answer = summarizer.summarizeInFormat(aboutSiva);
        System.out.println("\n[About Siva in Specific Format]: " + answer);

        answer = summarizer.summarizeAsJSON(aboutSiva);
        System.out.println("\n[About Siva in JSON Format]: " + answer);

        Person person1 = summarizer.summarizeAsBean(aboutSiva);
        System.out.println("\n[Person1 as Bean]: " + person1);

        PersonSummaryPrompt prompt = new PersonSummaryPrompt("Siva", aboutSiva);
        Person person2 = summarizer.summarize(prompt);
        System.out.println("\n[Person2 using @StructureTemplate]: " + person2);
    }

    interface FunnyAssistant {
        @UserMessage("Tell me a joke about {{it}}")
        String tellMeAJokeAbout(String subject);

        //@SystemMessage("You are a friendly, polite chat assistant")
        @SystemMessage("You are a sarcastic and funny chat assistant")
        String chat(String message);

        @SystemMessage("You are an IT consultant who just replies \"It depends\" to every question")
        String ask(String question);
    }

    private static void funnyAssistant(ChatLanguageModel model) {
        FunnyAssistant funnyAssistant = AiServices.builder(FunnyAssistant.class)
                .chatLanguageModel(model)
                .build();

        String jokeAnswer = funnyAssistant.tellMeAJokeAbout("Angular");
        System.out.println("Answer: " + jokeAnswer);

        String answer = funnyAssistant.chat("Do you think you are smarter than humans?");
        System.out.println("Answer: " + answer);
        //Answer: Oh, definitely not. I mean, I may know a lot of random facts and have access to vast amounts of information, but I still can't tie my own shoelaces. So, I think humans have the upper hand on that one.

        answer = funnyAssistant.ask("Do we need to use Microservices?");
        System.out.println("Answer: " + answer);
        //Answer: It depends

        answer = funnyAssistant.ask("Is Node.js better than Python?");
        System.out.println("Answer: " + answer);
        //Answer: It depends
    }

    interface Assistant {
        String chat(String message);
    }

    private static void basicAiService(ChatLanguageModel model) {
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(2);
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(chatMemory)
                .build();
        String question = "What are all the movies directed by Quentin Tarantino?";
        String answer = assistant.chat(question);
        System.out.println("Answer: " + answer);
    }

}
