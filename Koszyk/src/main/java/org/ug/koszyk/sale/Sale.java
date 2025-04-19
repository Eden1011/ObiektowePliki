package org.ug.koszyk.sale;

import org.ug.koszyk.cart.Cart;

public interface Sale {
  void apply(Cart cart);

  void cancel(Cart cart);

  double calculateBenefit(Cart cart);

  String getDescription();
}
