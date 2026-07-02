package com.prestamos.sistema_prestamos.shared.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import java.nio.file.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    @Value("${app.upload.dir}")
    private String uploadDir;

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


    @Async
    public void enviarConfirmacionPrestamo(String destinatario, String nombre,
                                           BigDecimal monto, Integer numPagos,
                                           BigDecimal tasaInteres, String rutaPdf) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("✅ Confirmación de Préstamo — Sistema de Préstamos");
            helper.setText(construirHtmlPrestamo(nombre, monto, numPagos, tasaInteres), true);

            // Adjuntar PDF firmado
            if (rutaPdf != null && !rutaPdf.isBlank()) {
                try {
                    Path pathPdf = Paths.get(uploadDir, rutaPdf);
                    if (Files.exists(pathPdf)) {
                        helper.addAttachment("contrato-prestamo.pdf",
                                new org.springframework.core.io.FileSystemResource(pathPdf.toFile()));
                    }
                } catch (Exception e) {
                    log.warn("No se pudo adjuntar el PDF: {}", e.getMessage());
                }
            }

            mailSender.send(mensaje);
            log.info("Email de confirmación de préstamo enviado a: {}", destinatario);
        } catch (Exception e) {
            log.error("Error al enviar email de préstamo a {}: {}", destinatario, e.getMessage());
        }
    }

    private String construirHtmlPrestamo(String nombre, BigDecimal monto,
                                         Integer numPagos, BigDecimal tasaInteres) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { font-family: 'Arial', sans-serif; background: #f4f6f9; margin: 0; padding: 0; }
                .container { max-width: 600px; margin: 40px auto; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                .header { background: linear-gradient(135deg, #1e3a8a, #2563eb); padding: 40px; text-align: center; }
                .header h1 { color: white; margin: 0; font-size: 26px; }
                .header p { color: rgba(255,255,255,0.85); margin: 8px 0 0; font-size: 15px; }
                .body { padding: 40px; }
                .body h2 { color: #1e293b; font-size: 20px; margin-bottom: 16px; }
                .body p { color: #475569; line-height: 1.7; font-size: 15px; margin-bottom: 16px; }
                .datos { background: #f8fafc; border-radius: 10px; padding: 8px 20px; margin: 20px 0; }
                .dato-row {
                    display: table;
                    width: 100%%;
                    padding: 12px 0;
                    border-bottom: 1px solid #e2e8f0;
                }
                .dato-row:last-child { border-bottom: none; }
                .dato-label {
                    display: table-cell;
                    color: #64748b;
                    font-size: 14px;
                    font-weight: 600;
                    width: 60%%;
                    padding-right: 16px;
                }
                .dato-value {
                    display: table-cell;
                    color: #1e293b;
                    font-size: 14px;
                    font-weight: 700;
                    text-align: right;
                }
                .adjunto { background: #eff6ff; border: 1px solid #bfdbfe; border-radius: 8px; padding: 16px; margin: 20px 0; }
                .adjunto-inner { display: table; width: 100%%; }
                .adjunto-icon { display: table-cell; font-size: 24px; width: 40px; vertical-align: middle; }
                .adjunto-text { display: table-cell; color: #1e40af; font-size: 14px; font-weight: 500; vertical-align: middle; padding-left: 12px; }
                .footer { background: #f8fafc; padding: 24px 40px; text-align: center; border-top: 1px solid #e2e8f0; }
                .footer p { color: #94a3b8; font-size: 13px; margin: 4px 0; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>🏦 Sistema de Préstamos</h1>
                    <p>Confirmación de crédito aprobado</p>
                </div>
                <div class="body">
                    <h2>¡Felicidades, %s! 🎉</h2>
                    <p>Tu solicitud de crédito ha sido <strong>aprobada y registrada</strong> exitosamente en nuestro sistema.</p>

                    <div class="datos">
                        <div class="dato-row">
                            <span class="dato-label">💰 Monto aprobado</span>
                            <span class="dato-value">$%s MXN</span>
                        </div>
                        <div class="dato-row">
                            <span class="dato-label">📅 Número de pagos</span>
                            <span class="dato-value">%d mensualidades</span>
                        </div>
                        <div class="dato-row">
                            <span class="dato-label">📈 Tasa de interés anual</span>
                            <span class="dato-value">%s%%</span>
                        </div>
                    </div>

                    <div class="adjunto">
                        <div class="adjunto-inner">
                            <span class="adjunto-icon">📎</span>
                            <span class="adjunto-text">Se adjunta el contrato firmado para tus registros personales.</span>
                        </div>
                    </div>

                    <p>Recuerda realizar tus pagos puntualmente para mantener un buen historial crediticio.</p>
                    <p>Si tienes alguna duda, no dudes en contactarnos.</p>
                </div>
                <div class="footer">
                    <p>© 2026 Sistema de Préstamos. Todos los derechos reservados.</p>
                    <p>Este es un correo automático, por favor no respondas a este mensaje.</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(nombre, monto.toPlainString(), numPagos, tasaInteres.toPlainString());
    }

    @Async
    public void enviarConfirmacionPago(String destinatario, String nombre,
                                       BigDecimal montoPagado, Integer numPago,
                                       BigDecimal saldoRestante, LocalDate fechaPago) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject("💳 Confirmación de Pago — Sistema de Préstamos");
            helper.setText(construirHtmlPago(nombre, montoPagado, numPago, saldoRestante, fechaPago), true);

            mailSender.send(mensaje);
            log.info("Email de confirmación de pago enviado a: {}", destinatario);
        } catch (Exception e) {
            log.error("Error al enviar email de pago a {}: {}", destinatario, e.getMessage());
        }
    }

    private String construirHtmlPago(String nombre, BigDecimal montoPagado,
                                     Integer numPago, BigDecimal saldoRestante,
                                     LocalDate fechaPago) {
        String saldoTexto = saldoRestante.compareTo(BigDecimal.ZERO) == 0
                ? "🎉 ¡Préstamo liquidado!"
                : "$" + saldoRestante.toPlainString() + " MXN";

        String mensajeSaldo = saldoRestante.compareTo(BigDecimal.ZERO) == 0
                ? "<p style='background:#dcfce7;border:1px solid #86efac;border-radius:8px;padding:16px;color:#15803d;font-weight:600;text-align:center;font-size:16px;'>🎉 ¡Felicidades! Has liquidado tu préstamo completamente.</p>"
                : "<p style='color:#475569;line-height:1.7;font-size:15px;margin-bottom:16px;'>Recuerda continuar con tus pagos puntualmente para mantener un buen historial crediticio.</p>";

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { font-family: 'Arial', sans-serif; background: #f4f6f9; margin: 0; padding: 0; }
                .container { max-width: 600px; margin: 40px auto; background: white; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                .header { background: linear-gradient(135deg, #065f46, #059669); padding: 40px; text-align: center; }
                .header h1 { color: white; margin: 0; font-size: 26px; }
                .header p { color: rgba(255,255,255,0.85); margin: 8px 0 0; font-size: 15px; }
                .body { padding: 40px; }
                .body h2 { color: #1e293b; font-size: 20px; margin-bottom: 16px; }
                .body p { color: #475569; line-height: 1.7; font-size: 15px; margin-bottom: 16px; }
                .datos { background: #f8fafc; border-radius: 10px; padding: 8px 20px; margin: 20px 0; }
                .dato-row { display: table; width: 100%%; padding: 12px 0; border-bottom: 1px solid #e2e8f0; }
                .dato-row:last-child { border-bottom: none; }
                .dato-label { display: table-cell; color: #64748b; font-size: 14px; font-weight: 600; width: 60%%; padding-right: 16px; }
                .dato-value { display: table-cell; color: #1e293b; font-size: 14px; font-weight: 700; text-align: right; }
                .dato-value-success { display: table-cell; color: #059669; font-size: 14px; font-weight: 700; text-align: right; }
                .footer { background: #f8fafc; padding: 24px 40px; text-align: center; border-top: 1px solid #e2e8f0; }
                .footer p { color: #94a3b8; font-size: 13px; margin: 4px 0; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>💳 Pago Confirmado</h1>
                    <p>Tu pago ha sido registrado exitosamente</p>
                </div>
                <div class="body">
                    <h2>Hola, %s 👋</h2>
                    <p>Te confirmamos que hemos recibido tu pago correctamente.</p>
                    <div class="datos">
                        <div class="dato-row">
                            <span class="dato-label">💳 Monto pagado</span>
                            <span class="dato-value-success">$%s MXN</span>
                        </div>
                        <div class="dato-row">
                            <span class="dato-label">📅 Fecha de pago</span>
                            <span class="dato-value">%s</span>
                        </div>
                        <div class="dato-row">
                            <span class="dato-label">🔢 Número de pago</span>
                            <span class="dato-value">Cuota #%d</span>
                        </div>
                        <div class="dato-row">
                            <span class="dato-label">📊 Saldo restante</span>
                            <span class="dato-value">%s</span>
                        </div>
                    </div>
                    %s
                    <p>Si tienes alguna duda, no dudes en contactarnos.</p>
                </div>
                <div class="footer">
                    <p>© 2026 Sistema de Préstamos. Todos los derechos reservados.</p>
                    <p>Este es un correo automático, por favor no respondas a este mensaje.</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(
                nombre,
                montoPagado.toPlainString(),
                fechaPago.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                numPago,
                saldoTexto,
                mensajeSaldo
        );
    }




}