package com.zindarna.orderservice.service;

import com.zindarna.orderservice.exception.ResourceNotFoundException;
import com.zindarna.orderservice.model.Invoice;
import com.zindarna.orderservice.model.Order;
import com.zindarna.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.awt.Color;

@Service
public class InvoiceService {

    private final OrderRepository orderRepository;

    public InvoiceService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Get an invoice using its order ID
    public Invoice getInvoiceByOrderId(String orderId) {

        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID is required");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with ID: " + orderId
                        )
                );

        if (order.getInvoice() == null) {
            throw new ResourceNotFoundException(
                    "Invoice not found for order: " + orderId
            );
        }

        return order.getInvoice();
    }

    // Generate an invoice PDF using the order ID

    public byte[] generateInvoicePdf(String orderId) throws IOException {

        // 1. Validate the order ID
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("Order ID is required");
        }

        // 2. Retrieve the order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with ID: " + orderId
                        )
                );

        // 3. Retrieve the invoice
        Invoice invoice = order.getInvoice();

        if (invoice == null) {
            throw new ResourceNotFoundException(
                    "Invoice not found for order: " + orderId
            );
        }

        // Keep the rest of your existing PDF generation code here...

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDType1Font regularFont =
                    new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            PDType1Font boldFont =
                    new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            Color darkBlue = new Color(33, 75, 96);
            Color lightGray = new Color(245, 246, 247);

            try (PDPageContentStream content =
                         new PDPageContentStream(document, page)) {

                // 1. HEADER
                drawRectangle(content, 0, 690, 612, 102, darkBlue);

                writeText(content, boldFont, 28, 50, 735,
                        "INVOICE", Color.WHITE);

                writeText(content, boldFont, 17, 390, 740,
                        "ZIN DARNA", Color.WHITE);

                writeText(content, regularFont, 9, 390, 722,
                        "Interior Design & Renovation", Color.WHITE);

                // 2. BILL TO / BILL FROM
                writeText(content, boldFont, 11, 50, 655,
                        "BILL TO:", darkBlue);

                writeText(content, regularFont, 10, 50, 635,
                        "Customer ID: " + order.getUserId(), Color.BLACK);

                writeText(content, boldFont, 11, 290, 655,
                        "BILL FROM:", darkBlue);

                writeText(content, regularFont, 10, 290, 635,
                        "ZIN DARNA", Color.BLACK);

                // 3. INVOICE DETAILS
                writeText(content, boldFont, 11, 430, 655,
                        "Invoice Number:", darkBlue);

// Display the invoice number on two lines
                String invoiceNumber = invoice.getInvoiceNumber();

                if (invoiceNumber != null && invoiceNumber.length() > 25) {

                    writeText(content, regularFont, 7, 430, 638,
                            invoiceNumber.substring(0, 25), Color.BLACK);

                    writeText(content, regularFont, 7, 430, 626,
                            invoiceNumber.substring(25), Color.BLACK);

                } else {

                    writeText(content, regularFont, 8, 430, 638,
                            invoiceNumber, Color.BLACK);
                }

// Date
                writeText(content, boldFont, 11, 430, 598,
                        "Date:", darkBlue);

                writeText(content, regularFont, 9, 430, 581,
                        String.valueOf(invoice.getInvoiceDate()), Color.BLACK);
                // 4. ITEMS TABLE HEADER
                float tableY = 545;

                drawRectangle(content, 40, tableY, 532, 30, darkBlue);

                writeText(content, boldFont, 10, 50, tableY + 10,
                        "SL.", Color.WHITE);

                writeText(content, boldFont, 10, 95, tableY + 10,
                        "Item Description", Color.WHITE);

                writeText(content, boldFont, 10, 345, tableY + 10,
                        "Price", Color.WHITE);

                writeText(content, boldFont, 10, 430, tableY + 10,
                        "Qty", Color.WHITE);

                writeText(content, boldFont, 10, 485, tableY + 10,
                        "Total", Color.WHITE);

                // 5. ITEMS TABLE ROWS
                float rowY = tableY - 28;
                int index = 1;

                if (invoice.getItems() != null) {

                    for (var item : invoice.getItems()) {

                        if (index % 2 == 0) {
                            drawRectangle(content, 40, rowY, 532, 28, lightGray);
                        }

                        writeText(content, regularFont, 9, 50, rowY + 9,
                                String.valueOf(index), Color.BLACK);

                        writeText(content, regularFont, 9, 95, rowY + 9,
                                item.getProductName(), Color.BLACK);

                        writeText(content, regularFont, 9, 345, rowY + 9,
                                item.getUnitPrice() + " DT", Color.BLACK);

                        writeText(content, regularFont, 9, 430, rowY + 9,
                                String.valueOf(item.getQuantity()), Color.BLACK);

                        writeText(content, regularFont, 9, 485, rowY + 9,
                                item.getSubtotal() + " DT", Color.BLACK);

                        rowY -= 28;
                        index++;
                    }
                }

                // 6. PAYMENT INFORMATION
                float paymentY = rowY - 65;

                writeText(content, boldFont, 12, 50, paymentY,
                        "PAYMENT INFORMATION", darkBlue);

                if (invoice.getPayment() != null) {

                    writeText(content, regularFont, 10, 50, paymentY - 20,
                            "Payment Method: " +
                                    invoice.getPayment().getPaymentMethod(),
                            Color.BLACK);

                    writeText(content, regularFont, 10, 50, paymentY - 38,
                            "Status: " +
                                    invoice.getPayment().getPaymentStatus(),
                            Color.BLACK);

                    writeText(content, regularFont, 8, 50, paymentY - 56,
                            "Transaction: " +
                                    invoice.getPayment().getTransactionReference(),
                            Color.BLACK);
                }

                // 7. TOTAL AMOUNT
                drawRectangle(content, 390, paymentY - 45, 182, 42, darkBlue);

                writeText(content, boldFont, 12, 405, paymentY - 29,
                        "TOTAL:", Color.WHITE);

                writeText(content, boldFont, 12, 490, paymentY - 29,
                        invoice.getTotalAmount() + " DT", Color.WHITE);

                // 8. FOOTER
                drawRectangle(content, 0, 0, 612, 45, darkBlue);

                writeText(content, boldFont, 10, 150, 18,
                        "THANK YOU FOR CHOOSING ZIN DARNA",
                        Color.WHITE);
            }

            document.save(outputStream);

            return outputStream.toByteArray();
        }
    }



    // Draw a colored rectangle in the PDF
    private void drawRectangle(
            PDPageContentStream content,
            float x,
            float y,
            float width,
            float height,
            Color color
    ) throws IOException {

        content.setNonStrokingColor(color);
        content.addRect(x, y, width, height);
        content.fill();
    }

    // Write colored text into the PDF

    private void writeText(
            PDPageContentStream content,
            PDType1Font font,
            int fontSize,
            float x,
            float y,
            String text,
            Color color
    ) throws IOException {

        content.setNonStrokingColor(color);

        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(toPdfSafeText(text));
        content.endText();
    }



    // Replace unsupported characters for the standard PDF fonts
    private String toPdfSafeText(String value) {
        if (value == null) {
            return "";
        }

        return value.replaceAll("[^\\x20-\\x7E]", "?");
    }

}