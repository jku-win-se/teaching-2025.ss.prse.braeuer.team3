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
import model.Session;
import model.UserDAO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

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

    @FXML
    public void initialize() {
        setupTable();
        setupFilters();
        loadInvoices();
    }

    private void setupTable() {
        emailColumn.setCellValueFactory(cd -> cd.getValue().emailProperty());
        dateColumn.setCellValueFactory(cd -> cd.getValue().submissionDateProperty());
        invoiceAmountColumn.setCellValueFactory(cd -> cd.getValue().invoiceAmountProperty());
        reimbursementAmountColumn.setCellValueFactory(cd -> cd.getValue().reimbursementAmountProperty());
        classificationColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getCategory().toString())
        );
        statusColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getStatus().toString())
        );
    }

    private void setupFilters() {
        classificationFilter.getItems().addAll("Restaurant", "Supermarket", "All");
        classificationFilter.setValue("All");

        searchEmailField.textProperty()
                .addListener((o, oldV, newV) -> filterInvoices());
        classificationFilter.valueProperty()
                .addListener((o, oldV, newV) -> filterInvoices());
        monthPicker.valueProperty()
                .addListener((o, oldV, newV) -> filterInvoices());
    }

    private void loadInvoices() {
        List<Invoice> invoices = invoiceDAO.findAllInvoices();
        invoices.forEach(inv -> {
            String email = userDAO.findEmailByBenutzerId(inv.getUserId());
            inv.setEmail(email != null ? email : "Unknown");
        });
        allInvoices.setAll(invoices);
        invoiceTable.setItems(allInvoices);
    }

    private void filterInvoices() {
        String emailFilter = searchEmailField.getText().toLowerCase();
        String classification = classificationFilter.getValue();
        LocalDate month = monthPicker.getValue();

        List<Invoice> filtered = allInvoices.stream()
                .filter(inv -> inv.getEmail().toLowerCase().contains(emailFilter))
                .filter(inv -> classification.equals("All")
                        || inv.getCategory().name().equalsIgnoreCase(classification))
                .filter(inv -> {
                    if (month == null) return true;
                    return inv.getSubmissionDate().getMonth().equals(month.getMonth())
                            && inv.getSubmissionDate().getYear() == month.getYear();
                })
                .collect(Collectors.toList());

        invoiceTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void deleteInvoice() {
        Invoice sel = invoiceTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            invoiceDAO.deleteInvoice(sel.getId());
            loadInvoices();
            messageLabel.setText("Invoice deleted.");
        }
    }

    @FXML
    private void rejectInvoice() {
        Invoice sel = invoiceTable.getSelectionModel().getSelectedItem();
        if (sel != null && sel.getStatus() == Invoice.InvoiceStatus.SUBMITTED) {
            boolean ok = invoiceDAO.updateInvoiceStatus(sel.getId(), Invoice.InvoiceStatus.REJECTED);
            if (ok) {
                loadInvoices();
                messageLabel.setText("Invoice rejected successfully.");
            } else {
                messageLabel.setText("Failed to reject invoice.");
            }
        } else {
            messageLabel.setText("No SUBMITTED invoice selected.");
        }
    }

    @FXML
    private void editInvoice() {
        Invoice sel = invoiceTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            TextInputDialog dlg = new TextInputDialog(String.valueOf(sel.getInvoiceAmount()));
            dlg.setTitle("Edit Invoice");
            dlg.setHeaderText("Edit invoice amount:");
            dlg.setContentText("New amount:");

            dlg.showAndWait().ifPresent(str -> {
                try {
                    double amt = Double.parseDouble(str);
                    sel.setInvoiceAmount(amt);
                    sel.setReimbursementAmount(
                            Math.min(amt,
                                    sel.getCategory()== Invoice.InvoiceCategory.RESTAURANT ? 3.0 : 2.5)
                    );
                    if (invoiceDAO.updateInvoice(sel)) {
                        loadInvoices();
                        messageLabel.setText("Invoice updated successfully.");
                    } else {
                        messageLabel.setText("Failed to update invoice.");
                    }
                } catch (NumberFormatException e) {
                    messageLabel.setText("Invalid amount.");
                }
            });
        }
    }
    @FXML
    private void exportCSV() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save CSV File");
        chooser.setInitialFileName("invoices.csv");
        File file = chooser.showSaveDialog(invoiceTable.getScene().getWindow());
        if (file == null) return;

        try (FileWriter w = new FileWriter(file)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            w.write("\"User Email\";\"Date\";\"Invoice Amount\";\"Reimbursement\";\"Classification\";\"Status\"\n");

            for (Invoice inv : invoiceTable.getItems()) {
                w.write(String.join(";",
                        "\"" + inv.getEmail() + "\"",
                        "\"" + inv.getSubmissionDate().format(formatter) + "\"",
                        "\"" + String.format("%.2f", inv.getInvoiceAmount()).replace(".", ",") + "\"",
                        "\"" + String.format("%.2f", inv.getReimbursementAmount()).replace(".", ",") + "\"",
                        "\"" + inv.getCategory().toString() + "\"",
                        "\"" + inv.getStatus().toString() + "\""
                ) + "\n");
            }

            messageLabel.setText("Exported CSV successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error exporting CSV.");
        }
    }

    @FXML
    private void exportPayrollPDF() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save PDF File");
        chooser.setInitialFileName("invoices.pdf");
        File file = chooser.showSaveDialog(invoiceTable.getScene().getWindow());
        if (file == null) return;

        try {
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Paragraph title = new Paragraph("Invoice Export")
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            Table table = new Table(6);
            table.addCell("User Email");
            table.addCell("Date");
            table.addCell("Invoice Amount");
            table.addCell("Reimbursement");
            table.addCell("Classification");
            table.addCell("Status");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Invoice inv : invoiceTable.getItems()) {
                table.addCell(inv.getEmail());
                table.addCell(inv.getSubmissionDate().format(formatter));
                table.addCell(String.format("%.2f", inv.getInvoiceAmount()).replace(".", ","));
                table.addCell(String.format("%.2f", inv.getReimbursementAmount()).replace(".", ","));
                table.addCell(inv.getCategory().toString());
                table.addCell(inv.getStatus().toString());
            }

            document.add(table);
            document.close();
            messageLabel.setText("Exported PDF successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error exporting PDF.");
        }
    }


    @FXML
    private void exportPayrollJSON() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Payroll JSON");
        chooser.setInitialFileName("payroll.json");
        File file = chooser.showSaveDialog(invoiceTable.getScene().getWindow());
        if (file == null) return;

        try (FileWriter w = new FileWriter(file)) {
            w.write("[\n");
            var items = invoiceTable.getItems();
            for (int i = 0; i < items.size(); i++) {
                Invoice inv = items.get(i);
                w.write("  {\n");
                w.write("    \"email\": \"" + inv.getEmail() + "\",\n");
                w.write("    \"invoiceAmount\": " + inv.getInvoiceAmount() + ",\n");
                w.write("    \"reimbursementAmount\": " + inv.getReimbursementAmount() + "\n");
                w.write(i < items.size()-1 ? "  },\n" : "  }\n");
            }
            w.write("]\n");
            messageLabel.setText("Exported Payroll JSON successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error exporting Payroll JSON.");
        }
    }

    @FXML
    private void exportPayrollXML() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Payroll XML");
        chooser.setInitialFileName("payroll.xml");
        File file = chooser.showSaveDialog(invoiceTable.getScene().getWindow());
        if (file == null) return;

        try (FileWriter w = new FileWriter(file)) {
            w.write("<Payroll>\n");
            for (Invoice inv : invoiceTable.getItems()) {
                w.write("  <Employee>\n");
                w.write("    <Email>" + inv.getEmail() + "</Email>\n");
                w.write("    <InvoiceAmount>" + inv.getInvoiceAmount() + "</InvoiceAmount>\n");
                w.write("    <ReimbursementAmount>" + inv.getReimbursementAmount() + "</ReimbursementAmount>\n");
                w.write("  </Employee>\n");
            }
            w.write("</Payroll>\n");
            messageLabel.setText("Exported Payroll XML successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error exporting Payroll XML.");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Lade LoginView komplett neu
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Parent loginRoot = loader.load();

            Scene scene = new Scene(loginRoot);
            scene.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm()
            );

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.setResizable(false);
            stage.show();

            // Session löschen
            Session.clearCurrentUser();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Could not load login screen."
            ).showAndWait();
        }
    }

    @FXML
    private void openReimbursementSettings() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/ReimbursementSettingsView.fxml"));
        Stage st = new Stage();
        st.setTitle("Reimbursement Settings");
        st.setScene(new Scene(root));
        st.show();
    }

    @FXML
    private void openUserManagement() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/UserManagementView.fxml"));
        Stage st = new Stage();
        st.setTitle("User Management");
        st.setScene(new Scene(root));
        st.show();
    }
}