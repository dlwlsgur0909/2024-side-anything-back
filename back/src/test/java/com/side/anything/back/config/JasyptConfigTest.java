package com.side.anything.back.config;

import org.assertj.core.api.Assertions;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JasyptConfigTest {

    @Autowired
    public StringEncryptor jasyptEncryptor;

    @Test
    void jasyptEncryptTest() {

        // given
        String url = "jdbc:mariadb://127.0.0.1:3306/side_anything";
        String username = "root";
        String password = "admin";

        String secret = "vmfhaltmskdlstkfkdgodyroqkfwkdbalroqkfwkdbaklwjeiobjvpawkldjwlkcnzxklcjogr";

        String gmailUsername = "dlwlsgur0927@gmail.com";
        String gmailPassword = "ivur zple agxa hmjg";

        // when
        try {
            String encryptedUrl = jasyptEncryptor.encrypt(url);
            String encryptedUsername = jasyptEncryptor.encrypt(username);
            String encryptedPassword = jasyptEncryptor.encrypt(password);
            String encryptedSecret = jasyptEncryptor.encrypt(secret);
            String encryptedGmailUsername = jasyptEncryptor.encrypt(gmailUsername);
            String encryptedGmailPassword = jasyptEncryptor.encrypt(gmailPassword);

            System.out.println("encryptedUrl = " + encryptedUrl);
            System.out.println("encryptedUsername = " + encryptedUsername);
            System.out.println("encryptedPassword = " + encryptedPassword);
            System.out.println("encryptedSecret = " + encryptedSecret);
            System.out.println("encryptedGmailUsername = " + encryptedGmailUsername);
            System.out.println("encryptedGmailPassword = " + encryptedGmailPassword);

            String decryptedUrl = jasyptEncryptor.decrypt(encryptedUrl);
            String decryptedUsername = jasyptEncryptor.decrypt(encryptedUsername);
            String decryptedPassword = jasyptEncryptor.decrypt(encryptedPassword);
            String decryptedSecret = jasyptEncryptor.decrypt(encryptedSecret);
            String decryptedGmailUsername = jasyptEncryptor.decrypt(encryptedGmailUsername);
            String decryptedGmailPassword = jasyptEncryptor.decrypt(encryptedGmailPassword);

            System.out.println("decryptedUrl = " + decryptedUrl);
            System.out.println("decryptedUsername = " + decryptedUsername);
            System.out.println("decryptedPassword = " + decryptedPassword);
            System.out.println("decryptedSecret = " + decryptedSecret);
            System.out.println("decryptedGmailUsername = " + decryptedGmailUsername);
            System.out.println("decryptedGmailPassword = " + decryptedGmailPassword);

            // then
            Assertions.assertThat(url).isEqualTo(decryptedUrl);
            Assertions.assertThat(username).isEqualTo(decryptedUsername);
            Assertions.assertThat(password).isEqualTo(decryptedPassword);
            Assertions.assertThat(secret).isEqualTo(decryptedSecret);
            Assertions.assertThat(gmailUsername).isEqualTo(decryptedGmailUsername);
            Assertions.assertThat(gmailPassword).isEqualTo(decryptedGmailPassword);



        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}