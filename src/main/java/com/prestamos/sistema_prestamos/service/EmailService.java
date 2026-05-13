package com.prestamos.sistema_prestamos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void enviarBienvenida(String destinatario, String nombre) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("¡Bienvenido al Sistema de Préstamos!");
            helper.setText(construirHtmlBienvenida(nombre), true);

            mailSender.send(mensaje);
            log.info("Email de bienvenida enviado a: {}", destinatario);
        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", destinatario, e.getMessage());
        }
    }

    private String construirHtmlBienvenida(String nombre) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Arial', sans-serif; background: #f4f6f9; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 40px auto; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                    .header { background: linear-gradient(135deg, #1e3a8a, #2563eb); padding: 40px; text-align: center; }
                    .header h1 { color: white; margin: 0; font-size: 28px; }
                    .header p { color: rgba(255,255,255,0.85); margin: 8px 0 0; font-size: 15px; }
                    .body { padding: 40px; }
                    .body h2 { color: #1e293b; font-size: 22px; margin-bottom: 16px; }
                    .body p { color: #475569; line-height: 1.7; font-size: 15px; margin-bottom: 16px; }
                    .card { background: #eff6ff; border-left: 4px solid #2563eb; border-radius: 8px; padding: 20px; margin: 24px 0; }
                    .card p { margin: 0; color: #1e40af; font-weight: 500; }
                    .footer { background: #f8fafc; padding: 24px 40px; text-align: center; border-top: 1px solid #e2e8f0; }
                    .footer p { color: #94a3b8; font-size: 13px; margin: 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🏦 Sistema de Préstamos</h1>
                        <p>Tu socio financiero de confianza</p>
                    </div>
                    <div class="body">
                        <h2>¡Bienvenido, %s! 👋</h2>
                        <p>Nos complace informarte que tu registro en nuestro Sistema de Préstamos ha sido completado exitosamente.</p>
                        <p>A partir de ahora tienes acceso a nuestros servicios financieros donde podrás:</p>
                        <div class="card">
                            <p>✅ Solicitar préstamos personales</p>
                        </div>
                        <div class="card">
                            <p>📊 Consultar tu tabla de amortización</p>
                        </div>
                        <div class="card">
                            <p>💳 Realizar y consultar tus pagos</p>
                        </div>
                        <p>Si tienes alguna duda o necesitas asistencia, no dudes en contactarnos.</p>
                        <p>¡Gracias por confiar en nosotros!</p>
                    </div>
                    <div class="footer">
                        <p>© 2026 Sistema de Préstamos. Todos los derechos reservados.</p>
                        <p>Este es un correo automático, por favor no respondas a este mensaje.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nombre);
    }
}