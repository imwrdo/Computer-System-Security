package org.example;

import org.example.Classes.CryptoPart.CryptoMainWindow;

import javax.swing.*;

public class Crypto_Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");
        CryptoMainWindow cryptoWindow = new CryptoMainWindow();
        cryptoWindow.setVisible(true);
        cryptoWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
}