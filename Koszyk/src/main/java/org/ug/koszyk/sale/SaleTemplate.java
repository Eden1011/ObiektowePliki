package org.ug.koszyk.sale;

import org.ug.koszyk.product.Product;

public class SaleTemplate {
  public static Sale createTotalValueDiscountSale(double threshold, double discountPercentage) {
    return new TotalValueDiscountSale(threshold, discountPercentage,
        String.format("%.0f%% discount for purchases above %.2f", discountPercentage, threshold));
  }

  public static Sale createBuyTwoGetOneSale() {
    return new BuyTwoGetOneSale("Buy 2 products, get 1 cheapest free.");
  }

  public static Sale createFreeGiftSale(double threshold, Product gift) {
    return new FreeGiftSale(threshold, gift,
        String.format("Free %s for orders above %.2f", gift.getName(), threshold));
  }

  public static Sale createProductCouponSale(String productCode, double discountPercentage) {
    return new ProductCouponSale(productCode, discountPercentage,
        String.format("Coupon %.0f%% discount for product with code %s", discountPercentage, productCode));
  }
}
