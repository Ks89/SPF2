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
package it.polimi.spf.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import it.polimi.spf.app.fragments.ActivityFragment;
import it.polimi.spf.app.fragments.NotificationFragment;
import it.polimi.spf.app.fragments.advertising.AdvertisingFragment;
import it.polimi.spf.app.fragments.appmanager.AppManagerFragment;
import it.polimi.spf.app.fragments.contacts.ContactsFragment;
import it.polimi.spf.app.fragments.personas.PersonasFragment;
import it.polimi.spf.app.fragments.profile.ProfileFragment;
import it.polimi.spf.app.navigation.NavigationDrawerFragment;
import it.polimi.spf.app.navigation.NavigationFragment;
import it.polimi.spf.app.permissiondisclaimer.PermissionDisclaimerDialogFragment;
import lombok.Getter;

public class MainActivity extends Activity implements
        NavigationFragment.ItemSelectedListener,
        PermissionDisclaimerDialogFragment.PermissionDisclaimerListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1234;
    private static final String DIAG_TAG = "DIAG_TAG";


    /**
     * Fragment managing the behaviors, interactions and presentation of the
     * navigation drawer.
     */
    @Getter
    private NavigationFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in
     * {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * Array that contains the names of sections
     */
    private String[] mSectionNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = getTitle();
        mSectionNames = getResources().getStringArray(R.array.content_fragments_titles);
        mNavigationDrawerFragment = (NavigationFragment) getFragmentManager().findFragmentById(R.id.navigation);

        getFragmentManager().executePendingTransactions();

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout != null) {
            // We are in two panes mode, thus set up the drawer.
            ((NavigationDrawerFragment) mNavigationDrawerFragment).setUpDrawer(R.id.navigation, drawerLayout);
        }

        /*
         * Workaround to fix the crash:
		 * android.view.WindowManager$BadTokenException: Unable to add window
		 * android.view.ViewRootImpl$W@3d67307 -- permission denied for this window type
		 * that appears only on Android 6.0 Marshmallow or greater.
		 * Start a dialog fragment to explain the procedure to the user.
		 * When the user accepts, onClickOnUnderstoodButton() will be called.
		 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            PermissionDisclaimerDialogFragment diagFrag =
                    (PermissionDisclaimerDialogFragment) getFragmentManager().findFragmentByTag(DIAG_TAG);
            if (diagFrag == null) {
                diagFrag = PermissionDisclaimerDialogFragment.newInstance();
                diagFrag.show(getFragmentManager(), DIAG_TAG);
                getFragmentManager().executePendingTransactions();
            }
        }

    }

    /**
     * Method defined in
     * {@link it.polimi.spf.app.permissiondisclaimer.PermissionDisclaimerDialogFragment.PermissionDisclaimerListener}
     * and called by
     * {@link it.polimi.spf.app.permissiondisclaimer.PermissionDisclaimerDialogFragment}
     */
    @Override
    public void onClickConfirmButton() {
        /*
         * Workaround to fix the crash:
		 * android.view.WindowManager$BadTokenException: Unable to add window
		 * android.view.ViewRootImpl$W@3d67307 -- permission denied for this window type
		 * that appears only on Android 6.0 Marshmallow or greater.
		 * See onActivityResult in this class!!!
		 * This solution comes from: TODO ADD LINK HERE
		 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            Log.d(TAG, "canDrawOverlay not enabled. Requesting... ");
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }
    }

    /**
     * Method defined in
     * {@link it.polimi.spf.app.permissiondisclaimer.PermissionDisclaimerDialogFragment.PermissionDisclaimerListener}
     * and called by
     * {@link it.polimi.spf.app.permissiondisclaimer.PermissionDisclaimerDialogFragment}
     */
    @Override
    public void onClickDenyButton() {
        Toast.makeText(this, "You must confirm to activate SPF", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "User clicked on DenyButton in Permission Disclaimer Dialog Fragment");
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
         * Workaround to fix the crash:
		 * android.view.WindowManager$BadTokenException: Unable to add window
		 * android.view.ViewRootImpl$W@3d67307 -- permission denied for this window type
		 * that appears only on Android 6.0 Marshmallow or greater.
		 * This code will be executed at the end of the onCreate() in this activity!!!
		 * This solution comes from: TODO ADD LINK HERE
		 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Required permission not enabled!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Required permission not enabled!");
                finish();
            } else {
                Log.d(TAG, "WOW!!! Required permission enabled! Thank you ;)");
            }
        }
    }

    @Override
    public void onItemSelect(int position, boolean replace) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        mTitle = getPageTitle(position);
        if (replace) {
            fragmentManager.beginTransaction().replace(R.id.container, createFragment(position)).commit();
        }
        invalidateOptionsMenu();
    }

    private String getPageTitle(int position) {
        return mSectionNames[position];
    }

    private Fragment createFragment(int position) {
        switch (position) {
            case 0:
                // Displays the profile
                return ProfileFragment.createViewSelfProfileFragment();
            case 1:
                // Displays available personas
                return new PersonasFragment();
            case 2:
                // Displays the list of friends
                return new ContactsFragment();
            case 3:
                // Displays the list of notifications
                return new NotificationFragment();
            case 4:
                // Displays advertising options
                return new AdvertisingFragment();
            case 5:
                // Displays the list of apps authorized to interact with SPF
                return new AppManagerFragment();
            case 6:
                return new ActivityFragment();
            default:
                throw new IndexOutOfBoundsException();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        restoreActionBar();
        if (mNavigationDrawerFragment.hasOptionsMenu()) {
            mNavigationDrawerFragment.onCreateOptionsMenu(menu, getMenuInflater());
            return true;
        }

        getCurrentFragment().onCreateOptionsMenu(menu, getMenuInflater());
        return true;

    }

    private Fragment getCurrentFragment() {
        getFragmentManager().executePendingTransactions();
        return getFragmentManager().findFragmentById(R.id.container);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mNavigationDrawerFragment.hasOptionsMenu()) {
            mNavigationDrawerFragment.onPrepareOptionsMenu(menu);
            return true;
        }

        getCurrentFragment().onPrepareOptionsMenu(menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mNavigationDrawerFragment.onOptionsItemSelected(item) ||
                getCurrentFragment().onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}