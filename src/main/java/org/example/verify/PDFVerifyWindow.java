package org.example.verify;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

/**
 * The PDFVerifyWindow class provides a graphical user interface (GUI)
 * for verifying the authenticity of PDF files using digital signatures.
 * It allows users to select a PDF file from their filesystem and verify
 * its integrity and signature validity. The UI comprises components for
 * selecting a file, displaying results, and showing helpful tooltips.
 */
public class PDFVerifyWindow extends JFrame {

    // Instance of PDFVerifyBody responsible for handling the PDF verification logic
    private final PDFVerifyBody verifyBody = new PDFVerifyBody();

    // UI components
    private final JLabel chosenDoc;
    private final JLabel resultLabel;

    // Path of the selected document
    private String documentPath = null;

    /**
     * Constructor initializes the PDF verification window, sets up the GUI components,
     * and configures actions such as choosing a PDF and verifying the document.
     */
    public PDFVerifyWindow() {
        // Configuring tooltips appearance
        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
        ToolTipManager.sharedInstance().setReshowDelay(0);

        // Set up the window size and title
        this.setSize(600, 600);
        this.setTitle("RSA Key Generator and Signer");

        // Create the main panel
        JPanel panel = new JPanel();

        // Labels for UI components
        JLabel title = new JLabel("PDF Verification Tool");
        JLabel info = new JLabel("Choose the .pdf you need to verify");
        chosenDoc = new JLabel("None");
        JButton changeDocButton = new JButton("Change PDF");
        JButton verifyButton = new JButton("Verify document");

        // Icon for the help button (tooltip)
        Icon questionIcon = UIManager.getIcon("OptionPane.informationIcon");
        JLabel helpLabel = new JLabel(questionIcon);

        // Label to show the verification result
        this.resultLabel = new JLabel();

        // Styling for title, information, result, and document labels
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

        // Set up the button actions for selecting a document and verifying it
        changeDocButton.setPreferredSize(new Dimension(250, 20));
        changeDocButton.addActionListener(e -> choosePDF());  // Change PDF action

        verifyButton.setPreferredSize(new Dimension(250, 20));
        verifyButton.addActionListener(e -> {
            try {
                verifyDocument();  // Verify the chosen document
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        // Help label with tooltip providing guidance
        helpLabel.setPreferredSize(new Dimension(580, 20));
        helpLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        helpLabel.setVerticalAlignment(SwingConstants.BOTTOM);
        helpLabel.setToolTipText("To check a pdf signature, you need to choose your pdf with the 'Change PDF'" +
                " button and then just click the 'Verify document' button");

        // Add components to the main panel
        panel.add(title);
        panel.add(info);
        panel.add(scrollDoc);
        panel.add(changeDocButton);
        panel.add(verifyButton);
        panel.add(resultLabel);
        panel.add(helpLabel);

        // Add panel to the frame
        this.add(panel);
    }

    /**
     * Opens a file chooser dialog to select a PDF file.
     * Updates the UI with the chosen file path, or displays an error if no file is selected.
     */
    private void choosePDF() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);  // Only allow files to be selected
        fileChooser.setAcceptAllFileFilterUsed(false);  // Disable "All files" filter
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF", "pdf"));  // Only allow PDFs

        // Show file chooser and capture the selected file
        fileChooser.showOpenDialog(this);
        File chosenFile = fileChooser.getSelectedFile();
        if (chosenFile != null) {
            documentPath = chosenFile.getAbsolutePath();
            chosenDoc.setText(documentPath);  // Update the label to show the selected file path
            resultLabel.setText("");  // Clear any previous results
        } else {
            resultLabel.setForeground(Color.RED);
            resultLabel.setText("Please, choose a PDF file");  // Show error if no file is selected
        }
    }

    /**
     * Verifies the selected PDF document by calling the `verifyPDF` method from `PDFVerifyBody`.
     * Updates the result label based on the verification outcome.
     *
     * @throws Exception If an error occurs during the verification process.
     */
    private void verifyDocument() throws Exception {
        try {
            if (documentPath != null) {  // Check if a document is selected
                if (verifyBody.verifyPDF(new File(documentPath))) {
                    resultLabel.setForeground(Color.GREEN);
                    resultLabel.setText("Everything is OK!");  // Verification successful
                } else {
                    resultLabel.setForeground(Color.RED);
                    resultLabel.setText("<html>Something went wrong: probably" +
                            " your pdf wasn't signed by hash or is corrupted!</html>");  // Verification failed
                }
            } else {
                resultLabel.setForeground(Color.RED);
                resultLabel.setText("Please, choose a PDF file");  // Error if no file is selected
            }
        }
        catch (RuntimeException e) {
            resultLabel.setForeground(Color.RED);
            resultLabel.setText("<html>" + e.getLocalizedMessage() + "</html>");  // Display error message
        }
    }
}