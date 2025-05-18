package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Invoice;
import model.InvoiceDAO;
import model.Session;

import java.time.LocalDate;
import java.util.List;

public class RequestHistoryController {

    @FXML private TableView<Invoice> invoiceTable;
    @FXML private TableColumn<Invoice, String> submissionDateColumn;
    @FXML private TableColumn<Invoice, String> invoiceAmountColumn;
    @FXML private TableColumn<Invoice, String> reimbursementAmountColumn;
    @FXML private TableColumn<Invoice, String> categoryColumn;
    @FXML private TableColumn<Invoice, String> statusColumn;
    @FXML private PieChart invoicePieChart;

    private ObservableList<Invoice> invoiceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadInvoices();

        invoiceTable.setRowFactory(tv -> {
            TableRow<Invoice> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showInvoicePopup(row.getItem());
                }
            });
            return row;
        });
    }

    private void setupTable() {
        submissionDateColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSubmissionDate().toString()));

        invoiceAmountColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f €", data.getValue().getInvoiceAmount())));

        reimbursementAmountColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f €", data.getValue().getReimbursementAmount())));

        categoryColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCategory().toString()));

        statusColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatus().toString()));

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

    /**
     * Öffnet die Rechnung im Popup mit Scroll- und Zoom-Funktion
     */
    private void showInvoicePopup(Invoice invoice) {
        String rawName = invoice.getFileName();
        String trimmed = rawName.replaceAll("^/+", "");
        String publicUrl = "https://onvxredsmjqlufgjjojh.supabase.co"
                + "/storage/v1/object/public/rechnung//" + trimmed;
        System.out.println("Loading invoice image from: " + publicUrl);

        Image image = new Image(publicUrl, true);
        image.errorProperty().addListener((obs, oldErr, isErr) -> {
            if (isErr) {
                showAlert(Alert.AlertType.ERROR, "Could not load invoice image.");
            }
        });

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(800);
        imageView.addEventFilter(ScrollEvent.SCROLL, ev -> {
            double factor = ev.getDeltaY() > 0 ? 1.1 : 0.9;
            imageView.setFitWidth(imageView.getFitWidth() * factor);
            ev.consume();
        });

        ScrollPane scrollPane = new ScrollPane(imageView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        BorderPane pane = new BorderPane(scrollPane);
        pane.setStyle("-fx-background-color: rgba(0,0,0,0.8);");

        Stage popup = new Stage();
        popup.initOwner(invoiceTable.getScene().getWindow());
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Invoice Preview");
        popup.setScene(new Scene(pane, 800, 600));
        popup.showAndWait();
    }

    @FXML
    private void handleEditInvoice() {
        Invoice sel = invoiceTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an invoice to edit.");
            return;
        }
        LocalDate today = LocalDate.now();
        boolean sameMonth = sel.getSubmissionDate().getMonth() == today.getMonth()
                && sel.getSubmissionDate().getYear() == today.getYear();
        if (!sameMonth || sel.getStatus() == Invoice.InvoiceStatus.APPROVED) {
            showAlert(Alert.AlertType.WARNING, "Cannot edit past-month or approved invoices.");
            return;
        }

        TextInputDialog dlg = new TextInputDialog(String.valueOf(sel.getInvoiceAmount()));
        dlg.setTitle("Edit Invoice Amount");
        dlg.setHeaderText("Enter new amount:");
        dlg.showAndWait().ifPresent(v -> {
            try {
                double amt = Double.parseDouble(v);
                sel.setInvoiceAmount(amt);
                sel.setReimbursementAmount(Math.min(amt,
                        sel.getCategory() == Invoice.InvoiceCategory.RESTAURANT ? 3.0 : 2.5));
                if (InvoiceDAO.updateInvoice(sel)) {
                    invoiceTable.refresh(); updateChart();
                    showAlert(Alert.AlertType.INFORMATION, "Invoice updated.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Update failed.");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid number.");
            }
        });
    }

    @FXML
    private void deleteSelectedInvoice() {
        Invoice sel = invoiceTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Please select an invoice to delete.");
            return;
        }
        if (!sel.isEditable()) {
            showAlert(Alert.AlertType.WARNING, "Cannot delete past-month or approved invoices.");
            return;
        }
        if (InvoiceDAO.deleteInvoice(sel.getId())) {
            loadInvoices(); updateChart();
            showAlert(Alert.AlertType.INFORMATION, "Invoice deleted.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Delete failed.");
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("Request History");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}