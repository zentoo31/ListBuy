package com.zentoo.buylist.data;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;

public class MarketRepositoryTest {
  @Test
  public void preservesLegacyDataAndNewFieldsAcrossReloads() {
    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    SharedPreferences isolated =
        context.getSharedPreferences("repository_test", Context.MODE_PRIVATE);
    try {
      isolated
          .edit()
          .clear()
          .putString(
              "items",
              "[{\"name\":\"Arroz\",\"quantity\":\"2 kg\",\"price\":\"8.00\",\"bought\":true}]")
          .putString("catalog", "[\"Arroz\",\"Leche\"]")
          .commit();
      MarketRepository loaded = new MarketRepository(context, isolated);
      assertEquals("Arroz", loaded.items().get(0).name);
      assertEquals("", loaded.items().get(0).kiloPrice);
      assertTrue(loaded.items().get(0).bought);
      loaded.items().get(0).kiloPrice = "4.00";
      loaded.saveItems();
      loaded.saveCatalog();
      MarketRepository reloaded = new MarketRepository(context, isolated);
      assertEquals("4.00", reloaded.items().get(0).kiloPrice);
      assertEquals("8.00", reloaded.items().get(0).price);
      assertEquals("2 kg", reloaded.items().get(0).quantity);
      assertEquals(2, reloaded.catalog().size());
    } finally {
      isolated.edit().clear().commit();
    }
  }
}
