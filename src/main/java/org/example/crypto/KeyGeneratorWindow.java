package org.example.crypto;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Paths;

/**
 * KeyGeneratorWindow is a GUI panel for generating RSA key pairs.
 * It allows users to choose storage locations for keys and input a PIN for encryption.
 */
public class KeyGeneratorWindow extends JPanel {

    // Key generation logic
    private final KeyGeneratorBody keyGeneratorBody = new KeyGeneratorBody(4096);

    // UI components
    private final JPasswordField passwordField;
    private final CardLayout parentLayout;
    private final JPanel cardPanel;
    private final JLabel privateInfo;
    private final JLabel publicInfo;
    private final JLabel resultLabel;
    private final String keyFileName;

    // Paths for storing generated keys
    private String privatePath;
    private String publicPath;

    /**
     * Constructor to initialize the key generator window.
     * @param cardLayout Parent CardLayout for navigation.
     * @param cardPanel Parent JPanel containing this window.
     * @param keyFileName Name of the private key file.
     */
    public KeyGeneratorWindow(CardLayout cardLayout, JPanel cardPanel, String keyFileName) {
        this.setSize(600, 600);
        this.parentLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.keyFileName = keyFileName;

        // UI Components
        JLabel title = new JLabel("RSA Key Generator");
        JLabel info = new JLabel("Choose the placement for generated keys AND enter an 8-DIGIT PIN to secure it.");

        privateInfo = new JLabel("Desktop");
        publicInfo = new JLabel("This Project Root Folder");
        passwordField = new JPasswordField();

        JButton privatePlace = new JButton("Change Private Location");
        JButton publicPlace = new JButton("Change Public Location");
        JButton generateButton = new JButton("Generate key pair");

        Icon questionIcon = UIManager.getIcon("OptionPane.informationIcon");
        JLabel helpLabel = new JLabel(questionIcon);

        this.resultLabel = new JLabel();

        // Configure UI Components
        title.setPreferredSize(new Dimension(600, 60));
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        info.setPreferredSize(new Dimension(600, 60));
        info.setHorizontalAlignment(SwingConstants.CENTER);

        resultLabel.setPreferredSize(new Dimension(580, 75));
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        privateInfo.setMinimumSize(new Dimension(250, 75));
        privateInfo.setHorizontalAlignment(SwingConstants.CENTER);
        JScrollPane privatePane = new JScrollPane(privateInfo);
        privatePane.setPreferredSize(new Dimension(250, 75));

        publicInfo.setMinimumSize(new Dimension(250, 75));
        publicInfo.setHorizontalAlignment(SwingConstants.CENTER);
        JScrollPane publicPane = new JScrollPane(publicInfo);
        publicPane.setPreferredSize(new Dimension(250, 75));

        privatePlace.setPreferredSize(new Dimension(250, 25));
        publicPlace.setPreferredSize(new Dimension(250, 25));

        publicPlace.setToolTipText("This function is disabled in the final version due to testing complications.");

        passwordField.setPreferredSize(new Dimension(100, 20));

        generateButton.setPreferredSize(new Dimension(200, 20));
        generateButton.addActionListener(e -> checkPasswordAndGenerateRSA());

        privatePlace.addActionListener(e -> choosePath("Private"));
        // publicPlace.addActionListener(e -> choosePath("Public")); // Disabled

        // Default paths
        privatePath = Paths.get(System.getProperty("user.home"), "Desktop").toString();
        publicPath = "";

        helpLabel.setPreferredSize(new Dimension(580, 20));
        helpLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        helpLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        helpLabel.setToolTipText("To generate a 4096-bit RSA key pair, choose the locations for PRIVATE (left) and " +
                "PUBLIC (right) keys, enter an 8-DIGIT PIN, and click 'Generate key pair'.");

        // Back Button
        JButton backButton = new JButton("Back");
        JPanel tmpPan = new JPanel();
        tmpPan.add(backButton);
        backButton.addActionListener(e -> returnToMainPage());

        // Add components to panel
        this.add(title);
        this.add(info);
        this.add(privatePane);
        this.add(publicPane);
        this.add(privatePlace);
        this.add(publicPlace);
        this.add(passwordField);
        this.add(generateButton);
        this.add(tmpPan);
        this.add(resultLabel);
        this.add(helpLabel);
    }

    /**
     * Opens a file chooser dialog to select a directory for storing keys.
     * @param type "Private" for private key location, "Public" for public key location.
     */
    private void choosePath(String type) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fileChooser.showOpenDialog(this);
        File chosenFile = fileChooser.getSelectedFile();
        if (chosenFile != null) {
            switch (type) {
                case "Private":
                    privatePath = chosenFile.getAbsolutePath();
                    privateInfo.setText(privatePath);
                    break;
                case "Public":
                    publicPath = chosenFile.getAbsolutePath();
                    publicInfo.setText(publicPath);
                    break;
            }
        } else {
            resultLabel.setForeground(Color.RED);
            resultLabel.setText("Please, choose a correct path!");
        }
    }

    /**
     * Validates the password and generates an RSA key pair.
     */
    private void checkPasswordAndGenerateRSA() {
        String password = String.valueOf(passwordField.getPassword());
        System.out.println(password);

        if (password.length() == 8) {
            try {
                keyGeneratorBody.generatePair(password, privatePath, publicPath, keyFileName);
                passwordField.setText("");
                resultLabel.setForeground(Color.GREEN);
                resultLabel.setText("The RSA key pair was successfully generated!");
            } catch (RuntimeException e) {
                resultLabel.setForeground(Color.RED);
                resultLabel.setText("<html>" + e.getLocalizedMessage() + "</html>");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        } else {
            resultLabel.setForeground(Color.RED);
            resultLabel.setText("You must enter an 8-DIGIT PIN!");
        }
    }

    /**
     * Returns to the main menu by switching the card layout.
     */
    private void returnToMainPage() {
        parentLayout.show(cardPanel, "main");
    }
}
