package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.AbstractRepository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateProductServiceTest {

    private ProductService productService;

    @BeforeEach
    void setUp() {
        AbstractRepository<Integer, Product> productRepo = new AbstractRepository<>() {
            @Override
            protected Integer getId(Product entity) {
                return entity.getId();
            }
        };
        ProductValidator productValidator = new ProductValidator();
        productService = new ProductService(productRepo, productValidator);

        // Pre-populate a product to update
        Product existing = new Product(1, "Limonada", 15.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);
        productService.addProduct(existing);
    }

    // ==================== ECP Tests ====================

    // TestCase01 - ECP Pret Valid
    // Pret > 0 => produsul ar trebuii sa fie updatat
    @Test
    void ShouldUpdateProduct_WhenPriceIsValid() {
        assertDoesNotThrow(() ->
                productService.updateProduct(1, "Limonada", 25.0, CategorieBautura.JUICE, TipBautura.WATER_BASED)
        );
        Product updated = productService.findById(1);
        assertEquals(25.0, updated.getPret());
    }

    // TestCase02 - ECP Pret Invalid
    // Pret <= 0 => ar trebui sa fie aruncata o exceptie
    @Test
    void ShouldThrowException_WhenPriceIsInvalid() {
        assertThrows(ValidationException.class, () ->
                productService.updateProduct(1, "Limonada", -5.0, CategorieBautura.JUICE, TipBautura.WATER_BASED)
        );
    }

    // ==================== BVA Tests ====================

    // TestCase03 - BVA Pret Limita Inferioara Valida
    // Pret = 0.01 (just above lower boundary) => produsul trebuie updatat
    @Test
    void ShouldUpdateProduct_WhenPriceIsOverTheLowerBoundary() {
        assertDoesNotThrow(() ->
                productService.updateProduct(1, "Limonada", 0.01, CategorieBautura.JUICE, TipBautura.WATER_BASED)
        );
        Product updated = productService.findById(1);
        assertEquals(0.01, updated.getPret());
    }

    // TestCase04 - BVA Pret Pe Limita Inferioara Invalid
    // Pret = 0 (on lower boundary) => ar trebui sa fie aruncata o exceptie
    @Test
    void ShouldThrowException_WhenPriceIsOnTheLowerBoundary() {
        assertThrows(ValidationException.class, () ->
                productService.updateProduct(1, "Limonada", 0, CategorieBautura.JUICE, TipBautura.WATER_BASED)
        );
    }

    // TestCase05 - BVA Pret Sub Limita Inferioara Invalid
    // Pret = -0.01 (just below lower boundary) => ar trebui sa fie aruncata o exceptie
    @Test
    void ShouldThrowException_WhenPriceIsBelowTheLowerBoundary() {
        assertThrows(ValidationException.class, () ->
                productService.updateProduct(1, "Limonada", -0.01, CategorieBautura.JUICE, TipBautura.WATER_BASED)
        );
    }

    // TestCase06 - BVA Pret Valoare Nominala Valida
    // Pret = 50.0 (nominal valid value, well above boundary) => produsul trebuie updatat
    @Test
    void ShouldUpdateProduct_WhenPriceIsNominalValue() {
        assertDoesNotThrow(() ->
                productService.updateProduct(1, "Limonada", 50.0, CategorieBautura.JUICE, TipBautura.WATER_BASED)
        );
        Product updated = productService.findById(1);
        assertEquals(50.0, updated.getPret());
    }
}
