package org.ug.koszyk.sale;

import org.ug.koszyk.cart.Cart;
import org.ug.koszyk.product.Product;

import java.util.List;

public class BuyTwoGetThirdFreeSale implements Sale {
  private final String description;

  public BuyTwoGetThirdFreeSale(String description) {
    if (description == null || description.trim().isEmpty()) {
      throw new IllegalArgumentException("Description cannot be null or empty");
    }
    this.description = description;
  }

  @Override
  public void apply(Cart cart) {
    if (cart == null || cart.getSize() < 3) {
      return;
    }

    List<Product> cheapestProducts = cart.findCheapest(3);
    if (cheapestProducts.size() < 3) {
      return;
    }

    Product thirdCheapest = cheapestProducts.get(0);

    if (!thirdCheapest.isDiscounted()) {
      thirdCheapest.discountProduct(0);
    }
  }

  @Override
  public void cancel(Cart cart) {
    if (cart == null || cart.getSize() < 3) {
      return;
    }

    List<Product> products = cart.getProducts();
    for (Product product : products) {
      if (product.isDiscounted() && product.getPrice() == 0) {
        try {
          product.revertPrice();
        } catch (IllegalStateException e) {
        }
      }
    }
  }

  @Override
  public double calculateBenefit(Cart cart) {
    if (cart == null || cart.getSize() < 3) {
      return 0.0;
    }

    List<Product> cheapestProducts = cart.findCheapest(3);
    if (cheapestProducts.size() < 3) {
      return 0.0;
    }

    Product thirdCheapest = cheapestProducts.get(0);
    return thirdCheapest.getDiscountPrice();
  }

  @Override
  public String getDescription() {
    return description;
  }
}
