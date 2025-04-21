package org.ug.koszyk.sale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ug.koszyk.cart.Cart;
import org.ug.koszyk.product.Product;

import static org.junit.jupiter.api.Assertions.*;

class ProductCouponSaleTest {
  private Cart cart;
  private ProductCouponSale sale;
  private Product laptop;
  private Product phone;
  private Product headphones;

  @BeforeEach
  void setUp() {
    cart = new Cart();
    sale = new ProductCouponSale("PHO001", 30.0, "30% off smartphone");

    laptop = new Product("Laptop", "LAP001", 2500.0);
    phone = new Product("Smartphone", "PHO001", 1200.0);
    headphones = new Product("Headphones", "HEA001", 300.0);

    cart.addProduct(laptop);
    cart.addProduct(phone);
    cart.addProduct(headphones);
  }

  @Test
  void testConstructorValidation() {
    ProductCouponSale validSale = new ProductCouponSale("TEST001", 10.0, "Test Sale");
    assertNotNull(validSale);

    assertThrows(IllegalArgumentException.class,
        () -> new ProductCouponSale(null, 10.0, "Test Sale"));

    assertThrows(IllegalArgumentException.class,
        () -> new ProductCouponSale("", 10.0, "Test Sale"));
    assertThrows(IllegalArgumentException.class,
        () -> new ProductCouponSale("   ", 10.0, "Test Sale"));

    assertThrows(IllegalArgumentException.class,
        () -> new ProductCouponSale("TEST001", -10.0, "Test Sale"));

    assertThrows(IllegalArgumentException.class,
        () -> new ProductCouponSale("TEST001", 110.0, "Test Sale"));

    assertThrows(IllegalArgumentException.class,
        () -> new ProductCouponSale("TEST001", 10.0, null));

    assertThrows(IllegalArgumentException.class,
        () -> new ProductCouponSale("TEST001", 10.0, ""));
    assertThrows(IllegalArgumentException.class,
        () -> new ProductCouponSale("TEST001", 10.0, "   "));
  }

  @Test
  void testApply() {
    assertEquals(4000.0, cart.getTotalPrice(), 0.001);
    assertFalse(laptop.isDiscounted());
    assertFalse(phone.isDiscounted());
    assertFalse(headphones.isDiscounted());
    assertFalse(sale.isUsed());

    sale.apply(cart);

    assertFalse(laptop.isDiscounted());
    assertTrue(phone.isDiscounted());
    assertFalse(headphones.isDiscounted());
    assertTrue(sale.isUsed());

    assertEquals(840.0, phone.getPrice(), 0.001); // 1200 * 0.7
    assertEquals(1200.0, phone.getOriginalPrice(), 0.001);

    assertEquals(3640.0, cart.getTotalPrice(), 0.001); // 4000 - 360
  }

  @Test
  void testApplyWithoutTargetProduct() {
    Cart otherCart = new Cart();
    otherCart.addProduct(laptop);
    otherCart.addProduct(headphones);

    sale.apply(otherCart);

    assertFalse(laptop.isDiscounted());
    assertFalse(headphones.isDiscounted());
    assertFalse(sale.isUsed());

    assertEquals(2800.0, otherCart.getTotalPrice(), 0.001);
  }

  @Test
  void testApplyWithMultipleSameProducts() {
    Product anotherPhone = new Product("Another Smartphone", "PHO001", 1200.0);
    cart.addProduct(anotherPhone);

    sale.apply(cart);

    assertTrue(phone.isDiscounted() || anotherPhone.isDiscounted());
    if (phone.isDiscounted()) {
      assertFalse(anotherPhone.isDiscounted());
    } else {
      assertFalse(phone.isDiscounted());
    }

    assertEquals(5200.0 - 360.0, cart.getTotalPrice(), 0.001);
  }

  @Test
  void testApplyWhenAlreadyUsed() {
    sale.apply(cart);
    assertTrue(sale.isUsed());
    assertEquals(3640.0, cart.getTotalPrice(), 0.001);

    phone.revertPrice();

    sale.apply(cart);

    assertFalse(phone.isDiscounted());
    assertEquals(4000.0, cart.getTotalPrice(), 0.001);
  }

  @Test
  void testApplyEmptyCart() {
    Cart emptyCart = new Cart();

    sale.apply(emptyCart);

    assertFalse(sale.isUsed());

    assertEquals(0, emptyCart.getSize());
    assertEquals(0.0, emptyCart.getTotalPrice(), 0.001);
  }

  @Test
  void testApplyNullCart() {
    sale.apply(null);
    assertFalse(sale.isUsed());
  }

  @Test
  void testApplyToAlreadyDiscountedProduct() {
    phone.discountProduct(1000.0);

    Cart newCart = new Cart();
    newCart.addProduct(laptop);
    newCart.addProduct(phone);

    sale.apply(newCart);

    assertFalse(sale.isUsed());
    assertEquals(1000.0, phone.getPrice(), 0.001); // Original discount remains
  }

  @Test
  void testCancel() {
    sale.apply(cart);
    assertTrue(phone.isDiscounted());
    assertTrue(sale.isUsed());

    sale.cancel(cart);

    assertFalse(phone.isDiscounted());
    assertFalse(sale.isUsed());
    assertEquals(1200.0, phone.getPrice(), 0.001);

    assertEquals(4000.0, cart.getTotalPrice(), 0.001);
  }

  @Test
  void testCancelWhenNotUsed() {
    assertFalse(sale.isUsed());

    sale.cancel(cart);

    assertFalse(sale.isUsed());
    assertEquals(4000.0, cart.getTotalPrice(), 0.001);
  }

  @Test
  void testCancelEmptyCart() {
    sale.apply(cart);
    assertTrue(sale.isUsed());

    Cart emptyCart = new Cart();

    sale.cancel(emptyCart);

    assertTrue(sale.isUsed());
  }

  @Test
  void testCancelNullCart() {
    sale.apply(cart);
    assertTrue(sale.isUsed());

    sale.cancel(null);

    assertTrue(sale.isUsed());
  }

  @Test
  void testCancelWhenProductRemoved() {
    sale.apply(cart);
    assertTrue(sale.isUsed());

    cart.removeProduct(phone);

    sale.cancel(cart);

    assertFalse(sale.isUsed());
  }

  @Test
  void testCalculateBenefit() {
    double benefit = sale.calculateBenefit(cart);
    assertEquals(360.0, benefit, 0.001); // 1200 * 0.3

    Cart otherCart = new Cart();
    otherCart.addProduct(laptop);
    assertEquals(0.0, sale.calculateBenefit(otherCart), 0.001);

    Cart emptyCart = new Cart();
    assertEquals(0.0, sale.calculateBenefit(emptyCart), 0.001);

    assertEquals(0.0, sale.calculateBenefit(null), 0.001);

    sale.apply(cart);
    assertEquals(0.0, sale.calculateBenefit(cart), 0.001);
  }

  @Test
  void testGetDescription() {
    assertEquals("30% off smartphone", sale.getDescription());
  }
}
