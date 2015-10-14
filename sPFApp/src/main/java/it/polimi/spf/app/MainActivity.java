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
package it.polimi.spf.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.polimi.spf.app.fragments.ActivityFragment;
import it.polimi.spf.app.fragments.NotificationFragment;
import it.polimi.spf.app.fragments.advertising.AdvertisingFragment;
import it.polimi.spf.app.fragments.appmanager.AppManagerFragment;
import it.polimi.spf.app.fragments.contacts.ContactsFragment;
import it.polimi.spf.app.fragments.personas.PersonasFragment;
import it.polimi.spf.app.fragments.profile.ProfileFragment;
import it.polimi.spf.app.permissiondisclaimer.PermissionDisclaimerDialogFragment;
import it.polimi.spf.framework.SPF;
import it.polimi.spf.framework.SPFContext;
import it.polimi.spf.framework.local.SPFService;

public class MainActivity extends AppCompatActivity implements
        PermissionDisclaimerDialogFragment.PermissionDisclaimerListener,
        SPFContext.OnEventListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1234;
    private static final String DIAG_TAG = "DIAG_TAG";
    private static final String PREFERENCES_FILE = "it.polimi.spf.navigationdrawer";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    public enum Entry {
        PROFILE, PERSONAS, CONTACTS, NOTIFICATIONS, ADVERTISING, APPS, ACTIVITIES;
    }

    private Map<Entry, PrimaryDrawerItem> mEntries;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private FrameLayout mContentFrame;
    private boolean mUserLearnedDrawer;
    private int mCurrentSelectedPosition;
    private boolean mFromSavedInstanceState;
    private Fragment currentFragment;
    private boolean tabletSize;
    private Drawer drawer;
    private DrawerBuilder drawerBuilder;
    private SwitchDrawerItem goSwitch;
    private SwitchDrawerItem autonomousSwitch;
    private SwitchDrawerItem proximitySwitch;
    private PrimaryDrawerItem contactsDrawerItem;
    private PrimaryDrawerItem notificationsDrawerItem;
    private PrimaryDrawerItem advertisingDrawerItem;

    /**
     * Array that contains the names of sections
     */
    private String[] mSectionNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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
                    (PermissionDisclaimerDialogFragment) getSupportFragmentManager().findFragmentByTag(DIAG_TAG);
            if (diagFrag == null) {
                diagFrag = PermissionDisclaimerDialogFragment.newInstance();
                diagFrag.show(getSupportFragmentManager(), DIAG_TAG);
                getSupportFragmentManager().executePendingTransactions();
            }
        }

        mSectionNames = getResources().getStringArray(R.array.content_fragments_titles);

        //this var says if we are on a tablet (true) or a smartphone (false)
        this.tabletSize = getResources().getBoolean(R.bool.isTablet);

        this.setupToolBar();

        mUserLearnedDrawer = Boolean.valueOf(readSharedSetting(this, PREF_USER_LEARNED_DRAWER, "false"));

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        mContentFrame = (FrameLayout) findViewById(R.id.container);

        goSwitch = new SwitchDrawerItem().withName("Group Owner")/*.withIcon(Octicons.Icon.oct_tools)*/.withChecked(false).withEnabled(true).withOnCheckedChangeListener(goSwitchListener);
        autonomousSwitch = new SwitchDrawerItem().withName("Autonomous GO")/*.withIcon(Octicons.Icon.oct_tools)*/.withChecked(true).withEnabled(false).withOnCheckedChangeListener(autonomousSwitchListener);
        proximitySwitch = new SwitchDrawerItem().withName("Proximity")/*.withIcon(Octicons.Icon.oct_tools)*/.withChecked(false).withEnabled(true).withOnCheckedChangeListener(proximitySwitchListener);

        contactsDrawerItem = new PrimaryDrawerItem().withName(mSectionNames[2]);
        notificationsDrawerItem = new PrimaryDrawerItem().withName(mSectionNames[3]);
        advertisingDrawerItem = new PrimaryDrawerItem().withName(mSectionNames[4]);

        drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withHeader(R.layout.drawer_header)
                .withToolbar(toolbar)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(mSectionNames[0]),
                        new PrimaryDrawerItem().withName(mSectionNames[1]),
                        contactsDrawerItem,
                        notificationsDrawerItem,
                        advertisingDrawerItem,
                        new PrimaryDrawerItem().withName(mSectionNames[5]),
                        new PrimaryDrawerItem().withName(mSectionNames[6]),
                        new DividerDrawerItem(),
                        goSwitch,
                        autonomousSwitch,
                        proximitySwitch
                )
                .withCloseOnClick(true)
                .withOnDrawerItemClickListener(drawerItemClickListener);


        if (tabletSize) {
            //on tablet like explained to me here:
            //https://github.com/mikepenz/MaterialDrawer/issues/743
            drawer = drawerBuilder.buildView();
            ((ViewGroup) findViewById(R.id.nav_tablet)).addView(drawer.getSlider());
        } else {
            //on smartphones
            drawer = drawerBuilder.build();
        }

        //default
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.container, createFragment(0)).
                commit();

        this.mEntries = new HashMap<>();
        this.mEntries.put(Entry.CONTACTS, contactsDrawerItem);
        this.mEntries.put(Entry.NOTIFICATIONS, notificationsDrawerItem);
        this.mEntries.put(Entry.ADVERTISING, advertisingDrawerItem);

        //update the navigation drawer badges
        Iterator it = this.mEntries.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry el = (Map.Entry)it.next();
            updateViewNotification((Entry)el.getKey(), (PrimaryDrawerItem)el.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }

        SPFContext.get().registerEventListener(this);
    }

    public void setupToolBar() {
        if (toolbar != null) {
            toolbar.setTitle("SPF");
            toolbar.setTitleTextColor(getResources().getColor(R.color.white_main));
            setSupportActionBar(toolbar);
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
		 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Required permission not enabled!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Required permission not enabled!");
                finish();
            } else {
                Log.d(TAG, "WOW!!! Required permission enabled! Thank you ;)");
                this.setUpFirstStartNavDrawer();
            }
        }
    }

    private Fragment createFragment(int position) {
        switch (position) {
            case 0:
                // Displays the profile
                currentFragment = ProfileFragment.createViewSelfProfileFragment();
                break;
            case 1:
                // Displays available personas
                currentFragment = new PersonasFragment();
                break;
            case 2:
                // Displays the list of friends
                currentFragment = new ContactsFragment();
                break;
            case 3:
                // Displays the list of notifications
                currentFragment = new NotificationFragment();
                break;
            case 4:
                // Displays advertising options
                currentFragment = new AdvertisingFragment();
                break;
            case 5:
                // Displays the list of apps authorized to interact with SPF
                currentFragment = new AppManagerFragment();
                break;
            case 6:
                currentFragment = new ActivityFragment();
                break;
        }
        return currentFragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notifications_delete_all:
                if (currentFragment instanceof NotificationFragment) {
                    ((NotificationFragment) currentFragment).clickedOptionItemDeleteAll();
                }
                break;
            case R.id.profileview_edit:
                if (currentFragment instanceof ProfileFragment) {
                    ((ProfileFragment) currentFragment).clickedOptionItemEdit();
                }
                break;
            case R.id.profileview_send_contact_request:
                if (currentFragment instanceof ProfileFragment) {
                    ((ProfileFragment) currentFragment).clickedOptionItemSendContactRequest();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, 0);
    }

    private void setUpFirstStartNavDrawer() {
        if (!tabletSize) {
            if (!mUserLearnedDrawer) {
                drawer.openDrawer();
                mUserLearnedDrawer = true;
                saveSharedSetting(this, PREF_USER_LEARNED_DRAWER, "true");
            }
        }
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    private Drawer.OnDrawerItemClickListener drawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            // do something with the clicked item :D
            mCurrentSelectedPosition = position - 1;
            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.container, createFragment(mCurrentSelectedPosition)).
                    commit();

            switch (mCurrentSelectedPosition) {
                case 0:
                    toolbar.inflateMenu(R.menu.menu_view_self_profile);

                    if (currentFragment instanceof ProfileFragment) {
//                                    MenuItem item = menu.findItem(R.id.profileview_persona_selector);
//                                    Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
//                                    List<SPFPersona> personas = SPF.get().getProfileManager().getAvailablePersonas();
//                                    spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, personas));
//                                    spinner.setSelection(personas.indexOf(((ProfileFragment) currentFragment).getMCurrentPersona()), false);
//                                    spinner.setOnItemSelectedListener((ProfileFragment) currentFragment);
                    }
                    break;
                case 3:
                    toolbar.inflateMenu(R.menu.menu_notifications);
                    break;
            }

            return true;
        }
    };


    private OnCheckedChangeListener goSwitchListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Log.d(TAG, "connectSwitch checked -> gointent=15");
                autonomousSwitch.withSwitchEnabled(true);
                ((SPFApp) getApplication()).updateIdentifier(15);
            } else {
                Log.d(TAG, "connectSwitch unchecked -> gointent=0");
                autonomousSwitch.withSwitchEnabled(false);
                ((SPFApp) getApplication()).updateIdentifier(0);
            }
            drawer.updateItem(autonomousSwitch);
        }
    };

    private OnCheckedChangeListener autonomousSwitchListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            Log.d(TAG, "goSwitch status = " + isChecked);
        }
    };

    private OnCheckedChangeListener proximitySwitchListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (goSwitch.isChecked()) {
                    Log.d(TAG, "connectSwitch checked -> gointent=15");
                    ((SPFApp) getApplication()).initSPF(15, autonomousSwitch.isChecked());
                } else {
                    ((SPFApp) getApplication()).initSPF(0, false);

                }
                SPFService.startForeground(getBaseContext());
                autonomousSwitch.withSwitchEnabled(false);
                goSwitch.withSwitchEnabled(false);
            } else {
                SPFService.stopForeground(getBaseContext());
                autonomousSwitch.withSwitchEnabled(true);
                goSwitch.withSwitchEnabled(true);
            }
            drawer.updateItem(autonomousSwitch);
            drawer.updateItem(goSwitch);
        }
    };

    @Override
    public void onEvent(int eventCode, Bundle payload) {
        Entry entry;

        switch (eventCode) {
            case SPFContext.EVENT_ADVERTISING_STATE_CHANGED: {
                entry = Entry.ADVERTISING;
                break;
            }
            case SPFContext.EVENT_NOTIFICATION_MESSAGE_RECEIVED: {
                entry = Entry.NOTIFICATIONS;
                break;
            }
            case SPFContext.EVENT_CONTACT_REQUEST_RECEIVED: {
                entry = Entry.CONTACTS;
                break;
            }
            default:
                return;
        }

        PrimaryDrawerItem item = mEntries.get(entry);
        updateViewNotification(entry, item);
    }

    private void updateViewNotification(Entry entry, PrimaryDrawerItem item) {
        switch (entry) {
            case NOTIFICATIONS:
                int notifCount = SPF.get().getNotificationManager().getAvailableNotificationCount();
                showNotificationOrClear(item, notifCount > 0 ? String.valueOf(notifCount) : null);
                break;
            case CONTACTS:
                int msgCount = SPF.get().getSecurityMonitor().getPersonRegistry().getPendingRequestCount();
                showNotificationOrClear(item, msgCount > 0 ? String.valueOf(msgCount) : null);
                break;
            case ADVERTISING:
                boolean active = SPF.get().getAdvertiseManager().isAdvertisingEnabled();
                showNotificationOrClear(item, active ? "ON" : null);
                break;
            default:
                break;
        }
    }

    private void showNotificationOrClear(PrimaryDrawerItem item, String text) {
        if (item == null) {
            return;
        }
        if (text == null) {
            item.withBadge("");
        } else {
            item.withBadge(text);
        }
        drawer.updateItem(item);
    }
}