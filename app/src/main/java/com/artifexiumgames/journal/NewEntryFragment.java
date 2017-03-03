package com.artifexiumgames.journal;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AlertDialogLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewEntryFragmentListner} interface
 * to handle interaction events.
 * Use the {@link NewEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewEntryFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button tabButton;
    private Button timeButton;
    private Button dateButton;
    private Button upButton;
    private Button downButton;
    private EditText entryText;
    private NewEntryFragmentListner mListener;

    public NewEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewEntryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewEntryFragment newInstance(String param1, String param2) {
        NewEntryFragment fragment = new NewEntryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_entry, container, false);

        tabButton = (Button) v.findViewById(R.id.tabButton);
        timeButton = (Button) v.findViewById(R.id.timeButton);
        dateButton = (Button) v.findViewById(R.id.dateButton);
        upButton = (Button) v.findViewById(R.id.scrollUpButton);
        downButton = (Button) v.findViewById(R.id.scrollDownButton);
        entryText = (EditText) v.findViewById(R.id.newEntryTextView);
        tabButton.setOnClickListener(this);
        timeButton.setOnClickListener(this);
        dateButton.setOnClickListener(this);
        upButton.setOnClickListener(this);
        downButton.setOnClickListener(this);
        return v;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewEntryFragmentListner) {
            mListener = (NewEntryFragmentListner) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NewEntryFragmentListner");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        menu.clear();
        inflater.inflate(R.menu.new_entry, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_save_entry:
                //TODO: save encrypted journal log to memory
                return true;

            case R.id.action_clear_entry:
                new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Clear Entry?")
                        .setMessage("Are you sure you want to clear your entry?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                entryText.setText("");
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(View view) {
        if (mListener != null) {
            mListener.onFragmentClick(view);
        }
    }

    @Override
    public void onClick(View v) {
        int position = entryText.getSelectionStart();
        Calendar c = Calendar.getInstance();
        String newText;
        switch (v.getId()){
            case R.id.tabButton:
                newText = String.valueOf(entryText.getText().insert(position, getString(R.string.tabCharacter)));
                entryText.setText(newText);
                entryText.setSelection(position+4);
                break;
            case R.id.timeButton:
                String date = "<"+c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND)+">";
                newText = String.valueOf(entryText.getText().insert(position, date));
                entryText.setText(newText);
                entryText.setSelection(position+date.length());
                break;
            case R.id.dateButton:
                String time = "<"+c.get(Calendar.MONTH)+"-"+c.get(Calendar.DAY_OF_MONTH)+"-"+c.get(Calendar.YEAR)+">";
                newText = String.valueOf(entryText.getText().insert(position, time));
                entryText.setText(newText);
                entryText.setSelection(position+time.length());
                break;
            case R.id.scrollUpButton:
                entryText.setSelection(0);
                break;
            case R.id.scrollDownButton:
                entryText.setSelection(entryText.getText().length());
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface NewEntryFragmentListner {
        // TODO: Update argument type and name
        void onFragmentClick(View view);
    }
}
