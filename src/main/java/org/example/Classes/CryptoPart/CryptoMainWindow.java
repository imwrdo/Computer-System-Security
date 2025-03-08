package org.example.Classes.CryptoPart;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CryptoMainWindow extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final KeyGeneratorWindow keyWindow = new KeyGeneratorWindow(cardLayout, cardPanel);
    private final PDFCryptoWindow pdfWindow = new PDFCryptoWindow(cardLayout, cardPanel);

    public CryptoMainWindow() {
        this.setSize(600, 600);
        this.setTitle("RSA Key Generator and PDF Signer");

        JPanel panel = new JPanel();
        JLabel title = new JLabel("PDF Encryption and Key Generation tool");
        JLabel infoText = new JLabel();
        JButton keyButton = new JButton("Create RSA key pair");
        JButton pdfButton = new JButton("Sign a document");

        //panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        title.setPreferredSize(new Dimension(600, 120));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(new EmptyBorder(20, 0, 10, 0));

        //infoText.setEditable(false);
        infoText.setText("What would you like to do?");
        infoText.setPreferredSize(new Dimension(600, 130));
        infoText.setBorder(new EmptyBorder(0, 0, 100, 0));
        infoText.setHorizontalAlignment(SwingConstants.CENTER);

        keyButton.setPreferredSize(new Dimension(200, 50));
        keyButton.setFont(new Font("Arial", Font.BOLD, 15));
        keyButton.setMargin(new Insets(0, 0, 0, 0));


        pdfButton.setPreferredSize(new Dimension(200, 50));
        pdfButton.setFont(new Font("Arial", Font.BOLD, 15));

        panel.add(title);
        panel.add(infoText);
        panel.add(keyButton);
        panel.add(pdfButton);

        //JButton revertButton = new JButton("Back");
        //keyWindow.add(revertButton);
        //pdfWindow.add(revertButton);

        cardPanel.add("main", panel);
        cardPanel.add("RSA", keyWindow);
        cardPanel.add("PDF", pdfWindow);


        //revertButton.addActionListener(e -> cardLayout.show(cardPanel, "main"));
        keyButton.addActionListener(e -> cardLayout.show(cardPanel, "RSA"));
        pdfButton.addActionListener(e -> cardLayout.show(cardPanel, "PDF"));

        this.add(cardPanel);
    }
}

