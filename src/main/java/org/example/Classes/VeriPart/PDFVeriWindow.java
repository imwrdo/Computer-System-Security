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

    public PDFVeriWindow() {
        this.setSize(600, 600);
        this.setTitle("RSA Key Generator and Signer");

        JPanel panel = new JPanel();

        JLabel title = new JLabel("PDF Verification Tool");
        JLabel info = new JLabel("Choose the .pdf you need to verify");
        chosenDoc = new JLabel("None");
        JButton changeDocButton = new JButton("Change PDF");
        JButton verifyButton = new JButton("Verify document");

        title.setPreferredSize(new Dimension(600, 60));
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBorder(new EmptyBorder(20, 0, 10, 0));

        info.setPreferredSize(new Dimension(600, 60));
        info.setHorizontalAlignment(SwingConstants.CENTER);
        info.setBorder(new EmptyBorder(0, 0, 100, 0));

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



        //fileChooser.getSelectedFile();

        panel.add(title);
        panel.add(info);
        panel.add(scrollDoc);
        panel.add(changeDocButton);
        panel.add(verifyButton);

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
        }
    }

    private void VerifyDocument() throws Exception {
        if(documentPath != null)
            veriBody.VerifyPDF(new File(documentPath));
    }
}
