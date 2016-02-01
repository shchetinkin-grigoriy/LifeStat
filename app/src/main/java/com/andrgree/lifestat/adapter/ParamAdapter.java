package com.andrgree.lifestat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrgree.lifestat.R;

/**
 * Created by grag on 11/27/15.
 *
 * Адаптер для параметров на главной странице
 */
@Deprecated
public class ParamAdapter extends RecyclerView.Adapter<ParamAdapter.ViewHolder> {
    private String[] mDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public ImageView paramImage;
        public TextView paramName;
        public CheckBox paramValue;

        public ViewHolder(View v) {
            super(v);
            paramImage = (ImageView) v.findViewById(R.id.paramImage);
            paramName = (TextView) v.findViewById(R.id.paramName);
            paramValue = (CheckBox) v.findViewById(R.id.paramValue);
        }

    }

    // Инициализация
    public ParamAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Создание нового холдера
    @Override
    public ParamAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_param, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Перезаписыване текущего холдера
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.paramName.setText(mDataset[position]);

    }

    // Возвращение общего числа элементов
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}
