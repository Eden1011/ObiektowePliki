package org.ug.koszyk.sale;

import org.ug.koszyk.cart.Cart;
import org.ug.koszyk.product.Product;

import java.util.List;

public class TotalValueDiscountSale implements Sale {
  private final double threshold;
  private final double discountPercentage;
  private final String description;

  public TotalValueDiscountSale(double threshold, double discountPercentage, String description) {
    if (threshold < 0) {
      throw new IllegalArgumentException("Threshold cannot be negative");
    }
    if (discountPercentage < 0 || discountPercentage > 100) {
      throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
    }
    if (description == null || description.trim().isEmpty()) {
      throw new IllegalArgumentException("Description cannot be null or empty");
    }

    this.threshold = threshold;
    this.discountPercentage = discountPercentage;
    this.description = description;
  }

  @Override
  public void apply(Cart cart) {
    if (cart == null || cart.getSize() == 0) {
      return;
    }

    double totalPrice = cart.getTotalPrice();
    if (totalPrice >= threshold) {
      List<Product> products = cart.getProducts();
      for (Product product : products) {
        if (!product.isDiscounted()) {
          double newPrice = product.getPrice() * (1 - discountPercentage / 100);
          product.discountProduct(newPrice);
        }
      }
    }
  }

  @Override
  public void cancel(Cart cart) {
    if (cart == null || cart.getSize() == 0) {
      return;
    }

    List<Product> products = cart.getProducts();
    for (Product product : products) {
      if (product.isDiscounted()) {
        try {
          product.revertPrice();
        } catch (IllegalStateException e) {
        }
      }
    }
  }

  @Override
  public double calculateBenefit(Cart cart) {
    if (cart == null || cart.getSize() == 0 || cart.getTotalPrice() < threshold) {
      return 0.0;
    }

    return cart.getTotalPrice() * discountPercentage / 100;
  }

  @Override
  public String getDescription() {
    return description;
  }
}
