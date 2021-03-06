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
package it.polimi.spf.app.fragments.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import it.polimi.spf.app.LoadersConfig;
import it.polimi.spf.app.MainActivity;
import it.polimi.spf.app.R;
import it.polimi.spf.app.fragments.contacts.ContactConfirmDialogView;
import it.polimi.spf.framework.SPF;
import it.polimi.spf.framework.profile.SPFPersona;
import it.polimi.spf.framework.proximity.SPFRemoteInstance;
import it.polimi.spf.shared.model.ProfileField;
import it.polimi.spf.shared.model.ProfileFieldContainer;
import lombok.Getter;

public class ProfileFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<ProfileFieldContainer>,
        OnItemSelectedListener,
        OnClickListener {

    public interface ProfilePhotoListener {
        void onPhotoReady(Bitmap bitmap);
    }

    @Bind(R.id.profileedit_tabs)
    TabLayout tabLayout;
    @Bind(R.id.profileedit_pager)
    ViewPager mViewPager;

    @Nullable
    @Bind(R.id.profile_picture)
    CircleImageView resultView;

    /**
     * Possible visualization modes of fields values.
     *
     * @author darioarchetti
     */
    public enum Mode {
        /**
         * Shows the profile of the local user
         */
        SELF,

        /**
         * Shows the profile of a remote user
         */
        REMOTE,

        /**
         * Shows the profile of the local user and allows modifications
         */
        EDIT;
    }

    /**
     * Creates a new instance of {@link ProfileFragment} to show the profile of
     * a remote user. The fragment will allow only to view the fields and not to
     * modify them.
     *
     * @param personIdentifer - the identifier of the person whose profile to show
     * @return an instance of ProfileFragment
     */
    public static ProfileFragment createRemoteProfileFragment(String personIdentifer) {
        Bundle b = new Bundle();
        b.putInt(EXTRA_VIEW_MODE, Mode.REMOTE.ordinal());
        b.putString(EXTRA_PERSON_IDENTIFIER, personIdentifer);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(b);
        return fragment;
    }

    /**
     * Creates a new instance of ProfileFragment to show the local profile.
     *
     * @return an instance of ProfileFragment
     */
    public static ProfileFragment createViewSelfProfileFragment() {
        Bundle b = new Bundle();
        b.putInt(EXTRA_VIEW_MODE, Mode.SELF.ordinal());
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(b);
        return fragment;
    }

    public static ProfileFragment createEditSelfProfileFragment(SPFPersona persona) {
        Bundle b = new Bundle();
        b.putInt(EXTRA_VIEW_MODE, Mode.EDIT.ordinal());
        b.putParcelable(EXTRA_CURRENT_PERSONA, persona);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(b);
        return fragment;
    }

    private static final String EXTRA_PERSON_IDENTIFIER = "personIdentifier";
    private static final String EXTRA_PROFILE_CONTAINER = "profileContainer";
    private static final String EXTRA_CURRENT_PERSONA = "persona";
    private static final String EXTRA_VIEW_MODE = "viewMode";

    private static final int ACTIVITY_EDIT_PROFILE_CODE = 0;
    private static final String TAG = "ProfileFragment";

    private String mPersonIdentifier;
    @Getter
    private SPFPersona mCurrentPersona;
    private Mode mMode;
    private ProfileFieldContainer mContainer;

    private ProfileFieldViewFactory mFactory;
    private boolean mModifiedAtLeastOnce = false;
    private int deviceWidth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root;
        mMode = Mode.values()[getArguments().getInt(EXTRA_VIEW_MODE)];
        switch (mMode) {
            case EDIT:
            case REMOTE:
                root = inflater.inflate(R.layout.content_fragment_profile, container, false);
                break;
            case SELF:
            default:
                root = inflater.inflate(R.layout.content_fragment_profile_no_photo, container, false);
                break;
        }

        ButterKnife.bind(this, root);

        deviceWidth = root.getMeasuredWidth();

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null) {
            switch (mMode) {
                case SELF:
                    String callerApp = getActivity().getCallingPackage();
                    if (callerApp == null) {
                        mCurrentPersona = SPFPersona.DEFAULT;
                    } else {
                        mCurrentPersona = SPF.get().getSecurityMonitor().getPersonaOf(callerApp);
                    }
                    break;
                case REMOTE:
                    mPersonIdentifier = getArguments().getString(EXTRA_PERSON_IDENTIFIER);
                    break;
                case EDIT:
                    mCurrentPersona = getArguments().getParcelable(EXTRA_CURRENT_PERSONA);
            }

            // Initialize the loader of profile information
            startLoader(LoadersConfig.LOAD_PROFILE_LOADER_ID);
        } else {
            mPersonIdentifier = savedInstanceState.getString(EXTRA_PERSON_IDENTIFIER);
            mCurrentPersona = savedInstanceState.getParcelable(EXTRA_CURRENT_PERSONA);
            mMode = Mode.values()[savedInstanceState.getInt(EXTRA_VIEW_MODE)];
            mContainer = savedInstanceState.getParcelable(EXTRA_PROFILE_CONTAINER);

            onProfileDataAvailable();
        }

        //workaround used to center tabs or scroll tabs based on screen width
        //http://stackoverflow.com/questions/30616474/
        // android-support-design-tablayout-gravity-center-and-mode-scrollable/31861336#31861336
        tabLayout.post(new Runnable() {
            @Override
            public void run() {

                final int tabLayoutWidth = tabLayout.getWidth();

                Log.d(TAG, "tabLayoutWidth: " + tabLayoutWidth + " , deviceWidth: " + deviceWidth);

                //removed to prevent a crash during screen rotarion
//                DisplayMetrics metrics = new DisplayMetrics();
//                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

                if (tabLayoutWidth < deviceWidth) {
                    Log.d(TAG, (tabLayoutWidth < deviceWidth) + "");
                    tabLayout.setTabMode(TabLayout.MODE_FIXED);
                    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                } else {
                    Log.d(TAG, (tabLayoutWidth < deviceWidth) + "");
                    tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                }
            }
        });

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_PERSON_IDENTIFIER, mPersonIdentifier);
        outState.putParcelable(EXTRA_CURRENT_PERSONA, mCurrentPersona);
        outState.putInt(EXTRA_VIEW_MODE, mMode.ordinal());
        outState.putParcelable(EXTRA_PROFILE_CONTAINER, mContainer);
    }

    /*
     * LOADERS - Used to load and save profile data.
     */
    @Override
    public Loader<ProfileFieldContainer> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LoadersConfig.LOAD_PROFILE_LOADER_ID:
                return new AsyncTaskLoader<ProfileFieldContainer>(getActivity()) {

                    @Override
                    public ProfileFieldContainer loadInBackground() {
                        if (mMode == Mode.SELF || mMode == Mode.EDIT) {
                            return SPF.get().getProfileManager().getProfileFieldBulk(mCurrentPersona, ProfilePagerAdapter.DEFAULT_FIELDS);
                        } else {
                            SPFRemoteInstance instance = SPF.get().getPeopleManager().getPerson(mPersonIdentifier);
                            if (instance == null) {
                                throw new IllegalStateException("Person " + mPersonIdentifier + " not found in proximity");
                            } else {
                                String app = getActivity().getCallingPackage();
                                app = app == null ? "it.polimi.spf.app" : app;
                                return instance.getProfileBulk(ProfileField.toIdentifierList(ProfilePagerAdapter.DEFAULT_FIELDS), app);
                            }
                        }
                    }
                };

            case LoadersConfig.SAVE_PROFILE_LOADER_ID:
                if (mMode != Mode.EDIT) {
                    Log.e(TAG, "SAVE_PROFILE_LOADER initialized in mode " + mMode);
                }

                return new AsyncTaskLoader<ProfileFieldContainer>(getActivity()) {

                    @Override
                    public ProfileFieldContainer loadInBackground() {
                        SPF.get().getProfileManager().setProfileFieldBulk(mContainer, mCurrentPersona);
                        return null;
                    }
                };

            default:
                throw new IllegalArgumentException("No loader for id " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<ProfileFieldContainer> loader, ProfileFieldContainer data) {
        switch (loader.getId()) {
            case LoadersConfig.LOAD_PROFILE_LOADER_ID:
                mContainer = data;
                onProfileDataAvailable();
                break;
            case LoadersConfig.SAVE_PROFILE_LOADER_ID:
                mContainer.clearModified();
                mModifiedAtLeastOnce = true;
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<ProfileFieldContainer> loader) {
        // Do nothing
    }

    // Called when the profile data is available, thus we can set up the view
    private void onProfileDataAvailable() {
        Log.d(TAG, "onProfileDataAvailable");
        mFactory = new ProfileFieldViewFactory(getActivity(), mMode, mCurrentPersona, mContainer);

        String[] mPageTitles = this.getResources().getStringArray(R.array.profileedit_fragments_titles);

        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText(mPageTitles[0].toUpperCase(Locale.getDefault())));
        tabLayout.addTab(tabLayout.newTab().setText(mPageTitles[1].toUpperCase(Locale.getDefault())));
        tabLayout.addTab(tabLayout.newTab().setText(mPageTitles[2].toUpperCase(Locale.getDefault())));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        ProfilePagerAdapter mPagerAdapter = new ProfilePagerAdapter(getChildFragmentManager(), mMode, tabLayout.getTabCount());

        mViewPager.setAdapter(mPagerAdapter);
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


        if (mMode != Mode.SELF) {
            showPicture(mContainer.getFieldValue(ProfileField.PHOTO));
        } else {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).onPhotoReady(mContainer.getFieldValue(ProfileField.PHOTO));
            } else if (getActivity() instanceof ProfileViewActivity) {
                //TODO implement this
//                ((ProfileViewActivity) getActivity()).onPhotoReady(mContainer.getFieldValue(ProfileField.PHOTO));
            }
        }

        // Refresh field fragments
        mPagerAdapter.onRefresh();
    }

    private void showPicture(Bitmap photo) {
        if (this.resultView == null) {
            return;
        }

        // Show picture
        if (mMode == Mode.EDIT) {
            this.resultView.setOnClickListener(this);
        }

        if (photo != null) {
            this.resultView.setImageBitmap(photo);
            this.resultView.invalidate();
        }
    }

    // Methods to be called from child ProfileFieldsFragment to obtain views and
    // to notify of
    // changes in values of circles

    /**
     * Creates a view for the given profile field. Depending on the {@link Mode}
     * of visualization, the view may allow the modification of the value. This
     * method will not attach the view to the provided {@link ViewGroup}
     *
     * @param field     - the field for which to create the view.
     * @param container - the container to which the view will be attached, needed by
     *                  {@link LayoutInflater#inflate(int, ViewGroup, boolean)} to
     *                  evaluate layout params.
     * @return
     */
    public <E> View createViewFor(ProfileField<E> field, ViewGroup container) {
        switch (mMode) {
            case SELF:
            case REMOTE:
                return mFactory.createViewForField(field, container, null);
            case EDIT:
                return mFactory.createViewForField(field, container, new ProfileFieldViewFactory.FieldValueListener<E>() {

                    @Override
                    public void onFieldValueChanged(ProfileField<E> field, E value) {
                        mContainer.setFieldValue(field, value);
                    }

                    @Override
                    public void onInvalidFieldValue(ProfileField<E> field, String fieldFriendlyName) {
                        Toast.makeText(getActivity(), "Invalid value for field " + fieldFriendlyName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCircleAdded(ProfileField<E> field, String circle) {
                        Log.d(TAG, "Circle " + circle + " added to field " + field + " of persona " + mCurrentPersona);
                        SPF.get().getProfileManager().addGroupToField(field, circle, mCurrentPersona);
                    }

                    @Override
                    public void onCircleRemoved(ProfileField<E> field, String circle) {
                        Log.d(TAG, "Circle " + circle + " removed from field " + field + " of persona " + mCurrentPersona);
                        SPF.get().getProfileManager().removeGroupFromField(field, circle, mCurrentPersona);
                    }
                });
            default:
                return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ACTIVITY_EDIT_PROFILE_CODE:
                // Profile may have changed, reload it
                if (resultCode == Activity.RESULT_CANCELED) {
                    Log.d(TAG, "Edit finished but no data was modified");
                    onProfileDataNotSaved();
                } else {
                    onProfileDataSaved();
                }
                startLoader(LoadersConfig.LOAD_PROFILE_LOADER_ID);
                break;
        }
    }


    /**
     * Method to start the activity to crop an image.
     *
     * @param source
     */
    public void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(this.getActivity().getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this.getActivity());
    }

    /**
     * Method to set an show a cropped imaged.
     *
     * @param resultCode
     * @param result
     */
    public void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = Crop.getOutput(result);
            resultView.setImageURI(uri);

            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(uri.getPath());
                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                myBitmap = Bitmap.createScaledBitmap(myBitmap, 130, 130, false);

                mContainer.setFieldValue(ProfileField.PHOTO, myBitmap);
                showPicture(myBitmap);

            } catch (FileNotFoundException e) {
                Log.e(TAG, "handleCrop FileInputStream-file not found from uri.getpath", e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, "handleCrop closing input stream error", e);
                    }
                }
            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this.getActivity(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void clickedOptionItemEdit() {
        Intent i = new Intent(getActivity(), ProfileEditActivity.class);
        i.putExtra(ProfileEditActivity.EXTRA_PERSONA, mCurrentPersona);
        startActivityForResult(i, ACTIVITY_EDIT_PROFILE_CODE);
    }

    public void clickedOptionItemSendContactRequest() {
        final String displayName = mContainer.getFieldValue(ProfileField.DISPLAY_NAME);
        final Bitmap picture = mContainer.getFieldValue(ProfileField.PHOTO);

        final ContactConfirmDialogView view = new ContactConfirmDialogView(getActivity(), displayName, picture);
        new AlertDialog.Builder(getActivity()).setTitle("Send contact request?").setView(view).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SPF.get().getSecurityMonitor().getPersonRegistry().sendContactRequestTo(mPersonIdentifier, view.getPassphrase(), displayName, picture, view.getSelectedCircles());
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void clickedOptionItemSave() {
        if (mContainer.isModified()) {
            startLoader(LoadersConfig.SAVE_PROFILE_LOADER_ID);
        } else {
            onSaveNotNecessary();
        }
    }


    /*
        * Click on the profile picture in edit mode starts an activity to change
        * the profile picture
        */
    @Override
    public void onClick(View v) {
        Crop.pickImage(this.getActivity());
    }


    /*
     * ItemSelectListener for Persona spinner in actionbar
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SPFPersona persona = (SPFPersona) parent.getItemAtPosition(position);
        if (mCurrentPersona.equals(persona)) {
            return;
        }

        mCurrentPersona = persona;
        startLoader(LoadersConfig.LOAD_PROFILE_LOADER_ID);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    private void startLoader(int id) {
        getLoaderManager().destroyLoader(id);
        getLoaderManager().initLoader(id, null, this).forceLoad();
    }

    private void onProfileDataSaved() {
        Toast.makeText(getActivity(), "Profile data saved", Toast.LENGTH_SHORT).show();
    }

    private void onProfileDataNotSaved() {
        Toast.makeText(getActivity(), "Profile data NOT saved", Toast.LENGTH_SHORT).show();
    }

    private void onSaveNotNecessary() {
        Toast.makeText(getActivity(), "No field modified", Toast.LENGTH_SHORT).show();
    }

    public boolean isContainerModified() {
        return mContainer.isModified();
    }

    public boolean isContainerModifiedAtLeastOnce() {
        return mModifiedAtLeastOnce;
    }
}