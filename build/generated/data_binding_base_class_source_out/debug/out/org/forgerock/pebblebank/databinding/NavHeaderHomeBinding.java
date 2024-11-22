// Generated by view binder compiler. Do not edit!
package org.forgerock.pebblebank.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import org.forgerock.pebblebank.R;

public final class NavHeaderHomeBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView headerSubTitle;

  @NonNull
  public final TextView headerTitle;

  @NonNull
  public final ImageView me;

  private NavHeaderHomeBinding(@NonNull LinearLayout rootView, @NonNull TextView headerSubTitle,
      @NonNull TextView headerTitle, @NonNull ImageView me) {
    this.rootView = rootView;
    this.headerSubTitle = headerSubTitle;
    this.headerTitle = headerTitle;
    this.me = me;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static NavHeaderHomeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static NavHeaderHomeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.nav_header_home, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static NavHeaderHomeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.headerSubTitle;
      TextView headerSubTitle = ViewBindings.findChildViewById(rootView, id);
      if (headerSubTitle == null) {
        break missingId;
      }

      id = R.id.headerTitle;
      TextView headerTitle = ViewBindings.findChildViewById(rootView, id);
      if (headerTitle == null) {
        break missingId;
      }

      id = R.id.me;
      ImageView me = ViewBindings.findChildViewById(rootView, id);
      if (me == null) {
        break missingId;
      }

      return new NavHeaderHomeBinding((LinearLayout) rootView, headerSubTitle, headerTitle, me);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
