import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class RecipeManagerApp extends JFrame 
{

    private final ArrayList<Recipe> recipes = new ArrayList<>();
    private final DefaultListModel<Recipe> recipeListModel = new DefaultListModel<>();
    private JList<Recipe> recipeJList;
    private JTextArea ingredientsArea;
    private JTextArea instructionsArea;
    private JTextField searchField;

    private final String DATA_FILE = "recipes.txt";

    public RecipeManagerApp() 
    {
        setTitle("Система за управление на рецепти");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadRecipesFromFile(); 
        sortAndRefreshList();
    }

    private void initComponents() 
    {
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Рецепти"));
        recipeJList = new JList<>(recipeListModel);
        recipeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listPanel.add(new JScrollPane(recipeJList), BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Детайли"));

        ingredientsArea = new JTextArea(10, 30);
        ingredientsArea.setEditable(false);
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);
        JScrollPane ingredientsScrollPane = new JScrollPane(ingredientsArea);
        ingredientsScrollPane.setBorder(BorderFactory.createTitledBorder("Съставки"));

        instructionsArea = new JTextArea(15, 30);
        instructionsArea.setEditable(false);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        JScrollPane instructionsScrollPane = new JScrollPane(instructionsArea);
        instructionsScrollPane.setBorder(BorderFactory.createTitledBorder("Инструкции"));

        detailsPanel.add(ingredientsScrollPane, BorderLayout.NORTH);
        detailsPanel.add(instructionsScrollPane, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Добави");
        JButton editButton = new JButton("Редактирай");
        JButton deleteButton = new JButton("Изтрий");
        JButton saveButton = new JButton("Запази");
        JButton loadButton = new JButton("Зареди"); 

        searchField = new JTextField(15);
        JButton searchButton = new JButton("Търси");
        JButton clearSearchButton = new JButton("Изчисти търсене");


        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(saveButton);
        controlPanel.add(loadButton);
        controlPanel.add(new JLabel("Търсене:"));
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        controlPanel.add(clearSearchButton);

        setLayout(new BorderLayout());
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, detailsPanel);
        splitPane.setDividerLocation(250);
        add(splitPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        recipeJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) 
            {
                Recipe selectedRecipe = recipeJList.getSelectedValue();
                if (selectedRecipe != null) 
                {
                    ingredientsArea.setText(selectedRecipe.getIngredients());
                    instructionsArea.setText(selectedRecipe.getInstructions());
                } 
                else 
                {
                    ingredientsArea.setText("");
                    instructionsArea.setText("");
                }
            }
        });

        addButton.addActionListener(e -> addRecipeDialog());
        editButton.addActionListener(e -> editRecipeDialog());
        deleteButton.addActionListener(e -> deleteSelectedRecipe());
        saveButton.addActionListener(e -> saveRecipesToFile());
        loadButton.addActionListener(e -> {
            loadRecipesFromFile();
            sortAndRefreshList();
        });
        searchButton.addActionListener(e -> searchRecipes());
        clearSearchButton.addActionListener(e -> {
            searchField.setText("");
            refreshRecipeList(recipes); 
        });
    }

    private void addRecipeDialog() 
    {
        RecipeDialog dialog = new RecipeDialog(this, "Добавяне на рецепта", null);
        dialog.setVisible(true);
        Recipe newRecipe = dialog.getRecipe();

        if (newRecipe != null) 
        {
            if (recipes.contains(newRecipe)) 
            { 
                JOptionPane.showMessageDialog(this, "Рецепта с това име вече съществува!", "Грешка", JOptionPane.ERROR_MESSAGE);
            } 
            else 
            {
                recipes.add(newRecipe);
                sortAndRefreshList();
                JOptionPane.showMessageDialog(this, "Рецептата е добавена успешно.", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void editRecipeDialog() 
    {
        Recipe selectedRecipe = recipeJList.getSelectedValue();
        if (selectedRecipe == null) 
        {
            JOptionPane.showMessageDialog(this, "Моля, изберете рецепта за редакция.", "Информация", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        RecipeDialog dialog = new RecipeDialog(this, "Редакция на рецепта", selectedRecipe);
        dialog.setVisible(true);
        Recipe editedRecipe = dialog.getRecipe(); 

        if (editedRecipe != null) 
        {
            boolean nameChanged = !selectedRecipe.getName().equalsIgnoreCase(editedRecipe.getName());
            if (nameChanged) 
            {
                for(Recipe r : recipes) 
                {
                    if (r != selectedRecipe && r.getName().equalsIgnoreCase(editedRecipe.getName())) 
                    {
                        JOptionPane.showMessageDialog(this, "Рецепта с новото име вече съществува!", "Грешка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            selectedRecipe.setName(editedRecipe.getName());
            selectedRecipe.setIngredients(editedRecipe.getIngredients());
            selectedRecipe.setInstructions(editedRecipe.getInstructions());

            sortAndRefreshList();
            recipeJList.setSelectedValue(selectedRecipe, true);
            JOptionPane.showMessageDialog(this, "Рецептата е редактирана успешно.", "Успех", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteSelectedRecipe() 
    {
        Recipe selectedRecipe = recipeJList.getSelectedValue();
        if (selectedRecipe == null) 
        {
            JOptionPane.showMessageDialog(this, "Моля, изберете рецепта за изтриване.", "Информация", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Сигурни ли сте, че искате да изтриете рецептата '" + selectedRecipe.getName() + "'?",
                "Потвърждение за изтриване", JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) 
        {
            recipes.remove(selectedRecipe);
            sortAndRefreshList(); 
            ingredientsArea.setText("");
            instructionsArea.setText("");
            JOptionPane.showMessageDialog(this, "Рецептата е изтрита.", "Успех", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void searchRecipes() 
    {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) 
        {
            refreshRecipeList(recipes); 
            return;
        }

        ArrayList<Recipe> searchResults = new ArrayList<>();
        for (Recipe recipe : recipes) 
        {
            if (recipe.getName().toLowerCase().contains(searchTerm) ||
                    recipe.getIngredients().toLowerCase().contains(searchTerm)) 
            {
                searchResults.add(recipe);
            }
        }
        quickSort(searchResults, 0, searchResults.size() -1); 
        refreshRecipeList(searchResults);
    }

    private void quickSort(ArrayList<Recipe> list, int low, int high) 
    {
        if (list == null || list.isEmpty()) return;
        if (low < high) 
        {
            int pi = partition(list, low, high);
            quickSort(list, low, pi - 1);
            quickSort(list, pi + 1, high);
        }
    }

    private int partition(ArrayList<Recipe> list, int low, int high) 
    {
        Recipe pivot = list.get(high);
        int i = (low - 1); 
        for (int j = low; j < high; j++) 
        {
            if (list.get(j).getName().compareToIgnoreCase(pivot.getName()) <= 0) 
            {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    private void sortAndRefreshList() 
    {
        if (!recipes.isEmpty()) 
        {
            quickSort(recipes, 0, recipes.size() - 1);
        }
        refreshRecipeList(recipes);
    }

    private void refreshRecipeList(ArrayList<Recipe> sourceList) 
    {
        recipeListModel.clear();
        for (Recipe recipe : sourceList) 
        {
            recipeListModel.addElement(recipe);
        }
    }


    private void saveRecipesToFile() 
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) 
        {
            for (Recipe recipe : recipes) 
            {
                writer.println(recipe.toFileString());
            }
            JOptionPane.showMessageDialog(this, "Рецептите са запазени успешно.", "Запазване", JOptionPane.INFORMATION_MESSAGE);
        } 
        catch (IOException e) 
        {
            JOptionPane.showMessageDialog(this, "Грешка при запазване на рецептите: " + e.getMessage(), "Грешка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRecipesFromFile() 
    {
        recipes.clear();
        File file = new File(DATA_FILE);
        if (!file.exists()) 
        {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) 
        {
            String line;
            while ((line = reader.readLine()) != null) 
            {
                Recipe recipe = Recipe.fromFileString(line);
                if (recipe != null) {
                    recipes.add(recipe);
                }
            }
       } 
        catch (IOException e) 
        {
            JOptionPane.showMessageDialog(this, "Грешка при зареждане на рецептите: " + e.getMessage(), "Грешка", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> {
            RecipeManagerApp app = new RecipeManagerApp();
            app.setVisible(true);
        });
    }
}
