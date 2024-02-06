package palm.workshop;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.vertexai.VertexAiLanguageModel;

public class TextPrompts {

    // *** Basic Prompt ***
    // public static void main(String[] args) {
    //     VertexAiLanguageModel model = getModel(500);
    //     // Response<String> response = model.generate("What are large language models?");
    //     // Response<String> response = model.generate("""
    //     //         Extract the name and age of the person described below.
    //     //         Return a JSON document with a "name" and an "age" property, \
    //     //         following this structure: {"name": "John Doe", "age": 34}
    //     //         Return only JSON, without any markdown markup surrounding it.
    //     //         Here is the document describing the person:
    //     //         ---
    //     //         Anna is a 23 year old artist based in Brooklyn, New York. She was born and
    //     //         raised in the suburbs of Chicago, where she developed a love for art at a
    //     //         young age. She attended the School of the Art Institute of Chicago, where
    //     //         she studied painting and drawing. After graduating, she moved to New York
    //     //         City to pursue her art career. Anna's work is inspired by her personal
    //     //         experiences and observations of the world around her. She often uses bright
    //     //         colors and bold lines to create vibrant and energetic paintings. Her work
    //     //         has been exhibited in galleries and museums in New York City and Chicago.
    //     //         ---
    //     //         JSON:
    //     //         """);

    //     Response<String> responseTranslate = model.generate("Translate the following sentence in French: I am from New York");
    //     System.out.println("Response FinishReason: " + responseTranslate.finishReason() + ", content:" + responseTranslate.content());

    //     Response<String> responseWrite = model.generate("Write a poem about flowers");
    //     System.out.println("Response FinishReason: " + responseWrite.finishReason() + ", content:" + responseWrite.content());
    // }

    public static void main(String[] args) {
        VertexAiLanguageModel model = getModel(300);

        // *** Prompt Template version ***
        // PromptTemplate promptTemplate = PromptTemplate.from("""
        //     Create a recipe for a {{dish}} with the following ingredients: \
        //     {{ingredients}}, and give it a name.
        //     """
        // );

        // Map<String, Object> variables = new HashMap<>();
        // variables.put("dish", "dessert");
        // variables.put("ingredients", "strawberries, chocolate, whipped cream");
        // Prompt prompt = promptTemplate.apply(variables);

        // *** Structured Prompt version *** 
        // RecipeCreationPrompt createRecipePrompt = new RecipeCreationPrompt();
        // createRecipePrompt.dish = "salad";
        // createRecipePrompt.ingredients = List.of("cucumber", "tomato", "feta", "onion", "olives");
        // Prompt prompt = StructuredPromptProcessor.toPrompt(createRecipePrompt);

        PromptTemplate promptTemplate = PromptTemplate.from("""
            Analyze the sentiment of the text below. Respond only with one word to describe the sentiment.

            INPUT: This is fantastic news!
            OUTPUT: POSITIVE

            INPUT: Pi is roughly equal to 3.14
            OUTPUT: NEUTRAL

            INPUT: I really disliked the pizza. Who would use pineapples as a pizza topping?
            OUTPUT: NEGATIVE

            INPUT: {{text}}
            OUTPUT: 
            """);

        Prompt prompt = promptTemplate.apply(Map.of("text", "I love strawberries!"));
        Prompt prompt2 = promptTemplate.apply(Map.of("text", "Whatever"));
        Prompt prompt3 = promptTemplate.apply(Map.of("text", "42!"));

        Response<String> response = model.generate(prompt);
        Response<String> response2 = model.generate(prompt2);
        Response<String> response3 = model.generate(prompt3);

        System.out.println(response.content());
        System.out.println(response2.content());
        System.out.println(response3.content());
    }

    private static VertexAiLanguageModel getModel(int maxOutputTokens) {
        VertexAiLanguageModel model = VertexAiLanguageModel.builder()
            .endpoint("us-central1-aiplatform.googleapis.com:443")
            .project("codelab-langchain4j-palm")
            .location("us-central1")
            .publisher("google")
            .modelName("text-bison@001")
            .maxOutputTokens(maxOutputTokens)
            .build();
        return model;
    }

}
