package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.repository.Repository;
import drinkshop.service.validator.OrderValidator;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

public class ProductService {

    private final Repository<Integer, Product> productRepo;
    private final ProductValidator productValidator;

    public ProductService(Repository<Integer, Product> productRepo, ProductValidator productValidator) {
        this.productRepo = productRepo;
        this.productValidator = productValidator;
    }

    public void addProduct(Product p) {
        String errors = "";
        List<Product> products = productRepo.findAll();

        if (p.getId() <= 0)
            errors += "ID invalid!\n";

        if (p.getNume().isBlank())
            errors += "Numele nu poate fi gol!\n";

        if (p.getPret() <= 0)
            errors += "Pret invalid!\n";
        
        for (Product p1 : products) {
            if (p.getId() == p1.getId())
                errors += "ID folosit deja!\n";
        }

        if (!errors.isEmpty())
            throw new ValidationException(errors);
        
        productRepo.save(p);
    }

    public void updateProduct(int id, String name, double price, CategorieBautura categorie, TipBautura tip) {
        Product updated = new Product(id, name, price, categorie, tip);
        productValidator.validate(updated);
        productRepo.update(updated);
    }

    public void deleteProduct(int id) {
        productRepo.delete(id);
    }

    public List<Product> getAllProducts() {
//        Iterable<Product> it=productRepo.findAll();
//        ArrayList<Product> products=new ArrayList<>();
//        it.forEach(products::add);
//        return products;

//        return StreamSupport.stream(productRepo.findAll().spliterator(), false)
//                    .collect(Collectors.toList());
        return productRepo.findAll();
    }

    public Product findById(int id) {
        return productRepo.findOne(id);
    }

    public List<Product> filterByCategorie(CategorieBautura categorie) {
        if (categorie == CategorieBautura.ALL) return getAllProducts();
        return getAllProducts().stream()
                .filter(p -> p.getCategorie() == categorie)
                .collect(Collectors.toList());
    }

    public List<Product> filterByTip(TipBautura tip) {
        if (tip == TipBautura.ALL) return getAllProducts();
        return getAllProducts().stream()
                .filter(p -> p.getTip() == tip)
                .collect(Collectors.toList());
    }
}