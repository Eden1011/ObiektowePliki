package org.ug.koszyk.sale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ug.koszyk.cart.Cart;
import org.ug.koszyk.product.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FreeGiftSaleTest {
  private Cart cart;
  private Product giftMug;
  private FreeGiftSale sale;
  private Product laptop;
  private Product phone;

  @BeforeEach
  void setUp() {
    cart = new Cart();
    giftMug = new Product("JavaMarkt Mug", "MUG001", 25.0);
    sale = new FreeGiftSale(200.0, giftMug, "Free mug for orders over 200 zł");

    laptop = new Product("Laptop", "LAP001", 2500.0);
    phone = new Product("Smartphone", "PHO001", 1200.0);

    cart.addProduct(laptop);
  }

  @Test
  void testConstructorValidation() {
    FreeGiftSale validSale = new FreeGiftSale(100.0, giftMug, "Test Sale");
    assertNotNull(validSale);

    assertThrows(IllegalArgumentException.class,
        () -> new FreeGiftSale(-100.0, giftMug, "Test Sale"));

    assertThrows(IllegalArgumentException.class,
        () -> new FreeGiftSale(100.0, null, "Test Sale"));

    assertThrows(IllegalArgumentException.class,
        () -> new FreeGiftSale(100.0, giftMug, null));

    assertThrows(IllegalArgumentException.class,
        () -> new FreeGiftSale(100.0, giftMug, ""));
    assertThrows(IllegalArgumentException.class,
        () -> new FreeGiftSale(100.0, giftMug, "   "));
  }

  @Test
  void testApply() {
    assertEquals(2500.0, cart.getTotalPrice(), 0.001);
    assertEquals(1, cart.getSize());

    sale.apply(cart);

    assertEquals(2, cart.getSize());
    assertEquals(2500.0, cart.getTotalPrice(), 0.001); // Gift is free, price doesn't change

    List<Product> products = cart.getProducts();
    Product foundGift = null;
    for (Product product : products) {
      if (product.getCode().equals(giftMug.getCode())) {
        foundGift = product;
        break;
      }
    }

    assertNotNull(foundGift);
    assertTrue(foundGift.isDiscounted());
    assertEquals(0.0, foundGift.getPrice(), 0.001);
    assertEquals(25.0, foundGift.getOriginalPrice(), 0.001);
  }

  @Test
  void testApplyBelowThreshold() {
    Cart smallCart = new Cart();
    smallCart.addProduct(new Product("Charger", "CHA001", 120.0));

    sale.apply(smallCart);

    assertEquals(1, smallCart.getSize());
    assertEquals(120.0, smallCart.getTotalPrice(), 0.001);
  }

  @Test
  void testApplyMultipleTimes() {
    sale.apply(cart);
    assertEquals(2, cart.getSize());

    sale.apply(cart);

    assertEquals(2, cart.getSize());
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
    assertEquals(2, cart.getSize());

    sale.cancel(cart);

    assertEquals(1, cart.getSize());
    assertEquals(2500.0, cart.getTotalPrice(), 0.001);

    List<Product> products = cart.getProducts();
    for (Product product : products) {
      assertNotEquals(giftMug.getCode(), product.getCode());
    }
  }

  @Test
  void testCancelWithoutApplied() {
    assertEquals(1, cart.getSize());

    sale.cancel(cart);

    assertEquals(1, cart.getSize());
    assertEquals(2500.0, cart.getTotalPrice(), 0.001);
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
    assertEquals(25.0, benefit, 0.001); // Value of the gift

    Cart smallCart = new Cart();
    smallCart.addProduct(new Product("Charger", "CHA001", 120.0));
    assertEquals(0.0, sale.calculateBenefit(smallCart), 0.001);

    Cart emptyCart = new Cart();
    assertEquals(0.0, sale.calculateBenefit(emptyCart), 0.001);

    assertEquals(0.0, sale.calculateBenefit(null), 0.001);
  }

  @Test
  void testGetDescription() {
    assertEquals("Free mug for orders over 200 zł", sale.getDescription());
  }
}
