package at.jku.se.lunchify.Controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import controller.AddInvoiceController;


public class AddInvoiceControllerTest {

    @Test
    public void testRestaurantOverThreshold() {
        double result = AddInvoiceController.calculateReimbursement("Restaurant", 5.0);
        assertEquals(3.0, result, 0.001, "Should be max 3€ reimbursement for Restaurant");
    }

    @Test
    public void testSupermarketOverThreshold() {
        double result = AddInvoiceController.calculateReimbursement("Supermarket", 3.0);
        assertEquals(2.5, result, 0.001, "Should be max 2.5€ reimbursement for Supermarket");
    }

    @Test
    public void testRestaurantBelowThreshold() {
        double result = AddInvoiceController.calculateReimbursement("Restaurant", 2.0);
        assertEquals(2.0, result, 0.001, "Should be full amount if under threshold");
    }

    @Test
    public void testSupermarketBelowThreshold() {
        double result = AddInvoiceController.calculateReimbursement("Supermarket", 1.5);
        assertEquals(1.5, result, 0.001, "Should be full amount if under threshold");
    }

    @Test
    public void testInvalidCategoryDefaultsToZero() {
        double result = AddInvoiceController.calculateReimbursement("InvalidType", 4.0);
        assertEquals(0.0, result, 0.001, "Invalid type should return 0.0 reimbursement");
    }
}

