/*
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
package it.polimi.spf.app.permissiondisclaimer;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.polimi.spf.app.R;

/**
 * Created by Stefano Cappa on 09/10/15.
 */
public class PermissionDisclaimerDialogFragment extends DialogFragment {

    @Bind(R.id.permission_confirm_button)
    Button PermissionConfirmButton;

    @Bind(R.id.permission_deny_button)
    Button PermissionDenyButton;

    public interface PermissionDisclaimerListener {
        void onClickConfirmButton();

        void onClickDenyButton();
    }

    /**
     * Method to obtain a new Fragment's instance.
     *
     * @return This Fragment instance.
     */
    public static PermissionDisclaimerDialogFragment newInstance() {
        return new PermissionDisclaimerDialogFragment();
    }

    /**
     * Default Fragment constructor.
     */
    public PermissionDisclaimerDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_permission_disclaimer, container, false);

        //ButterKnife bind version for fragments
        ButterKnife.bind(this, v);

        //call this on this fragment, not on the dialog
        setCancelable(false);

        return v;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setOnDismissListener(null);
        }
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @OnClick(R.id.permission_confirm_button)
    public void onClickConfirmButton(View v) {
        ((PermissionDisclaimerListener) getActivity()).onClickConfirmButton();
        dismiss();
    }

    @OnClick(R.id.permission_deny_button)
    public void onClickDenyButton(View v) {
        ((PermissionDisclaimerListener) getActivity()).onClickDenyButton();
        dismiss();
    }
}
