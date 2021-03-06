package com.artifexiumgames.journal.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import com.artifexiumgames.journal.RichEditText.RichEditText;
import com.artifexiumgames.journal.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewEntryFragmentListner} interface
 * to handle interaction events.
 * Use the {@link NewEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewEntryFragment extends Fragment {
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
    private ToggleButton boldButton;
    private ToggleButton italicButton;
    private ToggleButton underlineButton;
    private ToggleButton strikeThroughButton;
    private Button subcriptButton;
    private Button superscriptButton;
    private ImageButton indentButton;
    private ImageButton unindentButton;
    private ImageButton textColorButton;
    private ImageButton backgroundButton;
    private RichEditText entryText;

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

        boldButton = (ToggleButton) v.findViewById(R.id.boldButton);
        italicButton = (ToggleButton) v.findViewById(R.id.italicButton);
        underlineButton = (ToggleButton) v.findViewById(R.id.underlineButton);
        strikeThroughButton = (ToggleButton) v.findViewById(R.id.strikeThroughButton);
        subcriptButton = (Button) v.findViewById(R.id.subscriptButton);
        superscriptButton = (Button) v.findViewById(R.id.superscriptButton);
        unindentButton = (ImageButton) v.findViewById(R.id.unindentButton);
        indentButton = (ImageButton) v.findViewById(R.id.indentButton);
        textColorButton = (ImageButton) v.findViewById(R.id.foregroundColorButton);
        backgroundButton = (ImageButton) v.findViewById(R.id.backgroundColorButton);

        //Set custom text styles for buttons
        underlineButton.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        strikeThroughButton.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        SpannableStringBuilder s = new SpannableStringBuilder("X2");
        s.setSpan(new SubscriptSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new RelativeSizeSpan(0.75f), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        subcriptButton.setText(s);
        s = new SpannableStringBuilder("X2");
        s.setSpan(new SuperscriptSpan(), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new RelativeSizeSpan(0.75f), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        superscriptButton.setText(s);

        entryText = (RichEditText) v.findViewById(R.id.newEntryTextView);
        entryText.setAllButtons(boldButton, italicButton, underlineButton, strikeThroughButton,
                                subcriptButton, superscriptButton,
                                unindentButton, indentButton,
                textColorButton, backgroundButton);

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
        inflater.inflate(R.menu.menu_new_entry, menu);
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
