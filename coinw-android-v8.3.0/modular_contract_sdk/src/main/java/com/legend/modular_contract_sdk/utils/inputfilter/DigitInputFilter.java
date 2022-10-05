package com.legend.modular_contract_sdk.utils.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by Administrator on 2017/11/1.
 */

public class DigitInputFilter implements InputFilter {
    int KEEP_DIGIT;
    Function function;

    public DigitInputFilter(int KEEP_DIGIT) {
        this.KEEP_DIGIT = KEEP_DIGIT;
    }

    public DigitInputFilter(Function function) {
        this.function = function;
    }

    public int getDigit() {
        if (function != null) {
            return function.get();
        } else {
            return KEEP_DIGIT;
        }
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
        int dstart, int dend) {
        // source:当前输入的字符
        // start:输入字符的开始位置
        // end:输入字符的结束位置
        // dest：当前已显示的内容
        // dstart:当前光标开始位置
        // dent:当前光标结束位置
        if ("".equals(source.toString())) {
            return null;
        }

        StringBuilder SB = new StringBuilder(dest);
        SB.replace(dstart, dend, source.toString());

        if (SB.indexOf(".") >= 0 && getDigit() == 0) {
            return "";
        }

        if (dest.length() == 0 && source.equals(".")) {
            return "0.";
        }
        String dValue = dest.toString();
        String[] splitArray = dValue.split("\\.");
        int pointIndex = dValue.indexOf('.');
        if (pointIndex == -1 || dstart <= pointIndex) {
            return null;
        }
        if (splitArray.length > 1) {
            String dotValue = splitArray[1];
            int diff = dotValue.length() + 1 - getDigit();
            int newEnd = end - diff;
            if (diff > 0 &&
                source.length() > newEnd &&
                (newEnd >= 0)) {
                return source.subSequence(start, newEnd);
            }
        }
        return null;
    }

    public interface Function {
        int get();
    }
}
