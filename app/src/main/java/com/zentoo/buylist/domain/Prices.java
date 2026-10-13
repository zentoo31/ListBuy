package com.zentoo.buylist.domain;

import com.zentoo.buylist.model.ShoppingItem;
import java.math.BigDecimal;
import java.util.List;

public final class Prices {
  private Prices() {}

  public static String normalize(String value) {
    String cleaned = value.trim().replace(',', '.');
    if (cleaned.isEmpty()) return "";
    BigDecimal amount = new BigDecimal(cleaned);
    if (amount.signum() < 0 || amount.scale() > 2) throw new NumberFormatException("Invalid price");
    return amount.setScale(2).toPlainString();
  }

  public static String format(String price) {
    return "S/ " + new BigDecimal(price).setScale(2).toPlainString();
  }

  public static BigDecimal total(List<ShoppingItem> items) {
    BigDecimal total = BigDecimal.ZERO;
    for (ShoppingItem item : items) {
      if (!item.price.isEmpty()) total = total.add(new BigDecimal(item.price));
    }
    return total;
  }
}
