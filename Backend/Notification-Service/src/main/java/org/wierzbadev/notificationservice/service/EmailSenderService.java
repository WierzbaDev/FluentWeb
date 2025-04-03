package org.wierzbadev.notificationservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.wierzbadev.notificationservice.model.dto.UserEmailDto;
import org.wierzbadev.notificationservice.model.dto.UserForgotPassword;
import org.wierzbadev.notificationservice.model.dto.UserLearningDto;
import org.wierzbadev.notificationservice.model.dto.UserVerifyDto;

@Slf4j
@Service
public class EmailSenderService {

    private final JavaMailSender sender;

    @Value("${app.frontendUrl}")
    private String frontendUrl;

    public EmailSenderService(JavaMailSender sender) {
        this.sender = sender;
    }

    @Async
    public void sendVerifyCode(UserVerifyDto verifyDto) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String verifyUrl = frontendUrl + "/verify/" + verifyDto.getUuid();

        String htmlContent = """
            <!DOCTYPE html>
            <html lang="pl">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Weryfikacja e-maila</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f4f4f4;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        margin: 20px auto;
                        background-color: #ffffff;
                        padding: 20px;
                        border-radius: 10px;
                        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                        text-align: center;
                    }
                    .header {
                        background-color: #4CAF50;
                        color: white;
                        padding: 15px;
                        font-size: 24px;
                        font-weight: bold;
                        border-radius: 10px 10px 0 0;
                    }
                    .content {
                        padding: 20px;
                        font-size: 16px;
                        color: #333;
                    }
                    .button {
                        display: inline-block;
                        background-color: #4CAF50;
                        color: white;
                        text-decoration: none;
                        padding: 12px 25px;
                        border-radius: 5px;
                        font-size: 16px;
                        font-weight: bold;
                        margin-top: 20px;
                    }
                    .footer {
                        margin-top: 20px;
                        font-size: 12px;
                        color: #777;
                    }
                </style>
            </head>
            <body>
            <div class="container">
                <div class="header">Weryfikacja e-maila</div>
                <div class="content">
                    <p>Cześć <strong>%s</strong>,</p>
                    <p>Dziękujemy za rejestrację w naszej aplikacji FluentWeb! Aby zakończyć proces i aktywować swoje konto, kliknij przycisk poniżej:</p>
                    <a href="%s" id="verifyButton" class="button">Zweryfikuj e-mail</a>
                    <p>Jeśli nie rejestrowałeś się w naszym serwisie, po prostu zignoruj tę wiadomość.</p>
                </div>
                <div class="footer">
                    © 2025 FluentWeb | Wszelkie prawa zastrzeżone.
                </div>
            </div>
            </body>
                <script>
                    document.getElementById('verifyButton').addEventListener('click', function(event) {
                        setTimeout(function() {
                            window.close();
                        }, 500);
                    });
                </script>
            </html>
            
            """.formatted(verifyDto.getName(), verifyUrl);

        helper.setTo(verifyDto.getEmail());
        helper.setSubject("Zweryfikuj swój e-mail");
        helper.setText(htmlContent, true);
        helper.setFrom("no-reply@fluentweb.pl");

        sender.send(message);
        log.info("Sent verify code for user with id: {}", verifyDto.getUserId());
    }

    @Async
    public void sendResetPassword(UserForgotPassword forgotPassword) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        String urlResetPassword = frontendUrl + "/reset-password/" + forgotPassword.getToken();
        String urlForgotPassword = frontendUrl + "/forgot-password";

                String htmlContext = """
                <!DOCTYPE html>
                <html lang="pl">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Reset Hasła - FluentWeb</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background-color: #f4f4f4;
                            margin: 0;
                            padding: 0;
                            text-align: center;
                        }
                        .email-container {
                            max-width: 600px;
                            margin: 20px auto;
                            background: #ffffff;
                            padding: 20px;
                            border-radius: 10px;
                            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
                            border-top: 5px solid #007BFF;
                        }
                        .header {
                            font-size: 22px;
                            font-weight: bold;
                            color: #333;
                            margin-bottom: 20px;
                        }
                        .content {
                            font-size: 16px;
                            color: #555;
                            line-height: 1.6;
                        }
                        .button {
                            display: inline-block;
                            margin-top: 20px;
                            padding: 12px 20px;
                            font-size: 16px;
                            color: white;
                            background: #007BFF;
                            text-decoration: none;
                            border-radius: 5px;
                        }
                        .button:hover {
                            background: #0056b3;
                        }
                        .footer {
                            margin-top: 20px;
                            font-size: 12px;
                            color: #777;
                        }
                    </style>
                </head>
                <body>
                    <div class="email-container">
                        <div class="header">Zresetuj swoje hasło FluentWeb</div>
                        <div class="content">
                            <p><b>Resetowanie hasła</b></p>
                            <p>Otrzymaliśmy prośbę o zresetowanie hasła do Twojego konta FluentWeb.<br>
                               Aby ustawić nowe hasło, kliknij poniższy przycisk:</p>
                            <a href="%s" class="button">Zresetuj swoje hasło</a>
                            <p>Jeśli nie użyjesz tego linku w ciągu godziny, wygaśnie. Aby otrzymać nowy link do resetowania hasła, odwiedź:</p>
                            <a href="%s">FluentWeb - Reset Hasła</a>
                        </div>
                        <div class="footer">
                            <p>Jeśli to nie Ty zainicjowałeś reset hasła, możesz zignorować tę wiadomość.</p>
                            <p>Dziękujemy za korzystanie z FluentWeb!</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(urlResetPassword, urlForgotPassword);

        helper.setTo(forgotPassword.getEmail());
        helper.setSubject("Zresetuj swoje hasło FluentWeb");
        helper.setText(htmlContext, true);
        helper.setFrom("no-reply@fluentweb.pl");

        sender.send(message);
        log.info("Sent link to reset password for user with email: {}", forgotPassword.getEmail());
    }

    public void sendDailyStats(UserLearningDto stats, UserEmailDto email) {

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        int sum = stats.getCorrectWords() + stats.getFailedWords();
        float average = ((float) stats.getCorrectWords() / sum) * 100;
        average = Math.round(average * 100.0f) / 100.0f;

        String htmlContext = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Twoje codzienne statystyki</title>
            <style>
                body {
                    font-family: Arial, sans-serif;
                    background-color: #f4f4f4;
                    margin: 0;
                    padding: 0;
                }
                .email-container {
                    max-width: 600px;
                    margin: 20px auto;
                    background: #ffffff;
                    padding: 20px;
                    border-radius: 10px;
                    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
                    text-align: center;
                    border-top: 5px solid #4CAF50;
                }
                .header {
                    font-size: 26px;
                    font-weight: bold;
                    margin-bottom: 20px;
                    color: #333;
                }
                .stats {
                    font-size: 18px;
                    margin: 10px 0;
                    background: #f9f9f9;
                    padding: 10px;
                    border-radius: 5px;
                }
                .highlight {
                    font-weight: bold;
                    color: #4CAF50;
                }
                .footer {
                    margin-top: 20px;
                    font-size: 14px;
                    color: #555;
                }
                .button {
                    display: inline-block;
                    margin-top: 15px;
                    padding: 10px 20px;
                    font-size: 16px;
                    color: white;
                    background: #4CAF50;
                    text-decoration: none;
                    border-radius: 5px;
                }
                .button:hover {
                    background: #45a049;
                }
            </style>
        </head>
        <body>
            <div class="email-container">
                <div class="header">Cześć, <span class="highlight">%s</span>! 🎉</div>
                <p class="stats">✅ Poprawne słowa: <span class="highlight">%s</span></p>
                <p class="stats">❌ Błędne słowa: <span class="highlight">%s</span></p>
                <p class="stats">📊 Łączna liczba słów: <span class="highlight">%s</span></p>
                <p class="stats">📈 Procent poprawnych odpowiedzi: <span class="highlight">%s%%</span></p>
                <div class="footer">Dziękujemy za korzystanie z naszego serwisu! 🚀</div>
            </div>
        </body>
        </html>
        """.formatted(email.getName(), stats.getCorrectWords(), stats.getFailedWords(), sum, average);


        try {
            helper.setTo(email.getEmail());
            helper.setSubject("Twoje wczorajsze statystyki");
            helper.setText(htmlContext, true);
            helper.setFrom("no-reply@fluentweb.pl");
        } catch (Exception e) {
            log.error("Error while sending message");
            throw new RuntimeException(e);
        }
        sender.send(message);
        log.info("Sent daily stats for user with id: {}", stats.getUserId());
    }
}
