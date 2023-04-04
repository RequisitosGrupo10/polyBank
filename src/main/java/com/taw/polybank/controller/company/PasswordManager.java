package com.taw.polybank.controller.company;

import com.taw.polybank.entity.ClientEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class PasswordManager {

    private final String ENCODER_VERSION = "$2b";
    private final int ITERATIONS = 15;
    private final int SALT_SIZE = 32;
    private ClientEntity client;
    private SecureRandom secureRandom;
    private BCryptPasswordEncoder encoder;

    public PasswordManager(ClientEntity client){
        this.client = client;
        this.secureRandom = new SecureRandom();
    }

    public void savePassword(){
        if(client.getSalt() != null){
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
        this.client = null;
    }

    public boolean verifyPassword(String password){
        if (client != null) {
            String salt = client.getSalt();
            byte[] seed = salt.getBytes(StandardCharsets.ISO_8859_1);
            primeRandom(seed);
            initializeEncoder();
            return encoder.matches(password, client.getPassword());
        }
        return false;
    }

    public void resetPassword(String newPassword){
        client.setPassword(newPassword);
        client.setSalt(null);
        this.savePassword();
    }

    private void initializeEncoder() {
        this.encoder = new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.valueOf(ENCODER_VERSION), ITERATIONS, this.secureRandom);
    }

    private void primeRandom(byte[] bytes) {
        secureRandom.setSeed(bytes);
    }

}
