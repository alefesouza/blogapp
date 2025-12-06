package net.aloogle.apps.acasadocogumelo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import net.aloogle.apps.acasadocogumelo.fragment.MainFragment;
import net.aloogle.apps.acasadocogumelo.fragment.PopularFragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
	public TabPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		String title = null;
		if (position == 0) {
			title = "Recentes";
		} else if (position == 1) {
			title = "Populares";
		}
		return title;
	}

	@Override
	public Fragment getItem(int i) {
		switch (i) {
		case 0:
			return new MainFragment();
		case 1:
			return new PopularFragment();
		}
		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}
}
