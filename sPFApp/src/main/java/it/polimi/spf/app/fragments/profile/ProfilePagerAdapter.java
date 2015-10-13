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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import it.polimi.spf.shared.model.ProfileField;

public class ProfilePagerAdapter extends FragmentStatePagerAdapter {
    public static final ProfileField<?>[] DEFAULT_FIELDS = {ProfileField.IDENTIFIER, ProfileField.DISPLAY_NAME, ProfileField.GENDER, ProfileField.BIRTHDAY, ProfileField.LOCATION, ProfileField.EMAILS, ProfileField.ABOUT_ME, ProfileField.STATUS, ProfileField.PHOTO, ProfileField.INTERESTS};
    public static final ProfileField<?>[] PERSONAL_FIELDS = {ProfileField.IDENTIFIER, ProfileField.DISPLAY_NAME, ProfileField.GENDER, ProfileField.BIRTHDAY, ProfileField.LOCATION, ProfileField.EMAILS};
    public static final ProfileField<?>[] EDITABLE_PERSONAL_FIELDS = {ProfileField.DISPLAY_NAME, ProfileField.GENDER, ProfileField.BIRTHDAY, ProfileField.LOCATION, ProfileField.EMAILS};
    public static ProfileField<?>[] ABOUT_ME_FIELDS = {ProfileField.ABOUT_ME, ProfileField.STATUS};
    public static ProfileField<?>[] TAG_FIELDS = {ProfileField.INTERESTS};

    private int mNumOfTabs;
    private ProfileFieldsFragment[] mCurrentFragments;
    private ProfileFragment.Mode mMode;

    public ProfilePagerAdapter(FragmentManager fm, ProfileFragment.Mode mode, int mNumOfTabs) {
        super(fm);
        this.mMode = mode;
        this.mNumOfTabs = mNumOfTabs;
        this.mCurrentFragments = new ProfileFieldsFragment[this.mNumOfTabs];
    }

    @Override
    public Fragment getItem(int arg0) {
        ProfileField<?>[] fields;

        switch (arg0) {
            case 0:
                fields = mMode == ProfileFragment.Mode.EDIT ? EDITABLE_PERSONAL_FIELDS : PERSONAL_FIELDS;
                break;
            case 1:
                fields = TAG_FIELDS;
                break;
            case 2:
                fields = ABOUT_ME_FIELDS;
                break;
            default:
                throw new IndexOutOfBoundsException("Page " + arg0 + "/" + this.mNumOfTabs);
        }

        ProfileFieldsFragment fragment = ProfileFieldsFragment.newInstance(fields);
        mCurrentFragments[arg0] = fragment;
        return fragment;
    }

    public void onRefresh() {
        for (ProfileFieldsFragment fragment : mCurrentFragments) {
            if (fragment != null) {
                fragment.onRefresh();
            }
        }
    }

    @Override
    public int getCount() {
        return this.mNumOfTabs;
    }
}