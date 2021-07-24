package com.subconscious.atomdigitaldetox.views;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.ViewCompat;

import com.subconscious.atomdigitaldetox.R;


public class RoundedIconButton extends LinearLayout {

    private TextView buttonLabelTV;
    private ImageView buttonIconIV;
    private LinearLayout buttonContainer;

    public RoundedIconButton(Context context) {
        super(context);
        init(context);
    }

    public RoundedIconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        attrs(context, attrs);
    }

    public RoundedIconButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        attrs(context, attrs);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_rounded_icon_button, this);
    }

    private void attrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedIconButton);
        String buttonLabel = typedArray.getString(R.styleable.RoundedIconButton_button_label);
        int buttonIcon = typedArray.getResourceId(R.styleable.RoundedIconButton_button_icon, 0);
        int buttonGravity = typedArray.getInt(R.styleable.RoundedIconButton_gravity, Gravity.CENTER);
        int buttonB = typedArray.getResourceId(R.styleable.RoundedIconButton_button_background, R.drawable.background_rounded_button);
        int buttonTint = typedArray.getColor(R.styleable.RoundedIconButton_button_tint, getResources().getColor(R.color.background_white));
        int typeFaceId = typedArray.getResourceId(R.styleable.RoundedIconButton_font, R.font.montserratbold);
        int textColor = typedArray.getColor(R.styleable.RoundedIconButton_textColor, getResources().getColor(R.color.text600));
        typedArray.recycle();

        buttonLabelTV = findViewById(R.id.label);
        buttonIconIV = findViewById(R.id.icon);
        buttonLabelTV.setText(buttonLabel);
        buttonLabelTV.setTextColor(textColor);
        buttonContainer = findViewById(R.id.container);
        buttonContainer.setGravity(buttonGravity);
        if (buttonIcon != 0) {
            buttonIconIV.setVisibility(VISIBLE);
            buttonIconIV.setImageResource(buttonIcon);
        }
        setBackground(buttonB);
        if (buttonTint != getResources().getColor(R.color.background_white)) {
            setBackgroundTint(buttonTint);
        } else {
            resetBackgroundTint();
        }
        Typeface typeface = ResourcesCompat.getFont(context, typeFaceId);
        buttonLabelTV.setTypeface(typeface);
    }

    public void setButtonLabel(String buttonLabel) {
        buttonLabelTV.setText(buttonLabel);
    }

    public void setButtonIcon(int buttonIcon) {
        if (buttonIcon == 0)
        {
            buttonIconIV.setVisibility(View.GONE);
        }
        else {
            buttonIconIV.setVisibility(View.VISIBLE);
        }
        buttonIconIV.setImageResource(buttonIcon);
    }

    public void setBackgroundCol(int color) {
        final GradientDrawable background = (GradientDrawable) buttonContainer.getBackground();
        background.setColor(getResources().getColor(color));
    }

    public void setBackgroundTint(int buttonTint) {
        ViewCompat.setBackgroundTintList(
                buttonContainer,
                ColorStateList.valueOf(buttonTint)
        );
    }

    public void resetBackgroundTint() {
        ViewCompat.setBackgroundTintList(
                buttonContainer,
                null
        );
    }

    public void setBackgroundColor(int color) {
        buttonContainer.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void setBackground(int background) {
        buttonContainer.setBackground(getContext().getDrawable(background));
    }

    private void animateCompletionView() {
        final GradientDrawable background = (GradientDrawable) buttonContainer.getBackground();
        ValueAnimator.ofObject(new ArgbEvaluator(), R.color.background_green);
        background.setColor(getResources().getColor(R.color.text50));
    }
}
