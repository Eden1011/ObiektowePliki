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

  public static Product[] sort(Product[] productArray, Comparator<Product> comparator) {
    if (productArray == null || productArray.length == 0) {
      return new Product[0];
    }
    Product[] sortedArray = Arrays.copyOf(productArray, productArray.length);
    Arrays.sort(sortedArray, comparator);
    return sortedArray;
  }

  public static Product[] sort(Product[] productArray) {
    return sort(productArray, new Comparator<Product>() {
      @Override
      public int compare(Product p1, Product p2) {
        int priceComparison = Double.compare(p2.getPrice(), p1.getPrice());
        if (priceComparison == 0) {
          return p1.getName().compareToIgnoreCase(p2.getName());
        }
        return priceComparison;
      }
    });
  }

  public static Product findCheapest(Product[] productArray, boolean negate) {
    if (productArray == null || productArray.length == 0) {
      return null;
    }

    if (productArray.length == 1) {
      return productArray[0];
    }

    Comparator<Product> comparator = negate ? (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice())
        : (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice());

    Product[] sortedProducts = sort(productArray, comparator);
    return sortedProducts[0];
  }

  public static Product[] findCheapest(Product[] productArray, boolean negate, int slice) {
    if (productArray == null || productArray.length == 0) {
      return new Product[0];
    }

    if (productArray.length == 1) {
      return productArray;
    }

    if (slice <= 0) {
      throw new IllegalArgumentException("Slice must be positive");
    }

    Comparator<Product> comparator = negate ? (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice())
        : (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice());

    Product[] sortedProducts = sort(productArray, comparator);
    return Arrays.copyOf(sortedProducts, Math.min(slice, sortedProducts.length));
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
