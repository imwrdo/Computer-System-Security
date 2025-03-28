package org.example.crypto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * CryptoMainWindow serves as the main graphical user interface (GUI)
 * for the RSA Key Generator and PDF Signing application.
 * It provides navigation between different features using a CardLayout.
 */
public class CryptoMainWindow extends JFrame {

    // Layout manager to switch between different panels
    private final CardLayout cardLayout = new CardLayout();

    // Main container panel to hold different screens
    private final JPanel cardPanel = new JPanel(cardLayout);

    // Filename for storing the private key
    private final String privateFileName = "privateKey";

    // Windows for key generation and PDF signing functionalities
    private final KeyGeneratorWindow keyWindow = new KeyGeneratorWindow(cardLayout, cardPanel, privateFileName);
    private final PDFCryptoWindow pdfWindow = new PDFCryptoWindow(cardLayout, cardPanel, privateFileName);

    /**
     * Constructor to initialize the CryptoMainWindow.
     * Sets up the UI components and event listeners.
     */
    public CryptoMainWindow() {

        // Configure tooltip behavior
        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        ToolTipManager.sharedInstance().setReshowDelay(0);

        // Set window properties
        this.setSize(600, 600);
        this.setTitle("RSA Key Generator and PDF Signer");

        // Create the main panel
        JPanel panel = new JPanel();

        // Title label
        JLabel title = new JLabel("PDF Encryption and Key Generation Tool");

        // Information label
        JLabel infoText = new JLabel();

        // Buttons for navigation
        JButton keyButton = new JButton("Create RSA Key Pair");
        JButton pdfButton = new JButton("Sign a Document");

        // Help icon with tooltip
        Icon questionIcon = UIManager.getIcon("OptionPane.informationIcon");
        JLabel helpLabel = new JLabel(questionIcon);

        // Configure title label
        title.setPreferredSize(new Dimension(600, 120));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));

        // Configure information text label
        infoText.setText("What would you like to do?");
        infoText.setPreferredSize(new Dimension(600, 130));
        infoText.setBorder(new EmptyBorder(0, 0, 100, 0));
        infoText.setHorizontalAlignment(SwingConstants.CENTER);

        // Configure key generation button
        keyButton.setPreferredSize(new Dimension(200, 50));
        keyButton.setFont(new Font("Arial", Font.BOLD, 15));
        keyButton.setMargin(new Insets(0, 0, 0, 0));

        // Configure PDF signing button
        pdfButton.setPreferredSize(new Dimension(200, 50));
        pdfButton.setFont(new Font("Arial", Font.BOLD, 15));

        // Configure help label with tooltip
        helpLabel.setPreferredSize(new Dimension(580, 60));
        helpLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        helpLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        helpLabel.setToolTipText("To sign your PDF document, you need to generate an " +
                "RSA Key Pair (left button) first and then proceed with signing (right button). " +
                "Follow the instructions provided on each page.");

        // Add components to the main panel
        panel.add(title);
        panel.add(infoText);
        panel.add(keyButton);
        panel.add(pdfButton);
        panel.add(helpLabel);

        // Add panels to the CardLayout container
        cardPanel.add("main", panel);
        cardPanel.add("RSA", keyWindow);
        cardPanel.add("PDF", pdfWindow);

        // Add event listeners to buttons for navigation
        keyButton.addActionListener(e -> cardLayout.show(cardPanel, "RSA"));
        pdfButton.addActionListener(e -> cardLayout.show(cardPanel, "PDF"));

        // Add the card panel to the JFrame
        this.add(cardPanel);
    }
}