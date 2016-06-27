package com.backrestore.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.backrestore.fragments.FragmentBackup;
import com.backrestore.fragments.FragmentRestore;

/**
 * Created by Nadzer
 * 29/03/2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentBackup();
            case 1:
                return new FragmentRestore();
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Backup";
            case 1:
                return "Restore";
        }
        return super.getPageTitle(position);
    }
}
