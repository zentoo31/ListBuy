package com.zentoo.buylist.domain;

import static org.junit.Assert.*;

import com.zentoo.buylist.model.ShoppingItem;
import java.util.Arrays;
import org.junit.Test;

public class PricesTest {
  @Test
  public void acceptsOptionalPricesAndDecimalComma() {
    assertEquals("", Prices.normalize(" "));
    assertEquals("4.50", Prices.normalize("4,5"));
    assertEquals("0.00", Prices.normalize("0"));
  }

  @Test
  public void rejectsNegativeAndOverPrecisePrices() {
    for (String price : new String[] {"-1", "1.234", "abc"}) {
      try {
        Prices.normalize(price);
        fail("Accepted invalid price: " + price);
      } catch (NumberFormatException expected) {
      }
    }
  }

  @Test
  public void totalsAmountsWithoutCountingKiloPriceTwice() {
    ShoppingItem apples = new ShoppingItem("Manzana", "2 kg", "8.10", false);
    apples.kiloPrice = "4.05";
    ShoppingItem milk = new ShoppingItem("Leche", "1", "3.20", true);
    ShoppingItem pending = new ShoppingItem("Arroz", "1 kg", "", false);
    assertEquals("11.30", Prices.total(Arrays.asList(apples, milk, pending)).toPlainString());
  }
}
