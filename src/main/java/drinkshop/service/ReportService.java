package drinkshop.service;

import drinkshop.domain.Order;
import drinkshop.repository.Repository;

import java.util.List;

public class ReportService {

    private final Repository<Integer, Order> orderRepo;

    public ReportService(Repository<Integer, Order> orderRepo) {
        this.orderRepo = orderRepo;
    }

    /**
     * Calculates the total revenue from all orders in the repository.
     *
     * @return the sum of all order totals
     */
    public double getTotalRevenue() {
        List<Order> orders = orderRepo.findAll();
        return orders.stream()
                .mapToDouble(Order::getTotal)
                .sum();
    }
}
