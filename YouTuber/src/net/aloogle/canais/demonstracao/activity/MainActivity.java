package net.aloogle.canais.demonstracao.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import net.aloogle.canais.demonstracao.R;
import net.aloogle.canais.demonstracao.adapter.DrawerAdapter;
import net.aloogle.canais.demonstracao.fragment.PlaylistFragment;
import net.aloogle.canais.demonstracao.fragment.MainFragment;
import net.aloogle.canais.demonstracao.other.Icons;
import net.aloogle.canais.demonstracao.other.Other;
import com.google.android.youtube.player.*;
import android.widget.AdapterView.*;
import android.widget.*;
import com.nineoldandroids.view.*;
import android.os.*;
import com.github.ksoichiro.android.observablescrollview.*;
import android.util.*;
import android.graphics.Color;
import android.support.design.widget.*;
import android.view.animation.*;
import net.aloogle.canais.demonstracao.other.*;
import net.aloogle.canais.demonstracao.lib.*;

@SuppressLint({ "DefaultLocale", "CutPasteId" })
public class MainActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {
	final Context context = this;
	public Toolbar mToolbar2;
	private DrawerLayout mDrawerLayout;
	public static ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private ArrayList <Icons> icons;
	ArrayList<String> linksnames = new ArrayList<String>();
	ArrayList<String> linksurls = new ArrayList<String>();
	ArrayList<String> linksicons = new ArrayList<String>();
	
	ArrayList<String> fplaylistsids = new ArrayList<String>();
	ArrayList<String> fplaylistsnames = new ArrayList<String>();
	ArrayList<String> fplaylistsicons = new ArrayList<String>();
	
	ArrayList<String> playlistsids = new ArrayList<String>();
	ArrayList<String> playlistsnames = new ArrayList<String>();
	ViewGroup footer, footer2, footer3, footer4;
	private DrawerAdapter adapter2;
	private TypedArray categoryIcons;
	public static FloatingActionButton fabopen;
	SharedPreferences preferences;
	Editor editor;
	String titulo, suggestion, lastBanner, playliststoken, fplaylistsname, linksname;
	ImageView imagem, imagem2;
	public static int pos;
	int linkscount, fplaylistscount, playliststotal;
	boolean passed, start, home, drawerloaded, alreadyadded;
	public static boolean gohome, fabvisible;
	View mDropShadow;

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private static final boolean TOOLBAR_IS_STICKY = true;

    public static Interpolator mInterpolator = new AccelerateDecelerateInterpolator();
	
    private View mToolbar;
    private View mImageView;
    private View mOverlayView;
    private View mRecyclerViewBackground;
    private ObservableRecyclerView mRecyclerView;
    private TextView mTitleView;
    private int mActionBarSize;
    private int mFlexibleSpaceImageHeight;
    private int mToolbarColor;

	private View mToolbarView;
	private ViewPager mPager;
	private NavigationAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		editor = preferences.edit();
		setContentView(R.layout.toolbar_drawer);

		mToolbar2 = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar2);
		
		if(getIntent().hasExtra("fromnotification")) {
			Intent intent = new Intent(this, PlayerViewActivity.class);
			intent.putExtra("id", getIntent().getStringExtra("id"));
			startActivity(intent);
		}
		
		titulo = getString(R.string.app_name);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mActionBarSize = getActionBarSize(this);
        mToolbarColor = getResources().getColor(R.color.colorPrimary);

        mToolbar = findViewById(R.id.toolbar);
        if (!TOOLBAR_IS_STICKY) {
            mToolbar.setBackgroundColor(Color.TRANSPARENT);
        }
        mImageView = findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);

        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(getTitle());
        setTitle(null);

        // mRecyclerViewBackground makes RecyclerView's background except header view.
        mRecyclerViewBackground = findViewById(R.id.list_background);

        //since you cannot programatically add a headerview to a recyclerview we added an empty view as the header
        // in the adapter and then are shifting the views OnCreateView to compensate
        final float scale = 1 + MAX_TEXT_SCALE_DELTA;
        mRecyclerViewBackground.post(new Runnable() {
				@Override
				public void run() {
					ViewHelper.setTranslationY(mRecyclerViewBackground, mFlexibleSpaceImageHeight);
				}
			});
        ViewHelper.setTranslationY(mOverlayView, mFlexibleSpaceImageHeight);
        mTitleView.post(new Runnable() {
				@Override
				public void run() {
					ViewHelper.setTranslationY(mTitleView, (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale));
					ViewHelper.setPivotX(mTitleView, 0);
					ViewHelper.setPivotY(mTitleView, 0);
					ViewHelper.setScaleX(mTitleView, scale);
					ViewHelper.setScaleY(mTitleView, scale);
				}
			});
			
		initDrawer(0);
		initNotification();
		
		drawerloaded = false;
		fabvisible = true;
		alreadyadded = false;
		
		mToolbarView = findViewById(R.id.toolbar);
		mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mPagerAdapter);

		pos = 1;
		linkscount = 0;
		fplaylistscount = 0;
		passed = false;
		start = false;

		if (savedInstanceState != null) {
			pos = savedInstanceState.getInt("position");
		}
		
		fabopen = (FloatingActionButton)findViewById(R.id.fabopen);
		
		imagem = (ImageView)findViewById(R.id.image);
		imagem2 = (ImageView)findViewById(R.id.imageView1);
		lastBanner = preferences.getString("lastBanner", "");
		if(preferences.contains("lastBanner")) {
			setImage(imagem, true, lastBanner, true);
			setImage(imagem2, false, lastBanner, true);
		}
			
		if(Other.isConnected(this)) {
			loadDrawer();
		}
			
		selectItem(0);
	}
	
    public static int getActionBarSize(AppCompatActivity activity) {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = activity.obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }
	
	public void setImage(final ImageView image, final boolean config, final String url, final boolean fromOff) {
			Ion.with (context)
				.load(url)
				.withBitmap()
				.intoImageView(image)
				.setCallback(new FutureCallback<ImageView>() {
					@Override
					public void onCompleted(Exception e, final ImageView imageView) {
						if (e != null) {
							return;
						}
						if(config) {
							if(!alreadyadded) {
						new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {
									mOverlayView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, image.getHeight()));

									View paddingView = new View(MainActivity.this);
									AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, image.getHeight());
									paddingView.setLayoutParams(lp);

									// This is required to disable header's list selector effect
									paddingView.setClickable(true);

									MainFragment.list.addHeaderView(paddingView, MainFragment.list, true);
									alreadyadded = true;
								}
							}, 100);
						}}
						if(!fromOff) {
						editor.putString("lastBanner", url);
						editor.commit();
						}
					}
				});
	}
	
	public void loadDrawer() {
		Ion.with(this)
			.load("http://apps.aloogle.net/blogapp/youtuber/json/playlists.php?id=" + getString(R.string.channelid) + "&key=" + getString(R.string.developerkey))
			.asJsonObject()
			.setCallback(new FutureCallback<JsonObject>() {
				@Override
				public void onCompleted(Exception e, JsonObject json) {
					if(e != null) {
						return;
					}
					JsonArray links = json.get("links").getAsJsonObject().get("links").getAsJsonArray();
					for (int i = 0; i < links.size(); i++) {
						JsonObject c = links.get(i).getAsJsonObject();

						String name = c.get("name").getAsString();
						String url = c.get("url").getAsString();
						String icon = c.get("icon").getAsString();

						linksnames.add(name);
						linksurls.add(url);
						linksicons.add(icon);
					}
					linksname = json.get("links").getAsJsonObject().get("name").getAsString();

					JsonArray playlists = json.get("playlists").getAsJsonObject().get("playlists").getAsJsonArray();
					for (int i = 0; i < playlists.size(); i++) {
						JsonObject c = playlists.get(i).getAsJsonObject();

						String id = c.get("id").getAsString();
						String name = c.get("titulo").getAsString();

						playlistsids.add(id);
						playlistsnames.add(name);
					}
					playliststotal = json.get("playlists").getAsJsonObject().get("total").getAsInt();
					playliststoken = json.get("token").getAsString();

					JsonArray fplaylists = json.get("featuredplaylists").getAsJsonObject().get("playlists").getAsJsonArray();
					for (int i = 0; i < fplaylists.size(); i++) {
						JsonObject c = fplaylists.get(i).getAsJsonObject();

						String id = c.get("id").getAsString();
						String name = c.get("name").getAsString();
						String icon = c.get("icon").getAsString();

						fplaylistsids.add(id);
						fplaylistsnames.add(name);
						fplaylistsicons.add(icon);
					}
					fplaylistsname = json.get("featuredplaylists").getAsJsonObject().get("name").getAsString();
					
					drawerloaded = true;
					initDrawer(1);
				}
			});

		Ion.with(this)
			.load("http://apps.aloogle.net/blogapp/youtuber/json/channel.php?id=" + getString(R.string.channelid) + "&key=" + getString(R.string.developerkey))
			.asJsonObject()
			.setCallback(new FutureCallback<JsonObject>() {
				@Override
				public void onCompleted(Exception e, final JsonObject json) {
					if(e != null) {
						return;
					}
					String banner = json.get("images").getAsJsonArray().get(3).getAsString();
					if(!banner.equals(lastBanner)) {
						setImage(imagem, true, banner, false);
						setImage(imagem2, false, banner, false);
					}}
			});
	}
	
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Translate list background
        ViewHelper.setTranslationY(mRecyclerViewBackground, Math.max(0, -scrollY + mFlexibleSpaceImageHeight));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        setPivotXToTitle();
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        if (TOOLBAR_IS_STICKY) {
            titleTranslationY = Math.max(0, titleTranslationY);
        }
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);


        if (TOOLBAR_IS_STICKY) {
            // Change alpha of toolbar background
            if (-scrollY + mFlexibleSpaceImageHeight <= mActionBarSize) {
                mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(1, mToolbarColor));
            } else {
                mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, mToolbarColor));
            }
        } else {
            // Translate Toolbar
            if (scrollY < mFlexibleSpaceImageHeight) {
                ViewHelper.setTranslationY(mToolbar, 0);
            } else {
                ViewHelper.setTranslationY(mToolbar, -scrollY);
            }
        }
    }

    @Override
    public void onDownMotionEvent() {
    }
	
    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
		if(scrollState == ScrollState.DOWN) {
			if(!fabvisible) {
				fabvisible = true;
				showFab(fabopen, fabvisible);
			}
		} else if(scrollState == ScrollState.UP) {
			if(fabvisible) {
				fabvisible = false;
				showFab(fabopen, fabvisible);
			}
		}
    }

	public static void showFab(FloatingActionButton fab, boolean toShow) {
		int translationY = toShow ? 0 : fab.getHeight() + getMarginBottom(fab);
		ViewPropertyAnimator.animate(fab).setInterpolator(mInterpolator)
			.setDuration(200)
			.translationY(translationY);
		
	}
	
    public static int getMarginBottom(FloatingActionButton fab) {
        int marginBottom = 0;
        final ViewGroup.LayoutParams layoutParams = fab.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
        }
        return marginBottom;
    }

    private void setPivotXToTitle() {
        Configuration config = getResources().getConfiguration();
        if (Build.VERSION_CODES.JELLY_BEAN_MR1 <= Build.VERSION.SDK_INT
			&& config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            ViewHelper.setPivotX(mTitleView, findViewById(android.R.id.content).getWidth());
        } else {
            ViewHelper.setPivotX(mTitleView, 0);
        }
    }
	
	public void initDrawer(int fase) {
		LayoutInflater inflater = getLayoutInflater();
		if(fase == 0) {
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

		mDrawerList = (ListView)findViewById(R.id.left_drawer);

		mDrawerToggle = new ActionBarDrawerToggle(
				this,
				mDrawerLayout,
				mToolbar2,
				R.string.drawer_open,
				R.string.drawer_close) {

			public void onDrawerClosed(View view) {
				supportInvalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				supportInvalidateOptionsMenu();
			}
		};

		mDrawerToggle.syncState();

		mDrawerLayout.setDrawerListener(mDrawerToggle);

			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

			final ViewGroup header = (ViewGroup)inflater.inflate(R.layout.header,
																 mDrawerList, false);
			footer = (ViewGroup)inflater.inflate(R.layout.footer,
																 mDrawerList, false);
			footer2 = (ViewGroup)inflater.inflate(R.layout.footer2,
																  mDrawerList, false);
			footer3 = (ViewGroup)inflater.inflate(R.layout.footer3,
												  mDrawerList, false);
			footer4 = (ViewGroup)inflater.inflate(R.layout.footer4,
												  mDrawerList, false);

			mDrawerList.addHeaderView(header, null, true);
			if(Other.isConnected(this)) {
				mDrawerList.addFooterView(footer3, null, false);
			} else {
				CustomTextView text = (CustomTextView)footer4.findViewById(R.id.myTextView4);
				text.setText("Tentar novamente");
				
				footer4.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							if(Other.isConnected(MainActivity.this)) {
								mDrawerList.removeFooterView(footer);
								mDrawerList.removeFooterView(footer2);
								mDrawerList.removeFooterView(footer4);
								mDrawerList.addFooterView(footer3, null, false);
								mDrawerList.addFooterView(footer, null, false);
								mDrawerList.addFooterView(footer2, null, false);
								loadDrawer();
							} else {
								Toast toast = Toast.makeText(MainActivity.this, getString(R.string.needinternet), Toast.LENGTH_SHORT);
								toast.show();
							}
						}
					});
				mDrawerList.addFooterView(footer4, null, false);
			}
			mDrawerList.addFooterView(footer, null, false);
			footer.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent settings = new Intent(MainActivity.this, FragmentActivity.class);
						settings.putExtra("fragment", 0);
						startActivity(settings);
					}
				});
			mDrawerList.addFooterView(footer2, null, false);
			footer2.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent about = new Intent(MainActivity.this, FragmentActivity.class);
						about.putExtra("fragment", 2);
						startActivity(about);
					}
				});
			categoryIcons = getResources().obtainTypedArray(R.array.drawable_ids);

			icons = new ArrayList < Icons > ();

			icons.add(new Icons("Início", categoryIcons.getResourceId(0, -1), false, ""));
			icons.add(new Icons("Rede sociais", categoryIcons.getResourceId(0, -1), true, ""));
			icons.add(new Icons("Facebook", categoryIcons.getResourceId(1, -1), false, ""));
			icons.add(new Icons("Twitter", categoryIcons.getResourceId(2, -1), false, ""));
			
			adapter2 = new DrawerAdapter(getApplicationContext(), icons);
			mDrawerList.setAdapter(adapter2);

			mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

			mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		} else {
			if(linksnames.size() > 0) {
				icons.add(new Icons(linksname, categoryIcons.getResourceId(0, -1), true, ""));
			for (int i = 0; i < linksnames.size(); i++) {
				icons.add(new Icons(linksnames.get(i), categoryIcons.getResourceId(4, (i + 1)*-1), false, linksicons.get(i)));
			}
			linkscount = linksnames.size() + 1;
			}

			if(fplaylistsids.size() > 0) {
				icons.add(new Icons(fplaylistsname, categoryIcons.getResourceId(0, -1), true, ""));
			for (int i = 0; i < fplaylistsids.size(); i++) {
				icons.add(new Icons(fplaylistsnames.get(i), categoryIcons.getResourceId(3, (i + 1)*-1), false, fplaylistsicons.get(i)));
			}
			fplaylistscount = fplaylistsids.size() + 1;
			}

			if(playlistsids.size() > 0) {
				icons.add(new Icons("Playlists", categoryIcons.getResourceId(0, -1), true, ""));
				for (int i = 0; i < playlistsids.size(); i++) {
					icons.add(new Icons(playlistsnames.get(i), categoryIcons.getResourceId(3, (i + 1)*-1), false, ""));
				}
			}
			
			if(playliststotal > 25) {
				ViewGroup footer5 = (ViewGroup)inflater.inflate(R.layout.footer4,
																mDrawerList, false);
				CustomTextView text = (CustomTextView)footer5.findViewById(R.id.myTextView4);
				text.setText("Carregar mais");
				footer5.findViewById(R.id.divider).setVisibility(View.GONE);
				footer5.findViewById(R.id.divider2).setVisibility(View.GONE);

				footer5.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent playlists = new Intent(MainActivity.this, FragmentActivity.class);
							playlists.putExtra("fragment", 3);
							playlists.putExtra("token", playliststoken);
							startActivity(playlists);
						}
					});
				mDrawerList.removeFooterView(footer);
				mDrawerList.removeFooterView(footer2);
				mDrawerList.addFooterView(footer5, null, false);
				mDrawerList.addFooterView(footer, null, false);
				mDrawerList.addFooterView(footer2, null, false);
			}
			
			adapter2.notifyDataSetChanged();
			
			categoryIcons.recycle();
			
			mDrawerList.removeFooterView(footer3);
		}
	}

	public void initNotification() {
		boolean notification = preferences.getBoolean("prefNotification", true);
		if (!notification) {
			if (System.currentTimeMillis() > preferences.getLong("longNotification", 0)) {
				final AlertDialog dialogsprites = new AlertDialog.Builder(MainActivity.this)
					.setTitle(R.string.notifications)
					.setMessage(R.string.dialog_notification)
					.setPositiveButton(R.string.yes, null)
					.setNegativeButton(R.string.no, null)
					.create();

				dialogsprites.setOnShowListener(new
					DialogInterface.OnShowListener() {
					@Override
					public void onShow(DialogInterface dialog) {
						Button b = dialogsprites.getButton(AlertDialog.BUTTON_POSITIVE);
						b.setOnClickListener(new
							View.OnClickListener() {
							@Override
							public void onClick(View view) {
								editor.putBoolean("prefNotification", true);
								editor.commit();
								editor.putLong("longNotification", 0);
								editor.commit();
								dialogsprites.dismiss();
							}
						});
						Button n = dialogsprites.getButton(AlertDialog.BUTTON_NEGATIVE);
						n.setOnClickListener(new
							View.OnClickListener() {
							@Override
							public void onClick(View view) {
								editor.putLong("longNotification", System.currentTimeMillis() + 15*24*60*60*1000);
								editor.commit();
								dialogsprites.dismiss();
							}
						});
					}
				});
				dialogsprites.show();
			}
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {

		super.onPostCreate(savedInstanceState);

		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView <  ?  > parent, View view, int position, long id) {
			int posi;

			if (position == 0) {
				posi = 1;
				mDrawerList.setItemChecked(1, true);
			} else {
				posi = position;
			}

			if (posi != pos) {
				selectItem(posi);
			}
		}
	}

	public void Home() {
		if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					theHome();
				}
			}, 100);
		} else {
			theHome();
		}
	}

	public void theHome() {
		fabopen.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View p1) {
					Intent intent = YouTubeIntents.createUserIntent(MainActivity.this, getString(R.string.channeluser));
					startActivity(intent);
				}
			});

		fabopen.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View p1) {
					Toast toast = Toast.makeText(MainActivity.this, "Abrir no app do YouTube", Toast.LENGTH_SHORT);
					toast.show();
					return false;
				}
			});
			
		titulo = "";
		passed = false;
		start = false;
		home = true;
		pos = 1;
		mDrawerList.setItemChecked(1, true);
		mPager.setVisibility(View.VISIBLE);
		FrameLayout content = (FrameLayout)findViewById(R.id.content_frame);
		content.setVisibility(View.GONE);
		getSupportActionBar().setTitle("");
		supportInvalidateOptionsMenu();
	}

	public void Clear(String id, String title, int thepos) {
		mToolbar.setBackgroundColor(mToolbarColor);
		mDropShadow.setVisibility(View.VISIBLE);
		fabvisible = true;
		showFab(fabopen, fabvisible);
		titulo = title;
		passed = true;
		home = false;
		Bundle bundle = new Bundle();
		bundle.putString("id", id);
		bundle.putString("titulo", title);
		bundle.putInt("pos", thepos);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment category = new PlaylistFragment();
		category.setArguments(bundle);
		ft.replace(R.id.content_frame, category);
		if (start) {
			ft.addToBackStack(null);
		} else {
			start = true;
		}
		ft.commit();
		mPager.setVisibility(View.GONE);
		FrameLayout content = (FrameLayout)findViewById(R.id.content_frame);
		content.setVisibility(View.VISIBLE);
	}

	private void selectItem(int position) {
		switch (position) {
		case 0:
			Home();
			break;
		case 1:
			Home();
			break;
		}
		
		String[] socialnetworks = getResources().getStringArray(R.array.allow_sites2);
		int n = socialnetworks.length + 4;
			int n2 = position - socialnetworks.length - 1;

			if(position > 1 && position < n) {
				String[] usernames = getResources().getStringArray(R.array.socialnetworksusers);
				String[] sitenames = getResources().getStringArray(R.array.socialnetworksnames);
				Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
				intent.putExtra("fragment", 5);
				intent.putExtra("titulo", sitenames[n2]);
				intent.putExtra("url", "http://" + socialnetworks[n2] + "/" + usernames[n2]);
				startActivity(intent);
			}

		if(position >= n && position < n + linkscount && linkscount > 0) {
			Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
			intent.putExtra("fragment", 5);
			intent.putExtra("titulo", linksnames.get(position - n));
			intent.putExtra("url", linksurls.get(position - n));
			startActivity(intent);
		}
		
		if(position >= n + linkscount && position < n + linkscount + fplaylistscount && fplaylistscount > 0) {
			Clear(fplaylistsids.get(position - n - linkscount), fplaylistsnames.get(position - n - linkscount), position);
		}
			
		if(position >= n + linkscount + fplaylistscount) {
			Clear(playlistsids.get(position - n - linkscount - fplaylistscount), playlistsnames.get(position - n - linkscount - fplaylistscount), position);
		}
		
		if (position > 1 && position <= n + linkscount) {
			mDrawerList.setItemChecked(pos, true);
		}
		
		supportInvalidateOptionsMenu();

		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		MenuItem searchItem = menu.findItem(R.id.menu_search);
		SearchView searchView = (SearchView)MenuItemCompat.getActionView(searchItem);
		searchView.setQueryHint(getString(R.string.search));

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				try {
					Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
					intent.putExtra("fragment", 4);
					intent.putExtra("query", URLEncoder.encode(s, "UTF-8"));
					startActivity(intent);
				} catch (UnsupportedEncodingException e) {}
				return false;
			}

			@Override
			public boolean onQueryTextChange(final String s) {
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.menu_search).setIcon(R.drawable.ic_search);

		if (pos == 0 || pos == 1) {
			menu.findItem(R.id.menu_refresh).setVisible(true);
		} else if (pos > 1 && pos < 11) {
			menu.findItem(R.id.menu_refresh).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {

		private static final String[]TITLES = new String[]{
			"Recentes"
		};

		public NavigationAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		protected Fragment createItem(int position) {
			Fragment f;
			final int pattern = position % 1;
			switch (pattern) {
			case 0:
				f = new MainFragment();
				break;
			default:
				f = new MainFragment();
				break;
			}
			return f;
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
			if (drawerOpen) {
				mDrawerLayout.closeDrawer(mDrawerList);
				return true;
			} else {
				if (home) {
					MainActivity.this.finish();
					return true;
				} else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
					getSupportFragmentManager().popBackStack();
					return true;
				} else {
					selectItem(1);
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt("position", pos);
		super.onSaveInstanceState(savedInstanceState);
	}

	public void onResume() {
		if (home) {
			titulo = getString(R.string.app_name);
		}
		supportInvalidateOptionsMenu();
		super.onResume();
	}
}
