package drinkshop.service;

import drinkshop.domain.Order;
import drinkshop.repository.Repository;

public class ReportService {
    private Repository<Integer, Order> OrderRepo;

    public ReportService(Repository<Integer, Order> repo) {
        this.OrderRepo = repo;
    }

    public double getTotalRevenue() {
        return OrderRepo.findAll().stream().mapToDouble(Order::getTotal).sum();
    }

    public int getTotalOrders() {
        return OrderRepo.findAll().size();
    }
}
