package com.andrgree.lifestat.fragment;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.andrgree.lifestat.MainActivity;
import com.andrgree.lifestat.R;
import com.andrgree.lifestat.adapter.ParamCursorAdapter;
import com.andrgree.lifestat.db.DataBaseContentProvider;
import com.andrgree.lifestat.db.table.StatParam;
import com.andrgree.lifestat.listener.ParamButtonListener;
import com.andrgree.lifestat.listener.ParamEditListener;
import com.andrgree.lifestat.listener.ParamListListener;
import com.andrgree.lifestat.view.ContextMenuRecyclerView;

/**
 * Created by grag on 12/1/15.
 */
public class ParamSettingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, InputParamDialogFragment.InputParamDialogListener, ParamButtonListener {


    public static final int INPUT_PARAM_DIALOG_FRAGMENT = 1;
    private ContextMenuRecyclerView paramsRView;
    private RecyclerView.LayoutManager layoutManager;
    private ParamCursorAdapter paramsAdapter;
    private ParamEditListener paramEditListener = new ParamEditListener(this);

    private FloatingActionButton addStatParamButton;
//    public int editParamId = 0;

    /**
     *  инициализаци paramsRView
     */
    public void initParamsRView(View view) {
        paramsRView = (ContextMenuRecyclerView) view.findViewById(R.id.paramSettingList);

        paramsRView.setHasFixedSize(true);
        paramsRView.setLayoutManager(layoutManager);

        // Fields on the UI to which we map
        getLoaderManager().initLoader(0, null, this);
        paramsAdapter = new ParamCursorAdapter(getContext(),null, this);
        paramsRView.setAdapter(paramsAdapter);

        paramsRView.addOnItemTouchListener(new ParamListListener(getContext(), new ParamListListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Snackbar.make(paramsRView, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }));

        registerForContextMenu(paramsRView);
    }

    public static ParamSettingFragment init(int val) {
        ParamSettingFragment settingFragment = new ParamSettingFragment();
        Bundle args = new Bundle();
        args.putInt("tab", val);
        settingFragment.setArguments(args);

        return settingFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //final Fragment frag = this;
        View rootView = inflater.inflate(R.layout.fragment_param_setting, container, false);
        Bundle args = getArguments();
        layoutManager = new LinearLayoutManager(getContext());
        initParamsRView(rootView);
        //((TextView) rootView.findViewById(R.id.textView)).setText("Page " + args.getInt("page_position"));
        addStatParamButton = (FloatingActionButton) rootView.findViewById(R.id.addStatParam);
        addStatParamButton.setOnClickListener(paramEditListener);
/*
        addStatParamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                InputParamDialogFragment inputParamDialogFragment = new InputParamDialogFragment();
                inputParamDialogFragment.setTargetFragment(ParamSettingFragment.this, INPUT_PARAM_DIALOG_FRAGMENT);
                inputParamDialogFragment.show(fm, "inputParamDialogFragment" );
            }
        });
*/


        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { StatParam.COLUMN_ID, StatParam.COLUMN_NAME, StatParam.COLUMN_CODE, StatParam.COLUMN_DESC, StatParam.COLUMN_DESC_CODE, StatParam.COLUMN_STANDART, StatParam.COLUMN_ENABLE, StatParam.COLUMN_ACTIVE };
        CursorLoader cursorLoader = new CursorLoader(this.getContext(), DataBaseContentProvider.STAT_PARAM_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        paramsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        paramsAdapter.swapCursor(null);
    }

    /**
     * Подтверждение создания нового параметра
     */
    @Override
    public void onFinishInputParamDialog(int paramId, String name, String desc) {
        ContentValues values = new ContentValues();
        values.put(StatParam.COLUMN_NAME, name);
        values.put(StatParam.COLUMN_DESC, desc);

        if(paramId == 0) {
            values.put(StatParam.COLUMN_STANDART, false);
            getContext().getContentResolver().insert(DataBaseContentProvider.STAT_PARAM_URI, values);
        } else {
            getContext().getContentResolver().update(ContentUris.withAppendedId(DataBaseContentProvider.STAT_PARAM_URI, paramId), values, null, null);
        }
    }

    @Override
    public void onDeleteClick(StatParam statParam) {
        ContentValues values = new ContentValues();
        values.put(StatParam.COLUMN_ACTIVE, 0);

        //Сам объект не изменям, просто деактивируем по свойству
        getContext().getContentResolver().update( ContentUris.withAppendedId(DataBaseContentProvider.STAT_PARAM_URI, statParam.getId()) , values, null, null);

        Snackbar.make(paramsRView, "Delete button is clicked", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

    @Override
    public void onUpdateClick(StatParam statParam) {
        ContentValues values = new ContentValues();
        values.put(StatParam.COLUMN_ENABLE, statParam.isEnable());

        //Сам объект не изменям, просто деактивируем по свойству
        getContext().getContentResolver().update(ContentUris.withAppendedId(DataBaseContentProvider.STAT_PARAM_URI, statParam.getId()), values, null, null);

        Snackbar.make(paramsRView, "Update checkbox is clicked", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_context_param, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenuRecyclerView.RecyclerViewContextMenuInfo info = (ContextMenuRecyclerView.RecyclerViewContextMenuInfo) item.getMenuInfo();
        ParamCursorAdapter.ViewHolder vh = (ParamCursorAdapter.ViewHolder) paramsRView.findViewHolderForAdapterPosition(info.position);
        switch (item.getItemId()) {
            case R.id.action_menu_update:
                paramEditListener.onClick(vh.itemView, vh.statParam);
                return true;
            case R.id.action_menu_delete:
                onDeleteClick(vh.statParam);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
