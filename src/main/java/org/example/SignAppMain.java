package org.example;

import org.example.crypto.CryptoMainWindow;
import javax.swing.*;


public class SignAppMain {

    public static void main(String[] args) {
        System.out.println("Sign PDF App Start");
        CryptoMainWindow cryptoWindow = new CryptoMainWindow();
        cryptoWindow.setVisible(true);
        cryptoWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}