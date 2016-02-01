package com.andrgree.lifestat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrgree.lifestat.R;
import com.andrgree.lifestat.db.table.StatParam;
import com.andrgree.lifestat.db.table.UserStat;
import com.andrgree.lifestat.listener.ParamButtonListener;

/**
 * Created by grag on 11/29/15.
 *
 * Адаптер для параметров на странице настроек
 */
public class UserParamCursorAdapter extends RecyclerCursorAdapter<UserParamCursorAdapter.ViewHolder>{

    UserStat currentUserStat = null;

    public UserParamCursorAdapter(Context context, Cursor cursor){
        super(context,cursor);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public UserStat userStat;

        public ImageView paramImage;
        public TextView paramName;
        public TextView paramValue;

        public ViewHolder(View view) {
            super(view);
            paramImage = (ImageView) view.findViewById(R.id.paramImage);
            paramName = (TextView) view.findViewById(R.id.paramName);
            paramValue = (TextView) view.findViewById(R.id.paramValue);

            //paramName.setOnCreateContextMenuListener(this);
            paramName.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            //view.showContextMenu();
            itemView.showContextMenu();
            return true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_param, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        UserStat userStat = UserStat.fromCursor(cursor);
        currentUserStat = userStat;

        viewHolder.userStat = userStat;
        viewHolder.paramName.setText(userStat.getTitle(getContext()));
        viewHolder.paramValue.setText(new Integer(userStat.getMark()).toString());
    }

}
