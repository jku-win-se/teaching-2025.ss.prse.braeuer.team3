package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Invoice {
    // Static counter to automatically generate unique IDs for each invoice
    private static int idCounter = 1;

    // Unique ID for each invoice instance, set during object creation.
    private int invoiceId;
    private String fileName;
    private InvoiceCategory category;
    private double amount;
    private LocalDate date;
    private LocalDate submissionDate;
    private InvoiceStatus status;

    public enum InvoiceCategory {
        RESTAURANT, SUPERMARKET
    }

    public enum InvoiceStatus {
        PENDING, APPROVED, REJECTED, FLAGGED;
    }

    public Invoice(String fileName, InvoiceCategory category, double amount) {
        this.invoiceId = idCounter++; // Automatisch eine eindeutige ID zuweisen
        this.fileName = fileName;
        this.category = category;
        this.amount = amount;
    }



    public int getId() {
        return invoiceId;
    }

    public void setId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InvoiceCategory getCategory() {
        return category;
    }

    public void setCategory(InvoiceCategory type) {
        this.category = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public InvoiceStatus getStatus() {
        return status;
    }
    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }


}
