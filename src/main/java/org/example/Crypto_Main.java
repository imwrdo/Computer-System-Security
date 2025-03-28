package org.example;

import org.example.Classes.CryptoPart.CryptoMainWindow;
import org.example.Classes.USBSeeker.USBSeeker;

import javax.swing.*;
import java.nio.file.Files;

public class Crypto_Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");
        CryptoMainWindow cryptoWindow = new CryptoMainWindow();
        cryptoWindow.setVisible(true);
        cryptoWindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        USBSeeker meowUSBSeeker = new USBSeeker();
        System.out.println("Main find USBs: "+ meowUSBSeeker.FindUSBs());
        System.out.println("Main test file in pendrive: "+ Files.exists(meowUSBSeeker.FindUSBs().getFirst().toPath().resolve("privateKey")));
    }
}