package com.prestamos.sistema_prestamos.service;

import com.prestamos.sistema_prestamos.entity.Amortizacion;
import com.prestamos.sistema_prestamos.entity.Prestamo;
import com.prestamos.sistema_prestamos.repository.AmortizacionRepository;
import com.prestamos.sistema_prestamos.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final PrestamoRepository prestamoRepository;
    private final AmortizacionRepository amortizacionRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] generarTablaExcel(Long prestamoId) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        List<Amortizacion> amortizaciones = amortizacionRepository
                .findByPrestamoIdOrderByNumPago(prestamoId);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Amortización");
            sheet.setColumnWidth(0, 1500);
            sheet.setColumnWidth(1, 4000);
            sheet.setColumnWidth(2, 4000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 4000);
            sheet.setColumnWidth(5, 4500);
            sheet.setColumnWidth(6, 3500);

            int rowNum = 0;
            rowNum = agregarEncabezado(workbook, sheet, prestamo, rowNum);
            rowNum = agregarInfoPrestamo(workbook, sheet, prestamo, rowNum);
            rowNum = agregarTabla(workbook, sheet, amortizaciones, rowNum);
            agregarTotales(workbook, sheet, amortizaciones, rowNum);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel: " + e.getMessage());
        }
    }

    private int agregarEncabezado(XSSFWorkbook wb, XSSFSheet sheet, Prestamo prestamo, int rowNum) {
        // Título principal
        Row titleRow = sheet.createRow(rowNum++);
        titleRow.setHeight((short) 800);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("🏦 Sistema de Préstamos — Tabla de Amortización");
        titleCell.setCellStyle(estiloTitulo(wb));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 6));

        // Subtítulo
        Row subRow = sheet.createRow(rowNum++);
        Cell subCell = subRow.createCell(0);
        subCell.setCellValue("Préstamo #" + prestamo.getId() + " — " +
                prestamo.getCliente().getNombre() + " " + prestamo.getCliente().getApellido());
        subCell.setCellStyle(estiloSubtitulo(wb));
        sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 6));

        rowNum++; // espacio
        return rowNum;
    }

    private int agregarInfoPrestamo(XSSFWorkbook wb, XSSFSheet sheet, Prestamo prestamo, int rowNum) {
        String[][] info = {
                {"Cliente:", prestamo.getCliente().getNombre() + " " + prestamo.getCliente().getApellido(),
                        "RFC:", prestamo.getCliente().getRfc()},
                {"Monto:", "$" + prestamo.getMonto().toPlainString(),
                        "Tasa Anual:", prestamo.getTasaInteres() + "%"},
                {"No. Pagos:", prestamo.getNumPagos() + " meses",
                        "Fecha Inicio:", prestamo.getFechaInicio().format(FMT)},
                {"Estatus:", prestamo.getEstatus().name(), "", ""}
        };

        for (String[] fila : info) {
            Row row = sheet.createRow(rowNum++);
            Cell lbl1 = row.createCell(0); lbl1.setCellValue(fila[0]); lbl1.setCellStyle(estiloInfoLabel(wb));
            Cell val1 = row.createCell(1); val1.setCellValue(fila[1]); val1.setCellStyle(estiloInfoValue(wb));
            Cell lbl2 = row.createCell(3); lbl2.setCellValue(fila[2]); lbl2.setCellStyle(estiloInfoLabel(wb));
            Cell val2 = row.createCell(4); val2.setCellValue(fila[3]); val2.setCellStyle(estiloInfoValue(wb));
        }

        rowNum++; // espacio
        return rowNum;
    }

    private int agregarTabla(XSSFWorkbook wb, XSSFSheet sheet, List<Amortizacion> amortizaciones, int rowNum) {
        // Encabezados de tabla
        String[] headers = {"#", "Vencimiento", "Capital", "Interés", "Cuota", "Saldo Restante", "Estatus"};
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.setHeight((short) 500);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(estiloEncabezadoTabla(wb));
        }

        // Filas de datos
        for (Amortizacion a : amortizaciones) {
            Row row = sheet.createRow(rowNum++);
            row.setHeight((short) 400);
            boolean pagado = a.getEstatus() == Amortizacion.EstatusAmortizacion.PAGADO;

            Cell c0 = row.createCell(0); c0.setCellValue(a.getNumPago()); c0.setCellStyle(estiloCeldaCentro(wb, pagado));
            Cell c1 = row.createCell(1); c1.setCellValue(a.getFechaVencimiento().format(FMT)); c1.setCellStyle(estiloCeldaCentro(wb, pagado));
            Cell c2 = row.createCell(2); c2.setCellValue(a.getCapital().doubleValue()); c2.setCellStyle(estiloCeldaMoneda(wb, pagado));
            Cell c3 = row.createCell(3); c3.setCellValue(a.getInteres().doubleValue()); c3.setCellStyle(estiloCeldaMoneda(wb, pagado));
            Cell c4 = row.createCell(4); c4.setCellValue(a.getCuota().doubleValue()); c4.setCellStyle(estiloCeldaMonedaBold(wb, pagado));
            Cell c5 = row.createCell(5); c5.setCellValue(a.getSaldoRestante().doubleValue()); c5.setCellStyle(estiloCeldaMoneda(wb, pagado));
            Cell c6 = row.createCell(6); c6.setCellValue(a.getEstatus().name()); c6.setCellStyle(estiloCeldaCentro(wb, pagado));
        }

        return rowNum;
    }

    private void agregarTotales(XSSFWorkbook wb, XSSFSheet sheet, List<Amortizacion> amortizaciones, int rowNum) {
        BigDecimal totalCapital = amortizaciones.stream().map(Amortizacion::getCapital).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalInteres = amortizaciones.stream().map(Amortizacion::getInteres).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCuota  = amortizaciones.stream().map(Amortizacion::getCuota).reduce(BigDecimal.ZERO, BigDecimal::add);

        Row row = sheet.createRow(rowNum);
        row.setHeight((short) 500);

        Cell lbl = row.createCell(0); lbl.setCellValue("TOTAL"); lbl.setCellStyle(estiloTotal(wb));
        row.createCell(1).setCellStyle(estiloTotal(wb));
        Cell tc = row.createCell(2); tc.setCellValue(totalCapital.doubleValue()); tc.setCellStyle(estiloTotalMoneda(wb));
        Cell ti = row.createCell(3); ti.setCellValue(totalInteres.doubleValue()); ti.setCellStyle(estiloTotalMoneda(wb));
        Cell tcu = row.createCell(4); tcu.setCellValue(totalCuota.doubleValue()); tcu.setCellStyle(estiloTotalMoneda(wb));
        row.createCell(5).setCellStyle(estiloTotal(wb));
        row.createCell(6).setCellStyle(estiloTotal(wb));
    }

    // ── ESTILOS ──────────────────────────────────────────────

    private CellStyle estiloTitulo(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f = wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 16);
        f.setColor(new XSSFColor(new byte[]{(byte)30, (byte)64, (byte)175}, null));
        s.setFont(f);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        return s;
    }

    private CellStyle estiloSubtitulo(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f = wb.createFont();
        f.setFontHeightInPoints((short) 11);
        f.setColor(new XSSFColor(new byte[]{(byte)100, (byte)116, (byte)139}, null));
        s.setFont(f);
        s.setAlignment(HorizontalAlignment.CENTER);
        return s;
    }

    private CellStyle estiloInfoLabel(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f = wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 10);
        f.setColor(new XSSFColor(new byte[]{(byte)100, (byte)116, (byte)139}, null));
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)248, (byte)250, (byte)252}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setBorderBottom(BorderStyle.THIN);
        //s.setBottomBorderColor(new XSSFColor(new byte[]{(byte)226, (byte)232, (byte)240}, null));
        s.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex()); // Use indexed color instead

        return s;
    }

    private CellStyle estiloInfoValue(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f = wb.createFont();
        f.setFontHeightInPoints((short) 10);
        s.setFont(f);
        s.setBorderBottom(BorderStyle.THIN);
        //s.setBottomBorderColor(new XSSFColor(new byte[]{(byte)226, (byte)232, (byte)240}, null));
        s.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex()); // Use indexed color instead

        return s;
    }

    private CellStyle estiloEncabezadoTabla(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f = wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 10); f.setColor(IndexedColors.WHITE.getIndex());
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)30, (byte)64, (byte)175}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        s.setVerticalAlignment(VerticalAlignment.CENTER);
        setBordes(s, wb);
        return s;
    }

    private CellStyle estiloCeldaCentro(XSSFWorkbook wb, boolean pagado) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f = wb.createFont(); f.setFontHeightInPoints((short) 9); s.setFont(f);
        s.setAlignment(HorizontalAlignment.CENTER);
        if (pagado) setFondoPagado(s, wb); else setFondoNormal(s, wb);
        setBordes(s, wb);
        return s;
    }

    private CellStyle estiloCeldaMoneda(XSSFWorkbook wb, boolean pagado) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f = wb.createFont(); f.setFontHeightInPoints((short) 9); s.setFont(f);
        s.setDataFormat(wb.createDataFormat().getFormat("$#,##0.00"));
        s.setAlignment(HorizontalAlignment.RIGHT);
        if (pagado) setFondoPagado(s, wb); else setFondoNormal(s, wb);
        setBordes(s, wb);
        return s;
    }

    private CellStyle estiloCeldaMonedaBold(XSSFWorkbook wb, boolean pagado) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f = wb.createFont(); f.setBold(true); f.setFontHeightInPoints((short) 9); s.setFont(f);
        s.setDataFormat(wb.createDataFormat().getFormat("$#,##0.00"));
        s.setAlignment(HorizontalAlignment.RIGHT);
        if (pagado) setFondoPagado(s, wb); else setFondoNormal(s, wb);
        setBordes(s, wb);
        return s;
    }

    private CellStyle estiloTotal(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f = wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 10);
        f.setColor(new XSSFColor(new byte[]{(byte)30, (byte)64, (byte)175}, null));
        s.setFont(f);
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)239, (byte)246, (byte)255}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        s.setAlignment(HorizontalAlignment.CENTER);
        setBordes(s, wb);
        return s;
    }

    private CellStyle estiloTotalMoneda(XSSFWorkbook wb) {
        CellStyle s = wb.createCellStyle();
        XSSFFont f = wb.createFont();
        f.setBold(true); f.setFontHeightInPoints((short) 10);
        f.setColor(new XSSFColor(new byte[]{(byte)30, (byte)64, (byte)175}, null));
        s.setFont(f);
        s.setDataFormat(wb.createDataFormat().getFormat("$#,##0.00"));
        s.setAlignment(HorizontalAlignment.RIGHT);
        s.setFillForegroundColor(new XSSFColor(new byte[]{(byte)239, (byte)246, (byte)255}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        setBordes(s, wb);
        return s;
    }

    private void setFondoPagado(CellStyle s, XSSFWorkbook wb) {
        ((XSSFCellStyle)s).setFillForegroundColor(new XSSFColor(new byte[]{(byte)220, (byte)252, (byte)231}, null));
        s.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    private void setFondoNormal(CellStyle s, XSSFWorkbook wb) {}

    /*
    private void setBordes(CellStyle s, XSSFWorkbook wb) {
        s.setBorderTop(BorderStyle.THIN); s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN); s.setBorderRight(BorderStyle.THIN);
        XSSFColor gris = new XSSFColor(new byte[]{(byte)226, (byte)232, (byte)240}, null);
        ((XSSFCellStyle)s).setTopBorderColor(gris); ((XSSFCellStyle)s).setBottomBorderColor(gris);
        ((XSSFCellStyle)s).setLeftBorderColor(gris); ((XSSFCellStyle)s).setRightBorderColor(gris);
    }

    */

    private void setBordes(CellStyle s, XSSFWorkbook wb) {
        s.setBorderTop(BorderStyle.THIN);
        s.setBorderBottom(BorderStyle.THIN);
        s.setBorderLeft(BorderStyle.THIN);
        s.setBorderRight(BorderStyle.THIN);
    }
}