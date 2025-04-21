package org.ug.koszyk.sale;

import org.ug.koszyk.cart.Cart;
import org.ug.koszyk.product.Product;

import java.util.List;

public class ProductCouponSale implements Sale {
  private final String productCode;
  private final double discountPercentage;
  private final String description;
  private boolean used = false;

  public ProductCouponSale(String productCode, double discountPercentage, String description) {
    if (productCode == null || productCode.trim().isEmpty()) {
      throw new IllegalArgumentException("Product code cannot be null or empty");
    }
    if (discountPercentage < 0 || discountPercentage > 100) {
      throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
    }
    if (description == null || description.trim().isEmpty()) {
      throw new IllegalArgumentException("Description cannot be null or empty");
    }

    this.productCode = productCode;
    this.discountPercentage = discountPercentage;
    this.description = description;
  }

  @Override
  public void apply(Cart cart) {
    if (cart == null || cart.getSize() == 0 || used) {
      return;
    }

    List<Product> products = cart.getProducts();
    for (Product product : products) {
      if (product.getCode().equals(productCode) && !product.isDiscounted()) {
        double newPrice = product.getPrice() * (1 - discountPercentage / 100);
        product.discountProduct(newPrice);
        used = true;
        break;
      }
    }
  }

  @Override
  public void cancel(Cart cart) {
    if (cart == null || cart.getSize() == 0 || !used) {
      return;
    }

    List<Product> products = cart.getProducts();
    for (Product product : products) {
      if (product.getCode().equals(productCode) && product.isDiscounted()) {
        try {
          product.revertPrice();
        } catch (IllegalStateException e) {
        }
        used = false;
        break;
      }
    }
    if (used)
      used = false;
  }

  @Override
  public double calculateBenefit(Cart cart) {
    if (cart == null || cart.getSize() == 0 || used) {
      return 0.0;
    }

    List<Product> products = cart.getProducts();
    for (Product product : products) {
      if (product.getCode().equals(productCode)) {
        return product.getPrice() * discountPercentage / 100;
      }
    }

    return 0.0;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public boolean isUsed() {
    return used;
  }
}
