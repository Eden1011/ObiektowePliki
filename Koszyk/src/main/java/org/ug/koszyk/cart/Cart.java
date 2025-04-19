package org.ug.koszyk.cart;

import org.ug.koszyk.product.Product;
import org.ug.koszyk.sale.Sale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Cart {
  private Product[] products;
  private int size;
  private int capacity;
  private List<Sale> availableSales;
  private List<Sale> appliedSales;

  public Cart(int initialCapacity) {
    this.capacity = initialCapacity > 0 ? initialCapacity : 10;
    this.products = new Product[capacity];
    this.size = 0;
    this.availableSales = new ArrayList<>();
    this.appliedSales = new ArrayList<>();
  }

  public Cart() {
    this(10);
  }

  public void addProduct(Product product) {
    if (product == null) {
      throw new IllegalArgumentException("Product cannot be null");
    }

    ensureCapacity();
    products[size++] = product;
    sortProducts();
  }

  private void ensureCapacity() {
    if (size == capacity) {
      capacity *= 2;
      products = Arrays.copyOf(products, capacity);
    }
  }

  public void removeProduct(Product product) {
    if (product == null) {
      return;
    }

    for (int i = 0; i < size; i++) {
      if (products[i].getCode().equals(product.getCode())) {
        removeProductAt(i);
        return;
      }
    }
  }

  private void removeProductAt(int index) {
    if (index < 0 || index >= size) {
      return;
    }

    for (int i = index; i < size - 1; i++) {
      products[i] = products[i + 1];
    }

    products[--size] = null;
  }

  public Product[] getProducts() {
    return Arrays.copyOf(products, size);
  }

  public int getSize() {
    return size;
  }

  public double getTotalPrice() {
    return Product.calculateTotalPrice(getProducts());
  }

  public void sortProducts() {
    if (size <= 1) {
      return;
    }

    Arrays.sort(products, 0, size, (p1, p2) -> {
      int priceComparison = Double.compare(p2.getPrice(), p1.getPrice());
      if (priceComparison == 0) {
        return p1.getName().compareToIgnoreCase(p2.getName());
      }
      return priceComparison;
    });
  }

  public void sortProducts(Comparator<Product> comparator) {
    if (size <= 1 || comparator == null) {
      return;
    }

    Arrays.sort(products, 0, size, comparator);
  }

  public void addSale(Sale promotion) {
    if (promotion == null) {
      throw new IllegalArgumentException("Sale cannot be null");
    }

    availableSales.add(promotion);
  }

  public void removeSale(Sale promotion) {
    if (promotion == null) {
      return;
    }

    if (appliedSales.contains(promotion)) {
      cancelSale(promotion);
    }

    availableSales.remove(promotion);
  }

  public List<Sale> getAvailableSales() {
    return new ArrayList<>(availableSales);
  }

  public List<Sale> getAppliedSales() {
    return new ArrayList<>(appliedSales);
  }

  public double applySales() {
    cancelAllSales();

    List<Sale> remainingSales = new ArrayList<>(availableSales);

    while (!remainingSales.isEmpty()) {
      Sale bestSale = null;
      double bestBenefit = 0;

      for (Sale promotion : remainingSales) {
        double benefit = promotion.calculateBenefit(this);
        if (benefit > bestBenefit) {
          bestBenefit = benefit;
          bestSale = promotion;
        }
      }

      if (bestSale == null || bestBenefit <= 0) {
        break;
      }

      bestSale.apply(this);
      appliedSales.add(bestSale);
      remainingSales.remove(bestSale);
    }

    return getTotalPrice();
  }

  private void cancelSale(Sale promotion) {
    if (promotion == null) {
      return;
    }

    promotion.cancel(this);
    appliedSales.remove(promotion);
  }

  private void cancelAllSales() {
    List<Sale> promotionsToCancel = new ArrayList<>(appliedSales);
    for (Sale promotion : promotionsToCancel) {
      cancelSale(promotion);
    }
  }

  public Product findCheapest() {
    if (size == 0) {
      return null;
    }

    return Product.findCheapest(getProducts(), false);
  }

  public Product findMostExpensive() {
    if (size == 0) {
      return null;
    }

    return Product.findCheapest(getProducts(), true);
  }

  public Product[] findCheapest(int n) {
    if (size == 0 || n <= 0) {
      return new Product[0];
    }

    return Product.findCheapest(getProducts(), false, n);
  }

  public Product[] findMostExpensive(int n) {
    if (size == 0 || n <= 0) {
      return new Product[0];
    }

    return Product.findCheapest(getProducts(), true, n);
  }
}
