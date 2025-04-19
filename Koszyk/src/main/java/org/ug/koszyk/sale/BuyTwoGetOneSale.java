package org.ug.koszyk.sale;

import org.ug.koszyk.cart.Cart;
import org.ug.koszyk.product.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class BuyTwoGetOneSale implements Sale {
  private final String description;
  private List<Product> affectedProducts;

  public BuyTwoGetOneSale(String description) {
    this.description = description;
    this.affectedProducts = new ArrayList<>();
  }

  @Override
  public void apply(Cart cart) {
    Product[] products = cart.getProducts();

    if (products.length < 3) {
      return;
    }

    int numGroups = products.length / 3;

    Product[] sortedProducts = Arrays.copyOf(products, products.length);
    Arrays.sort(sortedProducts, Comparator.comparing(Product::getPrice));

    affectedProducts.clear();

    for (int i = 0; i < numGroups; i++) {
      Product cheapest = sortedProducts[i];

      if (!cheapest.isDiscounted()) {
        cheapest.discountProduct(0);
        affectedProducts.add(cheapest);
      }
    }
  }

  @Override
  public void cancel(Cart cart) {
    if (affectedProducts == null || affectedProducts.isEmpty()) {
      return;
    }

    for (Product product : affectedProducts) {
      if (product.isDiscounted()) {
        product.revertPrice();
      }
    }

    affectedProducts.clear();
  }

  @Override
  public double calculateBenefit(Cart cart) {
    Product[] products = cart.getProducts();

    if (products.length < 3) {
      return 0;
    }

    Product[] sortedProducts = Arrays.copyOf(products, products.length);
    Arrays.sort(sortedProducts, Comparator.comparing(Product::getPrice));

    double benefit = 0;
    int numGroups = products.length / 3;

    for (int i = 0; i < numGroups; i++) {
      benefit += sortedProducts[i].getPrice();
    }

    return benefit;
  }

  @Override
  public String getDescription() {
    return description;
  }
}
