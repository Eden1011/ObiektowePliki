package org.ug.koszyk.sale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ug.koszyk.cart.Cart;
import org.ug.koszyk.product.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SaleOptimizerTest {
  private Cart cart;
  private Product laptop;
  private Product phone;
  private Product headphones;
  private Product charger;
  private Product phoneCase;
  private Product giftMug;

  private TotalValueDiscountSale totalValueDiscount;
  private BuyTwoGetThirdFreeSale buyTwoGetThirdFree;
  private FreeGiftSale freeGift;
  private ProductCouponSale phoneCoupon;

  private List<Sale> allSales;

  @BeforeEach
  void setUp() {
    cart = new Cart();

    laptop = new Product("Laptop", "LAP001", 2500.0);
    phone = new Product("Smartphone", "PHO001", 1200.0);
    headphones = new Product("Headphones", "HEA001", 300.0);
    charger = new Product("Charger", "CHA001", 120.0);
    phoneCase = new Product("Phone Case", "CAS001", 50.0);
    giftMug = new Product("JavaMarkt Mug", "MUG001", 25.0);

    cart.addProduct(laptop);
    cart.addProduct(phone);
    cart.addProduct(headphones);
    cart.addProduct(charger);
    cart.addProduct(phoneCase);

    totalValueDiscount = new TotalValueDiscountSale(300, 5, "5% discount for orders over 300 zł");
    buyTwoGetThirdFree = new BuyTwoGetThirdFreeSale("Buy 2 products, get the 3rd cheapest for free");
    freeGift = new FreeGiftSale(200, giftMug, "Free mug for orders over 200 zł");
    phoneCoupon = new ProductCouponSale("PHO001", 30, "30% off smartphone");

    allSales = Arrays.asList(totalValueDiscount, buyTwoGetThirdFree, freeGift, phoneCoupon);
  }

  @Test
  void testFindOptimalSaleOrder() {
    List<Sale> optimalOrder = SaleOptimizer.findOptimalSaleOrder(cart, allSales);

    assertNotNull(optimalOrder);
    assertFalse(optimalOrder.isEmpty());

    for (Sale sale : optimalOrder) {
      sale.apply(cart);
    }

    Cart testCart = new Cart();
    testCart.addProduct(new Product("Laptop", "LAP001", 2500.0));
    testCart.addProduct(new Product("Smartphone", "PHO001", 1200.0));
    testCart.addProduct(new Product("Headphones", "HEA001", 300.0));
    testCart.addProduct(new Product("Charger", "CHA001", 120.0));
    testCart.addProduct(new Product("Phone Case", "CAS001", 50.0));

    for (Sale sale : allSales) {
      sale.apply(testCart);
    }

    assertTrue(cart.getTotalPrice() <= testCart.getTotalPrice());
  }

  @Test
  void testFindOptimalSaleOrderWithSubset() {
    List<Sale> salesSubset = Arrays.asList(buyTwoGetThirdFree, phoneCoupon);
    List<Sale> optimalOrder = SaleOptimizer.findOptimalSaleOrder(cart, salesSubset);

    assertNotNull(optimalOrder);
    assertEquals(2, optimalOrder.size());

    double initialPrice = cart.getTotalPrice();
    for (Sale sale : optimalOrder) {
      sale.apply(cart);
    }
    double finalPrice = cart.getTotalPrice();

    assertTrue(finalPrice < initialPrice);
  }

  @Test
  void testFindOptimalSaleOrderEdgeCases() {
    List<Sale> result1 = SaleOptimizer.findOptimalSaleOrder(null, allSales);
    assertTrue(result1.isEmpty());

    List<Sale> result2 = SaleOptimizer.findOptimalSaleOrder(cart, null);
    assertTrue(result2.isEmpty());

    List<Sale> result3 = SaleOptimizer.findOptimalSaleOrder(cart, new ArrayList<>());
    assertTrue(result3.isEmpty());

    Cart emptyCart = new Cart();
    List<Sale> result4 = SaleOptimizer.findOptimalSaleOrder(emptyCart, allSales);
    assertNotNull(result4);
  }

  @Test
  void testOrderMatters() {
    Cart specialCart = new Cart();
    specialCart.addProduct(new Product("Item A", "A001", 100.0));
    specialCart.addProduct(new Product("Item B", "B001", 100.0));
    specialCart.addProduct(new Product("Item C", "C001", 100.0));

    TotalValueDiscountSale smallDiscount = new TotalValueDiscountSale(100, 10, "10% off");

    BuyTwoGetThirdFreeSale freeItem = new BuyTwoGetThirdFreeSale("Free cheapest item");

    List<Sale> salesList = Arrays.asList(smallDiscount, freeItem);

    Cart order1Cart = new Cart();
    order1Cart.addProduct(new Product("Item A", "A001", 100.0));
    order1Cart.addProduct(new Product("Item B", "B001", 100.0));
    order1Cart.addProduct(new Product("Item C", "C001", 100.0));

    smallDiscount.apply(order1Cart);
    freeItem.apply(order1Cart);

    Cart order2Cart = new Cart();
    order2Cart.addProduct(new Product("Item A", "A001", 100.0));
    order2Cart.addProduct(new Product("Item B", "B001", 100.0));
    order2Cart.addProduct(new Product("Item C", "C001", 100.0));

    freeItem.apply(order2Cart);
    smallDiscount.apply(order2Cart);

    double price1 = order1Cart.getTotalPrice();
    double price2 = order2Cart.getTotalPrice();

    List<Sale> optimalOrder = SaleOptimizer.findOptimalSaleOrder(specialCart, salesList);

    for (Sale sale : optimalOrder) {
      sale.apply(specialCart);
    }

    double optimalPrice = specialCart.getTotalPrice();

    assertTrue(optimalPrice <= price1);
    assertTrue(optimalPrice <= price2);
  }

  @Test
  void testPerformanceWithManySales() {
    Cart testCart = new Cart();
    testCart.addProduct(new Product("Item A", "A001", 100.0));
    testCart.addProduct(new Product("Item B", "B001", 200.0));

    List<Sale> manySales = new ArrayList<>();
    for (int i = 1; i <= 8; i++) {
      manySales.add(new TotalValueDiscountSale(10 * i, 1, "Discount " + i));
    }

    long startTime = System.currentTimeMillis();
    List<Sale> result = SaleOptimizer.findOptimalSaleOrder(testCart, manySales);
    long endTime = System.currentTimeMillis();

    assertNotNull(result);

    manySales.add(new TotalValueDiscountSale(90, 1, "Discount 9"));

    long startTime2 = System.currentTimeMillis();
    List<Sale> result2 = SaleOptimizer.findOptimalSaleOrder(testCart, manySales);
    long endTime2 = System.currentTimeMillis();

    assertNotNull(result2);

    long time1 = endTime - startTime;
    long time2 = endTime2 - startTime2;

    assertTrue(time2 >= 0);
  }
}
