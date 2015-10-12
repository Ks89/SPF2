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
package it.polimi.spf.app.fragments.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.soundcloud.android.crop.Crop;

import it.polimi.spf.app.R;
import it.polimi.spf.framework.profile.SPFPersona;

public class ProfileEditActivity extends AppCompatActivity {

    public static final String EXTRA_PERSONA = "persona";

    private ProfileFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        if (savedInstanceState == null) {
            //received from ProfileFragment - onOptionsItemSelected - case R.id.profileview_edit
            SPFPersona persona = getIntent().getExtras().getParcelable(EXTRA_PERSONA);
            mFragment = ProfileFragment.createEditSelfProfileFragment(persona);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment).commit();
        } else {
            mFragment = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        }

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mFragment.onCreateOptionsMenu(menu, getMenuInflater());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profileedit_save:
                if (mFragment != null) {
                    (mFragment).clickedOptionItemSave();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return mFragment.onOptionsItemSelected(item);
        }
    }

    @Override
    public void finish() {
        setResult(mFragment.isContainerModifiedAtLeastOnce() ? RESULT_OK : RESULT_CANCELED);

        if (mFragment.isContainerModified()) {
            new AlertDialog.Builder(this).setMessage(R.string.profileedit_confirm_message).setPositiveButton(R.string.profileedit_confirm_yes, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setResult(RESULT_CANCELED);
                    ProfileEditActivity.super.finish();
                }
            }).setNegativeButton(R.string.profileedit_confirm_no, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        } else {
            super.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        Log.d("ProfileEditActivity", "Crop image calle by ProfileEditActivity");
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            mFragment.beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            mFragment.handleCrop(resultCode, result);
        }
    }
}