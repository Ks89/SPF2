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
package it.polimi.spf.app.fragments.appmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.polimi.spf.app.LoadersConfig;
import it.polimi.spf.app.R;
import it.polimi.spf.framework.SPF;
import it.polimi.spf.framework.security.AppAuth;

public class AppManagerFragment extends Fragment implements
        ListView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<List<AppAuth>> {

    private AppManagerListAdapter mAdapter;

    @Bind(R.id.app_manager_list)
    ListView mAppList;

    public static AppManagerFragment newInstance() {
        return new AppManagerFragment();
    }

    public AppManagerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_fragment_appmanager, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new AppManagerListAdapter(getActivity());
        mAppList.setAdapter(mAdapter);
        mAppList.setEmptyView(getView().findViewById(R.id.app_manager_list_emptyview));
        mAppList.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().initLoader(LoadersConfig.APP_LOADER, null, this).forceLoad();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppAuth auth = mAdapter.getItem(position);
        Intent i = new Intent(getActivity(), AppDetailActivity.class);
        i.putExtra(AppDetailActivity.APP_AUTH_KEY, auth);
        startActivity(i);
    }

    @Override
    public Loader<List<AppAuth>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LoadersConfig.APP_LOADER:
                return new AsyncTaskLoader<List<AppAuth>>(getActivity()) {

                    @Override
                    public List<AppAuth> loadInBackground() {
                        return SPF.get().getSecurityMonitor().getAvailableApplications();
                    }
                };

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<AppAuth>> loader, List<AppAuth> items) {
        mAdapter.clear();
        mAdapter.addAll(items);
    }

    @Override
    public void onLoaderReset(Loader<List<AppAuth>> loader) {
    }

}