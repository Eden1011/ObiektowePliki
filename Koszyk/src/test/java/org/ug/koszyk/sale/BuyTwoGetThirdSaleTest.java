package org.ug.koszyk.sale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ug.koszyk.cart.Cart;
import org.ug.koszyk.product.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BuyTwoGetThirdFreeSaleTest {
  private Cart cart;
  private BuyTwoGetThirdFreeSale sale;
  private Product laptop;
  private Product phone;
  private Product headphones;
  private Product charger;

  @BeforeEach
  void setUp() {
    cart = new Cart();
    sale = new BuyTwoGetThirdFreeSale("Buy 2 products, get the 3rd cheapest for free");

    laptop = new Product("Laptop", "LAP001", 2500.0);
    phone = new Product("Smartphone", "PHO001", 1200.0);
    headphones = new Product("Headphones", "HEA001", 300.0);
    charger = new Product("Charger", "CHA001", 120.0);

    cart.addProduct(laptop);
    cart.addProduct(phone);
    cart.addProduct(headphones);
  }

  @Test
  void testConstructorValidation() {
    BuyTwoGetThirdFreeSale validSale = new BuyTwoGetThirdFreeSale("Test Sale");
    assertNotNull(validSale);

    assertThrows(IllegalArgumentException.class,
        () -> new BuyTwoGetThirdFreeSale(null));

    assertThrows(IllegalArgumentException.class,
        () -> new BuyTwoGetThirdFreeSale(""));
    assertThrows(IllegalArgumentException.class,
        () -> new BuyTwoGetThirdFreeSale("   "));
  }

  @Test
  void testApply() {
    assertEquals(4000.0, cart.getTotalPrice(), 0.001);
    assertFalse(laptop.isDiscounted());
    assertFalse(phone.isDiscounted());
    assertFalse(headphones.isDiscounted());

    sale.apply(cart);

    assertFalse(laptop.isDiscounted());
    assertFalse(phone.isDiscounted());
    assertTrue(headphones.isDiscounted());
    assertEquals(0.0, headphones.getPrice(), 0.001);

    assertEquals(3700.0, cart.getTotalPrice(), 0.001);
  }

  @Test
  void testApplyWithFourProducts() {
    cart.addProduct(charger);

    sale.apply(cart);

    assertFalse(laptop.isDiscounted());
    assertFalse(phone.isDiscounted());
    assertFalse(headphones.isDiscounted());
    assertTrue(charger.isDiscounted());
    assertEquals(0.0, charger.getPrice(), 0.001);

    assertEquals(4000.0, cart.getTotalPrice(), 0.001);
  }

  @Test
  void testApplyWithTwoProducts() {
    Cart smallCart = new Cart();
    smallCart.addProduct(laptop);
    smallCart.addProduct(phone);

    sale.apply(smallCart);

    assertFalse(laptop.isDiscounted());
    assertFalse(phone.isDiscounted());

    assertEquals(3700.0, smallCart.getTotalPrice(), 0.001);
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
    assertTrue(headphones.isDiscounted());

    sale.cancel(cart);

    assertFalse(laptop.isDiscounted());
    assertFalse(phone.isDiscounted());
    assertFalse(headphones.isDiscounted());
    assertEquals(300.0, headphones.getPrice(), 0.001);

    assertEquals(4000.0, cart.getTotalPrice(), 0.001);
  }

  @Test
  void testCancelMultipleFreeBies() {
    cart.addProduct(charger);
    cart.addProduct(new Product("Cable", "CAB001", 50.0));

    sale.apply(cart);

    List<Product> products = cart.getProducts();
    boolean foundFree = false;
    for (Product product : products) {
      if (product.isDiscounted() && product.getPrice() == 0.0) {
        foundFree = true;
        break;
      }
    }
    assertTrue(foundFree);

    sale.cancel(cart);

    for (Product product : cart.getProducts()) {
      if (product.isDiscounted() && product.getPrice() == 0.0) {
        fail("Found a free product after cancelling the sale");
      }
    }
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
    assertEquals(300.0, benefit, 0.001);

    Cart smallCart = new Cart();
    smallCart.addProduct(laptop);
    smallCart.addProduct(phone);
    assertEquals(0.0, sale.calculateBenefit(smallCart), 0.001);

    Cart emptyCart = new Cart();
    assertEquals(0.0, sale.calculateBenefit(emptyCart), 0.001);

    assertEquals(0.0, sale.calculateBenefit(null), 0.001);
  }

  @Test
  void testGetDescription() {
    assertEquals("Buy 2 products, get the 3rd cheapest for free", sale.getDescription());
  }
}
