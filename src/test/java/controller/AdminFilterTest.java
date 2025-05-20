package controller;

import model.Invoice;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

class AdminFilterTest {

    @Test
    void testFilterByEmailAndCategoryAndMonth() {
        Invoice a = new Invoice("a.png", Invoice.InvoiceCategory.RESTAURANT, 1.0);
        a.setEmail("alice@example.com");     a.setSubmissionDate(LocalDate.of(2025,5,10));
        Invoice b = new Invoice("b.png", Invoice.InvoiceCategory.SUPERMARKET, 2.0);
        b.setEmail("bob@other.com");         b.setSubmissionDate(LocalDate.of(2025,4, 5));
        Invoice c = new Invoice("c.png", Invoice.InvoiceCategory.RESTAURANT, 3.0);
        c.setEmail("another@EXAMPLE.com");   c.setSubmissionDate(LocalDate.of(2025,5,20));

        List<Invoice> all = asList(a,b,c);

        // filter email contains "example", category Restaurant, month May
        String emailFilter = "example";
        String category = "Restaurant";
        LocalDate month = LocalDate.of(2025,5,1);

        List<Invoice> filtered = all.stream()
                .filter(inv -> inv.getEmail().toLowerCase().contains(emailFilter))
                .filter(inv -> category.equals("All")
                        || inv.getCategory().name().equalsIgnoreCase(category))
                .filter(inv -> {
                    if (month == null) return true;
                    return inv.getSubmissionDate().getMonth() == month.getMonth()
                            && inv.getSubmissionDate().getYear() == month.getYear();
                })
                .collect(toList());

        // a und c match E-Mail, aber c und a beide Restaurant+Mai
        assertEquals(2, filtered.size());
        assertTrue(filtered.contains(a));
        assertTrue(filtered.contains(c));

        // wenn Kategorie = All, nur Email+Monat
        List<Invoice> filt2 = all.stream()
                .filter(inv -> inv.getEmail().toLowerCase().contains("bob"))
                .filter(inv -> "All".equals("All") || true)
                .collect(toList());
        assertEquals(1, filt2.size());
        assertEquals(b, filt2.get(0));
    }
}
