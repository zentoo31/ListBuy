package com.zentoo.buylist.ui;

import static com.zentoo.buylist.ui.UiComponents.GREEN;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zentoo.buylist.R;
import com.zentoo.buylist.data.MarketRepository;
import com.zentoo.buylist.domain.Prices;
import com.zentoo.buylist.model.ShoppingItem;
import java.util.*;

public final class ShoppingScreen {
  private final AppCompatActivity activity;
  private final UiComponents ui;
  private final MarketRepository repository;
  private final List<ShoppingItem> items;
  private LinearLayout rows;
  private TextView summary, total;
  private EditText name, quantity;

  public ShoppingScreen(AppCompatActivity activity, UiComponents ui, MarketRepository repository) {
    this.activity = activity;
    this.ui = ui;
    this.repository = repository;
    items = repository.items();
  }

  public View createView() {
    ScrollView page = new ScrollView(activity);
    page.setFillViewport(true);
    LinearLayout root = ui.column();
    page.addView(root);
    TextView eyebrow = ui.text("TU COMPRA, A MANO", 12);
    eyebrow.setTextColor(GREEN);
    root.addView(eyebrow);
    LinearLayout brand = new LinearLayout(activity);
    brand.setGravity(android.view.Gravity.CENTER_VERTICAL);
    root.addView(brand);
    LinearLayout brandText = ui.column();
    brand.addView(brandText, new LinearLayout.LayoutParams(0, -2, 1));
    TextView title = ui.text("ListBuy", 32);
    title.setTypeface(ui.friendlyFont, Typeface.BOLD);
    brandText.addView(title);
    TextView author = ui.text("por zentoo31", 12);
    author.setTextColor(GREEN);
    brandText.addView(author);
    ImageView avatar = new ImageView(activity);
    avatar.setImageResource(R.drawable.author_avatar);
    avatar.setContentDescription("Avatar de zentoo31");
    avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
    GradientDrawable avatarShape = ui.background(Color.WHITE);
    avatarShape.setCornerRadius(ui.dp(28));
    avatar.setBackground(avatarShape);
    avatar.setClipToOutline(true);
    brand.addView(avatar, new LinearLayout.LayoutParams(ui.dp(56), ui.dp(56)));
    root.addView(ui.text("Anota lo que necesitas. Completa el precio al comprar.", 14));
    LinearLayout form = ui.column();
    form.setPadding(ui.dp(16), ui.dp(12), ui.dp(16), ui.dp(12));
    form.setBackground(ui.background(Color.WHITE));
    LinearLayout.LayoutParams formParams = new LinearLayout.LayoutParams(-1, -2);
    formParams.setMargins(0, ui.dp(20), 0, ui.dp(12));
    root.addView(form, formParams);
    name = ui.field(form, "¿Qué necesitas comprar?", UiComponents.FieldKind.PRODUCT);
    quantity = ui.field(form, "Cantidad · ej. 2 kg, 3 unidades", UiComponents.FieldKind.QUANTITY);
    MaterialButton add = ui.button("Agregar producto", R.drawable.ic_add);
    form.addView(add);
    add.setOnClickListener(
        v -> {
          String n = ui.value(name), q = ui.value(quantity);
          if (n.isEmpty()) {
            name.setError("Escribe el nombre del producto");
            name.requestFocus();
            return;
          }
          items.add(new ShoppingItem(n, q, "", false));
          repository.saveItems();
          render();
          name.setText("");
          quantity.setText("");
          name.requestFocus();
        });
    summary = ui.text("", 14);
    root.addView(summary);
    root.addView(ui.text("Mantén presionado un producto para eliminarlo.", 12));
    rows = ui.column();
    root.addView(rows);
    LinearLayout footer = ui.column();
    footer.setPadding(0, ui.dp(10), 0, ui.dp(8));
    total = ui.text("", 22);
    total.setTypeface(ui.friendlyFont, Typeface.BOLD);
    footer.addView(total);
    footer.addView(ui.text("Suma de los importes registrados por producto", 12));
    MaterialButton share = ui.button("Compartir resumen", R.drawable.ic_share);
    footer.addView(share);
    share.setOnClickListener(v -> share());
    root.addView(footer);

    render();
    return page;
  }

  private void render() {
    rows.removeAllViews();
    int bought = 0;
    for (ShoppingItem i : items) if (i.bought) bought++;
    summary.setText(
        items.size()
            + " productos · "
            + (items.size() - bought)
            + " pendientes · "
            + bought
            + " comprados");
    total.setText("Total registrado  " + Prices.format(Prices.total(items).toPlainString()));
    if (items.isEmpty()) {
      TextView empty =
          ui.text(
              "Tu próxima compra empieza aquí\n\n"
                  + "Agrega un producto arriba. Tu lista se guarda automáticamente en este"
                  + " teléfono.",
              17);
      empty.setPadding(ui.dp(16), ui.dp(28), ui.dp(16), ui.dp(28));
      rows.addView(empty);
      return;
    }
    for (ShoppingItem i : items) {
      LinearLayout card = ui.column();
      card.setPadding(ui.dp(12), ui.dp(8), ui.dp(12), ui.dp(8));
      card.setBackground(ui.background(Color.WHITE));
      LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2);
      cp.setMargins(0, ui.dp(8), 0, 0);
      rows.addView(card, cp);
      LinearLayout line = new LinearLayout(activity);
      line.setGravity(android.view.Gravity.CENTER_VERTICAL);
      card.addView(line);
      CheckBox check = new CheckBox(activity);
      check.setChecked(i.bought);
      check.setContentDescription("Marcar " + i.name + " como comprado");
      line.addView(check);
      LinearLayout details = ui.column();
      line.addView(details, new LinearLayout.LayoutParams(0, -2, 1));
      TextView label = ui.text(i.name, 18);
      label.setTypeface(ui.friendlyFont, Typeface.BOLD);
      if (i.bought) label.setPaintFlags(label.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
      details.addView(label);
      details.addView(
          ui.text(
              (i.quantity.isEmpty() ? "Sin cantidad" : i.quantity)
                  + " · "
                  + (i.bought ? "Comprado" : "Pendiente"),
              13));
      TextView price = ui.text(i.price.isEmpty() ? "Sin precio" : Prices.format(i.price), 14);
      price.setTextColor(GREEN);
      line.addView(price);
      if (!i.kiloPrice.isEmpty())
        details.addView(ui.text(Prices.format(i.kiloPrice) + " / kg", 13));
      check.setOnCheckedChangeListener(
          (b, c) -> {
            i.bought = c;
            repository.saveItems();
            render();
          });
      MaterialButton edit =
          ui.button(
              i.price.isEmpty() ? "Registrar precio / editar" : "Editar producto",
              R.drawable.ic_edit);
      edit.setBackgroundTintList(
          android.content.res.ColorStateList.valueOf(Color.rgb(233, 243, 235)));
      edit.setTextColor(GREEN);
      edit.setIconTint(android.content.res.ColorStateList.valueOf(GREEN));
      card.addView(edit);
      edit.setOnClickListener(v -> edit(i));
      View.OnLongClickListener delete =
          v -> {
            confirmDelete(i);
            return true;
          };
      card.setOnLongClickListener(delete);
      line.setOnLongClickListener(delete);
      details.setOnLongClickListener(delete);
      label.setOnLongClickListener(delete);
      price.setOnLongClickListener(delete);
      check.setOnLongClickListener(delete);
      edit.setOnLongClickListener(delete);
    }
  }

  private void confirmDelete(ShoppingItem item) {
    new MaterialAlertDialogBuilder(activity)
        .setTitle("¿Eliminar " + item.name + "?")
        .setIcon(R.drawable.ic_delete)
        .setMessage("Se quitará de tu lista del mercado.")
        .setNegativeButton("Cancelar", null)
        .setPositiveButton(
            "Eliminar",
            (dialog, which) -> {
              items.remove(item);
              repository.saveItems();
              render();
            })
        .show();
  }

  private void edit(ShoppingItem item) {
    LinearLayout content = ui.column();
    content.setPadding(ui.dp(24), ui.dp(8), ui.dp(24), 0);
    EditText n = ui.field(content, "Producto", UiComponents.FieldKind.PRODUCT);
    n.setText(item.name);
    EditText q = ui.field(content, "Cantidad", UiComponents.FieldKind.QUANTITY);
    q.setText(item.quantity);
    EditText p =
        ui.field(
            content, "Precio total del producto (S/) · opcional", UiComponents.FieldKind.PRICE);
    p.setText(item.price);
    EditText kg =
        ui.field(content, "Precio por kilo (S/) · opcional", UiComponents.FieldKind.PRICE);
    kg.setText(item.kiloPrice);
    content.addView(
        ui.text(
            "Ejemplo: si 2 bolsas costaron S/ 8, registra 8. Puedes dejarlo vacío y completarlo"
                + " después.",
            13));
    ScrollView editScroll = new ScrollView(activity);
    editScroll.addView(content);
    androidx.appcompat.app.AlertDialog dialog =
        new MaterialAlertDialogBuilder(activity)
            .setTitle("Editar producto")
            .setView(editScroll)
            .setNegativeButton("Cancelar", null)
            .setNeutralButton("Eliminar", (d, w) -> confirmDelete(item))
            .setPositiveButton("Guardar", null)
            .create();
    dialog.setOnDismissListener(d -> ui.releaseSuggestion(n));
    dialog.setOnShowListener(
        d ->
            dialog
                .getButton(-1)
                .setOnClickListener(
                    v -> {
                      if (ui.value(n).isEmpty()) {
                        n.setError("Escribe el nombre");
                        return;
                      }
                      String price = ui.validatedPrice(p);
                      if (price == null) return;
                      String kilo = ui.validatedPrice(kg);
                      if (kilo == null) return;
                      item.name = ui.value(n);
                      item.quantity = ui.value(q);
                      item.price = price;
                      item.kiloPrice = kilo;
                      repository.saveItems();
                      render();
                      dialog.dismiss();
                    }));
    dialog.show();
  }

  private void share() {
    if (items.isEmpty()) {
      Toast.makeText(activity, "Agrega productos para compartir tu resumen", Toast.LENGTH_SHORT)
          .show();
      return;
    }
    StringBuilder s = new StringBuilder("ListBuy · Mi mercado\n\n");
    for (ShoppingItem i : items)
      s.append(i.bought ? "✓ " : "○ ")
          .append(i.name)
          .append(i.quantity.isEmpty() ? "" : " — " + i.quantity)
          .append(i.price.isEmpty() ? " — sin precio" : " — " + Prices.format(i.price))
          .append(i.kiloPrice.isEmpty() ? "" : " — " + Prices.format(i.kiloPrice) + "/kg")
          .append('\n');
    s.append("\nTotal registrado: ").append(Prices.format(Prices.total(items).toPlainString()));
    Intent send = new Intent(Intent.ACTION_SEND);
    send.setType("text/plain");
    send.putExtra(Intent.EXTRA_TEXT, s.toString());
    activity.startActivity(Intent.createChooser(send, "Compartir resumen del mercado"));
  }
}
