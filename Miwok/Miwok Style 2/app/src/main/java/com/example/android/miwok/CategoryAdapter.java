package com.example.android.miwok;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by androidweardev on 5/25/17.
 */

public class CategoryAdapter extends FragmentPagerAdapter {

    private Context context;
    private String[] tabTitles = new String[]{"Numbers","Family","Colors","Phrases"};

    public CategoryAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        //you usally put if statement then initialize a new fragment depending of the position
        if (position == 0) {
            return new NumbersFragment();
        } else if (position == 1) {
            return new FamilyFragment();
        } else if (position == 2) {
            return new ColorsFragment();
        } else {
            return new PhrasesFragment();
        }


    }

    @Override
    public int getCount() {
        //return the element 0 to 3 (zero is counted)
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        // Generate title based on item position
        return tabTitles[position];
    }

}
