package org.ug.koszyk.sale;

import org.ug.koszyk.cart.Cart;
import org.ug.koszyk.product.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SaleOptimizer {
  public static List<Sale> findOptimalSaleOrder(Cart cart, List<Sale> availableSales) {
    if (cart == null || availableSales == null || availableSales.isEmpty()) {
      return new ArrayList<>();
    }

    Cart tempCart = createCartCopy(cart);

    List<Sale> sortedSales = new ArrayList<>(availableSales);
    sortedSales.sort(Comparator.comparing((Sale s) -> s.calculateBenefit(tempCart)).reversed());

    List<Sale> result = new ArrayList<>();
    double maxBenefit = 0;

    List<List<Sale>> permutations = generatePermutations(sortedSales);
    for (List<Sale> permutation : permutations) {
      Cart testCart = createCartCopy(cart);
      double totalBenefit = 0;

      for (Sale sale : permutation) {
        double beforePrice = testCart.getTotalPrice();
        sale.apply(testCart);
        double afterPrice = testCart.getTotalPrice();
        totalBenefit += (beforePrice - afterPrice);
      }

      if (totalBenefit > maxBenefit) {
        maxBenefit = totalBenefit;
        result = new ArrayList<>(permutation);
      }

      for (Sale sale : permutation) {
        sale.cancel(testCart);
      }
    }

    return result;
  }

  private static Cart createCartCopy(Cart original) {
    if (original == null) {
      return null;
    }

    Cart copy = new Cart();
    for (Product product : original.getProducts()) {
      copy.addProduct(new Product(product.getName(), product.getCode(), product.getPrice()));
    }

    return copy;
  }

  private static List<List<Sale>> generatePermutations(List<Sale> sales) {
    if (sales.size() > 8) {
      return Collections.singletonList(new ArrayList<>(sales));
    }

    List<List<Sale>> result = new ArrayList<>();
    permute(new ArrayList<>(), new ArrayList<>(sales), result);
    return result;
  }

  private static void permute(List<Sale> current, List<Sale> remaining, List<List<Sale>> result) {
    if (remaining.isEmpty()) {
      result.add(new ArrayList<>(current));
      return;
    }

    for (int i = 0; i < remaining.size(); i++) {
      Sale sale = remaining.get(i);
      List<Sale> newRemaining = new ArrayList<>(remaining);
      newRemaining.remove(i);

      current.add(sale);
      permute(current, newRemaining, result);
      current.remove(current.size() - 1);
    }
  }
}
