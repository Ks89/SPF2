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
package it.polimi.spf.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.polimi.spf.app.R;
import it.polimi.spf.app.fragments.profile.ProfileViewActivity;
import it.polimi.spf.framework.SPF;
import it.polimi.spf.framework.SPFContext;
import it.polimi.spf.framework.notification.NotificationMessage;

// TODO implement listener for event broadcaster to add real time notifications
public class NotificationFragment extends Fragment
        implements OnItemClickListener, SPFContext.OnEventListener, LoaderManager.LoaderCallbacks<List<NotificationMessage>> {

    private NotificationMessageAdapter mAdapter;

    public final static int MESSAGE_LOADER_ID = 0;
    public final static int MESSAGE_DELETER_ID = 1;

    private static final String EXTRA_MESSAGE_ID = "messageId";

    @Bind(R.id.notifications_list)
    ListView listview;

    @Bind(R.id.notifications_emptyview)
    TextView notifications_emptyview;

    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }

    public NotificationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_fragment_notifications, container, false);
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

        mAdapter = new NotificationMessageAdapter(getActivity());
        listview.setAdapter(mAdapter);
        listview.setEmptyView(notifications_emptyview);
        listview.setOnItemClickListener(this);

        getLoaderManager().initLoader(MESSAGE_LOADER_ID, null, this).forceLoad();
    }

    @Override
    public void onResume() {
        super.onResume();
        SPFContext.get().registerEventListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SPFContext.get().unregisterEventListener(this);
    }

    public void clickedOptionItemDeleteAll() {
        getLoaderManager().destroyLoader(MESSAGE_DELETER_ID);
        getLoaderManager().initLoader(MESSAGE_DELETER_ID, null, this);
    }

    @Override
    public void onEvent(int eventCode, Bundle payload) {
        if (eventCode == SPFContext.EVENT_NOTIFICATION_MESSAGE_RECEIVED) {
            getLoaderManager().initLoader(MESSAGE_LOADER_ID, null, this).forceLoad();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String sender = (String) view.getTag();
        startActivity(ProfileViewActivity.getIntent(getActivity(), sender));
    }

    private class NotificationMessageAdapter extends ArrayAdapter<NotificationMessage> implements OnClickListener {

        public NotificationMessageAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView != null ? convertView : LayoutInflater.from(getContext()).inflate(R.layout.notifications_list_element, parent, false);
            ViewHolder holder = ViewHolder.from(view);
            NotificationMessage message = getItem(position);

            holder.titleView.setText(message.getTitle());
            holder.messageView.setText(message.getMessage());
            view.setTag(message.getSenderId());
            holder.deleteButton.setTag(message.getId());
            holder.deleteButton.setOnClickListener(this);

            return view;
        }

        @Override
        public void onClick(View v) {
            Long id = (Long) v.getTag();
            Bundle args = new Bundle();
            args.putLong(EXTRA_MESSAGE_ID, id);
            getLoaderManager().destroyLoader(MESSAGE_DELETER_ID);
            getLoaderManager().initLoader(MESSAGE_DELETER_ID, args, NotificationFragment.this).forceLoad();
        }
    }

    private static class ViewHolder {
        public static ViewHolder from(View v) {
            Object o = v.getTag();
            if (o != null && (o instanceof ViewHolder)) {
                return (ViewHolder) o;
            }

            ViewHolder holder = new ViewHolder();
            v.setTag(holder);

            holder.titleView = (TextView) v.findViewById(R.id.notifications_title_view);
            holder.messageView = (TextView) v.findViewById(R.id.notifications_message_view);
            holder.deleteButton = (ImageButton) v.findViewById(R.id.notifications_delete_button);

            return holder;
        }

        public TextView titleView, messageView;
        public ImageButton deleteButton;
    }

    @Override
    public Loader<List<NotificationMessage>> onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case MESSAGE_LOADER_ID:
                return new AsyncTaskLoader<List<NotificationMessage>>(getActivity()) {

                    @Override
                    public List<NotificationMessage> loadInBackground() {
                        return SPF.get().getNotificationManager().getAvailableNotifications();
                    }
                };
            case MESSAGE_DELETER_ID:
                return new AsyncTaskLoader<List<NotificationMessage>>(getActivity()) {

                    @Override
                    public List<NotificationMessage> loadInBackground() {
                        if (args == null) {
                            SPF.get().getNotificationManager().deleteAllNotifications();
                        } else {
                            long msgId = args.getLong(EXTRA_MESSAGE_ID);
                            SPF.get().getNotificationManager().deleteNotification(msgId);
                        }
                        return SPF.get().getNotificationManager().getAvailableNotifications();
                    }
                };

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<NotificationMessage>> loader, List<NotificationMessage> data) {
        mAdapter.clear();
        mAdapter.addAll(data);

        Log.d("NotificationFargment" , "loader finishe with data size: " + data.size());
    }

    @Override
    public void onLoaderReset(Loader<List<NotificationMessage>> loader) {
        // Do nothing
    }
}
