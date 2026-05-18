package com.prestamos.sistema_prestamos.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.prestamos.sistema_prestamos.dto.PrestamoRequestDTO;
import com.prestamos.sistema_prestamos.entity.Cliente;
import com.prestamos.sistema_prestamos.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudPdfService {

    private final ClienteRepository clienteRepository;

    private static final Font FONT_TITLE    = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD,   new BaseColor(30, 64, 175));
    private static final Font FONT_SUBTITLE = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, new BaseColor(100, 116, 139));
    private static final Font FONT_SECTION  = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD,   new BaseColor(30, 64, 175));
    private static final Font FONT_LABEL    = new Font(Font.FontFamily.HELVETICA,  9, Font.BOLD,   new BaseColor(100, 116, 139));
    private static final Font FONT_VALUE    = new Font(Font.FontFamily.HELVETICA,  9, Font.NORMAL, new BaseColor(15, 23, 42));
    private static final Font FONT_HEADER   = new Font(Font.FontFamily.HELVETICA,  9, Font.BOLD,   BaseColor.WHITE);
    private static final Font FONT_CELL     = new Font(Font.FontFamily.HELVETICA,  8, Font.NORMAL, new BaseColor(51, 65, 85));
    private static final Font FONT_FIRMA    = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD,   new BaseColor(15, 23, 42));
    private static final Font FONT_FIRMA_SUB= new Font(Font.FontFamily.HELVETICA,  8, Font.NORMAL, new BaseColor(100, 116, 139));
    private static final Font FONT_LEGAL    = new Font(Font.FontFamily.HELVETICA,  8, Font.ITALIC, new BaseColor(100, 116, 139));

    private static final BaseColor COLOR_PRIMARY = new BaseColor(30, 64, 175);
    private static final BaseColor COLOR_ROW_ALT = new BaseColor(241, 245, 249);
    private static final DateTimeFormatter FMT    = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generarSolicitud(PrestamoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            agregarEncabezado(doc);
            agregarDatosCliente(doc, cliente);
            agregarDatosPrestamo(doc, dto);
            agregarTablaAmortizacion(doc, dto);
            agregarTerminos(doc);
            agregarFirmas(doc, cliente);
            agregarPie(doc);

            doc.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar solicitud: " + e.getMessage());
        }
    }

    private void agregarEncabezado(Document doc) throws DocumentException {
        // Línea azul
        PdfPTable linea = new PdfPTable(1);
        linea.setWidthPercentage(100);
        PdfPCell cel = new PdfPCell();
        cel.setBackgroundColor(COLOR_PRIMARY);
        cel.setFixedHeight(6);
        cel.setBorder(Rectangle.NO_BORDER);
        linea.addCell(cel);
        doc.add(linea);
        doc.add(Chunk.NEWLINE);

        Paragraph titulo = new Paragraph("🏦 Sistema de Préstamos", FONT_TITLE);
        titulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(titulo);

        Paragraph subtitulo = new Paragraph("SOLICITUD DE CRÉDITO", FONT_SUBTITLE);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(4);
        doc.add(subtitulo);

        Paragraph folio = new Paragraph("Fecha: " + LocalDate.now().format(FMT), FONT_SUBTITLE);
        folio.setAlignment(Element.ALIGN_CENTER);
        folio.setSpacingAfter(20);
        doc.add(folio);
    }

    private void agregarDatosCliente(Document doc, Cliente cliente) throws DocumentException {
        Paragraph seccion = new Paragraph("DATOS DEL SOLICITANTE", FONT_SECTION);
        seccion.setSpacingBefore(10);
        seccion.setSpacingAfter(8);
        doc.add(seccion);

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1.5f, 2.5f, 1.5f, 2.5f});
        tabla.setSpacingAfter(15);

        agregarCeldaInfo(tabla, "Nombre:", cliente.getNombre() + " " + cliente.getApellido());
        agregarCeldaInfo(tabla, "RFC:", cliente.getRfc());
        agregarCeldaInfo(tabla, "Email:", cliente.getEmail());
        agregarCeldaInfo(tabla, "Teléfono:", cliente.getTelefono());

        doc.add(tabla);
    }

    private void agregarDatosPrestamo(Document doc, PrestamoRequestDTO dto) throws DocumentException {
        Paragraph seccion = new Paragraph("CONDICIONES DEL CRÉDITO", FONT_SECTION);
        seccion.setSpacingBefore(5);
        seccion.setSpacingAfter(8);
        doc.add(seccion);

        BigDecimal tasaMensual = dto.getTasaInteres()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        BigDecimal unoPlusI = BigDecimal.ONE.add(tasaMensual);
        BigDecimal unoPlusIPowN = unoPlusI.pow(dto.getNumPagos(), new MathContext(10));
        BigDecimal cuota = dto.getMonto()
                .multiply(tasaMensual.multiply(unoPlusIPowN))
                .divide(unoPlusIPowN.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        BigDecimal totalPagar = cuota.multiply(BigDecimal.valueOf(dto.getNumPagos()));
        BigDecimal totalInteres = totalPagar.subtract(dto.getMonto());

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1.5f, 2f, 1.5f, 2f});
        tabla.setSpacingAfter(15);

        agregarCeldaInfo(tabla, "Monto Solicitado:", "$" + dto.getMonto().toPlainString());
        agregarCeldaInfo(tabla, "Tasa Anual:", dto.getTasaInteres() + "%");
        agregarCeldaInfo(tabla, "Plazo:", dto.getNumPagos() + " meses");
        agregarCeldaInfo(tabla, "Fecha Inicio:", dto.getFechaInicio().format(FMT));
        agregarCeldaInfo(tabla, "Cuota Mensual:", "$" + cuota.toPlainString());
        agregarCeldaInfo(tabla, "Total Intereses:", "$" + totalInteres.toPlainString());
        agregarCeldaInfo(tabla, "Total a Pagar:", "$" + totalPagar.toPlainString());
        agregarCeldaInfo(tabla, "Tasa Mensual:", tasaMensual.multiply(BigDecimal.valueOf(100)).setScale(4, RoundingMode.HALF_UP) + "%");

        doc.add(tabla);
    }

    private void agregarTablaAmortizacion(Document doc, PrestamoRequestDTO dto) throws DocumentException {
        Paragraph seccion = new Paragraph("TABLA DE AMORTIZACIÓN", FONT_SECTION);
        seccion.setSpacingBefore(5);
        seccion.setSpacingAfter(8);
        doc.add(seccion);

        PdfPTable tabla = new PdfPTable(6);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{0.6f, 1.5f, 1.3f, 1.3f, 1.3f, 1.5f});
        tabla.setSpacingAfter(15);

        String[] headers = {"#", "Vencimiento", "Capital", "Interés", "Cuota", "Saldo"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, FONT_HEADER));
            cell.setBackgroundColor(COLOR_PRIMARY);
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            tabla.addCell(cell);
        }

        List<BigDecimal[]> filas = calcularAmortizacion(dto);
        for (int i = 0; i < filas.size(); i++) {
            BigDecimal[] fila = filas.get(i);
            BaseColor bg = i % 2 == 0 ? BaseColor.WHITE : COLOR_ROW_ALT;
            LocalDate vencimiento = dto.getFechaInicio().plusMonths(i + 1);

            agregarCeldaTabla(tabla, String.valueOf(i + 1),   bg, Element.ALIGN_CENTER);
            agregarCeldaTabla(tabla, vencimiento.format(FMT), bg, Element.ALIGN_CENTER);
            agregarCeldaTabla(tabla, "$" + fila[0].toPlainString(), bg, Element.ALIGN_RIGHT);
            agregarCeldaTabla(tabla, "$" + fila[1].toPlainString(), bg, Element.ALIGN_RIGHT);
            agregarCeldaTabla(tabla, "$" + fila[2].toPlainString(), bg, Element.ALIGN_RIGHT);
            agregarCeldaTabla(tabla, "$" + fila[3].toPlainString(), bg, Element.ALIGN_RIGHT);
        }

        doc.add(tabla);
    }

    private void agregarTerminos(Document doc) throws DocumentException {
        Paragraph seccion = new Paragraph("TÉRMINOS Y CONDICIONES", FONT_SECTION);
        seccion.setSpacingBefore(5);
        seccion.setSpacingAfter(8);
        doc.add(seccion);

        String terminos = "El solicitante declara que la información proporcionada es verídica y acepta " +
                "las condiciones del crédito establecidas en este documento. El incumplimiento en los " +
                "pagos generará cargos adicionales conforme a la tasa de interés moratorio vigente. " +
                "Este documento tiene validez legal una vez firmado por ambas partes.";

        Paragraph p = new Paragraph(terminos, FONT_LEGAL);
        p.setSpacingAfter(15);
        doc.add(p);
    }

    private void agregarFirmas(Document doc, Cliente cliente) throws DocumentException {
        Paragraph seccion = new Paragraph("FIRMAS", FONT_SECTION);
        seccion.setSpacingBefore(10);
        seccion.setSpacingAfter(20);
        doc.add(seccion);

        PdfPTable firmas = new PdfPTable(3);
        firmas.setWidthPercentage(100);
        firmas.setWidths(new float[]{2f, 0.5f, 2f});
        firmas.setSpacingAfter(10);

        // Firma cliente
        PdfPCell firmaCliente = new PdfPCell();
        firmaCliente.setBorder(Rectangle.NO_BORDER);
        firmaCliente.setPadding(10);

        Paragraph lineaFirma = new Paragraph("_________________________________", FONT_FIRMA);
        lineaFirma.setAlignment(Element.ALIGN_CENTER);
        firmaCliente.addElement(lineaFirma);

        Paragraph nombreCliente = new Paragraph(cliente.getNombre() + " " + cliente.getApellido(), FONT_FIRMA);
        nombreCliente.setAlignment(Element.ALIGN_CENTER);
        firmaCliente.addElement(nombreCliente);

        Paragraph rfcCliente = new Paragraph("RFC: " + cliente.getRfc(), FONT_FIRMA_SUB);
        rfcCliente.setAlignment(Element.ALIGN_CENTER);
        firmaCliente.addElement(rfcCliente);

        Paragraph rolCliente = new Paragraph("SOLICITANTE", FONT_FIRMA_SUB);
        rolCliente.setAlignment(Element.ALIGN_CENTER);
        firmaCliente.addElement(rolCliente);

        // Separador
        PdfPCell separador = new PdfPCell();
        separador.setBorder(Rectangle.NO_BORDER);

        // Firma institución
        PdfPCell firmaInstitucion = new PdfPCell();
        firmaInstitucion.setBorder(Rectangle.NO_BORDER);
        firmaInstitucion.setPadding(10);

        Paragraph lineaFirma2 = new Paragraph("_________________________________", FONT_FIRMA);
        lineaFirma2.setAlignment(Element.ALIGN_CENTER);
        firmaInstitucion.addElement(lineaFirma2);

        Paragraph nombreInstitucion = new Paragraph("Autorizado por", FONT_FIRMA);
        nombreInstitucion.setAlignment(Element.ALIGN_CENTER);
        firmaInstitucion.addElement(nombreInstitucion);

        Paragraph rolInstitucion = new Paragraph("SISTEMA DE PRÉSTAMOS", FONT_FIRMA_SUB);
        rolInstitucion.setAlignment(Element.ALIGN_CENTER);
        firmaInstitucion.addElement(rolInstitucion);

        Paragraph fechaFirma = new Paragraph("Fecha: ___________________", FONT_FIRMA_SUB);
        fechaFirma.setAlignment(Element.ALIGN_CENTER);
        firmaInstitucion.addElement(fechaFirma);

        firmas.addCell(firmaCliente);
        firmas.addCell(separador);
        firmas.addCell(firmaInstitucion);
        doc.add(firmas);
    }

    private void agregarPie(Document doc) throws DocumentException {
        Paragraph pie = new Paragraph(
                "Documento generado el " + LocalDate.now().format(FMT) +
                        " — Sistema de Préstamos • Documento confidencial",
                new Font(Font.FontFamily.HELVETICA, 7, Font.ITALIC, new BaseColor(148, 163, 184))
        );
        pie.setAlignment(Element.ALIGN_CENTER);
        doc.add(pie);
    }

    private List<BigDecimal[]> calcularAmortizacion(PrestamoRequestDTO dto) {
        BigDecimal monto = dto.getMonto();
        BigDecimal tasaMensual = dto.getTasaInteres()
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);
        int n = dto.getNumPagos();

        BigDecimal unoPlusI = BigDecimal.ONE.add(tasaMensual);
        BigDecimal unoPlusIPowN = unoPlusI.pow(n, new MathContext(10));
        BigDecimal cuota = monto
                .multiply(tasaMensual.multiply(unoPlusIPowN))
                .divide(unoPlusIPowN.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);

        BigDecimal saldo = monto;
        List<BigDecimal[]> filas = new ArrayList<>();

        for (int i = 1; i <= n; i++) {
            BigDecimal interes = saldo.multiply(tasaMensual).setScale(2, RoundingMode.HALF_UP);
            BigDecimal capital = cuota.subtract(interes).setScale(2, RoundingMode.HALF_UP);
            if (i == n) { capital = saldo; cuota = capital.add(interes); }
            saldo = saldo.subtract(capital).setScale(2, RoundingMode.HALF_UP);
            filas.add(new BigDecimal[]{capital, interes, cuota, saldo});
        }
        return filas;
    }

    private void agregarCeldaInfo(PdfPTable tabla, String label, String value) {
        PdfPCell lbl = new PdfPCell(new Phrase(label, FONT_LABEL));
        lbl.setBorder(Rectangle.NO_BORDER);
        lbl.setPadding(4);
        lbl.setBackgroundColor(new BaseColor(248, 250, 252));
        tabla.addCell(lbl);

        PdfPCell val = new PdfPCell(new Phrase(value, FONT_VALUE));
        val.setBorder(Rectangle.NO_BORDER);
        val.setPadding(4);
        tabla.addCell(val);
    }

    private void agregarCeldaTabla(PdfPTable tabla, String texto, BaseColor color, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, FONT_CELL));
        cell.setBackgroundColor(color);
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderWidth(0.3f);
        tabla.addCell(cell);
    }
}