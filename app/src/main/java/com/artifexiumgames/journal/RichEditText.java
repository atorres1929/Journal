package com.artifexiumgames.journal;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.ParcelableSpan;
import android.util.AttributeSet;
import android.widget.EditText;
import android.text.TextWatcher;

/**
 * @author Adam Torres
 *         Created 3/5/2017.
 */

public class RichEditText extends AppCompatEditText implements TextWatcher {

    private OnSelectionChangeListener onSelectionChangeListener;
    private boolean isCurrentlyBold;
    private boolean isCurrentlyItalic;
    private boolean isCurrentlyUnderlined;

    public RichEditText(Context context) {
        super(context);
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (onSelectionChangeListener != null) {
            onSelectionChangeListener.onSelectionChange(selStart, selEnd);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public void setCurrentlyBold(boolean currentlyBold) {
        isCurrentlyBold = currentlyBold;
    }

    public void setCurrentlyItalic(boolean currentlyItalic) {
        isCurrentlyItalic = currentlyItalic;
    }

    public void setCurrentlyUnderlined(boolean currentlyUnderlined) {
        isCurrentlyUnderlined = currentlyUnderlined;
    }

    public void addOnSelectionChangeListener(OnSelectionChangeListener onSelectionChangeListener) {
        this.onSelectionChangeListener = onSelectionChangeListener;
    }

    public interface OnSelectionChangeListener {
        public void onSelectionChange(int start, int end);
    }
}
