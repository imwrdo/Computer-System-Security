package org.example.Classes.CryptoPart;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CryptoMainWindow extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final String privateFileName = "privateKey";
    private final KeyGeneratorWindow keyWindow = new KeyGeneratorWindow(cardLayout, cardPanel, privateFileName);
    private final PDFCryptoWindow pdfWindow = new PDFCryptoWindow(cardLayout, cardPanel, privateFileName);

    public CryptoMainWindow() {

        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        ToolTipManager.sharedInstance().setReshowDelay(0);

        this.setSize(600, 600);
        this.setTitle("RSA Key Generator and PDF Signer");

        JPanel panel = new JPanel();
        JLabel title = new JLabel("PDF Encryption and Key Generation tool");
        JLabel infoText = new JLabel();
        JButton keyButton = new JButton("Create RSA key pair");
        JButton pdfButton = new JButton("Sign a document");

        Icon questionIcon = UIManager.getIcon("OptionPane.informationIcon");
        Image img = ((ImageIcon) questionIcon).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(img);
        JLabel helpLabel = new JLabel(scaledIcon);

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

        helpLabel.setPreferredSize(new Dimension(580, 60));
        helpLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        helpLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        helpLabel.setToolTipText("To sign your PDF document you need to generate an"+
                " RSA Key Pair (left button) first and then go to the signing process (the right one)" +
                ". All you have to do is follow the instructions left on these relevant pages ;)");

        panel.add(title);
        panel.add(infoText);
        panel.add(keyButton);
        panel.add(pdfButton);
        panel.add(helpLabel);

        cardPanel.add("main", panel);
        cardPanel.add("RSA", keyWindow);
        cardPanel.add("PDF", pdfWindow);


        keyButton.addActionListener(e -> cardLayout.show(cardPanel, "RSA"));
        pdfButton.addActionListener(e -> cardLayout.show(cardPanel, "PDF"));

        this.add(cardPanel);
    }
}

