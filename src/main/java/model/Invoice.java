package model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Invoice {

    private static int idCounter = 1;

    private IntegerProperty invoiceId = new SimpleIntegerProperty();
    private IntegerProperty userId = new SimpleIntegerProperty();
    private StringProperty fileName = new SimpleStringProperty();
    private ObjectProperty<InvoiceCategory> category = new SimpleObjectProperty<>();
    private DoubleProperty invoiceAmount = new SimpleDoubleProperty();       // formerly amount
    private DoubleProperty reimbursementAmount = new SimpleDoubleProperty(); // new field
    private BooleanProperty starred = new SimpleBooleanProperty(false);     // new field
    private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> submissionDate = new SimpleObjectProperty<>();
    private ObjectProperty<InvoiceStatus> status = new SimpleObjectProperty<>();
    private StringProperty email = new SimpleStringProperty();

    public enum InvoiceCategory {
        RESTAURANT, SUPERMARKET
    }

    public enum InvoiceStatus {
        SUBMITTED, APPROVED, REJECTED
    }

    public Invoice(String fileName, InvoiceCategory category, double invoiceAmount) {
        this.invoiceId.set(idCounter++);
        this.fileName.set(fileName);
        this.category.set(category);
        this.invoiceAmount.set(invoiceAmount);
        this.reimbursementAmount.set(0.0);
        this.starred.set(false);
    }

    // --- Invoice and Reimbursement Getters & Setters ---

    public double getInvoiceAmount() {
        return invoiceAmount.get();
    }

    public void setInvoiceAmount(double invoiceAmount) {
        this.invoiceAmount.set(invoiceAmount);
    }

    public DoubleProperty invoiceAmountProperty() {
        return invoiceAmount;
    }

    public double getReimbursementAmount() {
        return reimbursementAmount.get();
    }

    public void setReimbursementAmount(double reimbursementAmount) {
        this.reimbursementAmount.set(reimbursementAmount);
    }

    public DoubleProperty reimbursementAmountProperty() {
        return reimbursementAmount;
    }

    // --- Starred Getter & Setter ---

    public boolean isStarred() {
        return starred.get();
    }

    public void setStarred(boolean starred) {
        this.starred.set(starred);
    }

    public BooleanProperty starredProperty() {
        return starred;
    }

    // --- Existing Getters & Setters ---

    public int getId() {
        return invoiceId.get();
    }

    public void setId(int id) {
        this.invoiceId.set(id);
    }

    public IntegerProperty idProperty() {
        return invoiceId;
    }

    public int getUserId() {
        return userId.get();
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    public IntegerProperty userIdProperty() {
        return userId;
    }

    public String getFileName() {
        return fileName.get();
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public StringProperty fileNameProperty() {
        return fileName;
    }

    public InvoiceCategory getCategory() {
        return category.get();
    }

    public void setCategory(InvoiceCategory category) {
        this.category.set(category);
    }

    public ObjectProperty<InvoiceCategory> categoryProperty() {
        return category;
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate.get();
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate.set(submissionDate);
    }

    public ObjectProperty<LocalDate> submissionDateProperty() {
        return submissionDate;
    }

    public InvoiceStatus getStatus() {
        return status.get();
    }

    public void setStatus(InvoiceStatus status) {
        this.status.set(status);
    }

    public ObjectProperty<InvoiceStatus> statusProperty() {
        return status;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public boolean isEditable() {
        LocalDate today = LocalDate.now();
        boolean sameMonth = (submissionDate.get().getMonth() == today.getMonth())
                && (submissionDate.get().getYear() == today.getYear());
        boolean notApproved = (status.get() != InvoiceStatus.APPROVED);
        return sameMonth && notApproved;
    }
}
