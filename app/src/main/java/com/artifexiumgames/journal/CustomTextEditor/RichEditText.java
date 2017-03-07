package com.artifexiumgames.journal.CustomTextEditor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.ParcelableSpan;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.artifexiumgames.journal.R;

/**
 * This is a simple Rich Text Editor that provides added functionality to {@link EditText}.
 * <p>
 *     This Editor uses {@link Button} and {@link ToggleButton} to
 *     activate the different controls associated with rich text.
 * </p>
 * <p>
 *     Implementation Features Include:
 *     <ul>
 *         <li>Automatic button functionality: reference buttons to the class with {@link #setAllButtons(ToggleButton, ToggleButton, ToggleButton)}</li>
 *         <li>Selection changes can now be listened for with {@link OnSelectionChangeListener}</li>
 *     </ul>
 * </p>
 * <p>
 *     Text Features Include:
 *     <ul>
 *         <li>Bold</li>
 *         <li>Italic</li>
 *         <li>Underline</li>
 *     </ul>
 * </p>
 *
 * <p>
 *     <strong>WARNING:</strong>
 *     <br>
 *     Before implementing your own {@link View.OnClickListener} for your buttons referenced by this class
 *     or {@link TextWatcher} to your {@link Activity} to monitor/change text of this editor, <br>
 *     <Strong>REMEMBER</Strong> you <strong>MUST</strong> call {@link RichEditText#onClick(View)} and/or the relevant TextWatcher methods.
 * </p>
 *
 * @see EditText
 * @see OnSelectionChangeListener
 * @see Button
 * @see ToggleButton
 * @see View.OnClickListener
 * @see TextWatcher
 */
public class RichEditText extends AppCompatEditText implements TextWatcher, View.OnClickListener {

    protected OnSelectionChangeListener onSelectionChangeListener;
    protected boolean isSelectionBold;
    protected boolean isSelectionItalic;
    protected boolean isSelectionUnderlined;
    protected ToggleButton boldButton;
    protected ToggleButton italicButton;
    protected ToggleButton underlineButton;

    public RichEditText(Context context) {
        super(context);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        addTextChangedListener(this);
    }

    /**
     * When the selection is changed, the checked status of all buttons are updated
     * <br>
     * Flow Chart:
     * <ol>
     *     <li>Selection Changed</li>
     *     <li>Check if styles within selection match the buttons<br><b>e.g. IF bold, then make bold button chekced = true</b></li>
     * </ol>
     * @param selStart start of selection
     * @param selEnd end of selection
     */
    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);

        if (onSelectionChangeListener != null) {
            onSelectionChangeListener.onSelectionChange(selStart, selEnd);
        }

        boolean bold = false;
        boolean italic = false;
        boolean underlined = false;
        for (ParcelableSpan span: getText().getSpans(selStart, selEnd, ParcelableSpan.class)){
            if (span instanceof StyleSpan){
                if (((StyleSpan) span).getStyle() == Typeface.BOLD){
                    bold = true;
                }
                else if (((StyleSpan) span).getStyle() == Typeface.ITALIC) {
                    italic = true;
                }
            }

            if (span instanceof MyUnderlineSpan){
                underlined = true;
            }
        }
        if (bold){
            isSelectionBold = true;
        }
        else{
            isSelectionBold = false;
        }

        if (italic){
            isSelectionItalic = true;
        }
        else{
            isSelectionItalic = false;
        }

        if (underlined){
            isSelectionUnderlined = true;
        }
        else{
            isSelectionUnderlined = false;
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

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    /**
     * Applies styles to the text upon being input by checking if the corresponding style button is checked.
     * @param s The text being changed (This editor's text with the recent added input)
     * @param start Where the input change starts within s
     * @param before length of the text that has been replaced (if nothing was selected, then this is 0)
     * @param count length of the new text being input (if not copy pasting, then 1)
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (boldButton != null && boldButton.isChecked()) {
            getText().setSpan(new StyleSpan(Typeface.BOLD), start, start + count, 0);
        }
        if (italicButton != null && italicButton.isChecked()){
            getText().setSpan(new StyleSpan(Typeface.ITALIC), start, start + count, 0);
        }
        if (underlineButton != null && underlineButton.isChecked()){
            getText().setSpan(new MyUnderlineSpan(), start, start + count, 0);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.boldToggleButton:
            case R.id.italicToggleButton:
            case R.id.underlineToggleButton:
                updateTextStyle();
                break;
        }
    }

    /**
     * Updates the text style of a selection when text is not being input into the editor
     * <br>
     * <strong>e.g.</strong> highlighting text and then applying bold to it <i>without changing the text</i>
     * <br><br>
     * Flow Chart:
     * <ol>
     *     <li>Text Highlighted</li>
     *     <li>Style Button Pressed</li>
     *     <li>Check if current selection has style
     *         <ul>
     *             <li>IF true: remove style</li>
     *             <li>IF false: apply style</li>
     *         </ul>
     *     </li>
     *
     * </ol>
     */
    protected void updateTextStyle() {
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();
        if (boldButton.isChecked()){
            if (start != end){
                this.getText().setSpan(new StyleSpan(Typeface.BOLD), start, end, 0);
            }
        }
        else if (!boldButton.isChecked()){
            removeSpansWithinSelection(Typeface.BOLD);
        }

        if (italicButton.isChecked()){
            if (start != end){
                this.getText().setSpan(new StyleSpan(Typeface.ITALIC), start, end, 0);
            }
        }
        else if (!italicButton.isChecked()){
            removeSpansWithinSelection(Typeface.ITALIC);
        }

        if (underlineButton.isChecked()){
            if (start != end){
                this.getText().setSpan(new MyUnderlineSpan(), start, end, 0);
            }
        }
        else if (!underlineButton.isChecked()){
            removeSpansWithinSelection(MyUnderlineSpan.class);
        }
    }

    /**
     * Removes the spans within the selection matching the {@link StyleSpan} Id given
     * @param spanId the Id of the style
     */
    protected void removeSpansWithinSelection(int spanId) {
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();
        for (StyleSpan span: this.getText().getSpans(start, end, StyleSpan.class)) {
            if (span.getStyle() == spanId) {
                this.getText().removeSpan(span);
            }
        }
    }

    /**
     * Removes the spans within the selection matching the class given.
     * @param c Not all spans are referenced by a {@link Typeface} Id
     * therefore, they are found by comparing their class to the class passed by this method
     */
    protected void removeSpansWithinSelection(Class c) {
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();
        for (ParcelableSpan span: this.getText().getSpans(start, end, ParcelableSpan.class)) {
            if (span.getClass().equals(c)) {
                this.getText().removeSpan(span);
            }
        }
    }

    /**
     * References all buttons required for the full functionality of this text editor and sets their
     * on click listener to be this editor.
     * <br>
     * <Strong>To add your own functionality to the buttons when clicked:</Strong> simply call
     * {@link #onClick(View)} within your own OnClickListener's method.
     */
    public void setAllButtons(ToggleButton boldButton, ToggleButton italicButton, ToggleButton underlineButton){
        this.boldButton = boldButton;
        this.italicButton = italicButton;
        this.underlineButton = underlineButton;
        this.boldButton.setOnClickListener(this);
        this.italicButton.setOnClickListener(this);
        this.underlineButton.setOnClickListener(this);
    }

    public void setBoldButton(ToggleButton button){
        boldButton = button;
        this.boldButton.setOnClickListener(this);
    }

    public void setItalicButton(ToggleButton button){
        italicButton = button;
        this.italicButton.setOnClickListener(this);
    }

    public void setUnderlineButton(ToggleButton button){
        underlineButton = button;
        this.underlineButton.setOnClickListener(this);
    }

    /**
     * Adds a SelectionChangeListener to this editor
     * @param onSelectionChangeListener the listener to be added
     */
    public void addOnSelectionChangeListener(OnSelectionChangeListener onSelectionChangeListener) {
        this.onSelectionChangeListener = onSelectionChangeListener;
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
     * @see #onSelectionChanged(int, int) 
     */
    @SuppressLint("ParcelCreator")
    private class MyUnderlineSpan extends UnderlineSpan {

    }


}
