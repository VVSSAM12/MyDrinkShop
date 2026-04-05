package drinkshop.builder;

import drinkshop.domain.*;
import drinkshop.repository.Repository;
import drinkshop.repository.file.*;
import drinkshop.service.DrinkShopService;
import drinkshop.service.validator.OrderValidator;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.RetetaValidator;
import drinkshop.service.validator.StocValidator;

public class DrinkShopServiceBuilder implements Builder<DrinkShopService> {
    @Override
    public DrinkShopService build() {
        Repository<Integer, Product> productRepo = new FileProductRepository("data/products.txt");
        Repository<Integer, Order> orderRepo = new FileOrderRepository("data/orders.txt", productRepo);
        Repository<Integer, Reteta> retetaRepo = new FileRetetaRepository("data/retete.txt");
        Repository<Integer, Stoc> stocRepo = new FileStocRepository("data/stocuri.txt");
        Repository<Integer, User> userRepo = new FileUserRepo("data/users.txt");
        
        ProductValidator productValidator = new ProductValidator();        
        OrderValidator orderValidator = new OrderValidator();
        RetetaValidator retetaValidator = new RetetaValidator();
        StocValidator stocValidator = new StocValidator();

        
        
        return new DrinkShopService(
                productRepo,
                orderRepo,
                retetaRepo,
                stocRepo,
                userRepo,
                productValidator,
                orderValidator,
                retetaValidator,
                stocValidator
        );
    }
}
