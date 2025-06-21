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
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Invoice;
import model.InvoiceDAO;
import model.NotificationDAO;
import model.Session;
import model.UserDAO;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AdminDashboardController {

    // --- Tabelle ---
    @FXML private TableView<Invoice> invoiceTable;
    @FXML private TableColumn<Invoice, String> emailColumn;
    @FXML private TableColumn<Invoice, LocalDate> dateColumn;
    @FXML private TableColumn<Invoice, Number> invoiceAmountColumn;
    @FXML private TableColumn<Invoice, Number> reimbursementAmountColumn;
    @FXML private TableColumn<Invoice, String> classificationColumn;
    @FXML private TableColumn<Invoice, String> statusColumn;

    // --- Filter & Suche ---
    @FXML private TextField searchEmailField;
    @FXML private ChoiceBox<String> classificationFilter;
    @FXML private DatePicker monthPicker;

    // --- Buttons & Feedback ---
    @FXML private Button deleteButton;
    @FXML private Button rejectButton;
    @FXML private Button exportCSVButton;
    @FXML private Button exportPayrollButton;
    @FXML private Label messageLabel;

    // --- Neue KPI-Labels & Chart ---
    @FXML private Label lblTotalPerMonth;
    @FXML private Label lblAvgPerEmp;
    @FXML private Label lblReimb12m;
    @FXML private PieChart pieCategoryChart;

    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<Invoice> allInvoices = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        setupFilters();
        loadInvoices();
        loadStatistics();
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
        searchEmailField.textProperty().addListener((o, ov, nv) -> filterInvoices());
        classificationFilter.valueProperty().addListener((o, ov, nv) -> filterInvoices());
        monthPicker.valueProperty().addListener((o, ov, nv) -> filterInvoices());
    }

    private void loadInvoices() {
        List<Invoice> invoices = InvoiceDAO.findAllInvoices();
        invoices.forEach(inv -> {
            String email = userDAO.findEmailByBenutzerId(inv.getUserId());
            inv.setEmail(email != null ? email : "Unknown");
        });
        allInvoices.setAll(invoices);
        invoiceTable.setItems(allInvoices);
    }

    private void filterInvoices() {
        String text = searchEmailField.getText().toLowerCase();
        String cls  = classificationFilter.getValue();
        LocalDate m = monthPicker.getValue();

        List<Invoice> filtered = allInvoices.stream()
                .filter(inv -> inv.getEmail().toLowerCase().contains(text))
                .filter(inv -> "All".equals(cls)
                        || inv.getCategory().name().equalsIgnoreCase(cls))
                .filter(inv -> m == null
                        || (inv.getSubmissionDate().getMonth().equals(m.getMonth())
                        && inv.getSubmissionDate().getYear() == m.getYear()))
                .collect(Collectors.toList());

        invoiceTable.setItems(FXCollections.observableArrayList(filtered));
    }

    @FXML
    private void deleteInvoice() {
        Invoice sel = invoiceTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            boolean ok = InvoiceDAO.deleteInvoice(sel.getId());
            if (ok) {
                loadInvoices();
                messageLabel.setText("Invoice deleted.");
            } else {
                messageLabel.setText("Failed to delete invoice.");
            }
        }
    }

    @FXML
    private void rejectInvoice() {
        Invoice sel = invoiceTable.getSelectionModel().getSelectedItem();
        if (sel != null && sel.getStatus() == Invoice.InvoiceStatus.SUBMITTED) {
            boolean ok = InvoiceDAO.updateInvoiceStatus(sel.getId(), Invoice.InvoiceStatus.REJECTED);
            if (ok) {
                NotificationDAO.createNotification(
                        sel.getUserId(),
                        "Your reimbursement request (ID " + sel.getId() + ") has been rejected."
                );
                loadInvoices();
                messageLabel.setText("Invoice rejected successfully.");
            } else {
                messageLabel.setText("Failed to reject invoice.");
            }
        } else {
            messageLabel.setText("Cannot reject invoice. Current status is not SUBMITTED.");
        }
    }

    @FXML
    private void editInvoice() {
        Invoice sel = invoiceTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            messageLabel.setText("No invoice selected.");
            return;
        }
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
                                sel.getCategory() == Invoice.InvoiceCategory.RESTAURANT ? 3.0 : 2.5)
                );
                boolean ok = InvoiceDAO.updateInvoice(sel);
                if (ok) {
                    InvoiceDAO.updateInvoiceStatus(sel.getId(), Invoice.InvoiceStatus.EDITED);
                    NotificationDAO.createNotification(
                            sel.getUserId(),
                            "Your reimbursement request (ID " + sel.getId() + ") was edited by an administrator."
                    );
                    loadInvoices();
                    messageLabel.setText("Invoice edited successfully.");
                } else {
                    messageLabel.setText("Failed to update invoice.");
                }
            } catch (NumberFormatException ex) {
                messageLabel.setText("Invalid amount format.");
            }
        });
    }

    @FXML
    private void exportCSV() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save CSV File");
        chooser.setInitialFileName("invoices.csv");
        File file = chooser.showSaveDialog(invoiceTable.getScene().getWindow());
        if (file == null) return;

        try (FileWriter w = new FileWriter(file)) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            w.write("\"User Email\";\"Date\";\"Invoice Amount\";\"Reimbursement\";\"Classification\";\"Status\"\n");
            for (Invoice inv : invoiceTable.getItems()) {
                w.write(String.join(";",
                        "\"" + inv.getEmail() + "\"",
                        "\"" + inv.getSubmissionDate().format(fmt) + "\"",
                        "\"" + String.format("%.2f", inv.getInvoiceAmount()).replace(".", ",") + "\"",
                        "\"" + String.format("%.2f", inv.getReimbursementAmount()).replace(".", ",") + "\"",
                        "\"" + inv.getCategory() + "\"",
                        "\"" + inv.getStatus() + "\""
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

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Invoice inv : invoiceTable.getItems()) {
                table.addCell(inv.getEmail());
                table.addCell(inv.getSubmissionDate().format(fmt));
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

        Map<String, double[]> userSums = new HashMap<>();
        for (Invoice inv : invoiceTable.getItems()) {
            userSums.putIfAbsent(inv.getEmail(), new double[]{0, 0});
            userSums.get(inv.getEmail())[0] += inv.getInvoiceAmount();
            userSums.get(inv.getEmail())[1] += inv.getReimbursementAmount();
        }

        try (FileWriter w = new FileWriter(file)) {
            w.write("[\n");
            int i = 0, size = userSums.size();
            for (Map.Entry<String, double[]> entry : userSums.entrySet()) {
                String email = entry.getKey();
                double[] sums = entry.getValue();
                w.write("  {\n");
                w.write("    \"email\": \"" + email + "\",\n");
                w.write("    \"totalInvoiceAmount\": " + String.format("%.2f", sums[0]) + ",\n");
                w.write("    \"totalReimbursementAmount\": " + String.format("%.2f", sums[1]) + "\n");
                w.write(i++ < size - 1 ? "  },\n" : "  }\n");
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

        Map<String, double[]> userSums = new HashMap<>();
        for (Invoice inv : invoiceTable.getItems()) {
            userSums.putIfAbsent(inv.getEmail(), new double[]{0, 0});
            userSums.get(inv.getEmail())[0] += inv.getInvoiceAmount();
            userSums.get(inv.getEmail())[1] += inv.getReimbursementAmount();
        }

        try (FileWriter w = new FileWriter(file)) {
            w.write("<Payroll>\n");
            for (Map.Entry<String, double[]> entry : userSums.entrySet()) {
                String email = entry.getKey();
                double[] sums = entry.getValue();
                w.write("  <Employee>\n");
                w.write("    <Email>" + email + "</Email>\n");
                w.write("    <TotalInvoiceAmount>" + String.format("%.2f", sums[0]).replace(".", ",") + "</TotalInvoiceAmount>\n");
                w.write("    <TotalReimbursementAmount>" + String.format("%.2f", sums[1]).replace(".", ",") + "</TotalReimbursementAmount>\n");
                w.write("  </Employee>\n");
            }
            w.write("</Payroll>\n");
            messageLabel.setText("Exported Payroll XML successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error exporting Payroll XML.");
        }
    }

    private void loadStatistics() {
        YearMonth now = YearMonth.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        // 1) Rechnungen dieses Monats
        List<Invoice> thisMonth = InvoiceDAO.findInvoicesByMonth(year, month);

        // 2) Total / Monat
        lblTotalPerMonth.setText(String.valueOf(thisMonth.size()));

        // 3) Ø / Mitarbeiter
        Set<Integer> users = thisMonth.stream()
                .map(Invoice::getUserId)
                .collect(Collectors.toSet());
        double avg = users.isEmpty() ? 0 : (double) thisMonth.size() / users.size();
        lblAvgPerEmp.setText(String.format("%.1f", avg));

        // 4) Summe erstattet (letzte 12 Monate)
        double sum12 = 0;
        for (int i = 0; i < 12; i++) {
            YearMonth ym = now.minusMonths(i);
            List<Invoice> list = InvoiceDAO.findInvoicesByMonth(ym.getYear(), ym.getMonthValue());
            sum12 += list.stream().mapToDouble(Invoice::getReimbursementAmount).sum();
        }
        lblReimb12m.setText(String.format("%.2f", sum12));

        // 5) PieChart-Kategorie-Verteilung
        Map<String, Long> counts = thisMonth.stream()
                .collect(Collectors.groupingBy(
                        inv -> inv.getCategory().toString(),
                        Collectors.counting()
                ));
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        counts.forEach((category, count) ->
                pieData.add(new PieChart.Data(category, count))
        );
        pieCategoryChart.setData(pieData);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
            Parent loginRoot = loader.load();
            Scene scene = new Scene(loginRoot);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.setResizable(false);
            stage.show();
            Session.clearCurrentUser();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(javafx.scene.control.Alert.AlertType.ERROR, "Could not load login screen.").showAndWait();
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