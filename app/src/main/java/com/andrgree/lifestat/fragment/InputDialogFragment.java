package com.andrgree.lifestat.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.andrgree.lifestat.R;
import com.andrgree.lifestat.adapter.MarkAdapter;
import com.andrgree.lifestat.adapter.UserParamAdapter;
import com.andrgree.lifestat.db.table.UserStat;
import com.andrgree.lifestat.listener.MarkListListener;
import com.andrgree.lifestat.listener.UserParamListListener;

import java.util.Arrays;

/**
 * Created by grag on 12/13/15.
 *
 * Ввод информации о дне
 */
public class InputDialogFragment extends AppCompatDialogFragment implements TextView.OnEditorActionListener {

    private Button buttonPrev;
    private Button buttonNext;
    private EditText descriptionET;
    private TextView paramNameTV;
    private boolean isNew = false;
    private int position = -1;
    private int count = -1;
    private RecyclerView marksRView;
    private RecyclerView.Adapter marksAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private int width;
    private int height;
    private String[] markValues;

    public interface InputDialogFragmentListener {
        void onFinishEditDialog(String inputText);
        void onMarkClick(UserStat userStat);
        UserStat onLoadUserStat(int position);
        int onLoadUncheckedPosition(int currentPosition);
    }

    /**
     *  инициализаци marksRView
     */
    public void initMarksRView(View view) {
        float scale = getContext().getResources().getDisplayMetrics().density;

        layoutManager = new LinearLayoutManager(getContext());
        marksRView = (RecyclerView) view.findViewById(R.id.markList);
        //String[] values = new String[] { "-5", "-4", "-3", "-2", "-1", "0", "+1", "+2", "+3", "+4", "+5"};
        markValues = new String[] { "+5", "+4", "+3", "+2", "+1", "0", "-1", "-2", "-3", "-4", "-5"};
        marksRView.setHasFixedSize(true);
        marksRView.setLayoutManager(layoutManager);
        marksAdapter = new MarkAdapter(markValues, scale);
        marksRView.setAdapter(marksAdapter);

        marksRView.addOnItemTouchListener(new MarkListListener(getContext(), new MarkListListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int markPosition) {
                InputDialogFragmentListener activity = (InputDialogFragmentListener) getActivity();
                UserStat userStat = activity.onLoadUserStat(position);
                userStat.setMark(Integer.parseInt(markValues[markPosition]));
                userStat.setDesc(descriptionET.getText().toString());
                if(isNew) {
                    userStat.setId(0);
                }
                activity.onMarkClick(userStat);
                //SystemClock.sleep(2000);
                int newPosition = activity.onLoadUncheckedPosition(position);
                if(newPosition != -1) {
                    position = newPosition;
                    changePosition();
                }
            }
        }));

        ViewGroup.LayoutParams params = marksRView.getLayoutParams();
        params.width = width - (int)(114 * scale);
        params.height = height - (int)(200 * scale);
        marksRView.setLayoutParams(params);
    }



    public InputDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNew = getArguments().getBoolean("new", false);
        position = getArguments().getInt("position", -1);
        count = getArguments().getInt("count", -1);
        width = getArguments().getInt("width", 400);
        height = getArguments().getInt("height", 400);

        width = width * 70 / 100;
        height = height * 80 / 100;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_dialog, container);
        getDialog().getWindow().setLayout(width, height);

        descriptionET = (EditText) view.findViewById(R.id.paramDescription);
        paramNameTV = (TextView) view.findViewById(R.id.paramName);
        getDialog().setTitle(getResources().getString(R.string.input_value));

        descriptionET.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        descriptionET.setOnEditorActionListener(this);

        initMarksRView(view);

        buttonPrev = (Button) view.findViewById(R.id.buttonPrev);
        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position > 0) {
                    position--;
                    changePosition();
                }
            }
        });

        buttonNext = (Button) view.findViewById(R.id.buttonNext);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position < (count - 1)) {
                    position++;
                    changePosition();
                }
            }
        });

        changePosition();

        return view;
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
        InputDialogFragmentListener activity = (InputDialogFragmentListener) getActivity();
            activity.onFinishEditDialog(descriptionET.getText().toString());
            this.dismiss();
            return true;
        //S}

        //return false;
    }

    //Смена рабочего параметра
    private void changePosition() {
        InputDialogFragmentListener activity = (InputDialogFragmentListener) getActivity();
        UserStat userStat = activity.onLoadUserStat(position);

        paramNameTV.setText(userStat.getTitle(getContext()));
        descriptionET.setText(userStat.getDesc());

        if(position == 0) {
            buttonPrev.setEnabled(false);
            buttonNext.setEnabled(true);
        } else if(position == (count - 1)) {
            buttonPrev.setEnabled(true);
            buttonNext.setEnabled(false);
        } else {
            buttonPrev.setEnabled(true);
            buttonNext.setEnabled(true);
        }

//        marksRView.setBackgroundColor(0xFF00FF00);
    }
}