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
    @FXML private TableColumn<Invoice, String> categoryColumn;
    @FXML private TableColumn<Invoice, String> amountColumn;
    @FXML private TableColumn<Invoice, String> statusColumn;
    @FXML private PieChart invoicePieChart;

    // Basis-URL zu deinem Supabase-Projekt und Bucket-Name
    private static final String SUPABASE_URL    = "https://onvxredsmjqlufgjjojh.supabase.co";
    private static final String SUPABASE_BUCKET = "rechnung";

    private ObservableList<Invoice> invoiceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadInvoices();

        // Double-Click auf Zeile öffnet das Popup
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
        amountColumn.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("%.2f €", data.getValue().getInvoiceAmount())));
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
     * Zeigt die Rechnung in einem Popup mit Zoom- und Scroll-Funktion.
     * Nutzt die Public-URL mit doppeltem Slash für den führenden Dateinamen.
     */
    private void showInvoicePopup(Invoice invoice) {
        // 1. Hole und bereinige den Dateinamen (entferne alle führenden Slashes)
        String rawName = invoice.getFileName();
        String trimmed = rawName.replaceAll("^/+", "");
        // 2. Baue URL mit genau zwei Slashes: bucket + // + filename
        String publicUrl = SUPABASE_URL
                + "/storage/v1/object/public/"
                + SUPABASE_BUCKET
                + "//"
                + trimmed;
        System.out.println("Loading invoice image from: " + publicUrl);

        // 3. Lade das Bild
        Image image = new Image(publicUrl, true);
        image.errorProperty().addListener((obs, oldErr, isErr) -> {
            if (isErr) {
                System.err.println("Image load failed: " + image.getException());
                showAlert(Alert.AlertType.ERROR, "Could not load invoice image.");
            }
        });
        image.progressProperty().addListener((obs, oldP, newP) ->
                System.out.printf("Image loading: %.0f%%%n", newP.doubleValue()*100)
        );

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(800);
        // Zoom per Mausrad
        imageView.addEventFilter(ScrollEvent.SCROLL, ev -> {
            double factor = ev.getDeltaY() > 0 ? 1.1 : 0.9;
            imageView.setFitWidth(imageView.getFitWidth() * factor);
            ev.consume();
        });

        // ScrollPane zum Verschieben
        ScrollPane scrollPane = new ScrollPane(imageView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        BorderPane pane = new BorderPane(scrollPane);
        pane.setStyle("-fx-background-color: rgba(0,0,0,0.8);");

        Scene scene = new Scene(pane, 800, 600);
        Stage popup = new Stage();
        popup.initOwner(invoiceTable.getScene().getWindow());
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Invoice Preview");
        popup.setScene(scene);
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
            showAlert(Alert.AlertType.WARNING,
                    "Cannot edit past-month or approved invoices.");
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
                boolean ok = InvoiceDAO.updateInvoice(sel);
                if (ok) {
                    invoiceTable.refresh();
                    updateChart();
                    showAlert(Alert.AlertType.INFORMATION, "Invoice updated.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Update failed.");
                }
            } catch (NumberFormatException ex) {
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
            showAlert(Alert.AlertType.WARNING,
                    "Cannot delete past-month or approved invoices.");
            return;
        }
        boolean deleted = InvoiceDAO.deleteInvoice(sel.getId());
        if (deleted) {
            loadInvoices();
            updateChart();
            showAlert(Alert.AlertType.INFORMATION, "Invoice deleted.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Delete failed.");
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type);
        a.setTitle("Request History");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}