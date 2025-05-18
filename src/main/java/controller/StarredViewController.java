package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.Invoice;
import model.InvoiceDAO;
import model.Session;

import java.util.List;

public class StarredViewController {

    @FXML private TableView<Invoice> starredTable;
    @FXML private TableColumn<Invoice,String> submissionDateColumn;
    @FXML private TableColumn<Invoice,String> invoiceAmountColumn;
    @FXML private TableColumn<Invoice,String> reimbursementAmountColumn;
    @FXML private TableColumn<Invoice,String> categoryColumn;
    @FXML private TableColumn<Invoice,String> statusColumn;

    private ObservableList<Invoice> starredList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
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

        loadStarred();
        starredTable.setItems(starredList);
    }

    private void loadStarred() {
        int userId = Session.getCurrentUser().getId();
        List<Invoice> starred = InvoiceDAO.getStarredInvoicesByUser(userId);
        starredList.setAll(starred);
    }
}
