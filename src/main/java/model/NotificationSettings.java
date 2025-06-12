package model;

public class NotificationSettings {
    private int userId;
    private boolean notifyInvoiceApproved;
    private boolean notifyInvoiceRejected;
    private boolean notifyMonthlySummary;

    public NotificationSettings(int userId,
                                boolean notifyInvoiceApproved,
                                boolean notifyInvoiceRejected,
                                boolean notifyMonthlySummary) {
        this.userId = userId;
        this.notifyInvoiceApproved = notifyInvoiceApproved;
        this.notifyInvoiceRejected = notifyInvoiceRejected;
        this.notifyMonthlySummary = notifyMonthlySummary;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isNotifyInvoiceApproved() {
        return notifyInvoiceApproved;
    }

    public void setNotifyInvoiceApproved(boolean notifyInvoiceApproved) {
        this.notifyInvoiceApproved = notifyInvoiceApproved;
    }

    public boolean isNotifyInvoiceRejected() {
        return notifyInvoiceRejected;
    }

    public void setNotifyInvoiceRejected(boolean notifyInvoiceRejected) {
        this.notifyInvoiceRejected = notifyInvoiceRejected;
    }

    public boolean isNotifyMonthlySummary() {
        return notifyMonthlySummary;
    }

    public void setNotifyMonthlySummary(boolean notifyMonthlySummary) {
        this.notifyMonthlySummary = notifyMonthlySummary;
    }
}
