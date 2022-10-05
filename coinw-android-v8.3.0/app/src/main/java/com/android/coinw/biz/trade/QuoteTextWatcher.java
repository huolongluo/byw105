package com.android.coinw.biz.trade;

import android.text.Editable;
import android.text.TextWatcher;

public class QuoteTextWatcher implements TextWatcher {
    private QuoteInputView input;
    public QuoteTextWatcher(QuoteInputView input){
        this.input=input;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public  void onTextChanged(CharSequence s, int start, int before, int count) {
        String str=s.toString();
        //不能输入00  01这类
        if(str.length()>=2&&str.substring(0,1).equals("0")){
            try {
                int num=Integer.parseInt(str.substring(0,2));
                if(num>=0){
                    input.setText(str.substring(1));
                    input.setSelection(input.getText().length());
                }
            }catch (Exception e){

            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
