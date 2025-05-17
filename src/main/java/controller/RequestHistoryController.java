package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import model.Invoice;
import model.InvoiceDAO;
import model.Session;

import java.time.LocalDate;
import java.util.List;

public class RequestHistoryController {

    @FXML private TableView<Invoice> invoiceTable;
    @FXML private TableColumn<Invoice, String> submissionDateColumn;
    @FXML private TableColumn<Invoice, String> categoryColumn;
    @FXML private TableColumn<Invoice, String> amountColumn;
    @FXML private TableColumn<Invoice, String> statusColumn;
    @FXML private PieChart invoicePieChart;

    private ObservableList<Invoice> invoiceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadInvoices();
    }

    private void setupTable() {
        submissionDateColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getSubmissionDate().toString()));

        amountColumn.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("%.2f €", data.getValue().getInvoiceAmount())
        ));

        categoryColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getCategory().toString()));

        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getStatus().toString()));

        invoiceTable.setItems(invoiceList);
    }

    private void loadInvoices() {
        int userId = Session.getCurrentUser().getId();
        List<Invoice> invoices = InvoiceDAO.getAllInvoicesByUser(userId);
        invoiceList.setAll(invoices);
        updateChart();
    }

    private void updateChart() {
        long restaurantCount = invoiceList.stream()
                .filter(inv -> inv.getCategory() == Invoice.InvoiceCategory.RESTAURANT)
                .count();
        long supermarketCount = invoiceList.stream()
                .filter(inv -> inv.getCategory() == Invoice.InvoiceCategory.SUPERMARKET)
                .count();

        invoicePieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Restaurant", restaurantCount),
                new PieChart.Data("Supermarket", supermarketCount)
        ));
    }

    @FXML
    private void handleEditInvoice() {
        Invoice selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            LocalDate today = LocalDate.now();
            boolean sameMonth = (selected.getSubmissionDate().getMonth() == today.getMonth())
                    && (selected.getSubmissionDate().getYear() == today.getYear());
            boolean notApproved = selected.getStatus() != Invoice.InvoiceStatus.APPROVED;

            if (sameMonth && notApproved) {
                TextInputDialog amountDialog = new TextInputDialog(
                        String.valueOf(selected.getInvoiceAmount()));
                amountDialog.setTitle("Edit Invoice Amount");
                amountDialog.setHeaderText("Edit the amount of the invoice:");
                amountDialog.setContentText("New amount:");

                amountDialog.showAndWait().ifPresent(newAmount -> {
                    try {
                        double amt = Double.parseDouble(newAmount);
                        selected.setInvoiceAmount(amt);
                        // optional: recalc reimbursement if needed
                        selected.setReimbursementAmount(
                                Math.min(amt,
                                        selected.getCategory() == Invoice.InvoiceCategory.RESTAURANT ? 3.0 : 2.5)
                        );

                        boolean success = InvoiceDAO.updateInvoice(selected);
                        if (success) {
                            invoiceTable.refresh();
                            updateChart();
                            showAlert(Alert.AlertType.INFORMATION, "Invoice updated successfully.");
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Failed to update invoice.");
                        }
                    } catch (NumberFormatException e) {
                        showAlert(Alert.AlertType.ERROR, "Invalid amount entered.");
                    }
                });
            } else {
                showAlert(Alert.AlertType.WARNING,
                        "You can only edit invoices from the current month that are not approved.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Please select an invoice to edit.");
        }
    }

    @FXML
    private void deleteSelectedInvoice() {
        Invoice selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.isEditable()) {
                boolean deleted = InvoiceDAO.deleteInvoice(selected.getId());
                if (deleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Invoice deleted successfully.");
                    loadInvoices();
                    updateChart();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error deleting the invoice.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING,
                        "You cannot delete an approved invoice or one from a past month.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Please select an invoice to delete.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Request History");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
