package org.ug.koszyk.sale;

import org.ug.koszyk.cart.Cart;
import org.ug.koszyk.product.Product;

public class FreeGiftSale implements Sale {
  private final double threshold;
  private final Product gift;
  private final String description;
  private boolean isApplied;

  public FreeGiftSale(double threshold, Product gift, String description) {
    this.threshold = threshold;
    this.gift = gift;
    this.description = description;
    this.isApplied = false;
  }

  @Override
  public void apply(Cart cart) {
    if (cart.getTotalPrice() <= threshold || isApplied) {
      return;
    }

    cart.addProduct(gift);
    isApplied = true;
  }

  @Override
  public void cancel(Cart cart) {
    if (!isApplied) {
      return;
    }

    cart.removeProduct(gift);
    isApplied = false;
  }

  @Override
  public double calculateBenefit(Cart cart) {
    if (cart.getTotalPrice() <= threshold) {
      return 0;
    }

    return gift.getPrice();
  }

  @Override
  public String getDescription() {
    return description;
  }
}
