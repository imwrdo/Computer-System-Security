package org.example.Classes.CryptoPart;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.Objects;

public class PDFCryptoWindow extends JPanel {

    final PDFCryptoBody keyGenerator = new PDFCryptoBody(4096);

    private JPasswordField passwordField;
    private final CardLayout parentLayout;
    private final JPanel cardPanel;
    private String origPath;
    private String signedPath;
    private final JLabel origInfo = new JLabel("None");
    private final JLabel signedInfo = new JLabel("None");


    public PDFCryptoWindow(CardLayout cardLayout, JPanel cardPanel) {
        this.setSize(600, 600);
        this.parentLayout = cardLayout;
        this.cardPanel = cardPanel;
        JLabel title = new JLabel("PDF Encryption Tool");
        JLabel info = new JLabel("Choose your PDF to encrypt, enter the correct key pin" +
                "\n and choose the location for signed document");
        passwordField = new JPasswordField();
        JButton signButton = new JButton("Sign the chosen document");
        JButton changeOrig = new JButton("Change Original Location");
        JButton changeSigned = new JButton("Change Signed Location");

        title.setPreferredSize(new Dimension(600, 60));
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(new EmptyBorder(20, 0, 10, 0));

        //info.setText("Generating a pair of RSA keys\n(Enter an 8-digit pin for encrypting)");
        info.setPreferredSize(new Dimension(600, 60));
        info.setHorizontalAlignment(SwingConstants.CENTER);
        info.setBorder(new EmptyBorder(0, 0, 100, 0));

        origInfo.setMinimumSize(new Dimension(250, 75));
        origInfo.setHorizontalAlignment(SwingConstants.CENTER);
        JScrollPane origPane = new JScrollPane(origInfo);
        origPane.setPreferredSize(new Dimension(250, 75));


        signedInfo.setMinimumSize(new Dimension(250, 75));
        signedInfo.setHorizontalAlignment(SwingConstants.CENTER);
        JScrollPane signedPane = new JScrollPane(signedInfo);
        signedPane.setPreferredSize(new Dimension(250, 75));


        changeOrig.setPreferredSize(new Dimension(250, 25));
        changeSigned.setPreferredSize(new Dimension(250, 25));

        passwordField.setPreferredSize(new Dimension(100, 20));

        signButton.setPreferredSize(new Dimension(200, 20));
        signButton.addActionListener(e -> {
            try {
                CheckKeyAndSign();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        changeOrig.addActionListener(e -> ChoosePath("Original"));
        changeSigned.addActionListener(e -> ChoosePath("Signed"));

        JButton backButton = new JButton("Back");
        JPanel tmpPan = new JPanel();
        tmpPan.add(backButton);

        backButton.addActionListener(e -> ReturnToMainPage());

        origPath = signedPath = "None";

        //fileChooser.getSelectedFile();
        this.add(title);
        this.add(info);
        this.add(origPane);
        this.add(signedPane);
        this.add(changeOrig);
        this.add(changeSigned);
        this.add(passwordField);
        this.add(signButton);
        this.add(tmpPan);
    }

    private void ChoosePath(String type) {
        JFileChooser fileChooser = new JFileChooser();
        if(type.equals("Original")) {
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new FileNameExtensionFilter("PDF", "pdf"));
        }
        else {
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);
        }

        fileChooser.showOpenDialog(this);
        File chosenFile = fileChooser.getSelectedFile();
        if(chosenFile != null)
            switch(type) {
                case "Original":
                    origPath = chosenFile.getAbsolutePath();
                    origInfo.setText(origPath);

                    if(!Objects.equals(signedPath, "None")) {
                        String tmp = origPath.substring(origPath.lastIndexOf("\\"));
                        signedPath = signedPath.substring(0, signedPath.lastIndexOf("\\"))
                                + tmp
                                .substring(0, tmp.lastIndexOf(".")) + "_signed.pdf";
                    }
                    else
                        signedPath = origPath.substring(0, origPath.lastIndexOf(".")) + "_signed.pdf";

                    signedInfo.setText(signedPath);
                    break;
                case "Signed":
                    signedPath = chosenFile.getAbsolutePath() + signedPath.substring(signedPath.lastIndexOf("\\"));
                    signedInfo.setText(signedPath);
                    break;
            }
    }

    private void CheckKeyAndSign() throws Exception {
        String pin = String.valueOf(passwordField.getPassword());
        if(!Objects.equals(origPath, "None") && !Objects.equals(signedPath, "None") && pin.length() == 8)
            keyGenerator.SignPDF(pin, origPath, signedPath);
    }

    private void ReturnToMainPage() {
        parentLayout.show(cardPanel, "main");
    }
}
