package drinkshop.service;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReceiptService {

    public ReceiptService() {
    }

    /**
     * Generates a receipt (bon de casă) for the given order.
     *
     * @param order    the order to generate the receipt for
     * @param products the full list of products (used to look up current prices)
     * @return a formatted receipt string
     */
    public String generate(Order order, List<Product> products) {
        StringBuilder sb = new StringBuilder();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

        sb.append("========== BON DE CASA ==========\n");
        sb.append("Data: ").append(date).append("\n");
        sb.append("Comanda #").append(order.getId()).append("\n");
        sb.append("---------------------------------\n");

        double total = 0.0;
        for (OrderItem item : order.getItems()) {
            Product p = findProduct(products, item.getProduct().getId());
            String name = (p != null) ? p.getNume() : "Produs necunoscut";
            double price = (p != null) ? p.getPret() : 0.0;
            double lineTotal = price * item.getQuantity();
            total += lineTotal;

            sb.append(String.format("%-20s x%d  %8.2f RON\n", name, item.getQuantity(), lineTotal));
        }

        sb.append("---------------------------------\n");
        sb.append(String.format("TOTAL: %23.2f RON\n", total));
        sb.append("=================================\n");

        return sb.toString();
    }

    private Product findProduct(List<Product> products, int productId) {
        return products.stream()
                .filter(p -> p.getId() == productId)
                .findFirst()
                .orElse(null);
    }
}
