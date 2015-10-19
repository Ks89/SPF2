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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import it.polimi.spf.framework.profile.SPFPersona;

public class MainActivity extends AppCompatActivity implements
        PermissionDisclaimerDialogFragment.PermissionDisclaimerListener,
        SPFContext.OnEventListener,
        ProfileFragment.ProfilePhotoListener {

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
    private ProfileDrawerItem profileDrawerItem;
    private AccountHeader headerResult;

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

        goSwitch = new SwitchDrawerItem().withName("Group Owner")/*.withIcon(Octicons.Icon.oct_tools)*/
                .withChecked(false).withEnabled(true).withOnCheckedChangeListener(goSwitchListener)
                .withSelectable(false)
                .withIdentifier(8);
        autonomousSwitch = new SwitchDrawerItem().withName("Autonomous GO")/*.withIcon(Octicons.Icon.oct_tools)*/
                .withChecked(true).withEnabled(true).withOnCheckedChangeListener(autonomousSwitchListener)
                .withSelectable(false)
                .withIdentifier(9);
        proximitySwitch = new SwitchDrawerItem().withName("Proximity")/*.withIcon(Octicons.Icon.oct_tools)*/
                .withChecked(false).withEnabled(true).withOnCheckedChangeListener(proximitySwitchListener)
                .withSelectable(false)
                .withIdentifier(10);

        contactsDrawerItem = new PrimaryDrawerItem().withName(mSectionNames[2]);
        notificationsDrawerItem = new PrimaryDrawerItem().withName(mSectionNames[3]);
        advertisingDrawerItem = new PrimaryDrawerItem().withName(mSectionNames[4]);

        profileDrawerItem = new ProfileDrawerItem().withIcon(getResources().getDrawable(R.drawable.empty_profile_picture)).withIdentifier(100);

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.drawer_header_new)
                .addProfiles(profileDrawerItem)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .withSelectionListEnabledForSingleProfile(false)
                .withSavedInstance(savedInstanceState)
                .build();

        drawerBuilder = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withHasStableIds(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(mSectionNames[0]).withIdentifier(0).withIcon(FontAwesome.Icon.faw_user),
                        new PrimaryDrawerItem().withName(mSectionNames[1]).withIdentifier(1).withIcon(FontAwesome.Icon.faw_users),
                        contactsDrawerItem.withIdentifier(2).withIcon(GoogleMaterial.Icon.gmd_book),
                        notificationsDrawerItem.withIdentifier(3).withIcon(FontAwesome.Icon.faw_exclamation_circle),
                        advertisingDrawerItem.withIdentifier(4).withIcon(FontAwesome.Icon.faw_eye),
                        new PrimaryDrawerItem().withName(mSectionNames[5]).withIdentifier(5).withIcon(GoogleMaterial.Icon.gmd_apps),
                        new PrimaryDrawerItem().withName(mSectionNames[6]).withIdentifier(6).withIcon(FontAwesome.Icon.faw_check_circle)
                )
                .addStickyDrawerItems(
                        goSwitch,
                        proximitySwitch
                )
                .withOnDrawerItemClickListener(drawerItemClickListener)
                .withSavedInstance(savedInstanceState);

        if (tabletSize) {
            //on tablet like explained to me here:
            //https://github.com/mikepenz/MaterialDrawer/issues/743
            drawer = drawerBuilder.buildView();
            ((ViewGroup) findViewById(R.id.nav_tablet)).addView(drawer.getSlider());
        } else {
            //on smartphones
            drawer = drawerBuilder.build();
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//            drawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
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
            Map.Entry el = (Map.Entry) it.next();
            updateViewNotification((Entry) el.getKey(), (PrimaryDrawerItem) el.getValue());
        }

        SPFContext.get().registerEventListener(this);
    }

    public void setupToolBar() {
        if (toolbar != null) {
            toolbar.setTitle(getResources().getString(R.string.app_name));
            toolbar.setTitleTextColor(getResources().getColor(R.color.toolbar_text_color));
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
                currentFragment = PersonasFragment.newInstance();
                break;
            case 2:
                // Displays the list of friends
                currentFragment = ContactsFragment.newInstance();
                break;
            case 3:
                // Displays the list of notifications
                currentFragment = NotificationFragment.newInstance();
                break;
            case 4:
                // Displays advertising options
                currentFragment = AdvertisingFragment.newInstance();
                break;
            case 5:
                // Displays the list of apps authorized to interact with SPF
                currentFragment = AppManagerFragment.newInstance();
                break;
            case 6:
                currentFragment = ActivityFragment.newInstance();
                break;
        }
        return currentFragment;
    }

    @Override
    public void onPhotoReady(Bitmap bitmap) {
        if (bitmap == null) {
            Log.d(TAG, "onPhotoReady, but photo is null");
            return;
        }
        Log.d(TAG, "onPhotoReady!!!");
        profileDrawerItem.withIcon(bitmap);
        headerResult.updateProfile(profileDrawerItem);
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
        //add the values which need to be saved from the drawer to the bundle
        outState = drawer.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
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
            getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.container, createFragment(drawerItem.getIdentifier())).
                    commit();

            //to prevent multiple items added every time that this method will be called
            toolbar.getMenu().clear();

            switch (drawerItem.getIdentifier()) {
                case 0:
                    initProfileFragmentToolbar();
                    break;
                case 3:
                    toolbar.inflateMenu(R.menu.menu_notifications);
                    break;
                case 8:
                    goSwitch.withChecked(true);
                    drawer.updateItem(goSwitch);
                    break;
                case 9:
                    autonomousSwitch.withChecked(true);
                    drawer.updateItem(autonomousSwitch);
                    break;
                case 10:
                    proximitySwitch.withChecked(true);
                    drawer.updateItem(proximitySwitch);
                    break;
            }

            drawer.closeDrawer();
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.initProfileFragmentToolbar();
        return super.onCreateOptionsMenu(menu);
    }

    private void initProfileFragmentToolbar() {
        toolbar.inflateMenu(R.menu.menu_view_self_profile);
        MenuItem item = toolbar.getMenu().findItem(R.id.profileview_persona_selector);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        List<SPFPersona> personas = SPF.get().getProfileManager().getAvailablePersonas();
        ArrayAdapter adapter = new ArrayAdapter<>(getBaseContext(), R.layout.spinner_toolbar_item, personas);
        adapter.setDropDownViewResource(R.layout.spinner_toolbar_item_dropdown);
        spinner.setAdapter(adapter);
        spinner.setSelection(personas.indexOf(((ProfileFragment) currentFragment).getMCurrentPersona()), false);
        spinner.setOnItemSelectedListener((ProfileFragment) currentFragment);

    }


    private OnCheckedChangeListener goSwitchListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Log.d(TAG, "connectSwitch checked -> gointent=15");
                drawer.removeAllStickyFooterItems();
                drawer.addStickyFooterItem(goSwitch);
                drawer.addStickyFooterItem(autonomousSwitch);
                drawer.addStickyFooterItem(proximitySwitch);
                autonomousSwitch.withChecked(true);
                ((SPFApp) getApplication()).updateIdentifier(15);
            } else {
                Log.d(TAG, "connectSwitch unchecked -> gointent=0");
                drawer.removeAllStickyFooterItems();
                drawer.addStickyFooterItem(goSwitch);
                drawer.addStickyFooterItem(proximitySwitch);
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
                drawer.removeAllStickyFooterItems();
                drawer.addStickyFooterItem(proximitySwitch);
            } else {
                SPFService.stopForeground(getBaseContext());
                drawer.removeAllStickyFooterItems();
                drawer.addStickyFooterItem(goSwitch);
                if(goSwitch.isChecked()) {
                    drawer.addStickyFooterItem(autonomousSwitch);
                }
                drawer.addStickyFooterItem(proximitySwitch);
            }
            drawer.updateItem(goSwitch);
            drawer.updateItem(autonomousSwitch);
            drawer.updateItem(proximitySwitch);
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
        item.withBadgeStyle(new BadgeStyle()
                .withTextColor(Color.WHITE)
                .withColorRes(R.color.md_red_700));
        StringHolder badge;
        if (text == null) {
            badge = null; //to remove the badge, use null
        } else {
            badge = new StringHolder(text);
        }
        drawer.updateBadge(item.getIdentifier(), badge);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}