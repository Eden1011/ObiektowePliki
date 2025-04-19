package org.ug.koszyk.application;

import org.ug.koszyk.cart.Cart;
import org.ug.koszyk.product.Product;
import org.ug.koszyk.sale.Sale;
import org.ug.koszyk.sale.SaleTemplate;

import java.util.Comparator;

public class Main {
  public static void main(String[] args) {
    System.out.println("JavaMarkt - Shopping Cart Demonstration");
    System.out.println("==========================================\n");

    Product laptop = new Product("Laptop MacBook Pro", "LAP001", 6500.0);
    Product phone = new Product("iPhone 15", "PHO001", 4200.0);
    Product headphones = new Product("AirPods Pro", "HEA001", 950.0);
    Product mouse = new Product("Magic Mouse", "MOU001", 450.0);
    Product keyboard = new Product("Magic Keyboard", "KEY001", 650.0);
    Product case1 = new Product("Phone Case", "CAS001", 120.0);
    Product mug = new Product("JavaMarkt Mug", "MUG001", 30.0);

    System.out.println("Products created:");
    printProduct(laptop);
    printProduct(phone);
    printProduct(headphones);
    printProduct(mouse);
    printProduct(keyboard);
    printProduct(case1);
    printProduct(mug);

    Cart cart = new Cart();
    System.out.println("\nAdding products to cart...");
    cart.addProduct(laptop);
    cart.addProduct(phone);
    cart.addProduct(headphones);
    cart.addProduct(keyboard);
    cart.addProduct(mouse);
    cart.addProduct(case1);

    System.out.println("\nCart contents (default sorting - descending by price):");
    printCart(cart);
    System.out.println("Cart value: " + cart.getTotalPrice() + " zł");

    System.out.println("\nProduct search:");
    System.out.println(
        "Cheapest product: " + cart.findCheapest().getName() + " - " + cart.findCheapest().getPrice() + " zł");
    System.out.println("Most expensive product: " + cart.findMostExpensive().getName() + " - "
        + cart.findMostExpensive().getPrice() + " zł");

    System.out.println("\n2 cheapest products:");
    for (Product p : cart.findCheapest(2)) {
      printProduct(p);
    }

    System.out.println("\n3 most expensive products:");
    for (Product p : cart.findMostExpensive(3)) {
      printProduct(p);
    }

    System.out.println("\nAlphabetical sorting:");
    cart.sortProducts(Comparator.comparing(Product::getName));
    printCart(cart);

    cart.sortProducts();

    System.out.println("\nAdding promotions to cart:");

    Sale discount5 = SaleTemplate.createTotalValueDiscountSale(10000.0, 5.0);
    System.out.println("1. " + discount5.getDescription() + " (inactive - cart below threshold)");
    cart.addSale(discount5);

    Sale discount10 = SaleTemplate.createTotalValueDiscountSale(5000.0, 10.0);
    System.out.println("2. " + discount10.getDescription() + " (active)");
    cart.addSale(discount10);

    Sale buyTwoGetOne = SaleTemplate.createBuyTwoGetOneSale();
    System.out.println("3. " + buyTwoGetOne.getDescription());
    cart.addSale(buyTwoGetOne);

    Sale freeGift = SaleTemplate.createFreeGiftSale(2000.0, mug);
    System.out.println("4. " + freeGift.getDescription());
    cart.addSale(freeGift);

    Sale phoneCoupon = SaleTemplate.createProductCouponSale("PHO001", 30.0);
    System.out.println("5. " + phoneCoupon.getDescription());
    cart.addSale(phoneCoupon);

    System.out.println("\nApplying promotions...");
    double finalPrice = cart.applySales();

    System.out.println("\nCart after applying promotions:");
    printCart(cart);
    System.out.println("Initial value: 12870.00 zł");
    System.out.println("Value after promotions: " + finalPrice + " zł");

    System.out.println("\nApplied promotions (automatically selected as most beneficial):");
    for (Sale sale : cart.getAppliedSales()) {
      System.out.println("- " + sale.getDescription());
    }
  }

  private static void printProduct(Product product) {
    System.out.printf("- %s (code: %s): %.2f zł%n",
        product.getName(), product.getCode(), product.getPrice());
  }

  private static void printCart(Cart cart) {
    Product[] products = cart.getProducts();
    for (Product product : products) {
      if (product.isDiscounted()) {
        System.out.printf("- %s (code: %s): %.2f zł (original price: %.2f zł)%n",
            product.getName(), product.getCode(), product.getPrice(),
            product.getDiscountPrice() == 0 ? product.getDiscountPrice() : product.getDiscountPrice());
      } else {
        System.out.printf("- %s (code: %s): %.2f zł%n",
            product.getName(), product.getCode(), product.getPrice());
      }
    }
  }
}
