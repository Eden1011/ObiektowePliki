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

  int compareTo(Product other);
}

public class Product implements ProductOperations, Comparable<Product> {
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

  public static double calculateTotalPrice(Product[] products) {
    if (products == null || products.length == 0) {
      return 0.0;
    }

    double total = 0.0;
    for (Product product : products) {
      total += product.getPrice();
    }
    return total;
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
        return p1.compareTo(p2);
      }
    });
  }

  public static Product findCheapest(Product[] products, boolean negate) {
    if (products == null || products.length == 0) {
      return null;
    }

    if (products.length == 1) {
      return products[0];
    }

    Comparator<Product> comparator = negate ? (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice())
        : (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice());

    Product[] sortedProducts = sort(products, comparator);
    return sortedProducts[0];
  }

  public static Product[] findCheapest(Product[] products, boolean negate, int slice) {
    if (products == null || products.length == 0) {
      return new Product[0];
    }

    if (products.length == 1) {
      return products;
    }

    if (slice <= 0) {
      throw new IllegalArgumentException("Slice must be positive");
    }

    Comparator<Product> comparator = negate ? (p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice())
        : (p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice());

    Product[] sortedProducts = sort(products, comparator);
    return Arrays.copyOf(sortedProducts, Math.min(slice, sortedProducts.length));
  }

  @Override
  public int compareTo(Product other) {
    int priceComparison = Double.compare(other.getPrice(), this.getPrice());
    if (priceComparison == 0) {
      return this.getName().compareToIgnoreCase(other.getName());
    }
    return priceComparison;
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
