package com.andrgree.lifestat.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.andrgree.lifestat.R;

/**
 * Created by grag on 12/13/15.
 *
 * Адаптер для выбора оценки
 */
public class MarkAdapter extends RecyclerView.Adapter<MarkAdapter.ViewHolder> {
    private String[] mDataset;
    float density = 1;
    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public ImageView paramImage;
        public TextView paramName;

        public ViewHolder(View v) {
            super(v);
            paramImage = (ImageView) v.findViewById(R.id.paramImage);
            paramName = (TextView) v.findViewById(R.id.paramName);
        }

    }

    // Инициализация
    public MarkAdapter(String[] myDataset, float mDensity) {
        mDataset = myDataset;
        density = mDensity;
    }

    // Создание нового холдера
    @Override
    public MarkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_mark, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Перезаписыване текущего холдера
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.paramName.setText(mDataset[position]);

        /*int paddingPixel = 100;
        float density = 1;
        int paddingDp = (int)(paddingPixel * density);
        holder.paramName.setPadding(paddingDp,0,0,0);


        holder.paramName.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);*/

    }

    // Возвращение общего числа элементов
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}
