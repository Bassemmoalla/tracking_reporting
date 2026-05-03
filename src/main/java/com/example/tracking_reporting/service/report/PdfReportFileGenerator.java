package com.example.tracking_reporting.service.report;

import com.example.tracking_reporting.dto.report.ReportDataBundle;
import com.example.tracking_reporting.dto.report.ReportMetricRow;
import com.example.tracking_reporting.enums.DocumentFormat;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class PdfReportFileGenerator implements ReportFileGenerator {

    @Override
    public DocumentFormat supports() {
        return DocumentFormat.PDF;
    }

    @Override
    public GeneratedBinaryFile generate(String title, ReportDataBundle dataBundle) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph(title));
            document.add(new Paragraph("Scope: " + dataBundle.scopeLabel()));
            document.add(new Paragraph("Project: " + dataBundle.projectName()));
            document.add(new Paragraph("Iteration: " + dataBundle.iterationName()));
            document.add(new Paragraph("Period: " + dataBundle.periodLabel()));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.addCell("Metric");
            table.addCell("Value");

            for (ReportMetricRow metric : dataBundle.metrics()) {
                table.addCell(metric.metric());
                table.addCell(metric.value());
            }

            document.add(table);
            document.close();

            return new GeneratedBinaryFile(
                    sanitize(title) + ".pdf",
                    "application/pdf",
                    outputStream.toByteArray()
            );
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate PDF report", ex);
        }
    }

    private String sanitize(String input) {
        return input.toLowerCase().replaceAll("[^a-z0-9-_]+", "-");
    }
}