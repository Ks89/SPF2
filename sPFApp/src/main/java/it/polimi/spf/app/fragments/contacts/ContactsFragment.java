/*
 * Copyright 2014 Jacopo Aliprandi, Dario Archetti
 * Copyright 2015 Stefano Cappa
 *
 * This file is part of SPF.
 *
 * SPF is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * SPF is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SPF.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package it.polimi.spf.app.fragments.contacts;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.polimi.spf.app.R;

public class ContactsFragment extends Fragment {

    @Bind(R.id.contacts_tabs)
    TabLayout tabLayout;
    @Bind(R.id.contacts_pager)
    ViewPager mViewPager;

    public static ContactsFragment newInstance() {
        return new ContactsFragment();
    }

    public ContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_fragment_contacts, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] mPageTitles = getResources().getStringArray(R.array.contacts_fragments_titles);
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText(mPageTitles[0].toUpperCase(Locale.getDefault())));
        tabLayout.addTab(tabLayout.newTab().setText(mPageTitles[1].toUpperCase(Locale.getDefault())));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        ContactsPagerAdapter pagerAdapter = new ContactsPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());

        mViewPager.setAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private static class ContactsPagerAdapter extends FragmentStatePagerAdapter {
        private int mNumOfTabs;

        public ContactsPagerAdapter(FragmentManager fm, int numOfTabs) {
            super(fm);
            this.mNumOfTabs = numOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PeopleFragment();
                case 1:
                    return new CircleFragment();
                default:
                    throw new IllegalArgumentException("Requested page outside boundaries");
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

}