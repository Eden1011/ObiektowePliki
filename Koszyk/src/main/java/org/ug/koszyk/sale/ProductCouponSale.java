package org.ug.koszyk.sale;

import org.ug.koszyk.cart.Cart;
import org.ug.koszyk.product.Product;

public class ProductCouponSale implements Sale {
  private final String productCode;
  private final double discountPercentage;
  private final String description;
  private Product affectedProduct;
  private boolean isUsed;

  public ProductCouponSale(String productCode, double discountPercentage, String description) {
    this.productCode = productCode;
    this.discountPercentage = discountPercentage;
    this.description = description;
    this.isUsed = false;
  }

  @Override
  public void apply(Cart cart) {
    if (isUsed) {
      return;
    }

    Product[] products = cart.getProducts();

    for (Product product : products) {
      if (product.getCode().equals(productCode) && !product.isDiscounted()) {
        double newPrice = product.getPrice() * (1 - discountPercentage / 100);
        product.discountProduct(newPrice);
        affectedProduct = product;
        isUsed = true;
        break;
      }
    }
  }

  @Override
  public void cancel(Cart cart) {
    if (!isUsed || affectedProduct == null) {
      return;
    }

    if (affectedProduct.isDiscounted()) {
      affectedProduct.revertPrice();
    }

    affectedProduct = null;
    isUsed = false;
  }

  @Override
  public double calculateBenefit(Cart cart) {
    if (isUsed) {
      return 0;
    }

    Product[] products = cart.getProducts();

    for (Product product : products) {
      if (product.getCode().equals(productCode)) {
        return product.getPrice() * discountPercentage / 100;
      }
    }

    return 0;
  }

  @Override
  public String getDescription() {
    return description;
  }
}
