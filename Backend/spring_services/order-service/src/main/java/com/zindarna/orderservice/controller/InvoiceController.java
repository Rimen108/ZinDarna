package com.zindarna.orderservice.controller;

import com.zindarna.orderservice.model.Invoice;
import com.zindarna.orderservice.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.io.IOException;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }
    // Get invoice details by order ID
    @GetMapping("/{orderId}")
    public ResponseEntity<Invoice> getInvoiceByOrderId(
            @PathVariable String orderId
    ) {
        Invoice invoice = invoiceService.getInvoiceByOrderId(orderId);

        return ResponseEntity.ok(invoice);
    }
    // Download an invoice as a PDF
    @GetMapping("/{orderId}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(
            @PathVariable String orderId
    ) throws IOException {

        byte[] pdf = invoiceService.generateInvoicePdf(orderId);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"invoice.pdf\""
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}