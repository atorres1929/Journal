package com.artifexiumgames.journal.RichEditText;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.ParcelableSpan;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.artifexiumgames.journal.R;

import java.util.ArrayList;
import java.util.Calendar;

import static android.R.id.list;

//TODO left, right, center align buttons
//TODO foreground, background color buttons
//TODO search text function
//TODO font family
//TODO font size

/**
 * This is a simple Rich Text Editor that provides added functionality to {@link EditText}.
 * <p>
 *  This Editor uses {@link Button} and {@link ToggleButton} to
 *  activate the different controls associated with rich text.
 * </p>
 * <p>
 *  Implementation Features Include:
 *  <ul>
 *      <li>Automatic button functionality: reference buttons to the editor with setAllButtons()</li>
 *      <li>Selection changes can now be listened for with {@link OnSelectionChangeListener}</li>
 *  </ul>
 * </p>
 * <p>
 *  Text Features Include:
 *  <ul>
 *      <li><b>Bold</b></li>
 *      <li><i>Italic</i></li>
 *      <li><u>Underline</u></li>
 *      <li><strike>Strikethrough</strike></li>
 *      <li>Sub<sub>script</sub></li>
 *      <li>Super<sup>script</sup></li>
 *  </ul>
 * </p>
 * <p>
 *  <b>WARNING:</b>
 *  <br>
 *  Before implementing your own {@link View.OnClickListener} for your buttons referenced by this class
 *  or {@link TextWatcher} to your {@link Activity} to monitor/change text of this editor, <br>
 *  <b>REMEMBER</b> you <b>MUST</b> call {@link RichEditText#onClick(View)} and/or the relevant TextWatcher methods.
 * </p>
 *
 * @see EditText
 * @see OnSelectionChangeListener
 * @see Button
 * @see ToggleButton
 * @see View.OnClickListener
 * @see TextWatcher
 * @author Adam Torres (Artifexium)
 */
public class RichEditText extends AppCompatEditText implements TextWatcher, View.OnClickListener {

    protected final String TAG = "RichEditText";
    protected final String spaceCharacter = " ";
    protected OnSelectionChangeListener onSelectionChangeListener;
    protected ToggleButton boldButton;
    protected ToggleButton italicButton;
    protected ToggleButton underlineButton;
    protected ToggleButton strikeThroughButton;
    protected View subscriptButton;
    protected View superscriptButton;
    protected View indentButton;
    protected View unindentButton;
    protected View textColorButton;
    protected ColorDrawable currentTextColor;
    protected View backgroundButton;
    protected ColorDrawable currentBackgroundColor;
    protected String[] colorList;

    //Settings
    protected float relativeSize;
    protected int numTabs;

    /**
     * Default Constructor needed to extend {@link EditText}
     */
    public RichEditText(Context context) {
        super(context);
        init();
    }
    /**
     * Default Constructor needed to extend {@link EditText}
     */
    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    /**
     * Default Constructor needed to extend {@link EditText}
     */
    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Adds this class as it's own text listener and initializes the default settings
     * for:
     * <ul>
     *     <li>Relative Size (subscript/superscript)</li>
     *     <li>Number of Spaces in a tab</li>
     *     <li>The colors you can choose from in the text color or background color dialog</li>
     * </ul>
     */
    protected void init() {
        addTextChangedListener(this);
        relativeSize = 0.5f;
        numTabs = 4;
        colorList = new String[]{"Black - #000000", "White - #FFFFFF",
                "Green - #00800", "Blue - #0000FF", "Purple - #800080",
                "Red - #FF0000", "Orange - #FFA500", "Yellow - #FFFF00",
                "Hot Pink - #FF69B4", "Light Blue - #00FFF9", "Brown - #A52A2A"};
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        updateTextStylesOnSelectionChange(selStart, selEnd);
    }

    /**
     * When the selection is changed, the checked status of all buttons are updated to match the styles
     * within the text.
     * <br>
     * Flow Chart:
     * <ol>
     *      <li>Selection Changed</li>
     *      <li>
     *          Check if styles within selection match the buttons
     *          <br>
     *          <b>e.g. IF bold, then make bold button chekced = true</b>
     *      </li>
     * </ol>
     *
     * @param selStart start of selection
     * @param selEnd   end of selection
     * @see #onSelectionChanged(int, int)
     */
    public void updateTextStylesOnSelectionChange(int selStart, int selEnd){
        if (onSelectionChangeListener != null) {
            onSelectionChangeListener.onSelectionChange(selStart, selEnd);
        }

        boolean isSelectionBold = false;
        boolean isSelectionItalic = false;
        boolean isSelectionUnderlined = false;
        boolean isSelectionStriked = false;
        for (ParcelableSpan span : getText().getSpans(selStart, selEnd, ParcelableSpan.class)) {
            if (span instanceof StyleSpan) {
                if (((StyleSpan) span).getStyle() == Typeface.BOLD) {
                    isSelectionBold = true;
                } else if (((StyleSpan) span).getStyle() == Typeface.ITALIC) {
                    isSelectionItalic = true;
                }
            }

            if (span instanceof MyUnderlineSpan) {
                isSelectionUnderlined = true;
            }

            if (span instanceof StrikethroughSpan) {
                isSelectionStriked = true;
            }

        }

        if (boldButton != null) {
            boldButton.setChecked(isSelectionBold);
        }
        if (italicButton != null) {
            italicButton.setChecked(isSelectionItalic);
        }
        if (underlineButton != null) {
            underlineButton.setChecked(isSelectionUnderlined);
        }
        if (strikeThroughButton != null) {
            strikeThroughButton.setChecked(isSelectionStriked);
        }
    }

    /**
     *@see TextWatcher
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Necessary to implement TextWatcher
    }

    /**
     * Applies styles to the text upon being input by checking if the corresponding style button is checked.
     *
     * @param s      The text being changed (This editor's text with the recent added input)
     * @param start  Where the input change starts within s
     * @param before length of the text that has been replaced (if nothing was selected, then this is 0)
     * @param count  length of the new text being input (if not copy pasting, then 1)
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (boldButton != null && boldButton.isChecked()) {
            getText().setSpan(new StyleSpan(Typeface.BOLD), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (italicButton != null && italicButton.isChecked()) {
            getText().setSpan(new StyleSpan(Typeface.ITALIC), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (underlineButton != null && underlineButton.isChecked()) {
            getText().setSpan(new MyUnderlineSpan(), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (strikeThroughButton != null && strikeThroughButton.isChecked()) {
            getText().setSpan(new StrikethroughSpan(), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * @see TextWatcher
     */
    @Override
    public void afterTextChanged(Editable s) {
        //Necessary to implement TextWatcher
    }

    /**
     * Handles the clicks of buttons referenced to this editor.
     * @param v the view from which the clicks are recieved.
     *          <br>
     *          i.e. this editor
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == boldButton.getId() || id == italicButton.getId() || id == underlineButton.getId()) {
            updateTextStyleAction();
        }
        else if (id == subscriptButton.getId()) {
            subscriptAction();
        }
        else if (id == superscriptButton.getId()) {
            superscriptAction();
        }
        else if (id == indentButton.getId()){
            indentAction();
        }
        else if (id == unindentButton.getId()){
            unindentAction();
        }
        else if (id == textColorButton.getId()){
            textColorAction();
        }
        else{
            Log.e(TAG, "You forgot to assign a button!");
        }

    }

    /**
     * Updates the text style of a selection when text is not being input into the editor
     * <br>
     * <b>e.g.</b> highlighting text and then applying bold to it <i>without changing the text</i>
     * <br><br>
     * Flow Chart:
     * <ol>
     * <li>Text Highlighted</li>
     * <li>Style Button Pressed</li>
     * <li>Check if current selection has style
     * <ul>
     * <li>IF true: remove style</li>
     * <li>IF false: apply style</li>
     * </ul>
     * </li>
     * <p>
     * </ol>
     */
    protected void updateTextStyleAction() {
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();
        if (boldButton.isChecked() && start != end) {
            this.getText().setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (!boldButton.isChecked()) {
            removeSpansWithinSelection(Typeface.BOLD);
        }

        if (italicButton.isChecked() && start != end) {
            this.getText().setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (!italicButton.isChecked()) {
            removeSpansWithinSelection(Typeface.ITALIC);
        }

        if (underlineButton.isChecked() && start != end) {
            this.getText().setSpan(new MyUnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (!underlineButton.isChecked()) {
            removeSpansWithinSelection(MyUnderlineSpan.class);
        }

        if (strikeThroughButton.isChecked() && start != end) {
            this.getText().setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (!strikeThroughButton.isChecked()) {
            removeSpansWithinSelection(StrikethroughSpan.class);
        }
    }

    /**
     * Removes the spans within the selection matching the class given.
     *
     * @param classes Not all spans are referenced by a {@link Typeface} Id
     *                therefore, they are found by comparing their class to the class passed by this method
     * @see #updateTextStyleAction()
     */
    protected void removeSpansWithinSelection(Class... classes) {
        for (ParcelableSpan span : this.getText().getSpans(getSelectionStart(), getSelectionEnd(), ParcelableSpan.class)) {
            for (Class c : classes) {
                if (span.getClass().equals(c)) {
                    this.getText().removeSpan(span);
                }
            }
        }
    }

    /**
     * Removes the spans within the selection matching the {@link StyleSpan} Id given
     *
     * @param spanId the Id of the style to be removed. See {@link Typeface} for Ids.
     * @see #updateTextStyleAction()
     */
    protected void removeSpansWithinSelection(int spanId) {
        for (StyleSpan span : this.getText().getSpans(getSelectionStart(), getSelectionEnd(), StyleSpan.class)) {
            if (span.getStyle() == spanId) {
                this.getText().removeSpan(span);
            }
        }
    }

    /**
     * Opens a dialog in which the user can enter text to either be
     * subscripted or superscripted depending on {@code span}
     * @param span The span to be used on the text. Either {@link SuperscriptSpan}
     *             or {@link SubscriptSpan} will be used
     * @see #subscriptAction()
     * @see #superscriptAction()
     */
    protected void relativeSizeFormat(final MetricAffectingSpan span) {
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Relative Size of Text: " + relativeSize);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SpannableString spannedText = new SpannableString(input.getText());
                spannedText.setSpan(span, 0, input.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannedText.setSpan(new RelativeSizeSpan(relativeSize), 0, input.getText().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                getText().insert(getSelectionStart(), spannedText);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    /**
     * @see #relativeSizeFormat(MetricAffectingSpan)
     */
    public void subscriptAction(){
        relativeSizeFormat(new SubscriptSpan());
    }

    /**
     * @see #relativeSizeFormat(MetricAffectingSpan)
     */
    public void superscriptAction() {
        relativeSizeFormat(new SuperscriptSpan());
    }

    /**
     * Indents the text by adding spaces. The number of spaces is determined by {@code numTabs}
     */
    public void indentAction(){
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        String tabs = "";
        for (int i = 0; i < numTabs; i++) {
           tabs += spaceCharacter;
        }
        Editable newText = getText().insert(getSelectionStart(), tabs);
        setText(newText);
        if (selectionStart != selectionEnd) {
            setSelection(selectionStart + numTabs, selectionEnd + numTabs);
        }
        else{
            setSelection(selectionStart + numTabs);
        }
    }

    /**
     * UnIndents the text by removing spaces. The number of spaces is determined by {@code numTabs}
     */
    public void unindentAction() {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        if (selectionStart >= numTabs) {
            String tabCharacter = "";
            for (int i = 0; i < numTabs; i++){
                tabCharacter += spaceCharacter;
            }
            CharSequence oldText = getText().subSequence(selectionStart - numTabs, selectionStart);
            if (oldText.toString().equals(tabCharacter)){
                Editable newText = getText().delete(selectionStart - numTabs, selectionStart);
                setText(newText);
                if (selectionStart != selectionEnd) {
                    setSelection(selectionStart - numTabs, selectionEnd - numTabs);
                } else {
                    setSelection(selectionStart - numTabs);
                }
            }

        }
    }

    //TODO Document Me!
    public void textColorAction(){
        final int selectionStart = getSelectionStart();
        final int selectionEnd = getSelectionEnd();
        int[] hexColors = new int[colorList.length];
        SpannableString[] spannedColorList = new SpannableString[colorList.length];
        for (int i = 0; i < colorList.length; i ++){
            String color = colorList[i];
            String hex = color.substring(color.indexOf('#'), color.length() - 1);
            hexColors[i] = Integer.decode(hex);
            spannedColorList[i] = new SpannableString(color);
            spannedColorList[i].setSpan(new ForegroundColorSpan(hexColors[i]), 0, color.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        SpannedAdapter listArray = new SpannedAdapter(getContext(), spannedColorList);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose Color for Text: " + relativeSize);
        builder.setAdapter(listArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getText().setSpan(new ForegroundColorSpan(Color.YELLOW), selectionStart, selectionEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void insertTimeAction(){
        Calendar c = Calendar.getInstance();
        String date = "<"+c.get(Calendar.HOUR)+":"+c.get(Calendar.MINUTE)+":"+c.get(Calendar.SECOND)+">";
        String newText = getText().insert(getSelectionStart(), date).toString();
        setText(newText);
        setSelection(getSelectionStart()+date.length());
    }

    protected void insertDateAction(){
        Calendar c = Calendar.getInstance();
        String time = "<"+c.get(Calendar.MONTH)+"-"+c.get(Calendar.DAY_OF_MONTH)+"-"+c.get(Calendar.YEAR)+">";
        String newText = getText().insert(getSelectionStart(), time).toString();
        setText(newText);
        setSelection(getSelectionStart()+time.length());
    }

    protected void pageUpAction(){
        setSelection(0);
    }


    protected void pageDownAction(){
        setSelection(getText().length());
    }

    /**
     * References all buttons required for the full functionality of this text editor and sets their
     * on click listener to be this editor.
     * <br>
     * <b>To add your own functionality to the buttons when clicked:</b> simply call
     * {@link #onClick(View)} within your own OnClickListener's method.
     */

    public void setAllButtons(ToggleButton boldButton, ToggleButton italicButton, ToggleButton underlineButton, ToggleButton strikeThroughButton,
                              View subscriptButton, View superscriptButton,
                              View unindentButton, View indentButton,
                              View textColorButton, View backgroundButton) {
        try {
            setBoldButton(boldButton);
            setItalicButton(italicButton);
            setUnderlineButton(underlineButton);
            setStrikeThroughButton(strikeThroughButton);
            setSubscriptButton(subscriptButton);
            setSuperscriptButton(superscriptButton);
            setUnindentButton(unindentButton);
            setIndentButton(indentButton);
            setTextColorButton(textColorButton);
            setBackgroundButton(backgroundButton);
        } catch (NullPointerException ex) {
            throw new NullPointerException("Remember, you can set buttons individually instead of setting them all at the same time");
        }
    }

    public void setBoldButton(ToggleButton button) {
        this.boldButton = button;
        this.boldButton.setOnClickListener(this);
    }

    public void setItalicButton(ToggleButton button) {
        this.italicButton = button;
        this.italicButton.setOnClickListener(this);
    }

    public void setUnderlineButton(ToggleButton button) {
        this.underlineButton = button;
        this.underlineButton.setOnClickListener(this);
    }

    public void setStrikeThroughButton(ToggleButton button) {
        this.strikeThroughButton = button;
        this.strikeThroughButton.setOnClickListener(this);
    }

    public void setSubscriptButton(View button) {
        this.subscriptButton = button;
        this.subscriptButton.setOnClickListener(this);
    }

    public void setSuperscriptButton(View button) {
        this.superscriptButton = button;
        this.superscriptButton.setOnClickListener(this);
    }

    public void setIndentButton(View button){
        this.indentButton = button;
        this.indentButton.setOnClickListener(this);
    }

    public void setUnindentButton(View button){
        this.unindentButton = button;
        this.unindentButton.setOnClickListener(this);
    }

    public void setTextColorButton(View button){
        this.textColorButton = button;
        this.textColorButton.setOnClickListener(this);
    }

    public void setBackgroundButton(View button){
        this.backgroundButton = button;
        this.backgroundButton.setOnClickListener(this);
    }

    public ColorDrawable getTextColor(){
        return currentTextColor;
    }

    public void setTextColor(ColorDrawable color){
        this.currentTextColor = color;
        this.textColorButton.setForeground(currentTextColor);
    }

    public ColorDrawable getCurrentBackgroundColor(){
        return currentBackgroundColor;
    }

    public void setCurrentBackgroundColor(ColorDrawable color){
        this.currentBackgroundColor = color;
        this.backgroundButton.setBackground(currentBackgroundColor);
    }

    public void setColorList(String[] colors){
        colorList = colors;
    }

    public String[] getColorList(){
        return colorList;
    }

    protected void settings(){
        //TODO for indent - set spacing
        //TODO set line spacing
        //TODO set font size
        //TODO set font family
        //TODO set relative size for super/subscript
    }

    /**
     * Adds a SelectionChangeListener to this editor
     *
     * @param onSelectionChangeListener the listener to be added
     */
    public void addOnSelectionChangeListener(OnSelectionChangeListener onSelectionChangeListener) {
        this.onSelectionChangeListener = onSelectionChangeListener;
    }

    /**
     * Set the relative size of the subscript and superscript text.
     * The value must be from 0 to 1. If 0.5 is the set value, then
     * the text will be half as small
     * <br>
     * If the size is not set, a default value of 0.5 is used.
     * @param f the relative size of the text to be formatted. Must be between 0 to 1.
     */
    public void setRelativeSize(float f){
        relativeSize = f;
    }

    /**
     * Listens for changes in {@link RichEditText} selections
     */
    public interface OnSelectionChangeListener {

        void onSelectionChange(int start, int end);
    }

    /**
     * Necessary to differentiate between the underlining done by the SpellChecker and the
     * underlining done by the user
     *
     * @see #onSelectionChanged(int, int)
     */
    @SuppressLint("ParcelCreator")
    protected class MyUnderlineSpan extends UnderlineSpan {

    }

    /**
     * Code adapted from http://stackoverflow.com/questions/4580664/how-put-spanned-into-list-setadapter
     * Credit where credit is due.
     * <br>
     * It's basically a custom adapter to allow you to have different colors for each item in an AlertDialog
     */
    protected static class SpannedAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private SpannableString[] list;

        protected SpannedAdapter(Context context, SpannableString[] articleList) {
            inflater = LayoutInflater.from(context);
            list = articleList;
        }

        @Override
        public int getCount() {
            return list.length;
        }

        @Override
        public Object getItem(int position) {
            return list[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_spinner_item, null);
                holder = new ViewHolder();
                holder.text = new TextView(convertView.getContext());

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text.setText(list[position]);

            return convertView;
        }

        protected static class ViewHolder{
            protected TextView text;
        }
    }


}
