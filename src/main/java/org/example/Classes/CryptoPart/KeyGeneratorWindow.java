package org.example.Classes.CryptoPart;

import javax.swing.*;
import java.awt.*;
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

    private JLabel resultLabel;

    private String keyFileName;
    public KeyGeneratorWindow(CardLayout cardLayout, JPanel cardPanel, String keyFileName) {
        this.setSize(600, 600);
        this.parentLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.keyFileName = keyFileName;
        //this.setTitle("RSA Key Generator and PDF Signer");
        JLabel title = new JLabel("RSA Key Generator");
        JLabel info = new JLabel("Choose the placement for generated keys AND enter the 8-DIGIT PIN to cipher it");
        privateInfo = new JLabel("Desktop");
        publicInfo = new JLabel("This Project Root Folder");
        passwordField = new JPasswordField();
        JButton privatePlace = new JButton("Change Private Location");
        JButton publicPlace = new JButton("Change Public Location");
        JButton generateButton = new JButton("Generate key pair");

        Icon questionIcon = UIManager.getIcon("OptionPane.informationIcon");
        Image img = ((ImageIcon) questionIcon).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(img);
        JLabel helpLabel = new JLabel(scaledIcon);

        this.resultLabel = new JLabel();

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
        publicPlace.setToolTipText("This function didn't appear in the final version" +
                " for this only brings up additional and unnecessary complications for testing");


        passwordField.setPreferredSize(new Dimension(100, 20));

        generateButton.setPreferredSize(new Dimension(200, 20));
        generateButton.addActionListener(e -> CheckPasswordAndRSA());
        privatePlace.addActionListener(e -> ChoosePath("Private"));
        //publicPlace.addActionListener(e -> ChoosePath("Public"));

        privatePath = Paths.get(System.getProperty("user.home"), "Desktop").toString();
        publicPath = "";

        helpLabel.setPreferredSize(new Dimension(580, 20));
        helpLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        helpLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        helpLabel.setToolTipText("To generate a pair of 4096-bit RSA Keys you need to choose the" +
                "location for both PRIVATE (left) and PUBLIC (right) keys with 'Change ... Location" +
                " buttons, enter your 8-DIGIT PIN to the field and clicking 'Generate key pair'");

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
        this.add(resultLabel);
        this.add(helpLabel);
    }

    private void ChoosePath(String type) {
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fileChooser.showOpenDialog(this);
        File chosenFile = fileChooser.getSelectedFile();
        if(chosenFile != null) {
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
        }
        else {
            resultLabel.setForeground(Color.RED);
            resultLabel.setText("Please, choose a correct path!");
        }
    }

    private void CheckPasswordAndRSA() {
        String password = String.valueOf(passwordField.getPassword());
        System.out.println(password);
        if(password.length() == 8){
            try {
                keyGenerator.GeneratePair(password, privatePath, publicPath, keyFileName);
                passwordField.setText("");
                resultLabel.setForeground(Color.GREEN);
                resultLabel.setText("The RSA key pair was successfully generated!");
            }
            catch (RuntimeException e) {
                resultLabel.setForeground(Color.RED);
                resultLabel.setText("<html>" + e.getLocalizedMessage() + "</html>");
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
        else {

            resultLabel.setForeground(Color.RED);
            resultLabel.setText("You must have an 8-DIGIT PIN!");
        }
    }

    private void ReturnToMainPage() {
        parentLayout.show(cardPanel, "main");
    }

}
