package com.zentoo.buylist.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.zentoo.buylist.model.ShoppingItem;
import java.util.ArrayList;
import java.util.List;
import org.json.*;

/** Owns storage; keeps the original keys to preserve existing user data. */
public final class MarketRepository {
  private final Context context;
  private final SharedPreferences preferences;
  private final ArrayList<ShoppingItem> items = new ArrayList<>();
  private final ArrayList<String> catalog = new ArrayList<>();

  public MarketRepository(Context context) {
    this(
        context,
        context.getApplicationContext().getSharedPreferences("market", Context.MODE_PRIVATE));
  }

  MarketRepository(Context context, SharedPreferences preferences) {
    this.context = context.getApplicationContext();
    this.preferences = preferences;
    loadItems();
    loadCatalog();
  }

  public List<ShoppingItem> items() {
    return items;
  }

  public List<String> catalog() {
    return catalog;
  }

  public void saveItems() {
    JSONArray data = new JSONArray();
    for (ShoppingItem i : items) {
      JSONObject o = new JSONObject();
      try {
        o.put("name", i.name);
        o.put("quantity", i.quantity);
        o.put("price", i.price);
        o.put("kiloPrice", i.kiloPrice);
        o.put("bought", i.bought);
        data.put(o);
      } catch (JSONException e) {
        throw new IllegalStateException(e);
      }
    }
    preferences.edit().putString("items", data.toString()).apply();
  }

  private void loadItems() {
    try {
      JSONArray data = new JSONArray(preferences.getString("items", "[]"));
      for (int x = 0; x < data.length(); x++) {
        JSONObject o = data.getJSONObject(x);
        ShoppingItem item =
            new ShoppingItem(
                o.getString("name"),
                o.optString("quantity"),
                o.optString("price"),
                o.optBoolean("bought"));
        item.kiloPrice = o.optString("kiloPrice");
        items.add(item);
      }
    } catch (JSONException e) {
      Toast.makeText(context, "No se pudo leer la lista guardada", Toast.LENGTH_LONG).show();
    }
  }

  public void saveCatalog() {
    preferences.edit().putString("catalog", new JSONArray(catalog).toString()).apply();
  }

  private void loadCatalog() {
    try {
      JSONArray data = new JSONArray(preferences.getString("catalog", "[]"));
      for (int i = 0; i < data.length(); i++) catalog.add(data.getString(i));
    } catch (JSONException e) {
      Toast.makeText(context, "No se pudo leer el catálogo", Toast.LENGTH_SHORT).show();
    }
  }
}
