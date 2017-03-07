package com.artifexiumgames.journal.CustomTextEditor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.ParcelableSpan;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
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
 *         <li>Automatic button functionality: reference buttons to the editor with setAllButtons()</li>
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
    protected ToggleButton boldButton;
    protected ToggleButton italicButton;
    protected ToggleButton underlineButton;
    protected ToggleButton strikeThroughButton;
    protected ToggleButton subscriptButton;
    protected ToggleButton superscriptButton;

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

        boolean isSelectionBold = false;
        boolean isSelectionItalic = false;
        boolean isSelectionUnderlined = false;
        boolean isSelectionStriked = false;
        boolean isSelectionSubscripted = false;
        boolean isSelectionSuperscripted = false;
        for (ParcelableSpan span: getText().getSpans(selStart, selEnd, ParcelableSpan.class)){
            if (span instanceof StyleSpan){
                if (((StyleSpan) span).getStyle() == Typeface.BOLD){
                    isSelectionBold = true;
                }
                else if (((StyleSpan) span).getStyle() == Typeface.ITALIC) {
                    isSelectionItalic = true;
                }
            }

            if (span instanceof MyUnderlineSpan){
                isSelectionUnderlined = true;
            }

            if (span instanceof StrikethroughSpan){
                isSelectionStriked = true;
            }

            if (span instanceof SubscriptSpan){
                isSelectionSubscripted = true;
            }

            if (span instanceof SuperscriptSpan){
                isSelectionSuperscripted = true;
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
        if (subscriptButton != null) {
            subscriptButton.setChecked(isSelectionSubscripted);
        }
        if (superscriptButton != null) {
            superscriptButton.setChecked(isSelectionSuperscripted);
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
            getText().setSpan(new StyleSpan(Typeface.BOLD), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (italicButton != null && italicButton.isChecked()){
            getText().setSpan(new StyleSpan(Typeface.ITALIC), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (underlineButton != null && underlineButton.isChecked()){
            getText().setSpan(new MyUnderlineSpan(), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (strikeThroughButton != null && strikeThroughButton.isChecked()){
            getText().setSpan(new StrikethroughSpan(), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (subscriptButton != null && subscriptButton.isChecked()){
            getText().setSpan(new SubscriptSpan(), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            getText().setSpan(new RelativeSizeSpan(0.5f), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (superscriptButton != null && superscriptButton.isChecked()){
            getText().setSpan(new SuperscriptSpan(), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            getText().setSpan(new RelativeSizeSpan(0.5f), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == boldButton.getId() || id == italicButton.getId()|| id == underlineButton.getId()){
            updateTextStyle();
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
        if (boldButton.isChecked() && start != end){
            this.getText().setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else if (!boldButton.isChecked()){
            removeSpansWithinSelection(Typeface.BOLD);
        }

        if (italicButton.isChecked() && start != end){
            this.getText().setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else if (!italicButton.isChecked()){
            removeSpansWithinSelection(Typeface.ITALIC);
        }

        if (underlineButton.isChecked() && start != end){
            this.getText().setSpan(new MyUnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else if (!underlineButton.isChecked()){
            removeSpansWithinSelection(MyUnderlineSpan.class);
        }

        if (strikeThroughButton.isChecked() && start != end){
            this.getText().setSpan(new StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else if (!strikeThroughButton.isChecked()){
            removeSpansWithinSelection(StrikethroughSpan.class);
        }

        if (subscriptButton.isChecked() && start != end){
            this.getText().setSpan(new SubscriptSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else if (!subscriptButton.isChecked()){
            removeSpansWithinSelection(SubscriptSpan.class, RelativeSizeSpan.class);
        }

        if (superscriptButton.isChecked() && start != end){
            this.getText().setSpan(new SuperscriptSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        else if (!superscriptButton.isChecked()){
            removeSpansWithinSelection(SuperscriptSpan.class, RelativeSizeSpan.class);
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
     * @param classes Not all spans are referenced by a {@link Typeface} Id
     * therefore, they are found by comparing their class to the class passed by this method
     */
    protected void removeSpansWithinSelection(Class... classes) {
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();
        for (ParcelableSpan span: this.getText().getSpans(start, end, ParcelableSpan.class)) {
            for (Class c: classes) {
                if (span.getClass().equals(c)) {
                    this.getText().removeSpan(span);
                }
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

    public void setAllButtons(ToggleButton boldButton, ToggleButton italicButton, ToggleButton underlineButton,
                              ToggleButton strikeThroughButton, ToggleButton subscriptButton, ToggleButton superscriptButton){
        try {
            this.boldButton = boldButton;
            this.italicButton = italicButton;
            this.underlineButton = underlineButton;
            this.strikeThroughButton = strikeThroughButton;
            this.subscriptButton = subscriptButton;
            this.superscriptButton = superscriptButton;
            this.boldButton.setOnClickListener(this);
            this.italicButton.setOnClickListener(this);
            this.underlineButton.setOnClickListener(this);
            this.strikeThroughButton.setOnClickListener(this);
            this.subscriptButton.setOnClickListener(this);
            this.superscriptButton.setOnClickListener(this);
        }catch(NullPointerException ex){
            Log.e("RichEditText", "Remember, you can set buttons individually instead of setting them all at the same time", ex);
        }
    }

    public void setBoldButton(ToggleButton button){
        this.boldButton = button;
        this.boldButton.setOnClickListener(this);
    }

    public void setItalicButton(@NonNull ToggleButton button){
        this.italicButton = button;
        this.italicButton.setOnClickListener(this);
    }

    public void setUnderlineButton(@NonNull ToggleButton button){
        this.underlineButton = button;
        this.underlineButton.setOnClickListener(this);
    }

    public void setStrikeThroughButton(@NonNull ToggleButton button){
        this.strikeThroughButton = button;
        this.strikeThroughButton.setOnClickListener(this);
    }

    public void setSubscriptButton(@NonNull ToggleButton button){
        this.subscriptButton = button;
        this.subscriptButton.setOnClickListener(this);
    }

    public void setSuperscriptButton(@NonNull ToggleButton button){
        this.superscriptButton = button;
        this.superscriptButton.setOnClickListener(this);
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
