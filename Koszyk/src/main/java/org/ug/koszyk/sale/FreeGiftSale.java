package org.ug.koszyk.sale;

import org.ug.koszyk.cart.Cart;
import org.ug.koszyk.product.Product;

public class FreeGiftSale implements Sale {
  private final double threshold;
  private final Product giftProduct;
  private final String description;
  private boolean giftAdded = false;

  public FreeGiftSale(double threshold, Product giftProduct, String description) {
    if (threshold < 0) {
      throw new IllegalArgumentException("Threshold cannot be negative");
    }
    if (giftProduct == null) {
      throw new IllegalArgumentException("Gift product cannot be null");
    }
    if (description == null || description.trim().isEmpty()) {
      throw new IllegalArgumentException("Description cannot be null or empty");
    }

    this.threshold = threshold;
    this.giftProduct = giftProduct;
    this.description = description;
  }

  @Override
  public void apply(Cart cart) {
    if (cart == null || giftAdded) {
      return;
    }

    double totalPrice = cart.getTotalPrice();
    if (totalPrice >= threshold) {
      Product discountedGift = new Product(
          giftProduct.getName(),
          giftProduct.getCode(),
          giftProduct.getPrice());
      discountedGift.discountProduct(0);
      cart.addProduct(discountedGift);
      giftAdded = true;
    }
  }

  @Override
  public void cancel(Cart cart) {
    if (cart == null || !giftAdded) {
      return;
    }

    cart.removeProduct(giftProduct);
    giftAdded = false;
  }

  @Override
  public double calculateBenefit(Cart cart) {
    if (cart == null || cart.getTotalPrice() < threshold) {
      return 0.0;
    }

    return giftProduct.getPrice();
  }

  @Override
  public String getDescription() {
    return description;
  }
}
