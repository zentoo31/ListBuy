package com.zentoo.buylist.model;

/** One entry in the persistent shopping list. Prices are decimal strings, never floats. */
public final class ShoppingItem {
  public String name, quantity, price, kiloPrice = "";
  public boolean bought;

  public ShoppingItem(String name, String quantity, String price, boolean bought) {
    this.name = name;
    this.quantity = quantity;
    this.price = price;
    this.bought = bought;
  }
}
