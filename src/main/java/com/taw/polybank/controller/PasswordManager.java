package com.taw.polybank.controller;

import com.taw.polybank.dto.ClientDTO;
import com.taw.polybank.service.ClientService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import static org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion.$2B;

public class PasswordManager {

    private final BCryptPasswordEncoder.BCryptVersion ENCODER_VERSION = $2B;
    private final int ITERATIONS = 5;
    private final int SALT_SIZE = 32;
    private SecureRandom secureRandom;
    private BCryptPasswordEncoder encoder;
    private ClientService clientService;

    public PasswordManager(ClientService clientService) {
        this.secureRandom = new SecureRandom();
        this.clientService = clientService;
    }

    public String[] savePassword(ClientDTO client, String plainPassword) {
        if (clientService.findById(client.getId()).isPresent()) {
            throw new RuntimeException("ERROR: can not reset password using this method.");
        }
        // generating new salt
        byte[] seed = new byte[SALT_SIZE];
        secureRandom.nextBytes(seed);
        // setting up the seed and initializing encoder
        primeRandom(seed);
        initializeEncoder();

        String[] saltAndPass = new String[2];
        //saving data
        //clientService.saveUserSaltAndPassword(client.getId(), new String(seed, StandardCharsets.ISO_8859_1), encoder.encode(plainPassword));
        /*
        client.setSalt(new String(seed, StandardCharsets.ISO_8859_1));
        client.setPassword(encoder.encode(client.getPassword()));
        */
        saltAndPass[0] = new String(seed, StandardCharsets.ISO_8859_1);
        saltAndPass[1] = encoder.encode(plainPassword);
        return saltAndPass;
    }

    public boolean verifyPassword(ClientDTO client, String password) {
        boolean result = false;
        if (client != null) {
            String salt = clientService.getSalt(client.getId());
            byte[] seed = salt.getBytes(StandardCharsets.ISO_8859_1);
            primeRandom(seed);
            initializeEncoder();
            result = encoder.matches(password, clientService.getPassword(client.getId()));
        }
        return result;
    }

    public void resetPassword(ClientDTO client, String newPassword) {

        String salt = clientService.getSalt(client.getId());
        byte[] seed = salt.getBytes(StandardCharsets.ISO_8859_1);
        primeRandom(seed);
        initializeEncoder();

        clientService.updateUserPassword(client.getId(), encoder.encode(newPassword));
    }

    private void initializeEncoder() {
        this.encoder = new BCryptPasswordEncoder(ENCODER_VERSION, ITERATIONS, this.secureRandom);
    }

    private void primeRandom(byte[] bytes) {
        secureRandom.setSeed(bytes);
    }

}
