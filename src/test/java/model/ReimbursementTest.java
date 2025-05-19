package model;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ReimbursementTest {

    @Test
    void testUpdateRestaurantAmount() {
        RefundConfigDAO dao = new RefundConfigDAO();
        double newAmount = 4.0;

        boolean success = dao.updateAmount("RESTAURANT", newAmount);
        assertTrue(success, "Administrator should be able to update reimbursement amount");

        double updatedAmount = dao.getCurrentAmounts().get("RESTAURANT");
        assertEquals(newAmount, updatedAmount, 0.01, "Reimbursement amount should be updated in database");
    }
}
