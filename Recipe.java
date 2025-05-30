import java.util.Objects;

public class Recipe 
{
    private String name;
    private String ingredients; 
    private String instructions;

    public Recipe(String name, String ingredients, String instructions) 
    {
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public String getName() 
    {
        return name;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getIngredients() 
    {
        return ingredients;
    }

    public void setIngredients(String ingredients) 
    {
        this.ingredients = ingredients;
    }

    public String getInstructions() 
    {
        return instructions;
    }

    public void setInstructions(String instructions) 
    {
        this.instructions = instructions;
    }

    @Override
    public String toString() 
    {
        return name;
    }

    @Override
    public boolean equals(Object o) 
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return Objects.equals(name.toLowerCase(), recipe.name.toLowerCase()); 
    }

    @Override
    public int hashCode() 
    {
        return Objects.hash(name.toLowerCase());
    }

    public String toFileString() 
    {
        String safeInstructions = instructions.replace("\n", "<NL>");
        String safeIngredients = ingredients.replace("\n", "<NL>"); 
        return name + "%%%" + safeIngredients + "%%%" + safeInstructions;
    }

    public static Recipe fromFileString(String fileString) 
    {
        String[] parts = fileString.split("%%%", 3);
        if (parts.length == 3) 
        {
            String name = parts[0];
            String ingredients = parts[1].replace("<NL>", "\n");
            String instructions = parts[2].replace("<NL>", "\n");
            return new Recipe(name, ingredients, instructions);
        }
        return null; 
    }
}