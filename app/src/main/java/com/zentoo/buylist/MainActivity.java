package com.zentoo.buylist;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zentoo.buylist.data.MarketRepository;
import com.zentoo.buylist.ui.CatalogScreen;
import com.zentoo.buylist.ui.ShoppingScreen;
import com.zentoo.buylist.ui.UiComponents;

/** Hosts navigation and window insets; screens own their presentation. */
public final class MainActivity extends AppCompatActivity {
  private static final int SHOPPING = 1, CATALOG = 2;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    MarketRepository repository = new MarketRepository(this);
    UiComponents ui = new UiComponents(this, repository);
    LinearLayout host = ui.column();
    host.setBackgroundColor(android.graphics.Color.rgb(247, 248, 242));
    View shopping = new ShoppingScreen(this, ui, repository).createView();
    View catalog = new CatalogScreen(this, ui, repository).createView();
    host.addView(shopping, new LinearLayout.LayoutParams(-1, 0, 1));
    host.addView(catalog, new LinearLayout.LayoutParams(-1, 0, 1));
    catalog.setVisibility(View.GONE);
    BottomNavigationView navigation = new BottomNavigationView(this);
    navigation.setId(R.id.bottom_navigation);
    navigation.getMenu().add(0, SHOPPING, 0, "Mi compra").setIcon(R.drawable.ic_cart);
    navigation.getMenu().add(0, CATALOG, 1, "Productos").setIcon(R.drawable.ic_quantity);
    // Material's default listener also adds bottom insets. The host owns them instead.
    ViewCompat.setOnApplyWindowInsetsListener(
        navigation,
        (view, insets) -> {
          view.setPadding(0, 0, 0, 0);
          return insets;
        });
    host.addView(navigation, new LinearLayout.LayoutParams(-1, ui.dp(64)));
    navigation.setOnItemSelectedListener(
        item -> {
          ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
              .hideSoftInputFromWindow(host.getWindowToken(), 0);
          shopping.setVisibility(item.getItemId() == SHOPPING ? View.VISIBLE : View.GONE);
          catalog.setVisibility(item.getItemId() == CATALOG ? View.VISIBLE : View.GONE);
          return true;
        });
    ViewCompat.setOnApplyWindowInsetsListener(
        host,
        (view, insets) -> {
          androidx.core.graphics.Insets bars =
              insets.getInsets(WindowInsetsCompat.Type.systemBars());
          int keyboard = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;
          view.setPadding(
              ui.dp(20) + bars.left,
              ui.dp(16) + bars.top,
              ui.dp(20) + bars.right,
              Math.max(bars.bottom, keyboard));
          return insets;
        });
    setContentView(host);
    ViewCompat.requestApplyInsets(host);
  }
}
