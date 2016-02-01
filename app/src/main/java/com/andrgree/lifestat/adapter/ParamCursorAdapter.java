package com.andrgree.lifestat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrgree.lifestat.R;
import com.andrgree.lifestat.db.table.StatParam;
import com.andrgree.lifestat.listener.ParamButtonListener;

/**
 * Created by grag on 11/27/15.
 *
 * Адаптер для параметров на странице настроек
 */
public class ParamCursorAdapter extends RecyclerCursorAdapter<ParamCursorAdapter.ViewHolder>{

    ParamButtonListener paramButtonListener = null;

    public ParamCursorAdapter(Context context, Cursor cursor, ParamButtonListener listener){
        super(context,cursor);
        this.paramButtonListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, View.OnLongClickListener  {
        public StatParam statParam;
        public ParamButtonListener buttonListener;

        public ImageView paramImage;
        public TextView paramName;
        public TextView paramDesc;
        public CheckBox paramCheck;
        public Button buttonDel;

        public ViewHolder(View view, ParamButtonListener paramButtonListener) {
            super(view);
            paramImage = (ImageView) view.findViewById(R.id.paramImage);
            paramName = (TextView) view.findViewById(R.id.paramName);
            paramDesc = (TextView) view.findViewById(R.id.paramDesc);
            paramCheck = (CheckBox) view.findViewById(R.id.paramValue);


            buttonListener  = paramButtonListener;
            buttonDel = (Button) view.findViewById(R.id.buttonDel);
            buttonDel.setOnClickListener(this);
            paramCheck.setOnCheckedChangeListener(this);

            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view){
            int position = getAdapterPosition();
            //Toast.makeText(mContext, "Clicked button at position: " + getPosition(), Toast.LENGTH_SHORT).show();
            buttonListener.onDeleteClick(statParam);
            return;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = getAdapterPosition();
            statParam.setEnable(isChecked);
            //Toast.makeText(mContext, "Clicked button at position: " + getPosition(), Toast.LENGTH_SHORT).show();
            buttonListener.onUpdateClick(statParam);
            return;
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_param, parent, false);
        ViewHolder vh = new ViewHolder(itemView, paramButtonListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        StatParam statParam = StatParam.fromCursor(cursor);

        viewHolder.statParam = statParam;
        viewHolder.paramName.setText(statParam.getTitle(getContext()));
        viewHolder.paramDesc.setText(statParam.getDescription(getContext()));

        viewHolder.paramCheck.setOnCheckedChangeListener(null);
        viewHolder.paramCheck.setChecked(statParam.isEnable());
        viewHolder.paramCheck.setOnCheckedChangeListener(viewHolder);

        viewHolder.buttonDel.setVisibility(statParam.getStandart() ? View.INVISIBLE : View.VISIBLE);
    }

}
