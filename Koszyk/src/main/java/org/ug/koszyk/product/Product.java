package org.ug.koszyk.product;

import java.util.Arrays;
import java.util.Comparator;

interface ProductOperations {
  String getName();

  String getCode();

  double getPrice();

  double getDiscountPrice();

  boolean isDiscounted();

  double discountProduct(double newPrice);

  double revertPrice();
}

public class Product implements ProductOperations {
  private String name;
  private double price;
  private double discountPrice;
  private String code;
  private boolean isDiscounted = false;

  public Product(String name, String code, double price) {
    if (name == null || code == null) {
      throw new IllegalArgumentException("Product name and code cannot be null");
    }
    if (name.trim().isEmpty() || code.trim().isEmpty()) {
      throw new IllegalArgumentException("Product name and code cannot be empty");
    }
    if (price < 0.0) {
      throw new IllegalArgumentException("Price cannot be negative");
    }
    this.name = name;
    this.code = code;
    this.price = price;
    this.discountPrice = price;
  }

  public static Product[] sort(Product[] productArray) {
    if (productArray == null || productArray.length == 0) {
      return new Product[0];
    }

    Product[] sortedArray = Arrays.copyOf(productArray, productArray.length);

    Arrays.sort(sortedArray, new Comparator<Product>() {
      @Override
      public int compare(Product p1, Product p2) {
        int priceComparison = Double.compare(p2.getPrice(), p1.getPrice());

        if (priceComparison == 0) {
          return p1.getName().compareToIgnoreCase(p2.getName());
        }

        return priceComparison;
      }
    });

    return sortedArray;
  }

  @Override
  public boolean isDiscounted() {
    return isDiscounted;
  }

  @Override
  public double discountProduct(double newPrice) {
    if (newPrice < 0) {
      throw new IllegalArgumentException("Discount price cannot be negative");
    }
    if (newPrice > price) {
      throw new IllegalArgumentException("Discount price cannot be higher than original price");
    }
    if (isDiscounted()) {
      throw new IllegalStateException("Cannot re-discount already discounted product");
    }
    isDiscounted = true;
    discountPrice = newPrice;
    return discountPrice;
  }

  @Override
  public double revertPrice() {
    if (!isDiscounted()) {
      throw new IllegalStateException("Cannot revert the price of a product that is not discounted");
    }
    isDiscounted = false;
    discountPrice = price;
    return price;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public double getPrice() {
    return isDiscounted() ? discountPrice : price;
  }

  @Override
  public double getDiscountPrice() {
    return discountPrice;
  }
}
