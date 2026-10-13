package com.zentoo.buylist.ui;

import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zentoo.buylist.R;
import java.util.*;

/** Centered keypad; changes are committed only when the user selects Listo. */
public final class NumericKeypadDialog {
  private final AppCompatActivity activity;
  private final UiComponents ui;

  public NumericKeypadDialog(AppCompatActivity activity, UiComponents ui) {
    this.activity = activity;
    this.ui = ui;
  }

  public void show(EditText target, boolean isQuantity) {
    ((android.view.inputmethod.InputMethodManager)
            activity.getSystemService(android.content.Context.INPUT_METHOD_SERVICE))
        .hideSoftInputFromWindow(target.getWindowToken(), 0);
    LinearLayout panel = ui.column();
    panel.setPadding(ui.dp(20), ui.dp(8), ui.dp(20), ui.dp(8));
    EditText draft = new EditText(activity);
    draft.setTypeface(ui.friendlyFont);
    draft.setText(target.getText());
    draft.setSingleLine(true);
    draft.setShowSoftInputOnFocus(false);
    draft.setTextSize(24);
    panel.addView(draft);
    draft.setSelection(draft.length());
    String[][] keys = {{"1", "2", "3"}, {"4", "5", "6"}, {"7", "8", "9"}, {".", "0", "⌫"}};
    for (String[] group : keys) {
      LinearLayout row = new LinearLayout(activity);
      panel.addView(row);
      for (String key : group) {
        MaterialButton b = new MaterialButton(activity);
        b.setText(key);
        b.setTextSize(22);
        b.setTypeface(ui.friendlyFont);
        b.setCornerRadius(0);
        b.setContentDescription(key.equals("⌫") ? "Borrar un carácter" : key);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ui.dp(56), 1);
        params.setMargins(ui.dp(2), 0, ui.dp(2), 0);
        row.addView(b, params);
        b.setOnClickListener(
            v -> {
              int start = Math.max(0, draft.getSelectionStart()),
                  end = Math.max(start, draft.getSelectionEnd());
              if (key.equals("⌫")) {
                if (end > start) draft.getText().delete(start, end);
                else if (start > 0) draft.getText().delete(start - 1, start);
              } else draft.getText().replace(start, end, key);
            });
      }
    }
    if (isQuantity) {
      LinearLayout units = new LinearLayout(activity);
      panel.addView(units);
      for (String unit : new String[] {"kg", "sol", "unid.", "bolsas"}) {
        MaterialButton b = new MaterialButton(activity);
        b.setText(unit);
        b.setAllCaps(false);
        b.setCornerRadius(0);
        b.setTypeface(ui.friendlyFont);
        b.setMinWidth(0);
        b.setPadding(0, 0, 0, 0);
        units.addView(b, new LinearLayout.LayoutParams(0, ui.dp(48), 1));
        b.setOnClickListener(
            v -> {
              String current =
                  draft.getText().toString().replaceAll("\\s*(kg|g|unid\\.|bolsas)$", "").trim();
              draft.setText(current + " " + unit);
              draft.setSelection(draft.length());
            });
      }
    }
    MaterialButton clear = ui.button("Limpiar", R.drawable.ic_delete);
    panel.addView(clear);
    clear.setOnClickListener(v -> draft.setText(""));
    ScrollView keypadScroll = new ScrollView(activity);
    keypadScroll.addView(panel);
    androidx.appcompat.app.AlertDialog dialog =
        new MaterialAlertDialogBuilder(activity)
            .setTitle(isQuantity ? "Cantidad" : "Precio en soles")
            .setView(keypadScroll)
            .setNegativeButton("Cancelar", null)
            .setPositiveButton("Listo", null)
            .create();
    dialog.setOnShowListener(
        d -> {
          dialog
              .getWindow()
              .setSoftInputMode(
                  android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
          dialog
              .getButton(-1)
              .setOnClickListener(
                  v -> {
                    String entered =
                        isQuantity ? draft.getText().toString().trim() : ui.validatedPrice(draft);
                    if (entered == null) return;
                    target.setText(entered);
                    target.setSelection(target.length());
                    dialog.dismiss();
                  });
        });
    dialog.show();
  }
}
