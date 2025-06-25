package controller;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class AdminDashboardControllerTest {

    private AdminDashboardController controller;

    @BeforeEach
    void setUp() {
        controller = new AdminDashboardController();
    }

    @Test
    void testExportPayrollJSONExists() throws NoSuchMethodException {
        Method method = AdminDashboardController.class.getDeclaredMethod("exportPayrollJSON");
        assertNotNull(method);
    }

    @Test
    void testExportCSVExists() throws NoSuchMethodException {
        Method method = AdminDashboardController.class.getDeclaredMethod("exportCSV");
        assertNotNull(method);
    }

    @Test
    void testExportPayrollPDFExists() throws NoSuchMethodException {
        Method method = AdminDashboardController.class.getDeclaredMethod("exportPayrollPDF");
        assertNotNull(method);
    }

    @Test
    void testExportPayrollXMLExists() throws NoSuchMethodException {
        Method method = AdminDashboardController.class.getDeclaredMethod("exportPayrollXML");
        assertNotNull(method);
    }
}
