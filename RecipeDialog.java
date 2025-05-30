import javax.swing.*;
import java.awt.*;

public class RecipeDialog extends JDialog 
{
    private final JTextField nameField;
    private final JTextArea ingredientsArea;
    private final JTextArea instructionsArea;
    private Recipe recipe; 

    public RecipeDialog(Frame owner, String title, Recipe existingRecipe) 
    {
        super(owner, title, true); 
        this.recipe = existingRecipe; 

        setLayout(new BorderLayout());
        setSize(450, 400);
        setLocationRelativeTo(owner);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Име на рецепта:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        gbc.weightx = 0; 

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHEAST;
        formPanel.add(new JLabel("Съставки:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.4;
        ingredientsArea = new JTextArea(5, 20);
        ingredientsArea.setLineWrap(true);
        ingredientsArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(ingredientsArea), gbc);
        gbc.weighty = 0; 

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHEAST;
        formPanel.add(new JLabel("Инструкции:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.6;
        instructionsArea = new JTextArea(10, 20);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(instructionsArea), gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Отказ");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        if (existingRecipe != null) 
        {
            nameField.setText(existingRecipe.getName());
            ingredientsArea.setText(existingRecipe.getIngredients());
            instructionsArea.setText(existingRecipe.getInstructions());
        }

        okButton.addActionListener(e -> {
            if (validateInput()) 
            {
                recipe = new Recipe(
                        nameField.getText().trim(),
                        ingredientsArea.getText().trim(),
                        instructionsArea.getText().trim()
                );
                dispose(); 
            }
        });

        cancelButton.addActionListener(e -> {
            recipe = null; 
            dispose();
        });
    }

    private boolean validateInput() 
    {
        if (nameField.getText().trim().isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "Името на рецептата е задължително!", "Грешка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (ingredientsArea.getText().trim().isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "Съставките са задължителни!", "Грешка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (instructionsArea.getText().trim().isEmpty()) 
        {
            JOptionPane.showMessageDialog(this, "Инструкциите са задължителни!", "Грешка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public Recipe getRecipe() 
    {
        return recipe;
    }
}