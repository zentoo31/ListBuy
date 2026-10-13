package com.zentoo.buylist.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.zentoo.buylist.R;
import com.zentoo.buylist.data.MarketRepository;
import com.zentoo.buylist.domain.Prices;
import java.util.*;

/** Shared presentation styles and input creation; no persistence logic. */
public final class UiComponents {
  public enum FieldKind {
    PRODUCT,
    TEXT,
    QUANTITY,
    PRICE
  }

  public static final int GREEN = Color.rgb(33, 107, 80), INK = Color.rgb(33, 48, 40);
  private final AppCompatActivity activity;
  private final MarketRepository repository;
  private final ArrayList<AutoCompleteTextView> suggestionFields = new ArrayList<>();
  public final Typeface friendlyFont;
  private final NumericKeypadDialog keypad;

  public UiComponents(AppCompatActivity activity, MarketRepository repository) {
    this.activity = activity;
    this.repository = repository;
    friendlyFont = androidx.core.content.res.ResourcesCompat.getFont(activity, R.font.nunito);
    keypad = new NumericKeypadDialog(activity, this);
  }

  public LinearLayout column() {
    LinearLayout l = new LinearLayout(activity);
    l.setOrientation(LinearLayout.VERTICAL);
    return l;
  }

  public int dp(int n) {
    return Math.round(n * activity.getResources().getDisplayMetrics().density);
  }

  public TextView text(String s, int size) {
    TextView t = new TextView(activity);
    t.setTypeface(friendlyFont);
    t.setText(s);
    t.setTextSize(size);
    t.setTextColor(INK);
    t.setPadding(0, dp(4), 0, dp(4));
    return t;
  }

  public GradientDrawable background(int color) {
    GradientDrawable d = new GradientDrawable();
    d.setColor(color);
    d.setCornerRadius(dp(18));
    return d;
  }

  public MaterialButton button(String label, int icon) {
    MaterialButton b = new MaterialButton(activity);
    b.setTypeface(friendlyFont, Typeface.BOLD);
    b.setText(label);
    b.setAllCaps(false);
    b.setCornerRadius(0);
    b.setIconResource(icon);
    b.setIconSize(dp(20));
    b.setIconPadding(dp(10));
    b.setIconGravity(MaterialButton.ICON_GRAVITY_TEXT_START);
    return b;
  }

  public EditText field(LinearLayout parent, String hint, FieldKind kind) {
    int type =
        kind == FieldKind.PRICE
            ? InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
            : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
    TextInputLayout box = new TextInputLayout(activity);
    box.setHint(hint);
    box.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
    box.setTypeface(friendlyFont);
    box.setBoxCornerRadii(0, 0, 0, 0);
    box.setStartIconDrawable(
        kind == FieldKind.PRICE
            ? R.drawable.ic_price
            : kind == FieldKind.QUANTITY ? R.drawable.ic_quantity : R.drawable.ic_cart);
    boolean product = kind == FieldKind.PRODUCT;
    EditText input;
    if (product) {
      AutoCompleteTextView auto = new AutoCompleteTextView(activity);
      auto.setThreshold(1);
      suggestionFields.add(auto);
      input = auto;
      refreshSuggestions();
    } else input = new EditText(activity);
    input.setTypeface(friendlyFont);
    input.setInputType(type);
    input.setSingleLine(true);
    box.addView(input);
    if (kind == FieldKind.QUANTITY || kind == FieldKind.PRICE) {
      input.setShowSoftInputOnFocus(false);
      input.setOnClickListener(v -> keypad.show(input, kind == FieldKind.QUANTITY));
    }
    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
    p.setMargins(0, dp(4), 0, dp(6));
    parent.addView(box, p);
    return input;
  }

  public String value(EditText input) {
    return input.getText() == null ? "" : input.getText().toString().trim();
  }

  public void refreshSuggestions() {
    for (AutoCompleteTextView field : suggestionFields) {
      field.setAdapter(
          new ArrayAdapter<>(
              activity,
              android.R.layout.simple_dropdown_item_1line,
              new ArrayList<>(repository.catalog())));
    }
  }

  public void releaseSuggestion(EditText input) {
    suggestionFields.remove(input);
  }

  public String validatedPrice(EditText input) {
    try {
      return Prices.normalize(value(input));
    } catch (NumberFormatException error) {
      input.setError("Usa un precio válido con hasta 2 decimales");
      return null;
    }
  }
}
