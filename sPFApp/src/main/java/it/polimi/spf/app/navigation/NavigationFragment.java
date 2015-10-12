///*
// * Copyright 2014 Jacopo Aliprandi, Dario Archetti
// *
// * This file is part of SPF.
// *
// * SPF is free software: you can redistribute it and/or modify it under the
// * terms of the GNU Lesser General Public License as published by the Free
// * Software Foundation, either version 3 of the License, or (at your option)
// * any later version.
// *
// * SPF is distributed in the hope that it will be useful, but WITHOUT ANY
// * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for
// * more details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with SPF.  If not, see <http://www.gnu.org/licenses/>.
// *
// */
//package it.polimi.spf.app.navigation;
//
//import android.app.Activity;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.Switch;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import butterknife.OnCheckedChanged;
//import it.polimi.spf.app.R;
//import it.polimi.spf.app.SPFApp;
//import it.polimi.spf.app.navigation.Navigation.Entry;
//import it.polimi.spf.framework.SPF;
//import it.polimi.spf.framework.SPFContext;
//import it.polimi.spf.framework.local.SPFService;
//import it.polimi.spf.wfd.events.NineBus;
//
///**
// * Base navigation fragment without drawer functionalities to be used in two
// * panes version of main activity
// */
//public class NavigationFragment extends Fragment {
//
//    /**
//     * Interface for components (main activity) called when user an item is
//     * selected
//     */
//    public interface ItemSelectedListener {
//        void onItemSelect(int position, boolean replace);
//    }
//
//    /**
//     * Remember the position of the selected item.
//     */
//    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
//    private static String ACTION_STOP_FOREGROUND_SWITCH = "it.polimi.spf.framework.SPFService.UPDATE_SWITCH";
//    private static String CALL_NAVIGATION_FRAGMENT_BROADCAST_INTENT = "it.polimi.spf.SPFService.spfservice-navigationfragment";
//    private static final int UPDATESWITCH = 1;
//
//    private static final String TAG = "NotificationFragment";
//
//    @Bind(R.id.navigation_entries)
//    ListView mNavigationListView;
//
//    @Bind(R.id.groupOwner_switch)
//    Switch groupOwnerSwitch;
//
//    @Bind(R.id.group_autonomous_switch)
//    Switch group_autonomous_switch;
//
//    @Bind(R.id.connect_switch)
//    Switch connectSwitch;
//
//    private int mCurrentSelectedPosition = 0;
//    private ItemSelectedListener mCallback;
//    private Navigation mNavigation;
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        mCallback = (ItemSelectedListener) activity;
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mCallback = null;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        if (savedInstanceState != null) {
//            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
//        }
//    }
//
//    @Override
//    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View root = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
//
//        ButterKnife.bind(this, root);
//
//        // Set up navigation entries
//        mNavigationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                selectItem(position, true);
//            }
//        });
//
//        String[] pageTitles = getResources().getStringArray(R.array.content_fragments_titles);
//        mNavigationListView.setAdapter(new NavigationArrayAdapter(getActivity(), pageTitles));
//        mNavigationListView.setItemChecked(mCurrentSelectedPosition, true);
//
//        //update switches
//        groupOwnerSwitch.setChecked(false);
//        group_autonomous_switch.setChecked(true);
//        group_autonomous_switch.setVisibility(View.INVISIBLE);
//        connectSwitch.setChecked(SPF.get().isConnected());
//
//        return root;
//    }
//
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        selectItem(mCurrentSelectedPosition, savedInstanceState == null);
//        mNavigation = new Navigation(getActivity());
//        SPFContext.get().registerEventListener(mNavigation);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver((mMessageReceiver),
//                new IntentFilter(ACTION_STOP_FOREGROUND_SWITCH));
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        SPFContext.get().unregisterEventListener(mNavigation);
//        LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(mMessageReceiver);
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//
//        ButterKnife.unbind(this);
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
//    }
//
//    @Override
//    public void setHasOptionsMenu(boolean hasMenu) {
//        super.setHasOptionsMenu(false);
//    }
//
//    protected void selectItem(int position, boolean replace) {
//        mCurrentSelectedPosition = position;
//
//        if (mNavigationListView == null || mCallback == null) {
//            return;
//        }
//
//        mCallback.onItemSelect(position, replace);
//    }
//
//    private class NavigationArrayAdapter extends ArrayAdapter<String> {
//
//        public NavigationArrayAdapter(Context context, String[] pageTitles) {
//            super(context, android.R.layout.simple_list_item_1, pageTitles);
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            return mNavigation.createEntryView(Entry.values()[position]);
//        }
//    }
//
//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.d(TAG, "Received message from SPFService, to update the switch");
//
//            if (intent != null && intent.getIntExtra(CALL_NAVIGATION_FRAGMENT_BROADCAST_INTENT, 0) == UPDATESWITCH) {
//                //force disable switch
//                connectSwitch.setChecked(false);
//            }
//        }
//    };
//
//    @OnCheckedChanged(R.id.groupOwner_switch)
//    public void onCheckChangedGOSwitch(boolean isChecked) {
//        if (isChecked) {
//            Log.d(TAG, "connectSwitch checked -> gointent=15");
//            group_autonomous_switch.setVisibility(View.VISIBLE);
//            ((SPFApp) getActivity().getApplication()).updateIdentifier(15);
//        } else {
//            Log.d(TAG, "connectSwitch unchecked -> gointent=0");
//            group_autonomous_switch.setVisibility(View.INVISIBLE);
//            ((SPFApp) getActivity().getApplication()).updateIdentifier(0);
//        }
//    }
//
//    @OnCheckedChanged(R.id.group_autonomous_switch)
//    public void onCheckChangedAutonomousSwitch(boolean isChecked) {
//        Log.d(TAG, "group_autonomous_switch status = " + isChecked);
//    }
//
//    @OnCheckedChanged(R.id.connect_switch)
//    public void onCheckChangedConnectSwitch(boolean isChecked) {
//        if (isChecked) {
//            if (groupOwnerSwitch.isChecked()) {
//                Log.d(TAG, "connectSwitch checked -> gointent=15");
//                ((SPFApp) getActivity().getApplication()).initSPF(15, group_autonomous_switch.isChecked());
//            } else {
//                ((SPFApp) getActivity().getApplication()).initSPF(0, false);
//
//            }
//            SPFService.startForeground(getActivity());
//
//            group_autonomous_switch.setEnabled(false);
//            groupOwnerSwitch.setEnabled(false);
//        } else {
//            SPFService.stopForeground(getActivity());
//
//            group_autonomous_switch.setEnabled(true);
//            groupOwnerSwitch.setEnabled(true);
//        }
//    }
//}