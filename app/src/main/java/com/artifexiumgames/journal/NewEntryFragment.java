package com.artifexiumgames.journal;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.ParcelableSpan;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewEntryFragmentListner} interface
 * to handle interaction events.
 * Use the {@link NewEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewEntryFragment extends Fragment implements View.OnClickListener, TextWatcher, RichEditText.OnSelectionChangeListener{
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
        tabButton = (Button) v.findViewById(R.id.tabButton);
        timeButton = (Button) v.findViewById(R.id.timeButton);
        dateButton = (Button) v.findViewById(R.id.dateButton);
        upButton = (Button) v.findViewById(R.id.scrollUpButton);
        downButton = (Button) v.findViewById(R.id.scrollDownButton);

        boldButton = (ToggleButton) v.findViewById(R.id.boldToggleButton);
        italicButton = (ToggleButton) v.findViewById(R.id.italicToggleButton);
        underlineButton = (ToggleButton) v.findViewById(R.id.underlineToggleButton);
        underlineButton.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        entryText = (RichEditText) v.findViewById(R.id.newEntryTextView);

        tabButton.setOnClickListener(this);
        timeButton.setOnClickListener(this);
        dateButton.setOnClickListener(this);
        upButton.setOnClickListener(this);
        downButton.setOnClickListener(this);

        boldButton.setOnClickListener(this);
        italicButton.setOnClickListener(this);
        underlineButton.setOnClickListener(this);

        entryText.addTextChangedListener(this);
        entryText.addOnSelectionChangeListener(this);


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

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            //QUICK TOOL BUTTONS
            case R.id.tabButton:
                tabButtonAction();
                break;
            case R.id.timeButton:
                timeButtonAction();
                break;
            case R.id.dateButton:
                dateButtonAction();
                break;
            case R.id.scrollUpButton:
                upButtonAction();
                break;
            case R.id.scrollDownButton:
                downButtonAction();
                break;

            //STYLE BUTTONS
            case R.id.boldToggleButton:
            case R.id.italicToggleButton:
            case R.id.underlineToggleButton:
                updateTextStyle();
                break;
        }
    }

    private void tabButtonAction() {
        int selectionStart = entryText.getSelectionStart();
        int selectionEnd = entryText.getSelectionEnd();
        String newText = entryText.getText().insert(entryText.getSelectionStart(), getString(R.string.tabCharacter)).toString();
        entryText.setText(newText);
        if (selectionStart != selectionEnd) {
            entryText.setSelection(selectionStart + 4, selectionEnd);
        }
        else{
            entryText.setSelection(selectionStart);
        }
    }

    private void timeButtonAction(){
        Calendar c = Calendar.getInstance();
        String date = "<"+c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND)+">";
        String newText = entryText.getText().insert(entryText.getSelectionStart(), date).toString();
        entryText.setText(newText);
        entryText.setSelection(entryText.getSelectionStart()+date.length());
    }

    private void dateButtonAction(){
        Calendar c = Calendar.getInstance();
        String time = "<"+c.get(Calendar.MONTH)+"-"+c.get(Calendar.DAY_OF_MONTH)+"-"+c.get(Calendar.YEAR)+">";
        String newText = entryText.getText().insert(entryText.getSelectionStart(), time).toString();
        entryText.setText(newText);
        entryText.setSelection(entryText.getSelectionStart()+time.length());
    }

    private void upButtonAction(){
        entryText.setSelection(0);
    }

    private void downButtonAction(){
        entryText.setSelection(entryText.getText().length());
    }

    private void updateTextStyle() {
        int start = entryText.getSelectionStart();
        int end = entryText.getSelectionEnd();
        if (boldButton.isChecked()){
            if (start != end){
                entryText.getText().setSpan(new StyleSpan(Typeface.BOLD), start, end, 0);
            }
        }
        else if (!boldButton.isChecked()){
            removeSpansWithinSelection(Typeface.BOLD);
        }

        if (italicButton.isChecked()){
            if (start != end){
                entryText.getText().setSpan(new StyleSpan(Typeface.ITALIC), start, end, 0);
            }
        }
        else if (!italicButton.isChecked()){
            removeSpansWithinSelection(Typeface.ITALIC);
        }

        if (underlineButton.isChecked()){
            if (start != end){
                entryText.getText().setSpan(new UnderlineSpan(), start, end, 0);
            }
        }
        else if (!underlineButton.isChecked()){
            removeSpansWithinSelection(UnderlineSpan.class);
        }
    }

    private void removeSpansWithinSelection(int spanId) {
        int start = entryText.getSelectionStart();
        int end = entryText.getSelectionEnd();
        for (StyleSpan span: entryText.getText().getSpans(start, end, StyleSpan.class)) {
            if (span.getStyle() == spanId) {
                entryText.getText().removeSpan(span);
            }
        }
    }

    private void removeSpansWithinSelection(Class c) {
        int start = entryText.getSelectionStart();
        int end = entryText.getSelectionEnd();
        for (ParcelableSpan span: entryText.getText().getSpans(start, end, ParcelableSpan.class)) {
            if (span.getClass().equals(c)) {
                entryText.getText().removeSpan(span);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (boldButton.isChecked()) {
            entryText.getText().setSpan(new StyleSpan(Typeface.BOLD), start, start + count, 0);
        }
        if (italicButton.isChecked()){
            entryText.getText().setSpan(new StyleSpan(Typeface.ITALIC), start, start + count, 0);
        }
        if (underlineButton.isChecked()){
            entryText.getText().setSpan(new UnderlineSpan(), start, start + count, 0);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onSelectionChange(int start, int end) {
        boolean isBold = false;
        boolean isItalic = false;
        boolean isUnderlined = false;
        for (ParcelableSpan span: entryText.getText().getSpans(start, end, ParcelableSpan.class)){
            if (span instanceof StyleSpan){
                if (((StyleSpan) span).getStyle() == Typeface.BOLD){
                    isBold = true;
                }
                else if (((StyleSpan) span).getStyle() == Typeface.ITALIC) {
                    isItalic = true;
                }
            }

            if (span instanceof UnderlineSpan){
                isUnderlined = true;
            }
        }
        if (isBold){
            boldButton.setChecked(true);
        }
        else{
            boldButton.setChecked(false);
        }

        if (isItalic){
            italicButton.setChecked(true);
        }
        else{
            italicButton.setChecked(false);
        }

        if (isUnderlined){
            underlineButton.setChecked(true);
        }
        else{
            underlineButton.setChecked(false);
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
