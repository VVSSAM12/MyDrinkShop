package drinkshop.service;

import drinkshop.domain.Order;
import drinkshop.repository.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ReportService {
    private Repository<Integer, Order> OrderRepo;

    public ReportService(Repository<Integer, Order> repo) {
        this.OrderRepo = repo;
    }

    public double getTotalRevenue() {
        return OrderRepo.findAll().stream().mapToDouble(Order::getTotal).sum();
    }

    public int getTotalOrders() {
      List<Order> orders = StreamSupport.stream(OrderRepo.findAll().spliterator(), false)
              .toList();
        return OrderRepo.findAll().size();
    }
}
