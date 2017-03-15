package com.artifexiumgames.journal.RichEditText;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.ParcelableSpan;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static android.R.id.text1;

//TODO left, right, center align buttons
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
public class RichEditText extends AppCompatEditText implements TextWatcher, View.OnClickListener, DialogInterface.OnClickListener{

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

    //Color Related
    protected View textColorButton;
    protected View highlightColorButton;
    protected ColorDrawable currentTextColor;
    protected ColorDrawable currentTextHighlightColor;
    //Color Dialog Related
    protected ArrayList<ColorString> colorList;
    protected AlertDialog mainColorChooserDialog;
    protected AlertDialog customColorChooserDialog;
    protected EditText colorTitle;
    protected EditText colorHex;
    protected boolean changingTextColor;

    //Settings
    protected float relativeSize;
    protected int numTabs;
    protected int highlightAlpha;

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
     *     <li>The colors you can choose from in the text color or highlight color dialog</li>
     * </ul>
     */
    protected void init() {
        addTextChangedListener(this);
        setRelativeSize(0.5f);
        setNumTabs(4);
        setHighlightAlpha(168);
        colorList = new ArrayList<>();
        try {
            colorList.add(new ColorString("Black", "#000000"));
            colorList.add(new ColorString("White", "#FFFFFF"));
            colorList.add(new ColorString("Green", "#008000"));
            colorList.add(new ColorString("Blue", "#0000FF"));
            colorList.add(new ColorString("Purple", "#800080"));
            colorList.add(new ColorString("Red", "#FF0000"));
            colorList.add(new ColorString("Orange", "#FFA500"));
            colorList.add(new ColorString("Yellow", "#FFFF00"));
            colorList.add(new ColorString("Hot Pink", "#FF69B4"));
            colorList.add(new ColorString("Light Blue", "#00FFF9"));
            colorList.add(new ColorString("Brown", "#A52A2A"));
        } catch (IOException ex){
            Log.wtf(TAG, "Default Settings for ColorString incorrect");
        }
        setCurrentTextColor(new ColorDrawable(Color.BLACK));
        setCurrentTextHighlightColor(new ColorDrawable(Color.WHITE));
        createMainColorChooserDialog();
        createCustomColorChooserDialog();
    }

    /**
     * Set up the default settings of the editor
     * @param startingTextColor sets the starting color of the text
     * @param startingHighlightColor sets the starting color of the text highlight.
     *                                <b>Note:</b> this should probably be contrasting to the starting
     *                                text color, and also is different from the background color
     *                                of the text editor as a whole. For that, see {@link #setBackgroundColor(int)}
     * @param colors The colors that will show in the dialog to choose colors
     * @param numTabs The number of spaces in a tab
     * @param relativeSize The relative size of subscript and superscript text
     */
    protected void setSettings(int startingTextColor, int startingHighlightColor, ArrayList<ColorString> colors, int numTabs, int relativeSize, int highlightAlpha){
        addTextChangedListener(this);
        setCurrentTextColor(new ColorDrawable(startingTextColor));
        setCurrentTextHighlightColor(new ColorDrawable(startingHighlightColor));
        setColorList(colors);
        setNumTabs(numTabs);
        setRelativeSize(relativeSize);
        setHighlightAlpha(highlightAlpha);
        //TODO set line spacing
        //TODO set font size
        //TODO set font family
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
        try {
            for (ParcelableSpan span : getText().getSpans(selStart, selEnd, ParcelableSpan.class)) {
                if (span instanceof RichEditBoldSpan) {
                    isSelectionBold = true;
                } else if (span instanceof RichEditItalicSpan) {
                    isSelectionItalic = true;
                } else if (span instanceof RichEditUnderlineSpan) {
                    isSelectionUnderlined = true;
                } else if (span instanceof StrikethroughSpan) {
                    isSelectionStriked = true;
                } else if (span instanceof ForegroundColorSpan) {
                    setCurrentTextColor(new ColorDrawable(((ForegroundColorSpan) span).getForegroundColor()));
                } else if (span instanceof BackgroundColorSpan) {
                    setCurrentTextHighlightColor(new ColorDrawable(((BackgroundColorSpan) span).getBackgroundColor()));
                } else {
                    throw new UnknownParcelableSpanException("Unknown ParcelableSpan when updating text styles on selection change");
                }

            }
        } catch (UnknownParcelableSpanException e){
            Log.e(TAG, "Was a new span type introduced and not accounted for?", e);
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
     * @param s      The text that has been changed (This editor's text with the recent added input)
     * @param start  Where the input change starts within s
     * @param before length of the text that has been replaced (if nothing was selected, then this is 0)
     * @param count  length of the new text being input (if not copy pasting, then 1)
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        SpannableStringBuilder text = (SpannableStringBuilder) s;
        StyleSpan[] spans = text.getSpans(0, s.length(), StyleSpan.class);
        for (StyleSpan span: spans){
            getText().removeSpan(span);
        }
        if (boldButton != null && boldButton.isChecked()) {
            getText().setSpan(new RichEditBoldSpan(), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (italicButton != null && italicButton.isChecked()) {
            getText().setSpan(new RichEditItalicSpan(), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (underlineButton != null && underlineButton.isChecked()) {
            getText().setSpan(new RichEditUnderlineSpan(), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if (strikeThroughButton != null && strikeThroughButton.isChecked()) {
            getText().setSpan(new StrikethroughSpan(), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //text color
        if (currentTextColor != null) { //On loading the editor, the color will be null
            getText().setSpan(new ForegroundColorSpan(currentTextColor.getColor()), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //text highlight color
        if (currentTextHighlightColor != null) { //On loading the edtior, the color will be null
            if (getBackground() instanceof ColorDrawable) { //if the background of the editor is a color, then
                if (((ColorDrawable) getBackground()).getColor() != currentTextHighlightColor.getColor()) { //check if the editor background color is != to the text's background color
                    getText().setSpan(new BackgroundColorSpan(currentTextHighlightColor.getColor()), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //if so, then okay to color
                }
            }
            else { //if the background color isn't a color, then it must be an image or something, so go ahead and set the background color
                getText().setSpan(new BackgroundColorSpan(currentTextHighlightColor.getColor()), start, start + count, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }

    /**
     * @see TextWatcher
     */
    @Override
    public void afterTextChanged(Editable s) {
        //Necessary to implement TextWatcher
    }

    //TODO document
    //if color already in list, color removed and added with new name
    @Override
    public void onClick(DialogInterface dialog, int which) {

        if (dialog.equals(mainColorChooserDialog)){
            switch (which){
                case Dialog.BUTTON_NEUTRAL:
                    customColorChooserDialog.show();
                    dialog.dismiss();
                    break;
                default: //which == index of the item cilcked in the list
                    ColorString color = colorList.remove(which);
                    colorList.add(0, color);
                    if (changingTextColor){
                        setCurrentTextColor(new ColorDrawable(color.getHex()));
                    }
                    else{
                        setCurrentTextHighlightColor(new ColorDrawable(color.getHex()));
                    }
                    dialog.dismiss();
                    break;

            }
        }
        else if (dialog.equals(customColorChooserDialog)){
            switch(which){
                case Dialog.BUTTON_POSITIVE:
                    try {
                        ColorString newColor = new ColorString(colorTitle.getText().toString(), colorHex.getText().toString());
                        if (!colorList.contains(newColor)) {
                            colorList.add(0, newColor);
                        }else {
                            colorList.remove(newColor);
                            colorList.add(0, newColor);
                        }
                        if (changingTextColor){
                            setCurrentTextColor(new ColorDrawable(newColor.getHex()));
                        }
                        else{
                            setCurrentTextHighlightColor(new ColorDrawable(newColor.getHex()));
                        }
                        dialog.dismiss();
                    } catch (IOException e) {
                        AlertDialog thisDialog = (AlertDialog) dialog;
                        thisDialog.setTitle("Incorrect Format");
                        thisDialog.setIcon(android.R.drawable.ic_dialog_alert);
                        thisDialog.cancel();
                    }
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    dialog.dismiss();
            }
        }

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
        try {
            if (id == boldButton.getId()) {
                updateTextStylesOnButtonPress(boldButton, RichEditBoldSpan.class);
            } else if (id == italicButton.getId()) {
                updateTextStylesOnButtonPress(italicButton, RichEditItalicSpan.class);
            } else if (id == underlineButton.getId()) {
                updateTextStylesOnButtonPress(underlineButton, RichEditUnderlineSpan.class);
            } else if (id == strikeThroughButton.getId()) {
                updateTextStylesOnButtonPress(strikeThroughButton, StrikethroughSpan.class);
            } else if (id == subscriptButton.getId()) {
                subscriptAction();
            } else if (id == superscriptButton.getId()) {
                superscriptAction();
            } else if (id == indentButton.getId()) {
                indentAction();
            } else if (id == unindentButton.getId()) {
                unindentAction();
            } else if (id == textColorButton.getId()) {
                textColorAction();
            } else if (id == highlightColorButton.getId()) {
                highlightColorAction();
            } else {
                Log.e(TAG, "Button pressed did not match any known Ids"); //TODO throw an error?
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating text styles", e);
        }
    }

    protected void updateTextStylesOnButtonPress(ToggleButton button, Class c) throws Exception {
        int selStart = this.getSelectionStart();
        int selEnd = this.getSelectionEnd();
        Editable text = getText();
        if (button.isChecked()) {
            if (selStart != selEnd) {
                String[] words = text.subSequence(selStart, selEnd).toString().split(" ");
                int i = 0;
                for (String word : words) {
                        text.setSpan(c.newInstance(), selStart + i, selStart + i + word.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    i += word.length() + 1;
                }
            }

            //Find word at cursor and set bold style
            else {
                boolean startFound = false;
                boolean endFound = false;
                int wordStart = -1;
                int wordEnd = -1;
                int i = 0;
                try {
                    while (!startFound) {
                        if (text.charAt(selStart - i) != ' ') {
                            i += 1;
                        } else {
                            wordStart = selStart - i;
                            startFound = true;
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    Log.d(TAG, "word search reached zero while updating text style");
                    wordStart = -1; //-1 because algorithm adds 1 at end to compensate for spaces
                }

                try {
                    i = 0;
                    while (!endFound) {
                        if (text.charAt(selStart + i) != ' ') {
                            i += 1;
                        } else {
                            wordEnd = selStart + i;
                            endFound = true;
                        }
                    }
                } catch (IndexOutOfBoundsException e){
                    Log.d(TAG, "word search reached end of text while updating text style");
                    wordEnd = getText().length();
                }
                    text.setSpan(c.newInstance(), wordStart+1, wordEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        else if (!button.isChecked()) {
            removeSpansWithinSelection(c);
        }
    }

    /**
     * Removes the spans within the selection matching the class given, and readjusts the span previous
     * to it to be properly formatted.
     *
     * @param c Not all spans are referenced by a {@link Typeface} Id
     *                therefore, they are found by comparing their class to the class passed by this method
     *
     * @see #updateTextStylesOnButtonPress(ToggleButton, Class)
     */
    protected void removeSpansWithinSelection(Class c) {
        int start = getSelectionStart();
        int end = getSelectionEnd();
        if (start != end){
            for (Object span : this.getText().getSpans(start, end, c)) {
                if (span.getClass().equals(c)) {
                    this.getText().removeSpan(span);
                }
            }
        }

        else {
            List<Object> spanList = Arrays.asList(getText().getSpans(0, getText().length(), c));
            for (Object span: getText().getSpans(start, end, c)) {
                if (span.getClass().equals(c)) {
                    int index = spanList.indexOf(span); //Grabs index of span at cursor
                    if (index > 0) {                    //Check if span is first span of this type, if so readjust previous span so that span does not include space between words
                        Object previous = spanList.get(index - 1); //Gets previous span
                        int previousStart = getText().getSpanStart(previous); //previous span start
                        int previousEnd = getText().getSpanEnd(previous);       //previous span end
                        if (getText().charAt(previousEnd - 1) == ' ')               //Check to see if the first character in the previous span is a space
                            getText().setSpan(previous, previousStart, previousEnd - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //set span end to be 1 less

                    }
                    this.getText().removeSpan(span);
                }
            }
        }
    }

    /**
     * Styles the text depending on the state of the button
     * <br>
     * <code>if (button.isChecked())</code>
     * <br>
     * <code>&nbsp; &nbsp; apply style to selection</code>
     * <br>
     * <code>else if (!button.isChecked())</code>
     * <br>
     * <code>&nbsp; &nbsp; remove style from thext</code>
     * @see #updateTextStylesOnButtonPress(ToggleButton, Class)
     */
    public void boldAction(){
        try {
            updateTextStylesOnButtonPress(boldButton, RichEditBoldSpan.class);
        } catch (Exception e) {
            Log.e(TAG, "Error while performing boldAction");
        }
    }

    /**
     * Styles the text depending on the state of the button
     * <br>
     * <code>if (button.isChecked())</code>
     * <br>
     * <code>&nbsp; &nbsp; apply style to selection</code>
     * <br>
     * <code>else if (!button.isChecked())</code>
     * <br>
     * <code>&nbsp; &nbsp; remove style from thext</code>
     * @see #updateTextStylesOnButtonPress(ToggleButton, Class)
     */
    public void italicAction(){
        try {
            updateTextStylesOnButtonPress(italicButton, RichEditItalicSpan.class);
        } catch (Exception e) {
            Log.e(TAG, "Error while performing italicAction");
        }
    }

    /**
     * Styles the text depending on the state of the button
     * <br>
     * <code>if (button.isChecked())</code>
     * <br>
     * <code>&nbsp; &nbsp; apply style to selection</code>
     * <br>
     * <code>else if (!button.isChecked())</code>
     * <br>
     * <code>&nbsp; &nbsp; remove style from thext</code>
     * @see #updateTextStylesOnButtonPress(ToggleButton, Class)
     */
    public void underlineAction(){
        try {
            updateTextStylesOnButtonPress(underlineButton, RichEditUnderlineSpan.class);
        } catch (Exception e) {
            Log.e(TAG, "Error while performing underlineAction");
        }
    }

    /**
     * Styles the text depending on the state of the button
     * <br>
     * <code>if (button.isChecked())</code>
     * <br>
     * <code>&nbsp; &nbsp; apply style to selection</code>
     * <br>
     * <code>else if (!button.isChecked())</code>
     * <br>
     * <code>&nbsp; &nbsp; remove style from thext</code>
     * @see #updateTextStylesOnButtonPress(ToggleButton, Class)
     */
    public void strikethroughAction(){
        try {
            updateTextStylesOnButtonPress(strikeThroughButton, StrikethroughSpan.class);
        } catch (Exception e) {
            Log.e(TAG, "Error while performing strikethroughAction");
        }
    }
    /**
     * Temporary variable needed, to keep selection constant before and after color chooser dialog shows
     */
    private int selectionStartBeforeFocusChange;

    /**
     * Temporary variable needed, to keep selection constant before and after color chooser dialog shows
     */
    private int selectionEndBeforeFocusChange;

    /**
     * Shows a dialog through which you can enter text that will be subscripted
     * @see #relativeSizeFormat(MetricAffectingSpan)
     */
    //TODO Make subscript/superscript a non dialog input
    public void subscriptAction(){
        relativeSizeFormat(new SubscriptSpan());
    }

    /**
     * Shows a dialog through which you can enter text that will be superscripted
     * @see #relativeSizeFormat(MetricAffectingSpan)
     */
    //TODO Make subscript/superscript a non dialog input
    public void superscriptAction() {
        relativeSizeFormat(new SuperscriptSpan());
    }

    //TODO document
    public void textColorAction(){
        selectionStartBeforeFocusChange = getSelectionStart();
        selectionEndBeforeFocusChange = getSelectionEnd();
        changingTextColor = true;
        mainColorChooserDialog.setTitle("Text Color");
        mainColorChooserDialog.show();
        //application of style handled in setCurrentTextColor(ColorDrawable color)
    }

    //TODO document
    public void highlightColorAction(){
        selectionStartBeforeFocusChange = getSelectionStart();
        selectionEndBeforeFocusChange = getSelectionEnd();
        changingTextColor = false;
        mainColorChooserDialog.setTitle("Highlight Color");
        mainColorChooserDialog.show();
        //application of style handled in setCurrentHighlightColor(ColorDrawable color)
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

    //TODO document
    protected void createMainColorChooserDialog(){
        final ArrayAdapter<ColorString> listArray = new ArrayAdapter<ColorString> (getContext(), android.R.layout.simple_list_item_1, colorList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(text1);

                int color = this.getItem(position).getHex();
                tv.setBackgroundColor(color);

                //Convert the color to RGB to decide whether white or black contrasts better with the color
                int r = (color >> 16) & 0xFF;
                int g = (color >> 8) & 0xFF;
                int b = (color >> 0) & 0xFF;
                if ((r*0.299 + g*0.587 + b*0.114) > 186){
                    tv.setTextColor(Color.BLACK);
                }
                else {
                    tv.setTextColor(Color.WHITE);
                }

                return view;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setAdapter(listArray, this);
        builder.setNeutralButton("Enter Custom Color Code", this);
        if (!isInEditMode()) { //Casting exception is made in editor unless this check is made
            mainColorChooserDialog = builder.create();
        }
    }

    //TODO document
    protected void createCustomColorChooserDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LinearLayout layout = new LinearLayout(getContext());
        LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams);
        layout.setOrientation(LinearLayout.VERTICAL);
        colorTitle = new EditText(layout.getContext());
        colorTitle.setLayoutParams(layoutParams);
        colorTitle.setHint("White");
        layout.addView(colorTitle);
        colorHex = new EditText(layout.getContext());
        colorHex.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        colorHex.setLayoutParams(layoutParams);
        colorHex.setHint("#FFFFFF");
        layout.addView(colorHex);
        builder.setView(layout);
        builder.setTitle("Enter Color in Hex Format");
        builder.setPositiveButton("Enter", this);
        builder.setNegativeButton("Cancel", this);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ((AlertDialog) dialog).show();
            }
        });
        if (!isInEditMode()) { //Casting exception is made in editor unless this check is made
            customColorChooserDialog = builder.create();
        }
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
     * @return The list of colors that will be displayed to the user when the color chooser dialog is opened.
     *         If the user has set custom colors, then the list will return those as well
     *         as they are contained in the same list as the default colors.
     */
    public ArrayList<ColorString> getColorList(){
        return colorList;
    }

    /**
     * References all buttons required for the full functionality of this text editor and sets their
     * on click listener to be this editor.
     */
    @NonNull
    public void setAllButtons(ToggleButton boldButton, ToggleButton italicButton, ToggleButton underlineButton, ToggleButton strikeThroughButton,
                              View subscriptButton, View superscriptButton,
                              View unindentButton, View indentButton,
                              View textColorButton, View highlightColorButton) {
            setBoldButton(boldButton);
            setItalicButton(italicButton);
            setUnderlineButton(underlineButton);
            setStrikeThroughButton(strikeThroughButton);
            setSubscriptButton(subscriptButton);
            setSuperscriptButton(superscriptButton);
            setUnindentButton(unindentButton);
            setIndentButton(indentButton);
            setTextColorButton(textColorButton);
            setHighlightColorButton(highlightColorButton);
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

    public void setHighlightColorButton(View button){
        this.highlightColorButton = button;
        this.highlightColorButton.setOnClickListener(this);
    }

    public void setCurrentTextColor(ColorDrawable color){
        this.currentTextColor = color;
        if (selectionStartBeforeFocusChange != selectionEndBeforeFocusChange){
            removeSpansWithinSelection(ForegroundColorSpan.class);
            this.getText().setSpan(new ForegroundColorSpan(currentTextColor.getColor()), selectionStartBeforeFocusChange, selectionEndBeforeFocusChange, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public void setCurrentTextHighlightColor(ColorDrawable color){
        if (color.getAlpha() != highlightAlpha) {
            if (color.getAlpha() > 128) {
                color.setAlpha(highlightAlpha);
            } else {
                color.setAlpha(highlightAlpha + 1); //Due to the color being stored in a signed int, the algorithm in ColorDrawable.setHighlightAlpha loses 1 bit every time
            }
        }
        this.currentTextHighlightColor = color;
        if (selectionStartBeforeFocusChange != selectionEndBeforeFocusChange){
            removeSpansWithinSelection(BackgroundColorSpan.class);
            this.getText().setSpan(new BackgroundColorSpan(currentTextHighlightColor.getColor()), selectionStartBeforeFocusChange, selectionEndBeforeFocusChange, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /**
     * Set the colors available to the user without having to add their own. Note that setting
     * this list will wipe any colors the user has added themselves. To add to the list without
     * removing their settings, call the list with {@link #getColorList()} then add to it.
     * @param colors array of {@link ColorString} that will be used to give the user a default
     *               set of colors
     * @see ColorString
     */
    public void setColorList(ArrayList<ColorString> colors){
        colorList = colors;
    }

    /**
     * Set the number of spaces in a tab
     * @param numTabs the number of spaces in a tab
     */
    public void setNumTabs(int numTabs){
        this.numTabs = numTabs;
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
     * Sets the highlightAlpha of all colors. It is recommended to have at least some transparency
     * because when selecting the text, unless there is highlightAlpha, it is unclear what parts of the
     * text are selected.
     * @param highlightAlpha a value from 0 to 255. 0 being complete transparent, and 255 being completely opaque.
     *
     */
    public void setHighlightAlpha(int highlightAlpha){
        this.highlightAlpha = highlightAlpha;
    }

    /**
     * Adds a SelectionChangeListener to this editor
     *
     * @param onSelectionChangeListener the listener to be added
     * @see #onSelectionChanged(int, int)
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
     */
    @SuppressLint("ParcelCreator")
    public static class RichEditUnderlineSpan extends UnderlineSpan {

        public RichEditUnderlineSpan(){
            //Necessary default constructor for reflection
        }

    }

    /**
     * Used to compare span types in {@link #removeSpansWithinSelection(Class)} instead of having
     * to deal with special cases where some spans are carrying an integer.
     * This makes things much simpler.
     */
    @SuppressLint("ParcelCreator")
    public static class RichEditBoldSpan extends StyleSpan {

        public RichEditBoldSpan(){
            this(Typeface.BOLD);
        }

        private RichEditBoldSpan(int style) {
            super(style);
        }
    }

    /**
     * Used to compare span types in {@link #removeSpansWithinSelection(Class)} (Constructor)} instead of having
     * to deal with special cases where some spans are carrying an integer.
     * This makes things much simpler.
     */
    @SuppressLint("ParcelCreator")
    public static class RichEditItalicSpan extends StyleSpan {

        public RichEditItalicSpan(){
            this(Typeface.ITALIC);
        }

        private RichEditItalicSpan(int style) {
            super(style);
        }
    }

    /**
     * A simple class that holds the information for the name of a color and it's hex code.
     * Will throw an {@link IOException} if information is put in incorrectly
     */
    protected class ColorString {

        private int hex;
        private String colorString;

        /**
         * The format for entering data into the class is as follows:
         * <p>
         *     <b>Title:</b>
         *     <br>
         *     The title can be a string of any length, however, keep in mind that there is a
         *     finite amount of space in the dialog provided by the color chooser dialog
         * </p>
         * <p>
         *     <b>Color:</b>
         *     <br>
         *     The color must be a properly formatted hex value.
         *     Example: "#ffffff"
         * </p>
         * This would give a color of white.
         *
         * @param title The title of the color being supplied
         * @param color The hex code of the color
         * @throws IOException
         */
        public ColorString(String title, String color) throws IOException{
            color = color.trim();
            try{
                hex = Color.parseColor(color);
            }catch (NumberFormatException ex){
                throw new IOException("The string " +color+ " is not a properly formatted hex code");
            }
            title = title.trim();
            colorString = title + " - " + color;
        }

        public int getHex() {
            return hex;
        }

        /**
         * @return "[Name of Color] - [Hex Code]"
         */
        @Override
        public String toString(){
            return colorString;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ColorString && ((ColorString) obj).getHex() == this.getHex();
        }
    }

    public class UnknownParcelableSpanException extends Exception{

        public UnknownParcelableSpanException(String s){
            super(s);
        }

    }

}
