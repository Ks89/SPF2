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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.polimi.spf.app.R;

public class ClientsRecyclerViewAdapter extends RecyclerView.Adapter<ClientsRecyclerViewAdapter.ViewHolder> {
    private ItemClickListener itemClickListener;
    private final Context context;

    public ClientsRecyclerViewAdapter(@NonNull ItemClickListener itemClickListener, @NonNull Context context) {
        this.itemClickListener = itemClickListener;
        this.context = context;
        setHasStableIds(true);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View parent;

        @Bind(R.id.logoImageView)
        IconicsImageView logo;
        @Bind(R.id.identifierTextView)
        TextView identifierTextView;

        public ViewHolder(View view) {
            super(view);
            this.parent = view;
            ButterKnife.bind(this, view);
        }

        public void setOnClickListener(OnClickListener listener) {
            parent.setOnClickListener(listener);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.device_card_view, viewGroup, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final DeviceGuiElement device = ClientsGuiList.get().getClients().get(position);

        viewHolder.identifierTextView.setText(device.getIdentifier());
        viewHolder.logo.setImageDrawable(new IconicsDrawable(context)
                .icon(FontAwesome.Icon.faw_android)
                .color(context.getResources().getColor(R.color.red))
                .sizeDp(30));

        viewHolder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.clientItemClicked(device);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ClientsGuiList.get().getClients().size();
    }

    public interface ItemClickListener {
        void clientItemClicked(final DeviceGuiElement device);
    }
}