package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class RequestHistoryController {

    @FXML
    private TableView<?> requestTable;

    @FXML
    private TableColumn<?, ?> dateColumn;

    @FXML
    private TableColumn<?, ?> typeColumn;

    @FXML
    private TableColumn<?, ?> amountColumn;

    @FXML
    private TableColumn<?, ?> statusColumn;

    @FXML
    public void initialize() {
        // Noch keine Logik, weil aktuell nur das visuelle Grundgerüst gebraucht wird
    }
}
