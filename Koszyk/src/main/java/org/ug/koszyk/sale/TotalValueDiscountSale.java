package org.ug.koszyk.sale;

import org.ug.koszyk.cart.Cart;
import org.ug.koszyk.product.Product;

public class TotalValueDiscountSale implements Sale {
  private final double threshold;
  private final double discountPercentage;
  private final String description;
  private Product[] affectedProducts;

  public TotalValueDiscountSale(double threshold, double discountPercentage, String description) {
    this.threshold = threshold;
    this.discountPercentage = discountPercentage;
    this.description = description;
  }

  @Override
  public void apply(Cart cart) {
    if (cart.getTotalPrice() <= threshold) {
      return;
    }

    Product[] products = cart.getProducts();
    affectedProducts = new Product[products.length];

    for (int i = 0; i < products.length; i++) {
      Product product = products[i];
      if (!product.isDiscounted()) {
        double newPrice = product.getPrice() * (1 - discountPercentage / 100);
        product.discountProduct(newPrice);
        affectedProducts[i] = product;
      }
    }
  }

  @Override
  public void cancel(Cart cart) {
    if (affectedProducts == null) {
      return;
    }

    for (Product product : affectedProducts) {
      if (product != null && product.isDiscounted()) {
        product.revertPrice();
      }
    }

    affectedProducts = null;
  }

  @Override
  public double calculateBenefit(Cart cart) {
    if (cart.getTotalPrice() <= threshold) {
      return 0;
    }

    return cart.getTotalPrice() * discountPercentage / 100;
  }

  @Override
  public String getDescription() {
    return description;
  }
}
