package org.example;

import org.example.Classes.VeriPart.PDFVeriWindow;

import javax.swing.*;

public class Veri_Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        PDFVeriWindow vw = new PDFVeriWindow();
        vw.setVisible(true);
        vw.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}