package com.bankapp.encryption;

import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public final class RSAKeyPair {

    private int keyLength;

    private PrivateKey privateKey;

    private PublicKey publicKey;

    public RSAKeyPair(int keyLength) throws GeneralSecurityException {

        this.keyLength = keyLength;
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(this.keyLength);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    public final byte[] getPrivateKey() {
        return privateKey.getEncoded();
    }

    public final byte[] getPublicKey() {
        return publicKey.getEncoded();
    }
}