package org.ug.koszyk.cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ug.koszyk.product.Product;
import org.ug.koszyk.sale.Sale;
import org.ug.koszyk.sale.TotalValueDiscountSale;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {
  private Cart cart;
  private Product laptop;
  private Product phone;
  private Product headphones;
  private Product charger;
  private Sale totalValueDiscount;

  @BeforeEach
  void setUp() {
    cart = new Cart();
    laptop = new Product("Laptop", "LAP001", 2500.0);
    phone = new Product("Smartphone", "PHO001", 1200.0);
    headphones = new Product("Headphones", "HEA001", 300.0);
    charger = new Product("Charger", "CHA001", 120.0);

    totalValueDiscount = new TotalValueDiscountSale(300, 5, "5% discount for orders over 300 zł");
  }

  @Test
  void testAddProduct() {
    cart.addProduct(laptop);
    assertEquals(1, cart.getSize());
    assertTrue(cart.getProducts().contains(laptop));

    assertThrows(IllegalArgumentException.class, () -> cart.addProduct(null));

    cart.addProduct(phone);
    cart.addProduct(headphones);
    assertEquals(3, cart.getSize());
  }

  @Test
  void testRemoveProduct() {
    cart.addProduct(laptop);
    cart.addProduct(phone);
    assertEquals(2, cart.getSize());

    cart.removeProduct(laptop);
    assertEquals(1, cart.getSize());
    assertFalse(cart.getProducts().contains(laptop));
    assertTrue(cart.getProducts().contains(phone));

    cart.removeProduct(headphones);
    assertEquals(1, cart.getSize());

    cart.removeProduct(null);
    assertEquals(1, cart.getSize());
  }

  @Test
  void testGetProducts() {
    assertTrue(cart.getProducts().isEmpty());

    cart.addProduct(laptop);
    cart.addProduct(phone);

    List<Product> products = cart.getProducts();
    assertEquals(2, products.size());
    assertTrue(products.contains(laptop));
    assertTrue(products.contains(phone));

    products.remove(0);
    assertEquals(2, cart.getSize());
  }

  @Test
  void testGetSize() {
    assertEquals(0, cart.getSize());

    cart.addProduct(laptop);
    assertEquals(1, cart.getSize());

    cart.addProduct(phone);
    assertEquals(2, cart.getSize());

    cart.removeProduct(laptop);
    assertEquals(1, cart.getSize());
  }

  @Test
  void testGetTotalPrice() {
    assertEquals(0.0, cart.getTotalPrice(), 0.001);

    cart.addProduct(laptop);
    assertEquals(2500.0, cart.getTotalPrice(), 0.001);

    cart.addProduct(phone);
    assertEquals(3700.0, cart.getTotalPrice(), 0.001);

    phone.discountProduct(1000.0);
    assertEquals(3500.0, cart.getTotalPrice(), 0.001);
  }

  @Test
  void testSortProducts() {
    cart.addProduct(headphones);
    cart.addProduct(laptop);
    cart.addProduct(charger);
    cart.addProduct(phone);

    List<Product> products = cart.getProducts();
    assertEquals(laptop, products.get(0));
    assertEquals(phone, products.get(1));
    assertEquals(headphones, products.get(2));
    assertEquals(charger, products.get(3));

    Product tablet = new Product("Tablet", "TAB001", 1200.0);
    cart.addProduct(tablet);

    products = cart.getProducts();
    assertEquals(laptop, products.get(0));
    assertEquals(phone, products.get(1));
    assertEquals(tablet, products.get(2));
  }

  @Test
  void testSortProductsWithComparator() {
    cart.addProduct(headphones);
    cart.addProduct(laptop);
    cart.addProduct(charger);
    cart.addProduct(phone);

    Comparator<Product> ascendingPrice = Comparator.comparing(Product::getPrice);
    cart.sortProducts(ascendingPrice);

    List<Product> products = cart.getProducts();
    assertEquals(charger, products.get(0));
    assertEquals(headphones, products.get(1));
    assertEquals(phone, products.get(2));
    assertEquals(laptop, products.get(3));

    Comparator<Product> byName = Comparator.comparing(Product::getName);
    cart.sortProducts(byName);

    products = cart.getProducts();
    assertEquals(charger, products.get(0));
    assertEquals(headphones, products.get(1));
    assertEquals(laptop, products.get(2));
    assertEquals(phone, products.get(3));

    cart.sortProducts(null);
    products = cart.getProducts();
    assertEquals(charger, products.get(0));
  }

  @Test
  void testAddSale() {
    cart.addSale(totalValueDiscount);
    assertEquals(1, cart.getAvailableSales().size());
    assertTrue(cart.getAvailableSales().contains(totalValueDiscount));

    assertThrows(IllegalArgumentException.class, () -> cart.addSale(null));
  }

  @Test
  void testRemoveSale() {
    cart.addSale(totalValueDiscount);
    assertEquals(1, cart.getAvailableSales().size());

    cart.removeSale(totalValueDiscount);
    assertEquals(0, cart.getAvailableSales().size());

    cart.removeSale(totalValueDiscount);
    assertEquals(0, cart.getAvailableSales().size());

    cart.removeSale(null);
    assertEquals(0, cart.getAvailableSales().size());
  }

  @Test
  void testGetAvailableSales() {
    assertTrue(cart.getAvailableSales().isEmpty());

    cart.addSale(totalValueDiscount);

    List<Sale> sales = cart.getAvailableSales();
    assertEquals(1, sales.size());
    assertTrue(sales.contains(totalValueDiscount));

    sales.clear();
    assertEquals(1, cart.getAvailableSales().size());
  }

  @Test
  void testGetAppliedSales() {
    cart.addProduct(laptop);
    cart.addProduct(phone);
    cart.addSale(totalValueDiscount);

    assertTrue(cart.getAppliedSales().isEmpty());

    cart.applySales();
    assertEquals(1, cart.getAppliedSales().size());
    assertTrue(cart.getAppliedSales().contains(totalValueDiscount));

    List<Sale> appliedSales = cart.getAppliedSales();
    appliedSales.clear();
    assertEquals(1, cart.getAppliedSales().size());
  }

  @Test
  void testApplySales() {
    cart.addProduct(laptop);
    cart.addProduct(phone);
    cart.addSale(totalValueDiscount);

    double finalPrice = cart.applySales();

    assertEquals(3515.0, finalPrice, 0.001);
    assertEquals(3515.0, cart.getTotalPrice(), 0.001);
    assertEquals(1, cart.getAppliedSales().size());

    assertEquals(2375.0, laptop.getPrice(), 0.001);
    assertEquals(1140.0, phone.getPrice(), 0.001);

    Cart smallCart = new Cart();
    smallCart.addProduct(charger);
    smallCart.addSale(totalValueDiscount);

    double smallFinalPrice = smallCart.applySales();
    assertEquals(120.0, smallFinalPrice, 0.001);
    assertTrue(smallCart.getAppliedSales().isEmpty());
  }

  @Test
  void testFindCheapest() {
    cart.addProduct(laptop);
    cart.addProduct(phone);
    cart.addProduct(headphones);
    cart.addProduct(charger);

    Product cheapest = cart.findCheapest();
    assertEquals(charger, cheapest);

    Product mostExpensive = cart.findCheapest(true);
    assertEquals(laptop, mostExpensive);

    Cart emptyCart = new Cart();
    assertNull(emptyCart.findCheapest());
  }

  @Test
  void testFindCheapestN() {
    cart.addProduct(laptop);
    cart.addProduct(phone);
    cart.addProduct(headphones);
    cart.addProduct(charger);

    List<Product> cheapestTwo = cart.findCheapest(2);
    assertEquals(2, cheapestTwo.size());
    assertEquals(charger, cheapestTwo.get(0));
    assertEquals(headphones, cheapestTwo.get(1));

    List<Product> expensiveTwo = cart.findCheapest(true, 2);
    assertEquals(2, expensiveTwo.size());
    assertEquals(laptop, expensiveTwo.get(0));
    assertEquals(phone, expensiveTwo.get(1));

    Cart emptyCart = new Cart();
    assertTrue(emptyCart.findCheapest(2).isEmpty());

    assertTrue(cart.findCheapest(0).isEmpty());
    assertTrue(cart.findCheapest(-1).isEmpty());
  }
}
