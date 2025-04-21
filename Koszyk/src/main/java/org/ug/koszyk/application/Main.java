package org.ug.koszyk.application;

import org.ug.koszyk.cart.Cart;
import org.ug.koszyk.product.Product;
import org.ug.koszyk.sale.*;

import java.util.List;

public class Main {
  public static void main(String[] args) {
    Cart cart = new Cart();

    Product laptop = new Product("Laptop", "LAP001", 2500.0);
    Product phone = new Product("Smartphone", "PHO001", 1200.0);
    Product headphones = new Product("Headphones", "HEA001", 300.0);
    Product charger = new Product("Charger", "CHA001", 120.0);
    Product phoneCase = new Product("Phone Case", "CAS001", 50.0);

    cart.addProduct(laptop);
    cart.addProduct(phone);
    cart.addProduct(headphones);
    cart.addProduct(charger);
    cart.addProduct(phoneCase);

    Sale totalValueDiscount = new TotalValueDiscountSale(300, 5, "5% discount for orders over 300 zł");
    Sale buyTwoGetThirdFree = new BuyTwoGetThirdFreeSale("Buy 2 products, get the 3rd cheapest for free");
    Sale freeGift = new FreeGiftSale(200, new Product("JavaMarkt Mug", "MUG001", 25.0),
        "Free mug for orders over 200 zł");
    Sale phoneCoupon = new ProductCouponSale("PHO001", 30, "30% off smartphone");

    cart.addSale(totalValueDiscount);
    cart.addSale(buyTwoGetThirdFree);
    cart.addSale(freeGift);
    cart.addSale(phoneCoupon);

    System.out.println("Initial cart contents:");
    displayCartContents(cart);
    System.out.println("Total price: " + cart.getTotalPrice() + " zł");
    System.out.println(cart.findCheapest().getName());

    System.out.println("\nApplying sales...");
    double finalPrice = cart.applySales();

    System.out.println("\nCart contents after applying sales:");
    displayCartContents(cart);
    System.out.println("Final price: " + finalPrice + " zł");

    System.out.println("\nApplied sales:");
    for (Sale sale : cart.getAppliedSales()) {
      System.out.println("- " + sale.getDescription());
    }

    List<Product> p = cart.findCheapest(3);
    for (Product pr : p) {
      System.out.println(pr.getName());
    }
  }

  private static void displayCartContents(Cart cart) {
    List<Product> products = cart.getProducts();
    for (Product product : products) {
      String priceInfo = product.isDiscounted()
          ? product.getPrice() + " zł (discounted from " + product.getOriginalPrice() + " zł)"
          : product.getPrice() + " zł";

      System.out.println("- " + product.getName() + " (" + product.getCode() + "): " + priceInfo);
    }
  }
}
