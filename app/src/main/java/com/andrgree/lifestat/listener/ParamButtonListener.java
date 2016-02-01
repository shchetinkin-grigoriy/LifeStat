package com.andrgree.lifestat.listener;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.andrgree.lifestat.db.table.StatParam;


/**
 * Created by grag on 11/28/15.
 */
public interface ParamButtonListener {
    void onDeleteClick(StatParam view);

    void onUpdateClick(StatParam view);
}
