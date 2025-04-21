package org.ug.koszyk.product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

interface ProductOperations {
  String getName();

  String getCode();

  double getPrice();

  double getDiscountPrice();

  boolean isDiscounted();

  double discountProduct(double newPrice);

  double getOriginalPrice();

  double revertPrice();

  int compareTo(Product other);

  int compareTo(Product other, boolean lexically);
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

  public static double calculateTotalPrice(List<Product> products) {
    if (products == null || products.isEmpty()) {
      return 0.0;
    }

    double total = 0.0;
    for (Product product : products) {
      total += product.getPrice();
    }
    return total;
  }

  public static List<Product> sort(List<Product> productList, Comparator<Product> comparator) {
    if (productList == null || productList.isEmpty()) {
      return new ArrayList<>();
    }
    List<Product> sortedList = new ArrayList<>(productList);
    Collections.sort(sortedList, comparator);
    return sortedList;
  }

  public static List<Product> sort(List<Product> productList) {
    if (productList == null || productList.isEmpty()) {
      return new ArrayList<>();
    }
    List<Product> sortedList = new ArrayList<>(productList);

    Collections.sort(sortedList, (p1, p2) -> p1.compareTo(p2, true));

    return sortedList;
  }

  public static Product findCheapest(List<Product> products, boolean negate) {
    if (products == null || products.isEmpty()) {
      return null;
    }

    return Collections.min(products, (p1, p2) -> negate ? p1.compareTo(p2) : -p1.compareTo(p2));
  }

  public static List<Product> findCheapest(List<Product> products, boolean negate, int slice) {
    if (products == null || products.isEmpty()) {
      return new ArrayList<>();
    }
    if (slice <= 0) {
      throw new IllegalArgumentException("Slice must be positive");
    }

    return products.stream()
        .sorted((p1, p2) -> negate ? p1.compareTo(p2) : -p1.compareTo(p2))
        .limit(slice)
        .collect(Collectors.toList());
  }

  @Override
  public int compareTo(Product other) {
    return Comparator
        .comparing(Product::getPrice, Comparator.reverseOrder())
        .compare(this, other);
  }

  @Override
  public int compareTo(Product other, boolean lexically) {
    return lexically ? Comparator
        .comparing(Product::getPrice, Comparator.reverseOrder())
        .thenComparing(Product::getName, String::compareToIgnoreCase)
        .compare(this, other) : compareTo(other);
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

  @Override
  public double getOriginalPrice() {
    return price;
  }

}
