package com.taw.polybank.controller.company;

import com.taw.polybank.entity.ClientEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import static org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion.$2B;

public class PasswordManager {

    private final BCryptPasswordEncoder.BCryptVersion ENCODER_VERSION = $2B;
    private final int ITERATIONS = 15;
    private final int SALT_SIZE = 32;
    private SecureRandom secureRandom;
    private BCryptPasswordEncoder encoder;

    public PasswordManager() {
        this.secureRandom = new SecureRandom();
    }

    public void savePassword(ClientEntity client) {
        if (client.getSalt() != null) {
            throw new RuntimeException("ERROR: can not reset password using this method.");
        }
        // generating new salt
        byte[] seed = new byte[SALT_SIZE];
        secureRandom.nextBytes(seed);
        // setting up the seed and initializing encoder
        primeRandom(seed);
        initializeEncoder();

        //saving data
        client.setSalt(new String(seed, StandardCharsets.ISO_8859_1));
        client.setPassword(encoder.encode(client.getPassword()));
    }

    public boolean verifyPassword(ClientEntity client, String password) {
        boolean result = false;
        if (client != null) {
            String salt = client.getSalt();
            byte[] seed = salt.getBytes(StandardCharsets.ISO_8859_1);
            primeRandom(seed);
            initializeEncoder();
            result = encoder.matches(password, client.getPassword());
        }
        return result;
    }

    public void resetPassword(ClientEntity client, String newPassword) {
        client.setPassword(newPassword);
        client.setSalt(null);
        this.savePassword(client);
    }

    private void initializeEncoder() {
        this.encoder = new BCryptPasswordEncoder(ENCODER_VERSION, ITERATIONS, this.secureRandom);
    }

    private void primeRandom(byte[] bytes) {
        secureRandom.setSeed(bytes);
    }

}
