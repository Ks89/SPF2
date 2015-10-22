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
package it.polimi.spf.app.fragments.groupinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.polimi.spf.app.R;

/**
 * Created by Stefano Cappa on 21/10/15.
 */
public class GroupInfoFragment extends Fragment implements
        ServicesRecyclerViewAdapter.ItemClickListener,
        ClientsRecyclerViewAdapter.ItemClickListener {
    private static final String TAG = GroupInfoFragment.class.getSimpleName();

    //this constants are also into WifiDirectMiddleware...why?
    //because, in theory this SPFApp should be moved in an external application and
    //for this reason some costants and informations must be replicated
    private static final String SERVICE_NAME = "service_name";
    private static final String SERVICE_ADDRESS = "service_address";
    private static final String SERVICE_IDENTIFIER = "service_identifier";
    private static final String SERVICES_ACTION = "it.polimi.spf.groupinfo.services";
    public static final String SERVICES_ADD = SERVICES_ACTION + "_add";
    public static final String SERVICES_REMOVE = SERVICES_ACTION + "_remove";
    public static final String SERVICES_REMOVE_ALL = SERVICES_ACTION + "_remove_all";

    private static final String CLIENT_IDENTIFIER = "client_identifier";
    private static final String CLIENTS_ACTION = "it.polimi.spf.groupinfo.clients";
    private static final String CLIENTS_ADD = CLIENTS_ACTION + "_add";
    private static final String CLIENTS_REMOVE = CLIENTS_ACTION + "_remove";
    private static final String CLIENTS_REMOVE_ALL = CLIENTS_ACTION + "_remove_all";

    @Bind(R.id.servicesRecyclerView)
    RecyclerView servicesRecyclerView;
    @Bind(R.id.clientsRecyclerView)
    RecyclerView clientsRecyclerView;

    private static ServicesRecyclerViewAdapter servicesAdapter;
    private static ClientsRecyclerViewAdapter clientsAdapter;

    public GroupInfoFragment() {
    }

    public static GroupInfoFragment newInstance() {
        GroupInfoFragment fragment = new GroupInfoFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.group_info_fragment, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        servicesRecyclerView.setHasFixedSize(true);
        servicesAdapter = new ServicesRecyclerViewAdapter(this, getContext());
        servicesRecyclerView.setAdapter(servicesAdapter);
        servicesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        clientsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        clientsRecyclerView.setHasFixedSize(true);
        clientsAdapter = new ClientsRecyclerViewAdapter(this, getContext());
        clientsRecyclerView.setAdapter(clientsAdapter);
        clientsRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void serviceItemClicked(DeviceGuiElement device) {
        //not implemented at the moment, but in the future this
        //listener can be used to open a dialog with additions informations
    }

    @Override
    public void clientItemClicked(DeviceGuiElement device) {
        //not implemented at the moment, but in the future this
        //listener can be used to open a dialog with additions informations
    }

    /**
     * Broadcast receiver that receives parcelable data from WiFiDirect Middleware.
     * This communication mechanism is remote. I mean that if you split SPFApp and the framework
     * in two different applications, this mechanism will work (because i used a Broadcast receiver declared into manifest
     * and not a Local broadcast receiver). I also decided to use two different broadcast receiver, but at the moment this
     * isn't really necessary, indeed this is the first one.
     */
    public static class ServicesBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "ServicesBroadcastReceiver onReceive called");
            if (intent.getAction().startsWith(SERVICES_ACTION)) {
                Log.d(TAG, "ServicesBroadcastReceiver onReceive called with SERVICES_ACTION");
                String name, address, identifier;
                name = intent.getStringExtra(SERVICE_NAME);
                address = intent.getStringExtra(SERVICE_ADDRESS);
                identifier = intent.getStringExtra(SERVICE_IDENTIFIER);
                switch (intent.getAction()) {
                    case SERVICES_ADD:
                        Log.d(TAG, "SERVICES_ADD: " + name + " , " + address + " , " + identifier);
                        ServicesGuiList.get().getServices().add(new DeviceGuiElement(name, address, identifier));
                        break;
                    case SERVICES_REMOVE:
                        Log.d(TAG, "SERVICES_REMOVE: " + name + " , " + address + " , " + identifier);
                        ServicesGuiList.get().getServices().remove(new DeviceGuiElement(name, address, identifier));
                        break;
                    case SERVICES_REMOVE_ALL:
                        ServicesGuiList.get().getServices().clear();
                        break;
                }
                if (servicesAdapter != null) {
                    servicesAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * Broadcast receiver that receives parcelable data from WiFiDirect Middleware.
     * This communication mechanism is remote. I mean that if you split SPFApp and the framework
     * in two different applications, this mechanism will work (because i used a Broadcast receiver declared into manifest
     * and not a Local broadcast receiver). I also decided to use two different broadcast receiver, but at the moment this
     * isn't really necessary, indeed this is the second one.
     */
    public static class ClientsBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "ClientsBroadcastReceiver onReceive called");
            if (intent.getAction().startsWith(CLIENTS_ACTION)) {
                Log.d(TAG, "ClientsBroadcastReceiver onReceive called with on the CLIENTS_ACTIONs");
                String identifier = intent.getStringExtra(CLIENT_IDENTIFIER);
                switch (intent.getAction()) {
                    case CLIENTS_ADD:
                        Log.d(TAG, "ClientsBroadcastReceiver - CLIENTS_ADD: " + identifier);
                        ClientsGuiList.get().addClientIfNotPresent(new DeviceGuiElement(null, null, identifier));
                        break;
                    case CLIENTS_REMOVE:
                        Log.d(TAG, "ClientsBroadcastReceiver - CLIENTS_REMOVE: " + identifier);
                        ClientsGuiList.get().getClients().remove(new DeviceGuiElement(null, null, identifier));
                        break;
                    case CLIENTS_REMOVE_ALL:
                        ClientsGuiList.get().getClients().clear();
                }
                if (clientsAdapter != null) {
                    clientsAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
