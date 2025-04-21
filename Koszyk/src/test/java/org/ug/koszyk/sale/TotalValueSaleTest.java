package org.ug.koszyk.sale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ug.koszyk.cart.Cart;
import org.ug.koszyk.product.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TotalValueDiscountSaleTest {
  private Cart cart;
  private TotalValueDiscountSale sale;
  private Product laptop;
  private Product phone;
  private Product headphones;

  @BeforeEach
  void setUp() {
    cart = new Cart();
    sale = new TotalValueDiscountSale(300.0, 5.0, "5% discount for orders over 300 zł");

    laptop = new Product("Laptop", "LAP001", 2500.0);
    phone = new Product("Smartphone", "PHO001", 1200.0);
    headphones = new Product("Headphones", "HEA001", 300.0);

    cart.addProduct(laptop);
    cart.addProduct(phone);
  }

  @Test
  void testConstructorValidation() {
    TotalValueDiscountSale validSale = new TotalValueDiscountSale(100.0, 10.0, "Test Sale");
    assertNotNull(validSale);

    assertThrows(IllegalArgumentException.class,
        () -> new TotalValueDiscountSale(-100.0, 10.0, "Test Sale"));

    assertThrows(IllegalArgumentException.class,
        () -> new TotalValueDiscountSale(100.0, -10.0, "Test Sale"));

    assertThrows(IllegalArgumentException.class,
        () -> new TotalValueDiscountSale(100.0, 110.0, "Test Sale"));

    assertThrows(IllegalArgumentException.class,
        () -> new TotalValueDiscountSale(100.0, 10.0, null));

    assertThrows(IllegalArgumentException.class,
        () -> new TotalValueDiscountSale(100.0, 10.0, ""));
    assertThrows(IllegalArgumentException.class,
        () -> new TotalValueDiscountSale(100.0, 10.0, "   "));
  }

  @Test
  void testApply() {
    assertEquals(3700.0, cart.getTotalPrice(), 0.001);
    assertFalse(laptop.isDiscounted());
    assertFalse(phone.isDiscounted());

    sale.apply(cart);

    assertTrue(laptop.isDiscounted());
    assertTrue(phone.isDiscounted());
    assertEquals(2375.0, laptop.getPrice(), 0.001); // 2500 * 0.95
    assertEquals(1140.0, phone.getPrice(), 0.001); // 1200 * 0.95

    assertEquals(3515.0, cart.getTotalPrice(), 0.001); // 3700 * 0.95
  }

  @Test
  void testApplyBelowThreshold() {
    Cart smallCart = new Cart();
    smallCart.addProduct(new Product("Charger", "CHA001", 120.0));

    sale.apply(smallCart);

    List<Product> products = smallCart.getProducts();
    for (Product product : products) {
      assertFalse(product.isDiscounted());
    }

    assertEquals(120.0, smallCart.getTotalPrice(), 0.001);
  }

  @Test
  void testApplyEmptyCart() {
    Cart emptyCart = new Cart();

    sale.apply(emptyCart);

    assertEquals(0, emptyCart.getSize());
    assertEquals(0.0, emptyCart.getTotalPrice(), 0.001);
  }

  @Test
  void testApplyNullCart() {
    sale.apply(null);
  }

  @Test
  void testCancel() {
    sale.apply(cart);
    assertTrue(laptop.isDiscounted());
    assertTrue(phone.isDiscounted());

    sale.cancel(cart);

    assertFalse(laptop.isDiscounted());
    assertFalse(phone.isDiscounted());
    assertEquals(2500.0, laptop.getPrice(), 0.001);
    assertEquals(1200.0, phone.getPrice(), 0.001);

    assertEquals(3700.0, cart.getTotalPrice(), 0.001);
  }

  @Test
  void testCancelEmptyCart() {
    Cart emptyCart = new Cart();

    sale.cancel(emptyCart);

    assertEquals(0, emptyCart.getSize());
    assertEquals(0.0, emptyCart.getTotalPrice(), 0.001);
  }

  @Test
  void testCancelNullCart() {
    sale.cancel(null);
  }

  @Test
  void testCalculateBenefit() {
    double benefit = sale.calculateBenefit(cart);
    assertEquals(185.0, benefit, 0.001); // 3700 * 0.05

    Cart smallCart = new Cart();
    smallCart.addProduct(new Product("Charger", "CHA001", 120.0));
    assertEquals(0.0, sale.calculateBenefit(smallCart), 0.001);

    Cart emptyCart = new Cart();
    assertEquals(0.0, sale.calculateBenefit(emptyCart), 0.001);

    assertEquals(0.0, sale.calculateBenefit(null), 0.001);
  }

  @Test
  void testGetDescription() {
    assertEquals("5% discount for orders over 300 zł", sale.getDescription());
  }
}
