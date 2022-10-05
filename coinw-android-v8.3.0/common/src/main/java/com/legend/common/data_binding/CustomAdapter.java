package com.legend.common.data_binding;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;

import com.google.android.material.tabs.TabLayout;
import com.legend.common.R;
import com.legend.common.util.ThemeUtil;
import com.legend.common.util.TypefaceUtil;

public class CustomAdapter {

    //设置等宽字体 typeface 传 TypefaceUtil里定义的常量
    @BindingAdapter({"localTypeface"})
    public static void setTypeface(TextView textView, String typeface) {
        TypefaceUtil.INSTANCE.setTypeface(textView.getContext(), textView, typeface);
    }

    //设置等宽字体 typeface 传 TypefaceUtil里定义的常量
    @BindingAdapter({"underscore"})
    public static void setUnderscore(TextView textView, boolean isUnderscore) {
        if (isUnderscore){
            textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        }
    }

    // 设置文本涨跌色 isUp=true为涨
    @BindingAdapter({"is_up"})
    public static void setTextTickerColor(TextView textView, boolean isUp) {
        int color = ThemeUtil.INSTANCE.getThemeColor(textView.getContext(), isUp ? R.attr.up_color : R.attr.drop_color);
        textView.setTextColor(color);
    }

    @BindingAdapter({"theme_textColor"})
    public static void setTextColor(TextView textView,int attrId){
        textView.setTextColor(ThemeUtil.INSTANCE.getThemeColor(textView.getContext(), attrId));
    }

    @BindingAdapter({"theme_hintColor"})
    public static void setTextHintColor(EditText textView, int attrId){
        textView.setHintTextColor(ThemeUtil.INSTANCE.getThemeColor(textView.getContext(), attrId));
    }

    @BindingAdapter({"theme_textColorStateList"})
    public static void setTextColorStateList(TextView textView,int attrId){
        textView.setTextColor(ThemeUtil.INSTANCE.getThemeColorStateList(textView.getContext(), attrId));
    }

    @BindingAdapter({"theme_background"})
    public static void setBackgroundDrawable(View view, int attrId){
        view.setBackground(ThemeUtil.INSTANCE.getThemeDrawable(view.getContext(), attrId));
    }

    @BindingAdapter({"theme_background"})
    public static void setBackgroundDrawable(View view, Drawable drawable){
        view.setBackground(drawable);
    }

    @BindingAdapter({"theme_backgroundColor"})
    public static void setBackgroundColor(View view, int attrId){
        view.setBackgroundColor(ThemeUtil.INSTANCE.getThemeColor(view.getContext(), attrId));
    }

    @BindingAdapter({"theme_src"})
    public static void setImageSrc(ImageView view, int attrId){
        view.setImageDrawable(ThemeUtil.INSTANCE.getThemeDrawable(view.getContext(), attrId));
    }
    @BindingAdapter("android:layout_marginTop")
    public static void setTopMargin(View view, int topMargin) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, topMargin,
                layoutParams.rightMargin,layoutParams.bottomMargin);
        view.setLayoutParams(layoutParams);
    }
}