package net.aloogle.apps.blogapp.other;

import android.app.Activity;
import android.net.ConnectivityManager;
import com.nineoldandroids.view.*;
import android.view.View;
import android.view.animation.*;
import android.view.ViewGroup;
import android.util.*;

public class Other {
	public static final Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
	public static final int numberPosts = 10;
	public static final String defaultUrl = "http://apps.aloogle.net/blogapp/blogger/json/";

	public static boolean isConnected(Activity activity) {
		@SuppressWarnings("static-access")
		ConnectivityManager cm = (ConnectivityManager)activity.getSystemService(activity.CONNECTIVITY_SERVICE);
		boolean connected = cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
		return connected;
	}

	public static void fabShow(boolean toShow, View v) {
		int translationY = toShow ? 0 : v.getHeight() + getMarginBottom(v);
		ViewPropertyAnimator.animate(v).setInterpolator(mInterpolator)
		.setDuration(200)
		.translationY(translationY);
	}

	public static int getMarginBottom(View v) {
		int marginBottom = 0;
		final ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
		if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
			marginBottom = ((ViewGroup.MarginLayoutParams)layoutParams).bottomMargin;
		}
		return marginBottom;
	}

	public static int dpToPx(Activity activity, int dp) {
		DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
		int px = Math.round(dp*(displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}
}
