package com.sochka.onlinegamestore.infrastructure;

import com.sochka.onlinegamestore.dto.OrderDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * High-fidelity implementation driving programmatic Excel assembly exploiting modern .xlsx matrix capabilities.
 */
@Component
public class ExcelReportGenerator implements ReportGenerator {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Override
    public void generateOrderReceipt(OrderDTO order, OutputStream output) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Receipt_" + order.getOrderId().toString().substring(0, 8));

            // 1. Define distinct visual styling primitives
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerStyle.setFont(headerFont);

            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            CellStyle borderedStyle = workbook.createCellStyle();
            borderedStyle.setBorderBottom(BorderStyle.THIN);
            borderedStyle.setBorderTop(BorderStyle.THIN);
            borderedStyle.setBorderRight(BorderStyle.THIN);
            borderedStyle.setBorderLeft(BorderStyle.THIN);

            // 2. Construct Branded Document Header
            int rowIdx = 0;
            Row r1 = sheet.createRow(rowIdx++);
            Cell titleCell = r1.createCell(0);
            titleCell.setCellValue("OFFICIAL DIGITAL PURCHASE RECEIPT");
            titleCell.setCellStyle(headerStyle);

            sheet.createRow(rowIdx++); // empty buffer row

            // 3. Populate Detailed Attribute Mapping
            writeRow(sheet, rowIdx++, boldStyle, "Game Title:", order.getGameTitle());
            writeRow(sheet, rowIdx++, boldStyle, "Total Transaction Price:", "$" + order.getPrice().toPlainString());
            writeRow(sheet, rowIdx++, boldStyle, "Purchasing Operator Email:", order.getUserEmail());
            writeRow(sheet, rowIdx++, boldStyle, "Temporal Stamp:", order.getPurchaseDate() != null ? order.getPurchaseDate().format(TIME_FMT) : "N/A");
            
            sheet.createRow(rowIdx++); // gap

            // 4. Critical Key Placement
            Row keyRow = sheet.createRow(rowIdx++);
            Cell keyHeader = keyRow.createCell(0);
            keyHeader.setCellValue("ACTIVATION KEY:");
            keyHeader.setCellStyle(boldStyle);
            
            Cell keyValue = keyRow.createCell(1);
            keyValue.setCellValue(order.getActivationKey());
            keyValue.setCellStyle(borderedStyle); // Highlight it visibly

            sheet.createRow(rowIdx++);
            sheet.createRow(rowIdx++).createCell(0).setCellValue("Thank you for utilizing our digital asset gateway. Retain this document for audit cycles.");

            // Finalize viewport columns
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(output);
        }
    }

    @Override
    public void exportOrderHistory(List<OrderDTO> orders, OutputStream output) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Operational Ledger");

            // Setup structural header
            String[] columnHeaders = {"Order ID", "Timestamp", "User Account", "Product Title", "Total Price ($)", "Activation Token"};
            
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columnHeaders.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(columnHeaders[i]);
                c.setCellStyle(headerStyle);
            }

            // Populate Grid Matrix
            int rowNum = 1;
            for (OrderDTO ord : orders) {
                Row r = sheet.createRow(rowNum++);
                r.createCell(0).setCellValue(ord.getOrderId().toString());
                r.createCell(1).setCellValue(ord.getPurchaseDate() != null ? ord.getPurchaseDate().format(TIME_FMT) : "N/A");
                r.createCell(2).setCellValue(ord.getUserEmail());
                r.createCell(3).setCellValue(ord.getGameTitle());
                r.createCell(4).setCellValue(ord.getPrice().doubleValue());
                r.createCell(5).setCellValue(ord.getActivationKey());
            }

            // Perform runtime dimension computation for best fit
            for (int i = 0; i < columnHeaders.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(output);
        }
    }

    private void writeRow(Sheet sheet, int rowIndex, CellStyle boldStyle, String label, String value) {
        Row r = sheet.createRow(rowIndex);
        Cell l = r.createCell(0);
        l.setCellValue(label);
        l.setCellStyle(boldStyle);
        r.createCell(1).setCellValue(value);
    }
}
