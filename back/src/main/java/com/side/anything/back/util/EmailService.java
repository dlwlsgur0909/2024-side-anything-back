package com.side.anything.back.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final static String SENDER_EMAIL = "dlwlsgur0927@gmail.com";

    // 회원가입 인증 메일 발송
    public String sendJoinMail(final String recipient) {

        String randomNumber = generateRandomNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(SENDER_EMAIL);
            message.setRecipients(MimeMessage.RecipientType.TO, recipient);
            message.setSubject("Side Anything 회원가입을 위한 인증 메일입니다");

            StringBuilder sb = new StringBuilder();

            sb.append("<h1>안녕하세요.</h1>");
            sb.append("<h3>회원가입을 위한 요청하신 인증 번호입니다.</h3><br>");
            sb.append("<h2>아래 코드를 입력해주세요.</h2>");
            sb.append("<div align='center' style='border:1px solid black; font-family:verdana;'>");
            sb.append("<h1 style='color:blue'>").append(randomNumber).append("</h1>");
            sb.append("</div>");

            message.setText(sb.toString(), "UTF-8", "html");

            javaMailSender.send(message);
        }catch(MessagingException e) {
            e.printStackTrace();
        }

        return randomNumber;
    }

    // 비밀번호 초기화 메일 발송
    public String sendResetPasswordMail(String recipient) {

        String randomNumber = generateRandomNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(SENDER_EMAIL);
            message.setRecipients(MimeMessage.RecipientType.TO, recipient);
            message.setSubject("Side Anything 비밀번호 초기화 메일입니다");

            StringBuilder sb = new StringBuilder();

            sb.append("<h1>안녕하세요.</h1>");
            sb.append("<h3>비밀번호가 초기화 되었습니다.</h3><br>");
            sb.append("<h2>아래 번호로 로그인 후 비밀번호를 변경해주세요</h2>");
            sb.append("<div align='center' style='border:1px solid black; font-family:verdana;'>");
            sb.append("<h1 style='color:blue'>").append(randomNumber).append("</h1>");
            sb.append("</div>");

            message.setText(sb.toString(), "UTF-8", "html");

            javaMailSender.send(message);
        }catch(MessagingException e) {
            e.printStackTrace();
        }

        return randomNumber;

    }


    // 인증번호 6자리 숫자 난수 생성
    private String generateRandomNumber() {

        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for(int i=0; i<6; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }

}
