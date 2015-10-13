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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import it.polimi.spf.app.fragments.appmanager.PermissionArrayAdapter;
import it.polimi.spf.framework.SPF;
import it.polimi.spf.framework.profile.SPFPersona;
import it.polimi.spf.framework.security.AppRegistrationHandler;
import it.polimi.spf.framework.security.PermissionHelper;
import it.polimi.spf.shared.model.AppDescriptor;
import it.polimi.spf.shared.model.Permission;

/**
 * {@link AppRegistrationHandler} implementation that displays a popup to let
 * the user choose whether or not to accept the registration request.
 *
 * @author darioarchetti
 */
public class PopupAppRegistrationHandler implements AppRegistrationHandler {

    /*
     * (non-Javadoc)
     *
     * @see it.polimi.spf.framework.security.AppRegistrationHandler#
     * handleRegistrationRequest(android.content.Context,
     * it.polimi.spf.shared.model.AppDescriptor,
     * it.polimi.spf.framework.security.AppRegistrationHandler.Callback)
     */
    @Override
    public void handleRegistrationRequest(Context context, AppDescriptor descriptor, final Callback callback) {
        final DialogView dialogView = new DialogView(context, descriptor);

        Dialog dialog = new AlertDialog.Builder(context).setPositiveButton(R.string.app_permission_dialog_allow, new AlertDialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onRequestAccepted(dialogView.getSelectedPersona());
            }
        }).setNegativeButton(R.string.app_permission_dialog_deny, new AlertDialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onRequestRefused();
            }
        }).setView(dialogView.getView()).create();


        /*
         * Workaround to fix the crash:
		 * android.view.WindowManager$BadTokenException: Unable to add window
		 * android.view.ViewRootImpl$W@3d67307 -- permission denied for this window type
		 * that appears only on Android 6.0 Marshmallow or greater.
		 * Start a dialog fragment to explain the procedure to the user.
		 * When the user accepts, onClickOnUnderstoodButton() will be called.
		 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //if android MarshMallow or greater
            if (Settings.canDrawOverlays(context)) {
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.show();
            } else {
                //to prevent a crash
                Toast.makeText(context, "You must activate SPF!", Toast.LENGTH_SHORT).show();
            }
        } else {
            //other older Android's versions
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.show();
        }
    }

    private static class DialogView implements OnItemSelectedListener {
        private AppDescriptor mDescriptor;
        private Context mContext;
        private SPFPersona mCurrentPersona;

        public DialogView(Context context, AppDescriptor mDescriptor) {
            this.mDescriptor = mDescriptor;
            this.mContext = context;
        }

        // See http://www.doubleencore.com/2013/05/layout-inflation-as-intended/
        public View getView() {
            View v = LayoutInflater.from(mContext).inflate(R.layout.app_auth_dialog, null);
            ((TextView) v.findViewById(R.id.app_name_view)).setText(mDescriptor.getAppName());
            ((TextView) v.findViewById(R.id.app_identifier_view)).setText(mDescriptor.getAppIdentifier());

            try {
                Drawable icon = mContext.getPackageManager().getApplicationIcon(mDescriptor.getAppIdentifier());
                ((ImageView) v.findViewById(R.id.app_icon_view)).setBackground(icon);
            } catch (NameNotFoundException e) {
                // TODO identifier is not valid
            }

            // Show permission list
            Permission[] permissionList = PermissionHelper.getPermissions(mDescriptor.getPermissionCode());
            PermissionArrayAdapter adapter = new PermissionArrayAdapter(mContext, permissionList);
            ((ListView) v.findViewById(R.id.app_permission_list)).setAdapter(adapter);

            // Show persona list
            List<SPFPersona> personas = SPF.get().getProfileManager().getAvailablePersonas();
            ArrayAdapter<SPFPersona> personaAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, personas);
            Spinner personaSpinner = (Spinner) v.findViewById(R.id.app_persona);
            personaSpinner.setOnItemSelectedListener(this);
            personaSpinner.setAdapter(personaAdapter);

            return v;
        }

        public SPFPersona getSelectedPersona() {
            return mCurrentPersona;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mCurrentPersona = (SPFPersona) parent.getItemAtPosition(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            mCurrentPersona = null;
        }

    }
}