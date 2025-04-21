package org.ug.koszyk.cart;

import org.ug.koszyk.product.Product;
import org.ug.koszyk.sale.Sale;
import org.ug.koszyk.sale.SaleOptimizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

interface CartOperations {
  void addProduct(Product product);

  void removeProduct(Product product);

  List<Product> getProducts();

  int getSize();

  double getTotalPrice();

  void sortProducts();

  void sortProducts(Comparator<Product> comparator);

  void addSale(Sale sale);

  void removeSale(Sale sale);

  List<Sale> getAvailableSales();

  List<Sale> getAppliedSales();

  double applySales();

  Product findCheapest();

  Product findCheapest(boolean negate);

  List<Product> findCheapest(int n);

  List<Product> findCheapest(boolean negate, int slice);
}

public class Cart implements CartOperations {
  private List<Product> products = new ArrayList<>();
  private List<Sale> availableSales = new ArrayList<>();
  private List<Sale> appliedSales = new ArrayList<>();

  public void addProduct(Product product) {
    if (product == null) {
      throw new IllegalArgumentException("Product cannot be null");
    }

    products.add(product);
    sortProducts();
  }

  public void removeProduct(Product product) {
    if (product == null) {
      return;
    }

    products.removeIf(p -> p.getCode().equals(product.getCode()));
  }

  public List<Product> getProducts() {
    return new ArrayList<>(products);
  }

  public int getSize() {
    return products.size();
  }

  public double getTotalPrice() {
    return Product.calculateTotalPrice(products);
  }

  public void sortProducts() {
    if (products.size() <= 1) {
      return;
    }

    Collections.sort(products, (p1, p2) -> p1.compareTo(p2, true));
  }

  public void sortProducts(Comparator<Product> comparator) {
    if (products.size() <= 1 || comparator == null) {
      return;
    }

    Collections.sort(products, comparator);
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

    if (availableSales.isEmpty() || products.isEmpty()) {
      return getTotalPrice();
    }

    List<Sale> optimalSales = SaleOptimizer.findOptimalSaleOrder(this, availableSales);

    for (Sale sale : optimalSales) {
      sale.apply(this);
      appliedSales.add(sale);
    }
    double afterPrice = getTotalPrice();

    return afterPrice;
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
    if (products.isEmpty()) {
      return null;
    }
    return Product.findCheapest(products, false);
  }

  public Product findCheapest(boolean negate) {
    if (products.isEmpty()) {
      return null;
    }
    return Product.findCheapest(products, negate);
  }

  public List<Product> findCheapest(int n) {
    if (products.isEmpty() || n <= 0) {
      return new ArrayList<>();
    }
    return Product.findCheapest(products, false, n);
  }

  public List<Product> findCheapest(boolean negate, int slice) {
    if (products.isEmpty() || slice <= 0) {
      return new ArrayList<>();
    }
    return Product.findCheapest(products, negate, slice);
  }
}
