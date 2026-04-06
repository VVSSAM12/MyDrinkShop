package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.AbstractRepository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceECPTest {

    private ProductService productService;

    // In-memory repository for testing (no file dependencies)
    private AbstractRepository<Integer, Product> productRepo;

    @BeforeAll
    static void setUpAll() {
        System.out.println("=== ECP Tests for updateProduct started ===");
    }

    @AfterAll
    static void tearDownAll() {
        System.out.println("=== ECP Tests for updateProduct finished ===");
    }

    @BeforeEach
    void setUp() {
        productRepo = new AbstractRepository<>() {
            @Override
            protected Integer getId(Product entity) {
                return entity.getId();
            }
        };

        ProductValidator productValidator = new ProductValidator();
        productService = new ProductService(productRepo, productValidator);

        // Arrange: add an existing product to update
        Product existing = new Product(1, "OldProduct", 20.0, CategorieBautura.JUICE, TipBautura.WATER_BASED);
        productRepo.save(existing);
    }

    @AfterEach
    void tearDown() {
        productRepo = null;
        productService = null;
    }

    /**
     * TC1 - ECP: EC1 (nume valid) + EC3 (pret valid)
     * Input: id=1, nume="Limonada", pret=25, categorie=JUICE, tip=WATER_BASED
     * Expected: record updated successfully
     */
    @Test
    void testUpdateProduct_TC1_NumeValid_PretValid() {
        // Arrange
        int id = 1;
        String nume = "Limonada";
        double pret = 25;
        CategorieBautura categorie = CategorieBautura.JUICE;
        TipBautura tip = TipBautura.WATER_BASED;

        // Act
        assertDoesNotThrow(() -> productService.updateProduct(id, nume, pret, categorie, tip));

        // Assert
        Product updated = productRepo.findOne(id);
        assertNotNull(updated);
        assertEquals("Limonada", updated.getNume());
        assertEquals(25, updated.getPret());
        assertEquals(CategorieBautura.JUICE, updated.getCategorie());
        assertEquals(TipBautura.WATER_BASED, updated.getTip());
    }

    /**
     * TC2 - ECP: EC1 (nume valid) + EC4 (pret invalid, <= 0)
     * Input: id=1, nume="Limonada", pret=-5, categorie=JUICE, tip=WATER_BASED
     * Expected: ValidationException - Pret invalid!
     */
    @Test
    void testUpdateProduct_TC2_NumeValid_PretInvalid() {
        // Arrange
        int id = 1;
        String nume = "Limonada";
        double pret = -5;
        CategorieBautura categorie = CategorieBautura.JUICE;
        TipBautura tip = TipBautura.WATER_BASED;

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> productService.updateProduct(id, nume, pret, categorie, tip));

        assertTrue(exception.getMessage().contains("Pret invalid!"));
    }

    /**
     * TC3 - ECP: EC2 (nume invalid, empty) + EC3 (pret valid)
     * Input: id=1, nume="", pret=25, categorie=JUICE, tip=WATER_BASED
     * Expected: ValidationException - Numele nu poate fi gol!
     */
    @Test
    void testUpdateProduct_TC3_NumeInvalid_PretValid() {
        // Arrange
        int id = 1;
        String nume = "";
        double pret = 25;
        CategorieBautura categorie = CategorieBautura.JUICE;
        TipBautura tip = TipBautura.WATER_BASED;

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> productService.updateProduct(id, nume, pret, categorie, tip));

        assertTrue(exception.getMessage().contains("Numele nu poate fi gol!"));
    }

    /**
     * TC4 - ECP: EC2 (nume invalid, empty) + EC4 (pret invalid, <= 0)
     * Input: id=1, nume="", pret=-5, categorie=JUICE, tip=WATER_BASED
     * Expected: ValidationException - Numele nu poate fi gol! + Pret invalid!
     */
    @Test
    void testUpdateProduct_TC4_NumeInvalid_PretInvalid() {
        // Arrange
        int id = 1;
        String nume = "";
        double pret = -5;
        CategorieBautura categorie = CategorieBautura.JUICE;
        TipBautura tip = TipBautura.WATER_BASED;

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> productService.updateProduct(id, nume, pret, categorie, tip));

        assertTrue(exception.getMessage().contains("Numele nu poate fi gol!"));
        assertTrue(exception.getMessage().contains("Pret invalid!"));
    }
}
