package com.zentoo.buylist;

import static org.junit.Assert.*;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.junit.Test;

public class KeyboardNavigationTest {
  @Test
  public void navigationKeepsHeightWithKeyboardOnBothScreens() {
    Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
    Intent intent =
        new Intent(instrumentation.getTargetContext(), MainActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    MainActivity activity = (MainActivity) instrumentation.startActivitySync(intent);
    instrumentation.waitForIdleSync();
    try {
      BottomNavigationView navigation = activity.findViewById(R.id.bottom_navigation);
      int expected = Math.round(64 * activity.getResources().getDisplayMetrics().density);
      assertEquals(expected, navigation.getHeight());
      for (int screen : new int[] {1, 2}) {
        instrumentation.runOnMainSync(() -> navigation.setSelectedItemId(screen));
        instrumentation.waitForIdleSync();
        instrumentation.runOnMainSync(
            () -> {
              EditText input = findVisibleTextInput(activity.getWindow().getDecorView());
              assertNotNull(input);
              input.requestFocus();
              ((InputMethodManager) activity.getSystemService(MainActivity.INPUT_METHOD_SERVICE))
                  .showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            });
        awaitKeyboard(activity, instrumentation, true);
        instrumentation.runOnMainSync(
            () -> {
              assertEquals(expected, navigation.getHeight());
              assertEquals(0, navigation.getPaddingBottom());
              assertEquals(0, navigation.getPaddingTop());
              ((InputMethodManager) activity.getSystemService(MainActivity.INPUT_METHOD_SERVICE))
                  .hideSoftInputFromWindow(navigation.getWindowToken(), 0);
            });
        awaitKeyboard(activity, instrumentation, false);
        assertEquals(expected, navigation.getHeight());
      }
    } finally {
      instrumentation.runOnMainSync(activity::finish);
    }
  }

  private static EditText findVisibleTextInput(View view) {
    if (view.getVisibility() != View.VISIBLE) return null;
    if (view instanceof EditText && ((EditText) view).getShowSoftInputOnFocus())
      return (EditText) view;
    if (view instanceof ViewGroup) {
      ViewGroup group = (ViewGroup) view;
      for (int i = 0; i < group.getChildCount(); i++) {
        EditText result = findVisibleTextInput(group.getChildAt(i));
        if (result != null) return result;
      }
    }
    return null;
  }

  private static void awaitKeyboard(
      MainActivity activity, Instrumentation instrumentation, boolean visible) {
    long deadline = SystemClock.uptimeMillis() + 8000;
    boolean[] actual = {false};
    do {
      instrumentation.waitForIdleSync();
      instrumentation.runOnMainSync(
          () -> {
            WindowInsetsCompat insets =
                ViewCompat.getRootWindowInsets(activity.getWindow().getDecorView());
            actual[0] = insets != null && insets.isVisible(WindowInsetsCompat.Type.ime());
          });
      if (actual[0] == visible) return;
      SystemClock.sleep(100);
    } while (SystemClock.uptimeMillis() < deadline);
    fail("Keyboard visibility did not become " + visible);
  }
}
