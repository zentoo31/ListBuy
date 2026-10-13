package com.zentoo.buylist.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zentoo.buylist.R;
import com.zentoo.buylist.data.MarketRepository;
import java.util.*;

public final class CatalogScreen {
  private final AppCompatActivity activity;
  private final UiComponents ui;
  private final MarketRepository repository;
  private final List<String> catalog;
  private LinearLayout catalogRows;

  public CatalogScreen(AppCompatActivity activity, UiComponents ui, MarketRepository repository) {
    this.activity = activity;
    this.ui = ui;
    this.repository = repository;
    catalog = repository.catalog();
  }

  public LinearLayout createView() {
    LinearLayout page = ui.column();
    TextView title = ui.text("Mis productos", 28);
    title.setTypeface(ui.friendlyFont, Typeface.BOLD);
    page.addView(title);
    page.addView(ui.text("Guarda solo nombres para encontrarlos al escribir tu compra.", 14));
    EditText entry = ui.field(page, "Nombre del producto o cosa", UiComponents.FieldKind.TEXT);
    MaterialButton add = ui.button("Guardar nombre", R.drawable.ic_add);
    page.addView(add);
    add.setOnClickListener(
        v -> {
          String n = ui.value(entry);
          if (n.isEmpty()) {
            entry.setError("Escribe un nombre");
            return;
          }
          for (String existing : catalog)
            if (existing.equalsIgnoreCase(n)) {
              entry.setError("Este nombre ya está guardado");
              return;
            }
          catalog.add(n);
          catalog.sort(String.CASE_INSENSITIVE_ORDER);
          repository.saveCatalog();
          entry.setText("");
          renderCatalog();
          ui.refreshSuggestions();
        });
    page.addView(ui.text("Mantén presionado un nombre para quitarlo del catálogo.", 12));
    ScrollView scroll = new ScrollView(activity);
    catalogRows = ui.column();
    scroll.addView(catalogRows);
    page.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
    renderCatalog();
    return page;
  }

  private void renderCatalog() {
    catalogRows.removeAllViews();
    if (catalog.isEmpty()) {
      catalogRows.addView(
          ui.text(
              "Todavía no hay nombres guardados. Agrega arroz, leche o cualquier cosa que suelas"
                  + " comprar.",
              16));
      return;
    }
    for (String n : catalog) {
      TextView label = ui.text(n, 18);
      label.setPadding(ui.dp(16), ui.dp(16), ui.dp(16), ui.dp(16));
      label.setBackground(ui.background(Color.WHITE));
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
      params.setMargins(0, ui.dp(8), 0, 0);
      catalogRows.addView(label, params);
      label.setOnLongClickListener(
          v -> {
            new MaterialAlertDialogBuilder(activity)
                .setTitle("¿Quitar " + n + " del catálogo?")
                .setMessage("Los productos de tu compra se conservan.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton(
                    "Quitar",
                    (d, w) -> {
                      catalog.remove(n);
                      repository.saveCatalog();
                      renderCatalog();
                      ui.refreshSuggestions();
                    })
                .show();
            return true;
          });
    }
  }
}
