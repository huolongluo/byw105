package com.android.coinw.utils;
import android.content.res.ColorStateList;
import android.graphics.Color;
public class ColorStateListUtils {
    public static ColorStateList getColorStateList(String selected, String pressed, String normal) {
        int[] colors = new int[]{Color.parseColor(selected), Color.parseColor(pressed), Color.parseColor(normal)};
        int[][] states = new int[3][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{android.R.attr.state_pressed};
        states[2] = new int[]{};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }

    public static ColorStateList getColor(int id) {
        int[] colors = new int[]{id, id, id};
        int[][] states = new int[3][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{android.R.attr.state_pressed};
        states[2] = new int[]{};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }
}
