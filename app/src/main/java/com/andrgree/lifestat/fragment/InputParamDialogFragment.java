package com.andrgree.lifestat.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.andrgree.lifestat.R;
import com.andrgree.lifestat.adapter.MarkAdapter;
import com.andrgree.lifestat.listener.MarkListListener;

/**
 * Created by grag on 12/13/15.
 *
 * Ввод информации о новом отслеживаемом параметре
 */
public class InputParamDialogFragment extends AppCompatDialogFragment implements TextView.OnEditorActionListener {

    private TextView paramName;
    private EditText paramValue;
    private TextView paramDescName;
    private EditText paramDescValue;
    private Button buttonOk;
    private Button buttonCancel;
    private int paramId;

    public interface InputParamDialogListener {
        void onFinishInputParamDialog(int id, String name, String desc);
    }

    public InputParamDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paramId = getArguments().getInt("id", 0);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_param_dialog, container);
        paramName = (TextView) view.findViewById(R.id.paramName);
        paramValue = (EditText) view.findViewById(R.id.paramValue);
        paramDescName = (TextView) view.findViewById(R.id.paramDescName);
        paramDescValue = (EditText) view.findViewById(R.id.paramDescValue);

        if(paramId != 0) {
            paramValue.setText(getArguments().getString("name", ""));
            paramDescValue.setText(getArguments().getString("desc", ""));
        }
        getDialog().setTitle(getResources().getString(R.string.input_value));

        buttonOk = (Button) view.findViewById(R.id.buttonOk);
        buttonCancel = (Button) view.findViewById(R.id.buttonCancel);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(paramValue.getText() == null || paramValue.getText().toString().trim().equals("")) {
                    Snackbar.make(v, getResources().getText(R.string.name_is_required), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }
                InputParamDialogListener parentFragment = (InputParamDialogListener) getTargetFragment();
                parentFragment.onFinishInputParamDialog(paramId, paramValue.getText().toString(), paramDescValue.getText().toString());
                getDialog().dismiss();

            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }


    @Deprecated
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        //if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text to activity
        InputParamDialogListener parentFragment = (InputParamDialogListener) getParentFragment();
        parentFragment.onFinishInputParamDialog(paramId, paramValue.getText().toString(), paramDescValue.getText().toString());
        this.dismiss();
        return true;
        //S}

        //return false;
    }
}