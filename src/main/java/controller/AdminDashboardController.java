package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Invoice;
import model.InvoiceDAO;
import model.UserDAO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AdminDashboardController {

    @FXML private TableView<Invoice> invoiceTable;
    @FXML private TableColumn<Invoice, String> emailColumn;
    @FXML private TableColumn<Invoice, LocalDate> dateColumn;
    @FXML private TableColumn<Invoice, Number> invoiceAmountColumn;
    @FXML private TableColumn<Invoice, Number> reimbursementAmountColumn;
    @FXML private TableColumn<Invoice, String> classificationColumn;
    @FXML private TableColumn<Invoice, String> statusColumn;

    @FXML private TextField searchEmailField;
    @FXML private ChoiceBox<String> classificationFilter;
    @FXML private DatePicker monthPicker;
    @FXML private Button deleteButton;
    @FXML private Button rejectButton;
    @FXML private Button exportCSVButton;
    @FXML private Button exportPayrollButton;
    @FXML private Label messageLabel;

    private InvoiceDAO invoiceDAO = new InvoiceDAO();
    private UserDAO userDAO = new UserDAO();
    private ObservableList<Invoice> allInvoices = FXCollections.observableArrayList();

    public void initialize() {
        loadInvoices();
        setupTable();
        setupFilters();
    }

    private void loadInvoices() {
        List<Invoice> invoices = invoiceDAO.findAllInvoices();
        invoices.forEach(invoice -> {
            String email = userDAO.findEmailByBenutzerId(invoice.getUserId());
            invoice.setEmail(email != null ? email : "Unknown");
        });
        allInvoices.setAll(invoices);
        invoiceTable.setItems(allInvoices);
    }

    private void setupTable() {
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().submissionDateProperty());
        invoiceAmountColumn.setCellValueFactory(cellData -> cellData.getValue().invoiceAmountProperty());
        reimbursementAmountColumn.setCellValueFactory(cellData -> cellData.getValue().reimbursementAmountProperty());
        classificationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getCategory().toString()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getStatus().toString()));
    }

    private void setupFilters() {
        classificationFilter.getItems().addAll("Restaurant", "Supermarket", "All");
        classificationFilter.setValue("All");

        searchEmailField.textProperty().addListener((observable, oldValue, newValue) -> filterInvoices());
        classificationFilter.valueProperty().addListener((observable, oldValue, newValue) -> filterInvoices());
        monthPicker.valueProperty().addListener((observable, oldValue, newValue) -> filterInvoices());
    }

    private void filterInvoices() {
        String emailFilter = searchEmailField.getText().toLowerCase();
        String classification = classificationFilter.getValue();
        LocalDate selectedMonth = monthPicker.getValue();

        List<Invoice> filtered = allInvoices.stream()
                .filter(invoice -> invoice.getEmail().toLowerCase().contains(emailFilter))
                .filter(invoice -> classification.equals("All") || invoice.getCategory().name().equalsIgnoreCase(classification))
                .filter(invoice -> {
                    if (selectedMonth == null) return true;
                    return invoice.getSubmissionDate().getMonth().equals(selectedMonth.getMonth())
                            && invoice.getSubmissionDate().getYear() == selectedMonth.getYear();
                })
                .collect(Collectors.toList());

        invoiceTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void deleteInvoice() {
        Invoice selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            invoiceDAO.deleteInvoice(selected.getId());
            loadInvoices();
            messageLabel.setText("Invoice deleted.");
        }
    }

    @FXML
    private void rejectInvoice() {
        Invoice selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            if (selected.getStatus() == Invoice.InvoiceStatus.SUBMITTED) {
                boolean success = invoiceDAO.updateInvoiceStatus(selected.getId(), Invoice.InvoiceStatus.REJECTED);
                if (success) {
                    loadInvoices();
                    messageLabel.setText("Invoice rejected successfully.");
                } else {
                    messageLabel.setText("Failed to reject invoice.");
                }
            } else {
                messageLabel.setText("Cannot reject invoice. Current status is not SUBMITTED.");
            }
        } else {
            messageLabel.setText("No invoice selected.");
        }
    }

    @FXML
    private void editInvoice() {
        Invoice selected = invoiceTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getInvoiceAmount()));
            dialog.setTitle("Edit Invoice");
            dialog.setHeaderText("Edit invoice amount:");
            dialog.setContentText("New amount:");

            dialog.showAndWait().ifPresent(newAmountStr -> {
                try {
                    double newAmount = Double.parseDouble(newAmountStr);
                    selected.setInvoiceAmount(newAmount);
                    selected.setReimbursementAmount( // recalc on edit
                            Math.min(newAmount, selected.getCategory() == Invoice.InvoiceCategory.RESTAURANT ? 3.0 : 2.5)
                    );

                    boolean success = invoiceDAO.updateInvoice(selected);
                    if (success) {
                        loadInvoices();
                        messageLabel.setText("Invoice updated successfully.");
                    } else {
                        messageLabel.setText("Failed to update invoice.");
                    }
                } catch (NumberFormatException e) {
                    messageLabel.setText("Invalid amount format.");
                }
            });
        } else {
            messageLabel.setText("No invoice selected.");
        }
    }

    @FXML
    private void exportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.setInitialFileName("invoices.csv");
        File file = fileChooser.showSaveDialog(invoiceTable.getScene().getWindow());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Email,Date,Invoice Amount,Reimbursement,Classification,Status\n");
                for (Invoice invoice : invoiceTable.getItems()) {
                    writer.write(invoice.getEmail() + "," +
                            invoice.getSubmissionDate() + "," +
                            invoice.getInvoiceAmount() + "," +
                            invoice.getReimbursementAmount() + "," +
                            invoice.getCategory() + "," +
                            invoice.getStatus() + "\n");
                }
                messageLabel.setText("Exported CSV successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                messageLabel.setText("Error exporting CSV.");
            }
        }
    }

    @FXML
    private void exportPayrollJSON() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Payroll JSON");
        fileChooser.setInitialFileName("payroll.json");
        File file = fileChooser.showSaveDialog(invoiceTable.getScene().getWindow());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("[\n");
                List<Invoice> items = invoiceTable.getItems();
                for (int i = 0; i < items.size(); i++) {
                    Invoice invoice = items.get(i);
                    writer.write("  {\n");
                    writer.write("    \"email\": \"" + invoice.getEmail() + "\",\n");
                    writer.write("    \"invoiceAmount\": " + invoice.getInvoiceAmount() + ",\n");
                    writer.write("    \"reimbursementAmount\": " + invoice.getReimbursementAmount() + "\n");
                    if (i < items.size() - 1) writer.write("  },\n"); else writer.write("  }\n");
                }
                writer.write("]\n");
                messageLabel.setText("Exported Payroll JSON successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                messageLabel.setText("Error exporting Payroll JSON.");
            }
        }
    }

    @FXML
    private void exportPayrollXML() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Payroll XML");
        fileChooser.setInitialFileName("payroll.xml");
        File file = fileChooser.showSaveDialog(invoiceTable.getScene().getWindow());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("<Payroll>\n");
                for (Invoice invoice : invoiceTable.getItems()) {
                    writer.write("  <Employee>\n");
                    writer.write("    <Email>" + invoice.getEmail() + "</Email>\n");
                    writer.write("    <InvoiceAmount>" + invoice.getInvoiceAmount() + "</InvoiceAmount>\n");
                    writer.write("    <ReimbursementAmount>" + invoice.getReimbursementAmount() + "</ReimbursementAmount>\n");
                    writer.write("  </Employee>\n");
                }
                writer.write("</Payroll>\n");
                messageLabel.setText("Exported Payroll XML successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                messageLabel.setText("Error exporting Payroll XML.");
            }
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openReimbursementSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ReimbursementSettingsView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Reimbursement Settings");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error opening reimbursement settings.");
        }
    }
}