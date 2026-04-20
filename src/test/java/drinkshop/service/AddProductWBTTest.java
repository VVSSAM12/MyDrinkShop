package drinkshop.service;

import drinkshop.domain.CategorieBautura;
import drinkshop.domain.Product;
import drinkshop.domain.TipBautura;
import drinkshop.repository.AbstractRepository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * White-Box Tests for ProductService.addProduct()
 * Based on Control Flow Graph (CFG) analysis.
 *
 * CFG Nodes:
 *   1  = String errors = ""; List products = ...;
 *   2  = if (p.getId() <= 0)              [Cond01]
 *   3  = errors += "ID invalid!\n";
 *   4  = if (p.getNume().isBlank())        [Cond02]
 *   5  = errors += "Numele nu poate fi gol!\n";
 *   6  = if (p.getPret() <= 0)             [Cond03]
 *   7  = errors += "Pret invalid!\n";
 *   8  = for-loop condition (hasNext?)     [Cond04]
 *   9  = if (p.getId() == p1.getId())      [Cond05]
 *  10  = errors += "ID folosit deja!\n";
 *  11  = if (!errors.isEmpty())            [Cond06]
 *  12  = throw new ValidationException(errors);
 *  13  = productRepo.save(p);
 *  14  = end
 *
 * Cyclomatic Complexity: CC = 7
 *   CC1 = #Regions             = 7
 *   CC2 = Edges - Nodes + 2    = 19 - 14 + 2 = 7
 *   CC3 = #Conditions + 1      = 6 + 1 = 7
 *
 * Independent Paths:
 *   P01: 1-2(F)-4(F)-6(F)-8(F)-11(F)-13-14          [all valid, no products]
 *   P02: 1-2(T)-3-4(F)-6(F)-8(F)-11(T)-12-14        [invalid id]
 *   P03: 1-2(F)-4(T)-5-6(F)-8(F)-11(T)-12-14        [invalid name]
 *   P04: 1-2(F)-4(F)-6(T)-7-8(F)-11(T)-12-14        [invalid price]
 *   P05: 1-2(F)-4(F)-6(F)-8(T)-9(T)-10-8(F)-11(T)-12-14  [duplicate id]
 *   P06: 1-2(F)-4(F)-6(F)-8(T)-9(F)-8(F)-11(F)-13-14     [loop, no match]
 *   P07: 1-2(T)-3-4(T)-5-6(T)-7-8(F)-11(T)-12-14         [all invalid]
 */
@DisplayName("WBT: ProductService.addProduct()")
class AddProductWBTTest {

    private ProductService productService;
    private AbstractRepository<Integer, Product> productRepo;

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
    }

    @AfterEach
    void tearDown() {
        productRepo = null;
        productService = null;
    }

    // =====================================================================
    // PATH COVERAGE (apc) — 7 independent paths
    // =====================================================================

    /**
     * F02_TC01 — Path P01: 1-2(F)-4(F)-6(F)-8(F)-11(F)-13-14
     * All inputs valid, products list is empty => product saved successfully.
     * Covers: sc(1,2,4,6,8,11,13,14), Cond01=F, Cond02=F, Cond03=F, Cond04=F, Cond06=F
     */
    @Test
    @DisplayName("TC01 [P01] Valid product, empty repo => saved")
    void testTC01_Path01_AllValid_EmptyRepo() {
        // Arrange
        Product p = new Product(1, "Espresso", 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act
        assertDoesNotThrow(() -> productService.addProduct(p));

        // Assert
        assertEquals(1, productService.getAllProducts().size());
        assertNotNull(productRepo.findOne(1));
        assertEquals("Espresso", productRepo.findOne(1).getNume());
    }

    /**
     * F02_TC02 — Path P02: 1-2(T)-3-4(F)-6(F)-8(F)-11(T)-12-14
     * Invalid id (<= 0), valid name, valid price, empty products list => ValidationException.
     * Covers: sc(1,2,3,4,6,8,11,12,14), Cond01=T, Cond02=F, Cond03=F, Cond04=F, Cond06=T
     */
    @Test
    @DisplayName("TC02 [P02] Invalid id => ValidationException")
    void testTC02_Path02_InvalidId() {
        // Arrange
        Product p = new Product(-1, "Espresso", 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act & Assert
        ValidationException ex = assertThrows(ValidationException.class,
                () -> productService.addProduct(p));
        assertTrue(ex.getMessage().contains("ID invalid!"));
        assertFalse(ex.getMessage().contains("Numele nu poate fi gol!"));
        assertFalse(ex.getMessage().contains("Pret invalid!"));
    }

    /**
     * F02_TC03 — Path P03: 1-2(F)-4(T)-5-6(F)-8(F)-11(T)-12-14
     * Valid id, blank name, valid price => ValidationException.
     * Covers: sc(1,2,4,5,6,8,11,12,14), Cond01=F, Cond02=T, Cond03=F, Cond04=F, Cond06=T
     */
    @Test
    @DisplayName("TC03 [P03] Blank name => ValidationException")
    void testTC03_Path03_BlankName() {
        // Arrange
        Product p = new Product(1, "   ", 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act & Assert
        ValidationException ex = assertThrows(ValidationException.class,
                () -> productService.addProduct(p));
        assertTrue(ex.getMessage().contains("Numele nu poate fi gol!"));
        assertFalse(ex.getMessage().contains("ID invalid!"));
        assertFalse(ex.getMessage().contains("Pret invalid!"));
    }

    /**
     * F02_TC04 — Path P04: 1-2(F)-4(F)-6(T)-7-8(F)-11(T)-12-14
     * Valid id, valid name, invalid price (<= 0) => ValidationException.
     * Covers: sc(1,2,4,6,7,8,11,12,14), Cond01=F, Cond02=F, Cond03=T, Cond04=F, Cond06=T
     */
    @Test
    @DisplayName("TC04 [P04] Invalid price => ValidationException")
    void testTC04_Path04_InvalidPrice() {
        // Arrange
        Product p = new Product(1, "Espresso", -5.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act & Assert
        ValidationException ex = assertThrows(ValidationException.class,
                () -> productService.addProduct(p));
        assertTrue(ex.getMessage().contains("Pret invalid!"));
        assertFalse(ex.getMessage().contains("ID invalid!"));
        assertFalse(ex.getMessage().contains("Numele nu poate fi gol!"));
    }

    /**
     * F02_TC05 — Path P05: 1-2(F)-4(F)-6(F)-8(T)-9(T)-10-8(F)-11(T)-12-14
     * Valid inputs but duplicate id exists in repo => ValidationException.
     * Covers: sc(1,2,4,6,8,9,10,11,12,14), Cond01=F, Cond02=F, Cond03=F, Cond04=T, Cond05=T, Cond06=T
     */
    @Test
    @DisplayName("TC05 [P05] Duplicate id => ValidationException")
    void testTC05_Path05_DuplicateId() {
        // Arrange — pre-populate with same id
        Product existing = new Product(1, "Latte", 15.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);
        productRepo.save(existing);
        Product p = new Product(1, "Espresso", 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act & Assert
        ValidationException ex = assertThrows(ValidationException.class,
                () -> productService.addProduct(p));
        assertTrue(ex.getMessage().contains("ID folosit deja!"));
    }

    /**
     * F02_TC06 — Path P06: 1-2(F)-4(F)-6(F)-8(T)-9(F)-8(F)-11(F)-13-14
     * Valid inputs, products list is non-empty but no id match => product saved.
     * Covers: sc(1,2,4,6,8,9,11,13,14), Cond01=F, Cond02=F, Cond03=F, Cond04=T then F, Cond05=F, Cond06=F
     */
    @Test
    @DisplayName("TC06 [P06] Non-empty repo, no id match => saved")
    void testTC06_Path06_NoIdMatch() {
        // Arrange — pre-populate with different id
        Product existing = new Product(99, "Latte", 15.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY);
        productRepo.save(existing);
        Product p = new Product(1, "Espresso", 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act
        assertDoesNotThrow(() -> productService.addProduct(p));

        // Assert
        assertEquals(2, productService.getAllProducts().size());
        assertNotNull(productRepo.findOne(1));
    }

    /**
     * F02_TC07 — Path P07: 1-2(T)-3-4(T)-5-6(T)-7-8(F)-11(T)-12-14
     * All inputs invalid => ValidationException with all error messages.
     * Covers: sc(1,2,3,4,5,6,7,8,11,12,14), Cond01=T, Cond02=T, Cond03=T, Cond04=F, Cond06=T
     */
    @Test
    @DisplayName("TC07 [P07] All invalid => ValidationException with all errors")
    void testTC07_Path07_AllInvalid() {
        // Arrange
        Product p = new Product(-1, "", -5.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act & Assert
        ValidationException ex = assertThrows(ValidationException.class,
                () -> productService.addProduct(p));
        assertTrue(ex.getMessage().contains("ID invalid!"));
        assertTrue(ex.getMessage().contains("Numele nu poate fi gol!"));
        assertTrue(ex.getMessage().contains("Pret invalid!"));
    }

    // =====================================================================
    // LOOP COVERAGE (lc) — simple loop at node 8 (for-each over products)
    //   0 iterations, 1 iteration, 2 iterations, n iterations
    // =====================================================================

    @Nested
    @DisplayName("Loop Coverage — for-each over products")
    class LoopCoverageTests {

        /**
         * F02_TC08 — Loop: 0 iterations (empty products list)
         */
        @Test
        @DisplayName("TC08 [Loop 0] Empty products list => no loop body")
        void testTC08_Loop0_EmptyProductsList() {
            // Arrange — repo is empty
            Product p = new Product(1, "Espresso", 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

            // Act
            assertDoesNotThrow(() -> productService.addProduct(p));

            // Assert
            assertEquals(1, productService.getAllProducts().size());
        }

        /**
         * F02_TC09 — Loop: 1 iteration, no id match
         */
        @Test
        @DisplayName("TC09 [Loop 1] 1 product in repo, no match => saved")
        void testTC09_Loop1_NoMatch() {
            // Arrange
            productRepo.save(new Product(10, "Latte", 15.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY));
            Product p = new Product(1, "Espresso", 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

            // Act
            assertDoesNotThrow(() -> productService.addProduct(p));

            // Assert
            assertEquals(2, productService.getAllProducts().size());
        }

        /**
         * F02_TC10 — Loop: 2 iterations, match on second
         */
        @Test
        @DisplayName("TC10 [Loop 2] 2 products in repo, match on second => ValidationException")
        void testTC10_Loop2_MatchOnSecond() {
            // Arrange
            productRepo.save(new Product(10, "Latte", 15.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY));
            productRepo.save(new Product(1, "Cappuccino", 18.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY));
            Product p = new Product(1, "Espresso", 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

            // Act & Assert
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> productService.addProduct(p));
            assertTrue(ex.getMessage().contains("ID folosit deja!"));
        }

        /**
         * F02_TC11 — Loop: n iterations (n=3), no match => saved
         */
        @Test
        @DisplayName("TC11 [Loop n] 3 products in repo, no match => saved")
        void testTC11_LoopN_NoMatch() {
            // Arrange
            productRepo.save(new Product(10, "Latte", 15.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY));
            productRepo.save(new Product(20, "Cappuccino", 18.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY));
            productRepo.save(new Product(30, "Mocha", 22.0, CategorieBautura.SPECIAL_COFFEE, TipBautura.DAIRY));
            Product p = new Product(1, "Espresso", 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

            // Act
            assertDoesNotThrow(() -> productService.addProduct(p));

            // Assert
            assertEquals(4, productService.getAllProducts().size());
        }

        /**
         * F02_TC12 — Loop: n+1 iterations (n=4), match on last => ValidationException
         */
        @Test
        @DisplayName("TC12 [Loop n+1] 4 products, match on last => ValidationException")
        void testTC12_LoopNPlus1_MatchOnLast() {
            // Arrange
            productRepo.save(new Product(10, "Latte", 15.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY));
            productRepo.save(new Product(20, "Cappuccino", 18.0, CategorieBautura.MILK_COFFEE, TipBautura.DAIRY));
            productRepo.save(new Product(30, "Mocha", 22.0, CategorieBautura.SPECIAL_COFFEE, TipBautura.DAIRY));
            productRepo.save(new Product(1, "Americano", 12.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC));
            Product p = new Product(1, "Espresso", 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

            // Act & Assert
            ValidationException ex = assertThrows(ValidationException.class,
                    () -> productService.addProduct(p));
            assertTrue(ex.getMessage().contains("ID folosit deja!"));
        }
    }

    // =====================================================================
    // MULTIPLE CONDITION COVERAGE (mcc) — additional combinations
    // =====================================================================

    /**
     * F02_TC13 — MCC: Cond01=T, Cond02=F, Cond03=T
     * Invalid id + valid name + invalid price => two errors
     */
    @Test
    @DisplayName("TC13 [MCC] Invalid id + invalid price => two errors")
    void testTC13_MCC_InvalidId_InvalidPrice() {
        // Arrange
        Product p = new Product(0, "Espresso", 0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act & Assert
        ValidationException ex = assertThrows(ValidationException.class,
                () -> productService.addProduct(p));
        assertTrue(ex.getMessage().contains("ID invalid!"));
        assertTrue(ex.getMessage().contains("Pret invalid!"));
        assertFalse(ex.getMessage().contains("Numele nu poate fi gol!"));
    }

    /**
     * F02_TC14 — MCC: Cond01=F, Cond02=T, Cond03=T
     * Valid id + blank name + invalid price => two errors
     */
    @Test
    @DisplayName("TC14 [MCC] Blank name + invalid price => two errors")
    void testTC14_MCC_BlankName_InvalidPrice() {
        // Arrange
        Product p = new Product(1, "", 0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act & Assert
        ValidationException ex = assertThrows(ValidationException.class,
                () -> productService.addProduct(p));
        assertFalse(ex.getMessage().contains("ID invalid!"));
        assertTrue(ex.getMessage().contains("Numele nu poate fi gol!"));
        assertTrue(ex.getMessage().contains("Pret invalid!"));
    }

    /**
     * F02_TC15 — MCC: Cond01=T, Cond02=T, Cond03=F
     * Invalid id + blank name + valid price => two errors
     */
    @Test
    @DisplayName("TC15 [MCC] Invalid id + blank name => two errors")
    void testTC15_MCC_InvalidId_BlankName() {
        // Arrange
        Product p = new Product(-1, "", 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act & Assert
        ValidationException ex = assertThrows(ValidationException.class,
                () -> productService.addProduct(p));
        assertTrue(ex.getMessage().contains("ID invalid!"));
        assertTrue(ex.getMessage().contains("Numele nu poate fi gol!"));
        assertFalse(ex.getMessage().contains("Pret invalid!"));
    }

    /**
     * F02_TC16 — Boundary: id = 0 (boundary for id <= 0)
     */
    @ParameterizedTest(name = "TC16 [Boundary] id={0} => ValidationException")
    @ValueSource(ints = {0, -1})
    @DisplayName("TC16 [Boundary] id on/below boundary => ValidationException")
    void testTC16_BoundaryId(int id) {
        // Arrange
        Product p = new Product(id, "Espresso", 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act & Assert
        ValidationException ex = assertThrows(ValidationException.class,
                () -> productService.addProduct(p));
        assertTrue(ex.getMessage().contains("ID invalid!"));
    }

    /**
     * F02_TC17 — Boundary: price = 0 (boundary for pret <= 0)
     */
    @Test
    @DisplayName("TC17 [Boundary] price = 0 => ValidationException")
    void testTC17_BoundaryPriceZero() {
        // Arrange
        Product p = new Product(1, "Espresso", 0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act & Assert
        ValidationException ex = assertThrows(ValidationException.class,
                () -> productService.addProduct(p));
        assertTrue(ex.getMessage().contains("Pret invalid!"));
    }

    /**
     * F02_TC18 — RepeatedTest: verify addProduct is idempotent-consistent
     */
    @RepeatedTest(value = 3, name = "TC18 [Repeated {currentRepetition}/{totalRepetitions}] Consistency check")
    @DisplayName("TC18 [Repeated] Consistency check")
    void testTC18_RepeatedConsistency(RepetitionInfo repetitionInfo) {
        // Arrange
        int id = 100 + repetitionInfo.getCurrentRepetition();
        Product p = new Product(id, "Espresso", 10.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);

        // Act
        assertDoesNotThrow(() -> productService.addProduct(p));

        // Assert
        assertNotNull(productRepo.findOne(id));
    }
}
