
# 🚀 PAdES Electronic Signature Tool

## 📌 Overview

The PAdES Electronic Signature Tool is a Java Swing application designed to create qualified electronic signatures following the PAdES (PDF Advanced Electronic Signature) standard. This tool ensures secure handling of RSA key pairs, encrypting the private key with AES for storage on a hardware device (pendrive).

![Fig. 1 – Block diagram of the project concept.](#)

## ✨ Features

- ✅ **Key Generation Tool**: An auxiliary application that generates RSA key pairs and encrypts the private key using AES with a user-provided PIN.
- ✅ **Signing Application**: Detects the hardware tool (pendrive), decrypts the private RSA key using the PIN, and signs PDF documents according to PAdES standards.
- ✅ **Verification Tool**: Enables users to verify signatures by generating document hashes and comparing them with those created during signing.

## 🔹 Usage Scenario

### 🔑 1. Key Generation

1. User A runs the auxiliary application to generate RSA key pairs.
2. The private key is encrypted using AES with a user-provided PIN.
3. The encrypted private key is stored securely on a pendrive.

### ✍️ 2. Signing Process

1. User A inserts the pendrive containing the encrypted private key.
2. The signing application automatically detects the hardware tool.
3. User A enters the PIN to decrypt the private RSA key.
4. A PDF document is selected for signing, embedding the electronic signature within the PDF according to PAdES standards.

### ✅ 3. Signature Verification

1. User B uses the same application to verify signatures.
2. User B must have the public key of User A and the signed PDF document.
3. The application generates a hash of the document and decrypts the hash embedded in the PDF using User A's public key.
4. If the computed hash matches the decrypted hash, the signature is successfully verified.

## 🔧 Installation

```sh
# Clone the repository
git clone https://github.com/imwrdo/Computer-System-Security.git

# Compile the Java source files
javac *.java

# Run the application
java MainApplication
```

## 📂 Dependencies

- Java Development Kit (JDK) 8 or higher
- Java Swing library for GUI components

## 🤝 Contributing

We welcome pull requests! For significant changes, please open an issue first to discuss the updates.

## 📜 License

This project is licensed under the MIT License - see the LICENSE file for details.

