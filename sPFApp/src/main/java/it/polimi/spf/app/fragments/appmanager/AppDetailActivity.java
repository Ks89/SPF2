/* 
 * Copyright 2014 Jacopo Aliprandi, Dario Archetti
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
package it.polimi.spf.app.fragments.appmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.polimi.spf.app.R;
import it.polimi.spf.framework.SPF;
import it.polimi.spf.framework.security.AppAuth;

public class AppDetailActivity extends AppCompatActivity {
    private static final String TAG = null;
    public static final String APP_AUTH_KEY = "app_auth";

    @Bind(R.id.app_detail_tabs)
    TabLayout tabLayout;
    @Bind(R.id.app_detail_pager)
    ViewPager mViewPager;
    @Bind(R.id.app_name_view)
    TextView appNameTextView;
    @Bind(R.id.app_identifier_view)
    TextView appIdTextView;
    @Bind(R.id.app_icon_view)
    ImageView appIconImageView;

    private AppAuth mAppAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            mAppAuth = getIntent().getParcelableExtra(APP_AUTH_KEY);
        } else {
            mAppAuth = savedInstanceState.getParcelable(APP_AUTH_KEY);
        }

        if (mAppAuth == null) {
            throw new IllegalStateException("App auth not found");
        }

        Drawable icon;
        try {
            icon = getPackageManager().getApplicationIcon(mAppAuth.getAppIdentifier());
        } catch (NameNotFoundException e) {
            Log.w(TAG, "Application identifier " + mAppAuth.getAppIdentifier() + " is not valid", e);
            return;
        }

        appNameTextView.setText(mAppAuth.getAppName());
        appIdTextView.setText(mAppAuth.getAppIdentifier());
        appIconImageView.setBackground(icon);

        String[] mPageTitles = getResources().getStringArray(R.array.appmanager_fragments_titles);
        tabLayout.addTab(tabLayout.newTab().setText(mPageTitles[0].toUpperCase(Locale.getDefault())));
        tabLayout.addTab(tabLayout.newTab().setText(mPageTitles[1].toUpperCase(Locale.getDefault())));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(mAppAuth,
                getSupportFragmentManager(), tabLayout.getTabCount());
        mViewPager.setAdapter(mSectionsPagerAdapter);
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

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(APP_AUTH_KEY, mAppAuth);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.appmanager_delete_app:
                onAppDelete();
                return true;
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onAppDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm app deletion")
                .setMessage("Do you want to remove " + mAppAuth.getAppName() + " from authorized applications?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPF.get().getSecurityMonitor().unregisterApplication(mAppAuth.getAppIdentifier());
                        finish();
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private static class SectionsPagerAdapter extends FragmentPagerAdapter {
        private AppAuth mappAuth;
        private int mNumOfTabs;

        public SectionsPagerAdapter(AppAuth appAuth, FragmentManager fm, int mNumOfTabs) {
            super(fm);
            this.mappAuth = appAuth;
            this.mNumOfTabs = mNumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(AppDetailActivity.APP_AUTH_KEY, mappAuth);
            Fragment fragment;

            switch (position) {
                case 0:
                    fragment = new AppPermissionsFragment();
                    break;
                case 1:
                    fragment = new AppServicesFragment();
                    break;
                default:
                    throw new IndexOutOfBoundsException("Pages count: " + mNumOfTabs + ", requested: " + position);
            }

            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }
}
