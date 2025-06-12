package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
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
import java.util.Set;
import java.util.stream.Collectors;

public class RequestHistoryController {

    @FXML private TableView<Invoice> invoiceTable;
    @FXML private TableColumn<Invoice, String> submissionDateColumn;
    @FXML private TableColumn<Invoice, String> invoiceAmountColumn;
    @FXML private TableColumn<Invoice, String> reimbursementAmountColumn;
    @FXML private TableColumn<Invoice, String> categoryColumn;
    @FXML private TableColumn<Invoice, String> statusColumn;
    @FXML private PieChart invoicePieChart;
    @FXML private Button infoButton;

    private ObservableList<Invoice> invoiceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTable();
        loadInvoices();
        setupRowFactory();
        setupInfoButton();
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
        List<Invoice> all = InvoiceDAO.getAllInvoicesByUser(userId);

        Set<Integer> starredIds = InvoiceDAO.getStarredInvoicesByUser(userId).stream()
                .map(Invoice::getId).collect(Collectors.toSet());
        all.forEach(inv -> inv.setStarred(starredIds.contains(inv.getId())));

        invoiceList.setAll(all);
        updateChart();
    }

    private void updateChart() {
        long restaurantCount = invoiceList.stream()
                .filter(i -> i.getCategory() == Invoice.InvoiceCategory.RESTAURANT)
                .count();
        long supermarketCount = invoiceList.stream()
                .filter(i -> i.getCategory() == Invoice.InvoiceCategory.SUPERMARKET)
                .count();

        invoicePieChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Restaurant", restaurantCount),
                new PieChart.Data("Supermarket", supermarketCount)
        ));
    }

    private void setupRowFactory() {
        invoiceTable.setRowFactory(tv -> {
            TableRow<Invoice> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showInvoicePopup(row.getItem());
                }
            });

            ContextMenu cm = new ContextMenu();
            MenuItem starItem = new MenuItem();
            cm.getItems().add(starItem);

            row.itemProperty().addListener((obs, oldInv, newInv) -> {
                if (newInv == null) {
                    starItem.setDisable(true);
                } else {
                    starItem.setDisable(false);
                    starItem.setText(newInv.isStarred() ? "Unstar Invoice" : "Star Invoice");
                }
            });

            starItem.setOnAction(e -> {
                Invoice inv = row.getItem();
                boolean now = !inv.isStarred();
                InvoiceDAO.toggleStar(Session.getCurrentUser().getId(), inv.getId(), now);
                inv.setStarred(now);
            });

            row.setContextMenu(cm);
            return row;
        });
    }

    private void setupInfoButton() {
        infoButton.setOnAction(e -> showInfoDialog());
    }

    @FXML
    private void showInfoDialog() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("How does starring work?");
        a.setHeaderText("Starring via right-click");
        a.setContentText(
                "• Right-click on an invoice row to open the context menu.\n \n" +
                        "• Select “Star Invoice” to add it to your starred list (right-click again to remove).\n \n" +
                        "• Your starred invoices appear in the STARRED tab in the dashboard."
        );
        a.showAndWait();
    }

    private void showInvoicePopup(Invoice invoice) {
        String rawName = invoice.getFileName();
        String trimmed = rawName.replaceAll("^/+", "");
        String publicUrl = "https://onvxredsmjqlufgjjojh.supabase.co"
                + "/storage/v1/object/public/rechnung//" + trimmed;

        Image image = new Image(publicUrl, true);
        image.errorProperty().addListener((obs, oldErr, isErr) -> {
            if (isErr) showAlert(Alert.AlertType.ERROR, "Could not load invoice image.");
        });
        image.progressProperty().addListener((obs, oldP, newP) ->
                System.out.printf("Image loading: %.0f%%%n", newP.doubleValue()*100)
        );

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
                && sel.getSubmissionDate().getYear()  == today.getYear();

        // Abbrechen, wenn außerhalb des Monats oder bereits rejected
        if (!sameMonth || sel.getStatus() == Invoice.InvoiceStatus.REJECTED) {
            showAlert(Alert.AlertType.WARNING, "Cannot edit past-month or rejected invoices.");
            return;
        }

        // Öffne den Dialog zum Ändern des Betrags
        TextInputDialog dialog = new TextInputDialog(String.valueOf(sel.getInvoiceAmount()));
        dialog.setTitle("Edit Invoice");
        dialog.setHeaderText("Edit invoice amount:");
        dialog.setContentText("New amount:");

        dialog.showAndWait().ifPresent(newAmountStr -> {
            try {
                double newAmount = Double.parseDouble(newAmountStr);

                // Werte setzen
                sel.setInvoiceAmount(newAmount);
                sel.setReimbursementAmount(
                        Math.min(newAmount, sel.getCategory() == Invoice.InvoiceCategory.RESTAURANT ? 3.0 : 2.5)
                );

                // 1) eigentliche Update-Methode (betrifft type + amount)
                boolean updated = InvoiceDAO.updateInvoice(sel);
                // 2) und zusätzlich Status auf EDITED setzen
                boolean statusUpdated = InvoiceDAO.updateInvoiceStatus(sel.getId(), Invoice.InvoiceStatus.EDITED);

                if (updated && statusUpdated) {
                    loadInvoices();
                    showAlert(Alert.AlertType.INFORMATION,"Invoice updated and marked as EDITED.");
                } else {
                    showAlert(Alert.AlertType.INFORMATION,"Failed to update invoice.");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid amount format.");
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