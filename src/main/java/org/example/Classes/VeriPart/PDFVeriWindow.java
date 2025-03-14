package org.example.Classes.VeriPart;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Objects;

public class PDFVeriWindow extends JFrame {
    private final PDFVeriBody veriBody = new PDFVeriBody();

    private final JLabel chosenDoc;
    private String documentPath = null;
    private JLabel resultLabel;

    public PDFVeriWindow() {

        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        ToolTipManager.sharedInstance().setReshowDelay(0);

        this.setSize(600, 600);
        this.setTitle("RSA Key Generator and Signer");

        JPanel panel = new JPanel();

        JLabel title = new JLabel("PDF Verification Tool");
        JLabel info = new JLabel("Choose the .pdf you need to verify");
        chosenDoc = new JLabel("None");
        JButton changeDocButton = new JButton("Change PDF");
        JButton verifyButton = new JButton("Verify document");

        Icon questionIcon = UIManager.getIcon("OptionPane.informationIcon");
        Image img = ((ImageIcon) questionIcon).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(img);
        JLabel helpLabel = new JLabel(scaledIcon);

        this.resultLabel = new JLabel();

        title.setPreferredSize(new Dimension(600, 60));
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(new EmptyBorder(20, 0, 10, 0));

        info.setPreferredSize(new Dimension(600, 50));
        info.setHorizontalAlignment(SwingConstants.CENTER);
        info.setBorder(new EmptyBorder(0, 0, 30, 0));

        resultLabel.setPreferredSize(new Dimension(580, 75));
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        chosenDoc.setMinimumSize(new Dimension(250, 75));
        chosenDoc.setHorizontalAlignment(SwingConstants.CENTER);
        JScrollPane scrollDoc = new JScrollPane(chosenDoc);
        scrollDoc.setPreferredSize(new Dimension(400, 75));

        changeDocButton.setPreferredSize(new Dimension(250, 20));
        changeDocButton.addActionListener(e -> ChoosePDF());

        verifyButton.setPreferredSize(new Dimension(250, 20));
        verifyButton.addActionListener(e -> {
            try {
                VerifyDocument();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        helpLabel.setPreferredSize(new Dimension(580, 20));
        helpLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        helpLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        helpLabel.setToolTipText("To check a pdf signature, you need to choose your pdf with the 'Change PDF'" +
                "button and then just click the 'Verify document' button");



        //fileChooser.getSelectedFile();

        panel.add(title);
        panel.add(info);
        panel.add(scrollDoc);
        panel.add(changeDocButton);
        panel.add(verifyButton);
        panel.add(resultLabel);
        panel.add(helpLabel);

        this.add(panel);
    }

    private void ChoosePDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF", "pdf"));

        fileChooser.showOpenDialog(this);
        File chosenFile = fileChooser.getSelectedFile();
        if(chosenFile != null) {
            documentPath = chosenFile.getAbsolutePath();
            chosenDoc.setText(documentPath);
            resultLabel.setText("");
        }
        else {
            resultLabel.setForeground(Color.RED);
            resultLabel.setText("Please, choose a PDF file");
        }
    }

    private void VerifyDocument() throws Exception {
        try {
            if (documentPath != null) {
                if (veriBody.VerifyPDF(new File(documentPath))) {
                    resultLabel.setForeground(Color.GREEN);
                    resultLabel.setText("Everything is OK!");
                } else {
                    resultLabel.setForeground(Color.RED);
                    resultLabel.setText("<html>Something went wrong: probably" +
                            " your pdf wasn't signed by hash or is corrupted!</html>");
                }
            } else {
                resultLabel.setForeground(Color.RED);
                resultLabel.setText("Please, choose a PDF file");
            }
        }
        catch(RuntimeException e) {
            resultLabel.setForeground(Color.RED);
            resultLabel.setText("<html>" + e.getLocalizedMessage() + "</html>");
        }
    }
}
