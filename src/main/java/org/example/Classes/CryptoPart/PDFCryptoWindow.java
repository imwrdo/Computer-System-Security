package org.example.Classes.CryptoPart;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.Objects;

public class PDFCryptoWindow extends JPanel {

    private PDFCryptoBody pdfCrypto;

    private JPasswordField passwordField;
    private final CardLayout parentLayout;
    private final JPanel cardPanel;
    private String origPath;
    private String signedPath;
    private final JLabel origInfo = new JLabel("None");
    private final JLabel signedInfo = new JLabel("None");
    private JLabel resultLabel;

    private String keyPath = "None";
    private JLabel keyLabel;

    private String keyFileName;


    public PDFCryptoWindow(CardLayout cardLayout, JPanel cardPanel, String keyFileName) {
        this.setSize(600, 600);
        this.parentLayout = cardLayout;
        this.cardPanel = cardPanel;
        this.keyFileName = keyFileName;
        JLabel title = new JLabel("PDF Encryption Tool");
        JLabel info = new JLabel("Choose the PDF file to get encrypted and enter your 8-digit pin");
        passwordField = new JPasswordField();
        JButton signButton = new JButton("Sign the chosen document");
        JButton changeOrig = new JButton("Change Original File");
        JButton changeSigned = new JButton("Change Signed Location");

        Icon questionIcon = UIManager.getIcon("OptionPane.informationIcon");
        Image img = ((ImageIcon) questionIcon).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(img);
        JLabel helpLabel = new JLabel(scaledIcon);

        this.resultLabel = new JLabel();
        this.keyLabel = new JLabel(keyPath);

        title.setPreferredSize(new Dimension(600, 60));
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(new EmptyBorder(20, 0, 10, 0));

        //info.setText("Generating a pair of RSA keys\n(Enter an 8-digit pin for encrypting)");
        info.setPreferredSize(new Dimension(600, 60));
        info.setHorizontalAlignment(SwingConstants.CENTER);

        resultLabel.setPreferredSize(new Dimension(580, 75));
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

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

        helpLabel.setPreferredSize(new Dimension(580, 20));
        helpLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        helpLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        helpLabel.setToolTipText("To sign a PDF document you need to choose " +
                "the ORIGINAL FILE (left) and the SIGNED FILE LOCATION with" +
                " 'Change ...', enter your 8-DIGIT PIN, find PRIVATE KEY with"+
                " clicking 'Scan for private key' and then click 'Sign the chosen document'");

        JButton backButton = new JButton("Back");
        JPanel tmpPan = new JPanel();
        tmpPan.add(backButton);

        backButton.addActionListener(e -> ReturnToMainPage());

        origPath = signedPath = "None";


        JPanel keyPanel = new JPanel();
        keyPanel.setPreferredSize(new Dimension(580, 110));
        keyPanel.setBorder(new EmptyBorder(0, 165, 0, 165));
        this.keyLabel = new JLabel(keyPath);
        keyLabel.setMinimumSize(new Dimension(250, 75));
        keyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        keyLabel.setVerticalAlignment(SwingConstants.CENTER);
        JScrollPane keyPane = new JScrollPane(keyLabel);
        keyPane.setPreferredSize(new Dimension(250, 75));

        JButton keyButton = new JButton("Scan for private key");
        keyButton.setPreferredSize(new Dimension(250, 25));

        keyButton.addActionListener(e -> TryToFindKey());

        keyPanel.add(keyPane);
        keyPanel.add(keyButton);


        //fileChooser.getSelectedFile();
        this.add(title);
        this.add(info);
        this.add(origPane);
        this.add(signedPane);
        this.add(changeOrig);
        this.add(changeSigned);
        this.add(keyPanel);
        this.add(passwordField);
        this.add(signButton);
        this.add(tmpPan);
        this.add(resultLabel);
        this.add(helpLabel);

        this.pdfCrypto = new PDFCryptoBody(4096, keyFileName);
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
        if(chosenFile != null) {
            switch (type) {
                case "Original":
                    origPath = chosenFile.getAbsolutePath();
                    origInfo.setText(origPath);

                    if (!Objects.equals(signedPath, "None")) {
                        String tmp = origPath.substring(origPath.lastIndexOf("\\"));
                        signedPath = signedPath.substring(0, signedPath.lastIndexOf("\\"))
                                + tmp
                                .substring(0, tmp.lastIndexOf(".")) + "_signed.pdf";
                    } else
                        signedPath = origPath.substring(0, origPath.lastIndexOf(".")) + "_signed.pdf";

                    signedInfo.setText(signedPath);
                    break;
                case "Signed":
                    signedPath = chosenFile.getAbsolutePath() + signedPath.substring(signedPath.lastIndexOf("\\"));
                    signedInfo.setText(signedPath);
                    break;
            }
        }
        else {
            resultLabel.setForeground(Color.RED);
            resultLabel.setText("Please, choose a PDF FILE or a LOCATION FOR SIGNED PDF");
        }
    }

    private void CheckKeyAndSign() throws Exception {
        try {
            if(!keyPath.equals("None")) {
                String pin = String.valueOf(passwordField.getPassword());
                if (!Objects.equals(origPath, "None") && !Objects.equals(signedPath, "None")) {
                    if (pin.length() == 8) {
                        if (pdfCrypto.SignPDF(pin, origPath, signedPath, keyPath)) {
                            resultLabel.setForeground(Color.GREEN);
                            resultLabel.setText("Successfully signed!");
                        }
                    }
                    else {
                        resultLabel.setForeground(Color.RED);
                        resultLabel.setText("You must enter an 8-DIGIT PIN!");
                    }
                } else {
                    resultLabel.setForeground(Color.RED);
                    resultLabel.setText("<html>You must choose AT LEAST ORIGINAL FILE! And might choose" +
                            "the LOCATION for the SIGNED DOCUMENT</html>");
                }
            }
            else throw new RuntimeException("The PENDRIVE with the PRIVATE KEY wasn't found! Ensure that" +
                    " you've inserted your pendrive and have all the rights to read it!");
        }
        catch(RuntimeException e) {
            resultLabel.setForeground(Color.RED);
            resultLabel.setText("<html>" + e.getLocalizedMessage() + "</html>");
        }
    }

    private void ReturnToMainPage() {
        parentLayout.show(cardPanel, "main");
    }

    private void TryToFindKey() {
        keyPath = pdfCrypto.ScanForKey(keyFileName);
        keyLabel.setText(keyPath);
        if(keyPath.equals("None")) {
            resultLabel.setForeground(Color.RED);
            resultLabel.setText("<html>The PENDRIVE with the PRIVATE KEY wasn't found! Ensure that " +
                    "you have your pendrive inserted and have all the rights to read it!</html>");
        }
        else resultLabel.setText("");
    }
}
