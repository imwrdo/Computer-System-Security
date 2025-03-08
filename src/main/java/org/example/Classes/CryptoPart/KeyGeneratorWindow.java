package org.example.Classes.CryptoPart;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Paths;

public class KeyGeneratorWindow extends JPanel {

    final KeyGeneratorBody keyGenerator = new KeyGeneratorBody(4096);

    private JPasswordField passwordField;
    private final CardLayout parentLayout;
    private final JPanel cardPanel;
    private String privatePath;
    private String publicPath;
    private JLabel privateInfo;
    private JLabel publicInfo;

    public KeyGeneratorWindow(CardLayout cardLayout, JPanel cardPanel) {
        this.setSize(600, 600);
        this.parentLayout = cardLayout;
        this.cardPanel = cardPanel;
        //this.setTitle("RSA Key Generator and PDF Signer");
        JLabel title = new JLabel("RSA Key Generator");
        JLabel info = new JLabel("Choose the placement for generated keys\nAnd enter the 8-digit pin to cipher it");
        privateInfo = new JLabel("Desktop");
        publicInfo = new JLabel("Desktop");
        passwordField = new JPasswordField();
        JButton privatePlace = new JButton("Change Private Location");
        JButton publicPlace = new JButton("Change Public Location");
        JButton generateButton = new JButton("Generate key pair");

        title.setPreferredSize(new Dimension(600, 60));
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        info.setPreferredSize(new Dimension(600, 60));
        info.setHorizontalAlignment(SwingConstants.CENTER);

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


        passwordField.setPreferredSize(new Dimension(100, 20));

        generateButton.setPreferredSize(new Dimension(200, 20));
        generateButton.addActionListener(e -> CheckPasswordAndRSA());
        privatePlace.addActionListener(e -> ChoosePath("Private"));
        publicPlace.addActionListener(e -> ChoosePath("Public"));

        privatePath = publicPath = Paths.get(System.getProperty("user.home"), "Desktop").toString();

        JButton backButton = new JButton("Back");
        JPanel tmpPan = new JPanel();
        tmpPan.add(backButton);

        backButton.addActionListener(e -> ReturnToMainPage());

        //fileChooser.getSelectedFile();
        this.add(title);
        this.add(info);
        this.add(privatePane);
        this.add(publicPane);
        this.add(privatePlace);
        this.add(publicPlace);
        this.add(passwordField);
        this.add(generateButton);
        this.add(tmpPan);
        //this.add(signButton);
    }

    private void ChoosePath(String type) {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fileChooser.showOpenDialog(this);
        File chosenFile = fileChooser.getSelectedFile();
        if(chosenFile != null)
            switch(type) {
                case "Private":
                    privatePath = chosenFile.getAbsolutePath();
                    privateInfo.setText(privatePath);
                    break;
                case "Public":
                    publicPath = chosenFile.getAbsolutePath();
                    publicInfo.setText(publicPath);
                    break;
            }
    }

    private void CheckPasswordAndRSA() {
        String password = String.valueOf(passwordField.getPassword());
        System.out.println(password);
        if(password.length() == 8){
            try {
                keyGenerator.GeneratePair(password, privatePath, publicPath);
                passwordField.setText("");
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }

    private void ReturnToMainPage() {
        parentLayout.show(cardPanel, "main");
    }

}
