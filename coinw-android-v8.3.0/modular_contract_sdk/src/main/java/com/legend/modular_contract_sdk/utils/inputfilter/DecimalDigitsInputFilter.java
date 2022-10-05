package com.legend.modular_contract_sdk.utils.inputfilter;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
/**
 * Created by Administrator on 2017/11/1.
 */

public class DecimalDigitsInputFilter implements InputFilter {
    Pattern mPattern;
    private int mDigitsAfterZero = 0;

    public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        try {
            mDigitsAfterZero = digitsAfterZero;
            String regex = String.format("[0-9]{0,%d}+(\\.[0-9]{0,%d})?", digitsBeforeZero, digitsAfterZero);
            mPattern = Pattern.compile(regex);
        }catch (PatternSyntaxException e){
            e.printStackTrace();
        }
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        // 小数位限制0位时，小数点输入直接清除
        if (mDigitsAfterZero == 0 && ".".equals(source.toString())) {
            return "";
        }
        //直接输入"."返回"0."
        //".x"删除"x"输出为"."，inputFilter无法处理成"0."，所以只处理直接输入"."的case
        if (".".equals(source) && "".equals(dest.toString())) {
            return "0.";
        }
        StringBuilder builder = new StringBuilder(dest);
        if ("".equals(source)) {
            builder.replace(dstart, dend, "");
        } else {
            builder.insert(dstart, source);
        }
        String resultTemp = builder.toString();
        //判断修改后的数字是否满足小数格式，不满足则返回 "",不允许修改
        if(mPattern==null){
            return "";
        }
        Matcher matcher = mPattern.matcher(resultTemp);
        if (!matcher.matches()) {
            return "";
        }
        return null;
    }

}
