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
package it.polimi.spf.app.fragments.personas;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.mikepenz.iconics.view.IconicsCompatButton;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.polimi.spf.app.LoadersConfig;
import it.polimi.spf.app.R;
import it.polimi.spf.framework.SPF;
import it.polimi.spf.framework.profile.SPFPersona;
import it.polimi.spf.framework.profile.SPFProfileManager;

public class PersonasFragment extends Fragment implements
        PersonasArrayAdapter.OnPersonaDeletedListener,
        OnClickListener,
        LoaderManager.LoaderCallbacks<List<SPFPersona>> {

    private PersonasArrayAdapter mAdapter;

    @Bind(R.id.personas_new_add)
    IconicsCompatButton addButton;
    @Bind(R.id.personas_new_name)
    EditText mNewPersonaName;
    @Bind(R.id.personas_container)
    ListView list;

    public static PersonasFragment newInstance() {
        return new PersonasFragment();
    }

    public PersonasFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_fragment_personas, container, false);
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

        mAdapter = new PersonasArrayAdapter(getActivity(), this);
        list.setAdapter(mAdapter);
        list.setOnItemClickListener(itemClickListener);

        addButton.setOnClickListener(this);
        getLoaderManager().destroyLoader(LoadersConfig.LOAD_PERSONAS_LOADER);
        getLoaderManager().initLoader(LoadersConfig.LOAD_PERSONAS_LOADER, null, this).forceLoad();
    }

    private OnItemClickListener itemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            SPFPersona p = mAdapter.getItem(position);
            PersonasCirclesActivity.start(getActivity(), p);
        }
    };

    @Override
    public void onPersonaDeleted(SPFPersona persona) {
        Bundle args = new Bundle();
        args.putParcelable(LoadersConfig.EXTRA_PERSONA, persona);
        getLoaderManager().destroyLoader(LoadersConfig.DELETE_PERSONA_LOADER);
        getLoaderManager().initLoader(LoadersConfig.DELETE_PERSONA_LOADER, args, this).forceLoad();
    }

    @Override
    public void onClick(View v) {
        String name = mNewPersonaName.getText().toString();
        if (name.length() == 0) {
            Toast.makeText(getActivity(), "Persona name cannot be empty", Toast.LENGTH_LONG).show();
            return;
        }

        SPFPersona persona = new SPFPersona(name);
        mNewPersonaName.setText("");

        Bundle args = new Bundle();
        args.putParcelable(LoadersConfig.EXTRA_PERSONA, persona);
        getLoaderManager().destroyLoader(LoadersConfig.CREATE_PERSONA_LOADER);
        getLoaderManager().initLoader(LoadersConfig.CREATE_PERSONA_LOADER, args, this).forceLoad();
    }

    @Override
    public Loader<List<SPFPersona>> onCreateLoader(final int id, final Bundle args) {
        final SPFProfileManager profile = SPF.get().getProfileManager();

        switch (id) {
            case LoadersConfig.CREATE_PERSONA_LOADER:
                return new AsyncTaskLoader<List<SPFPersona>>(getActivity()) {

                    @Override
                    public List<SPFPersona> loadInBackground() {
                        SPFPersona persona = args.getParcelable(LoadersConfig.EXTRA_PERSONA);
                        profile.addPersona(persona);
                        return profile.getAvailablePersonas();
                    }
                };

            case LoadersConfig.DELETE_PERSONA_LOADER:
                return new AsyncTaskLoader<List<SPFPersona>>(getActivity()) {

                    @Override
                    public List<SPFPersona> loadInBackground() {
                        SPFPersona persona = args.getParcelable(LoadersConfig.EXTRA_PERSONA);
                        profile.removePersona(persona);
                        return profile.getAvailablePersonas();
                    }
                };
            case LoadersConfig.LOAD_PERSONAS_LOADER:
                return new AsyncTaskLoader<List<SPFPersona>>(getActivity()) {

                    @Override
                    public List<SPFPersona> loadInBackground() {
                        return profile.getAvailablePersonas();
                    }
                };
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<SPFPersona>> loader, List<SPFPersona> data) {
        mAdapter.clear();
        mAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<SPFPersona>> loader) {
        // Do nothing
    }

}
