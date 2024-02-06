package palm.workshop;

import java.util.List;

import dev.langchain4j.model.input.structured.StructuredPrompt;

@StructuredPrompt("Create a recipe of a {{dish}} that can be prepared using only {{ingredients}}")
public class RecipeCreationPrompt {
    String dish;
    List<String> ingredients;
}
