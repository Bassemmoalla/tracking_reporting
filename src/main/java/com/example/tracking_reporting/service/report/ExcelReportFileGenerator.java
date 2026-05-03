package com.example.tracking_reporting.service.report;

import com.example.tracking_reporting.dto.report.ReportDataBundle;
import com.example.tracking_reporting.dto.report.ReportMetricRow;
import com.example.tracking_reporting.enums.DocumentFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class ExcelReportFileGenerator implements ReportFileGenerator {

    @Override
    public DocumentFormat supports() {
        return DocumentFormat.EXCEL;
    }

    @Override
    public GeneratedBinaryFile generate(String title, ReportDataBundle dataBundle) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Summary");
            int rowIndex = 0;

            Row r0 = sheet.createRow(rowIndex++);
            r0.createCell(0).setCellValue("Title");
            r0.createCell(1).setCellValue(title);

            Row r1 = sheet.createRow(rowIndex++);
            r1.createCell(0).setCellValue("Scope");
            r1.createCell(1).setCellValue(dataBundle.scopeLabel());

            Row r2 = sheet.createRow(rowIndex++);
            r2.createCell(0).setCellValue("Project");
            r2.createCell(1).setCellValue(dataBundle.projectName());

            Row r3 = sheet.createRow(rowIndex++);
            r3.createCell(0).setCellValue("Iteration");
            r3.createCell(1).setCellValue(dataBundle.iterationName());

            Row r4 = sheet.createRow(rowIndex++);
            r4.createCell(0).setCellValue("Period");
            r4.createCell(1).setCellValue(dataBundle.periodLabel());

            rowIndex++;
            Row header = sheet.createRow(rowIndex++);
            header.createCell(0).setCellValue("Metric");
            header.createCell(1).setCellValue("Value");

            for (ReportMetricRow metric : dataBundle.metrics()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(metric.metric());
                row.createCell(1).setCellValue(metric.value());
            }

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            workbook.write(outputStream);

            return new GeneratedBinaryFile(
                    sanitize(title) + ".xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    outputStream.toByteArray()
            );
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate Excel report", ex);
        }
    }

    private String sanitize(String input) {
        return input.toLowerCase().replaceAll("[^a-z0-9-_]+", "-");
    }
}