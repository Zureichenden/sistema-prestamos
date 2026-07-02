package com.prestamos.sistema_prestamos.modules.prestamos.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.prestamos.sistema_prestamos.modules.prestamos.entity.Amortizacion;
import com.prestamos.sistema_prestamos.modules.prestamos.entity.Prestamo;
import com.prestamos.sistema_prestamos.modules.prestamos.repository.AmortizacionRepository;
import com.prestamos.sistema_prestamos.modules.prestamos.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final PrestamoRepository prestamoRepository;
    private final AmortizacionRepository amortizacionRepository;

    private static final Font FONT_TITLE = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(30, 64, 175));
    private static final Font FONT_SUBTITLE = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, new BaseColor(100, 116, 139));
    private static final Font FONT_HEADER = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);
    private static final Font FONT_CELL = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, new BaseColor(51, 65, 85));
    private static final Font FONT_CELL_BOLD = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, new BaseColor(51, 65, 85));
    private static final Font FONT_INFO_LABEL = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, new BaseColor(100, 116, 139));
    private static final Font FONT_INFO_VALUE = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, new BaseColor(15, 23, 42));

    private static final BaseColor COLOR_PRIMARY = new BaseColor(30, 64, 175);
    private static final BaseColor COLOR_ROW_ALT = new BaseColor(241, 245, 249);
    private static final BaseColor COLOR_PAID = new BaseColor(220, 252, 231);
    private static final BaseColor COLOR_PENDING = new BaseColor(254, 249, 195);
    private static final BaseColor COLOR_TOTAL = new BaseColor(239, 246, 255);

    public byte[] generarTablaPdf(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        List<Amortizacion> amortizaciones = amortizacionRepository
                .findByPrestamoIdOrderByNumPago(prestamoId);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            agregarEncabezado(document, prestamo);
            agregarInfoPrestamo(document, prestamo);
            agregarTabla(document, amortizaciones);
            agregarTotales(document, amortizaciones);
            agregarPie(document);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage());
        }
    }

    private void agregarEncabezado(Document doc, Prestamo prestamo) throws DocumentException {
        // Línea azul superior
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        PdfPCell lineaAzul = new PdfPCell();
        lineaAzul.setBackgroundColor(COLOR_PRIMARY);
        lineaAzul.setFixedHeight(6);
        lineaAzul.setBorder(Rectangle.NO_BORDER);
        header.addCell(lineaAzul);
        doc.add(header);
        doc.add(Chunk.NEWLINE);

        // Título
        Paragraph titulo = new Paragraph("🏦 Sistema de Préstamos", FONT_TITLE);
        titulo.setAlignment(Element.ALIGN_CENTER);
        doc.add(titulo);

        Paragraph subtitulo = new Paragraph("Tabla de Amortización", FONT_SUBTITLE);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(20);
        doc.add(subtitulo);
    }

    private void agregarInfoPrestamo(Document doc, Prestamo prestamo) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(4);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{1.5f, 2f, 1.5f, 2f});
        infoTable.setSpacingAfter(20);

        String cliente = prestamo.getCliente().getNombre() + " " + prestamo.getCliente().getApellido();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        agregarCeldaInfo(infoTable, "Cliente:", cliente);
        agregarCeldaInfo(infoTable, "Préstamo #:", String.valueOf(prestamo.getId()));
        agregarCeldaInfo(infoTable, "Monto:", "$" + prestamo.getMonto().toPlainString());
        agregarCeldaInfo(infoTable, "Tasa Anual:", prestamo.getTasaInteres() + "%");
        agregarCeldaInfo(infoTable, "No. Pagos:", prestamo.getNumPagos() + " meses");
        agregarCeldaInfo(infoTable, "Fecha Inicio:", prestamo.getFechaInicio().format(fmt));
        agregarCeldaInfo(infoTable, "Estatus:", prestamo.getEstatus().name());
        agregarCeldaInfo(infoTable, "RFC:", prestamo.getCliente().getRfc());

        doc.add(infoTable);
    }

    private void agregarCeldaInfo(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, FONT_INFO_LABEL));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(4);
        labelCell.setBackgroundColor(new BaseColor(248, 250, 252));
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, FONT_INFO_VALUE));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(4);
        table.addCell(valueCell);
    }

    private void agregarTabla(Document doc, List<Amortizacion> amortizaciones) throws DocumentException {
        PdfPTable tabla = new PdfPTable(7);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{0.6f, 1.4f, 1.3f, 1.3f, 1.3f, 1.5f, 1.2f});
        tabla.setSpacingAfter(10);

        // Encabezados
        String[] headers = {"#", "Vencimiento", "Capital", "Interés", "Cuota", "Saldo", "Estatus"};
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, FONT_HEADER));
            cell.setBackgroundColor(COLOR_PRIMARY);
            cell.setPadding(7);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            tabla.addCell(cell);
        }

        // Filas
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (int i = 0; i < amortizaciones.size(); i++) {
            Amortizacion a = amortizaciones.get(i);
            boolean esPagado = a.getEstatus() == Amortizacion.EstatusAmortizacion.PAGADO;
            BaseColor rowColor = esPagado ? COLOR_PAID : (i % 2 == 0 ? BaseColor.WHITE : COLOR_ROW_ALT);

            agregarCeldaTabla(tabla, String.valueOf(a.getNumPago()), rowColor, FONT_CELL_BOLD, Element.ALIGN_CENTER);
            agregarCeldaTabla(tabla, a.getFechaVencimiento().format(fmt), rowColor, FONT_CELL, Element.ALIGN_CENTER);
            agregarCeldaTabla(tabla, "$" + a.getCapital().toPlainString(), rowColor, FONT_CELL, Element.ALIGN_RIGHT);
            agregarCeldaTabla(tabla, "$" + a.getInteres().toPlainString(), rowColor, FONT_CELL, Element.ALIGN_RIGHT);
            agregarCeldaTabla(tabla, "$" + a.getCuota().toPlainString(), rowColor, FONT_CELL_BOLD, Element.ALIGN_RIGHT);
            agregarCeldaTabla(tabla, "$" + a.getSaldoRestante().toPlainString(), rowColor, FONT_CELL, Element.ALIGN_RIGHT);
            agregarCeldaTabla(tabla, a.getEstatus().name(), esPagado ? COLOR_PAID : COLOR_PENDING, FONT_CELL, Element.ALIGN_CENTER);
        }

        doc.add(tabla);
    }

    private void agregarCeldaTabla(PdfPTable tabla, String texto, BaseColor color, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setBackgroundColor(color);
        cell.setPadding(6);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderColor(new BaseColor(226, 232, 240));
        cell.setBorderWidth(0.5f);
        tabla.addCell(cell);
    }

    private void agregarTotales(Document doc, List<Amortizacion> amortizaciones) throws DocumentException {
        BigDecimal totalCapital = amortizaciones.stream()
                .map(Amortizacion::getCapital)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalInteres = amortizaciones.stream()
                .map(Amortizacion::getInteres)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCuota = amortizaciones.stream()
                .map(Amortizacion::getCuota)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PdfPTable totales = new PdfPTable(7);
        totales.setWidthPercentage(100);
        totales.setWidths(new float[]{0.6f, 1.4f, 1.3f, 1.3f, 1.3f, 1.5f, 1.2f});
        totales.setSpacingAfter(20);

        Font fontTotal = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, COLOR_PRIMARY);

        agregarCeldaTabla(totales, "TOTAL", COLOR_TOTAL, fontTotal, Element.ALIGN_CENTER);
        agregarCeldaTabla(totales, "", COLOR_TOTAL, fontTotal, Element.ALIGN_CENTER);
        agregarCeldaTabla(totales, "$" + totalCapital.setScale(2).toPlainString(), COLOR_TOTAL, fontTotal, Element.ALIGN_RIGHT);
        agregarCeldaTabla(totales, "$" + totalInteres.setScale(2).toPlainString(), COLOR_TOTAL, fontTotal, Element.ALIGN_RIGHT);
        agregarCeldaTabla(totales, "$" + totalCuota.setScale(2).toPlainString(), COLOR_TOTAL, fontTotal, Element.ALIGN_RIGHT);
        agregarCeldaTabla(totales, "", COLOR_TOTAL, fontTotal, Element.ALIGN_CENTER);
        agregarCeldaTabla(totales, "", COLOR_TOTAL, fontTotal, Element.ALIGN_CENTER);

        doc.add(totales);
    }

    private void agregarPie(Document doc) throws DocumentException {
        Paragraph pie = new Paragraph(
                "Documento generado automáticamente por Sistema de Préstamos • " +
                        java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, new BaseColor(148, 163, 184))
        );
        pie.setAlignment(Element.ALIGN_CENTER);
        doc.add(pie);
    }
}