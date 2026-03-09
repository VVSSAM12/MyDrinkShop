package drinkshop.builder;

import drinkshop.domain.*;
import drinkshop.repository.Repository;
import drinkshop.repository.file.*;
import drinkshop.service.DrinkShopService;

public class DrinkShopServiceBuilder implements Builder<DrinkShopService> {
    @Override
    public DrinkShopService build() {
        Repository<Integer, Product> productRepo = new FileProductRepository("data/products.txt");
        Repository<Integer, Order> orderRepo = new FileOrderRepository("data/orders.txt", productRepo);
        Repository<Integer, Reteta> retetaRepo = new FileRetetaRepository("data/retete.txt");
        Repository<Integer, Stoc> stocRepo = new FileStocRepository("data/stocuri.txt");
        Repository<Integer, User> userRepo = new FileUserRepo("data/users.txt");
        
        return new DrinkShopService(productRepo, orderRepo, retetaRepo, stocRepo, userRepo);
    }
}
