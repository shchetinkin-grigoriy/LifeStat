package com.andrgree.lifestat.listener;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.StateSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.andrgree.lifestat.db.table.StatParam;
import com.andrgree.lifestat.fragment.InputParamDialogFragment;
import com.andrgree.lifestat.fragment.ParamSettingFragment;


/**
 * Created by grag on 11/28/15.
 */
public class ParamEditListener implements View.OnClickListener {

    private Fragment fragment;

    public ParamEditListener(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onClick(View view) {
        onClickImplementation(null);
    }

    private void onClickImplementation(StatParam statParam) {
        InputParamDialogFragment inputParamDialogFragment = new InputParamDialogFragment();

        Bundle bundle = new Bundle();
        if(statParam != null) {
            bundle.putInt("id", statParam.getId());
            bundle.putString("name", statParam.getName());
            bundle.putString("desc", statParam.getDesc());
        } else {
            bundle.putInt("id", 0);

        }
        inputParamDialogFragment.setArguments(bundle);
        inputParamDialogFragment.setTargetFragment(fragment, ParamSettingFragment.INPUT_PARAM_DIALOG_FRAGMENT);
        inputParamDialogFragment.show(fragment.getFragmentManager(), "inputParamDialogFragment" );
    }

    public void onClick(View view, StatParam statParam) {
        onClickImplementation(statParam);
    }
}