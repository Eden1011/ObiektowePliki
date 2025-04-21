package org.ug.koszyk.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
  private Product laptop;
  private Product phone;
  private Product headphones;
  private Product charger;
  private List<Product> products;

  @BeforeEach
  void setUp() {
    laptop = new Product("Laptop", "LAP001", 2500.0);
    phone = new Product("Smartphone", "PHO001", 1200.0);
    headphones = new Product("Headphones", "HEA001", 300.0);
    charger = new Product("Charger", "CHA001", 120.0);

    products = new ArrayList<>(Arrays.asList(laptop, phone, headphones, charger));
  }

  @Test
  void testConstructorValidation() {
    Product valid = new Product("Test", "TEST001", 10.0);
    assertNotNull(valid);

    assertThrows(IllegalArgumentException.class, () -> new Product(null, "CODE", 10.0));

    assertThrows(IllegalArgumentException.class, () -> new Product("", "CODE", 10.0));
    assertThrows(IllegalArgumentException.class, () -> new Product("  ", "CODE", 10.0));

    assertThrows(IllegalArgumentException.class, () -> new Product("Name", null, 10.0));

    assertThrows(IllegalArgumentException.class, () -> new Product("Name", "", 10.0));
    assertThrows(IllegalArgumentException.class, () -> new Product("Name", "  ", 10.0));

    assertThrows(IllegalArgumentException.class, () -> new Product("Name", "CODE", -10.0));
  }

  @Test
  void testCalculateTotalPrice() {
    assertEquals(4120.0, Product.calculateTotalPrice(products), 0.001);

    assertEquals(0.0, Product.calculateTotalPrice(new ArrayList<>()), 0.001);

    assertEquals(0.0, Product.calculateTotalPrice(null), 0.001);
  }

  @Test
  void testSortWithComparator() {
    Comparator<Product> ascendingPrice = Comparator.comparing(Product::getPrice);
    List<Product> sorted = Product.sort(products, ascendingPrice);

    assertEquals(charger, sorted.get(0));
    assertEquals(headphones, sorted.get(1));
    assertEquals(phone, sorted.get(2));
    assertEquals(laptop, sorted.get(3));

    List<Product> emptyList = Product.sort(new ArrayList<>(), ascendingPrice);
    assertTrue(emptyList.isEmpty());

    List<Product> nullList = Product.sort(null, ascendingPrice);
    assertTrue(nullList.isEmpty());
  }

  @Test
  void testSortDefault() {
    List<Product> sorted = Product.sort(products);

    assertEquals(laptop, sorted.get(0));
    assertEquals(phone, sorted.get(1));
    assertEquals(headphones, sorted.get(2));
    assertEquals(charger, sorted.get(3));

    List<Product> emptyList = Product.sort(new ArrayList<>());
    assertTrue(emptyList.isEmpty());

    List<Product> nullList = Product.sort(null);
    assertTrue(nullList.isEmpty());
  }

  @Test
  void testFindCheapest() {
    Product cheapest = Product.findCheapest(products, false);
    assertEquals(charger, cheapest);

    Product mostExpensive = Product.findCheapest(products, true);
    assertEquals(laptop, mostExpensive);

    assertNull(Product.findCheapest(new ArrayList<>(), false));

    assertNull(Product.findCheapest(null, false));
  }

  @Test
  void testFindCheapestSlice() {
    List<Product> cheapestTwo = Product.findCheapest(products, false, 2);
    assertEquals(2, cheapestTwo.size());
    assertEquals(charger, cheapestTwo.get(0));
    assertEquals(headphones, cheapestTwo.get(1));

    List<Product> expensiveTwo = Product.findCheapest(products, true, 2);
    assertEquals(2, expensiveTwo.size());
    assertEquals(laptop, expensiveTwo.get(0));
    assertEquals(phone, expensiveTwo.get(1));

    List<Product> allProducts = Product.findCheapest(products, false, 10);
    assertEquals(4, allProducts.size());

    List<Product> emptyList = Product.findCheapest(new ArrayList<>(), false, 2);
    assertTrue(emptyList.isEmpty());

    List<Product> nullList = Product.findCheapest(null, false, 2);
    assertTrue(nullList.isEmpty());

    assertThrows(IllegalArgumentException.class, () -> Product.findCheapest(products, false, 0));
    assertThrows(IllegalArgumentException.class, () -> Product.findCheapest(products, false, -1));
  }

  @Test
  void testCompareTo() {
    assertTrue(laptop.compareTo(phone) < 0);
    assertTrue(phone.compareTo(laptop) > 0);

    Product samePrice = new Product("Another Laptop", "LAP002", 2500.0);
    assertEquals(0, laptop.compareTo(samePrice));
  }

  @Test
  void testCompareToLexically() {
    assertTrue(laptop.compareTo(phone, true) < 0);

    Product samePrice = new Product("Another Laptop", "LAP002", 2500.0);
    assertTrue(laptop.compareTo(samePrice, true) > 0);

    Product exactSame = new Product("Laptop", "DIFF001", 2500.0);
    assertEquals(0, laptop.compareTo(exactSame, true));
  }

  @Test
  void testDiscountProduct() {
    double discounted = laptop.discountProduct(2000.0);
    assertEquals(2000.0, discounted, 0.001);
    assertTrue(laptop.isDiscounted());
    assertEquals(2000.0, laptop.getPrice(), 0.001);
    assertEquals(2500.0, laptop.getOriginalPrice(), 0.001);

    assertThrows(IllegalArgumentException.class, () -> phone.discountProduct(-100.0));

    assertThrows(IllegalArgumentException.class, () -> phone.discountProduct(1500.0));

    assertThrows(IllegalStateException.class, () -> laptop.discountProduct(1800.0));
  }

  @Test
  void testRevertPrice() {
    laptop.discountProduct(2000.0);
    double original = laptop.revertPrice();
    assertEquals(2500.0, original, 0.001);
    assertFalse(laptop.isDiscounted());
    assertEquals(2500.0, laptop.getPrice(), 0.001);

    assertThrows(IllegalStateException.class, () -> phone.revertPrice());
  }

  @Test
  void testGetters() {
    assertEquals("Laptop", laptop.getName());
    assertEquals("LAP001", laptop.getCode());
    assertEquals(2500.0, laptop.getPrice(), 0.001);
    assertEquals(2500.0, laptop.getDiscountPrice(), 0.001);
    assertEquals(2500.0, laptop.getOriginalPrice(), 0.001);
    assertFalse(laptop.isDiscounted());

    laptop.discountProduct(2000.0);
    assertEquals("Laptop", laptop.getName());
    assertEquals("LAP001", laptop.getCode());
    assertEquals(2000.0, laptop.getPrice(), 0.001);
    assertEquals(2000.0, laptop.getDiscountPrice(), 0.001);
    assertEquals(2500.0, laptop.getOriginalPrice(), 0.001);
    assertTrue(laptop.isDiscounted());
  }
}
