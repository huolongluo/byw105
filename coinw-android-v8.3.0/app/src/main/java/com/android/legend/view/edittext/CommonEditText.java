package com.android.legend.view.edittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import huolongluo.byw.R;
import huolongluo.byw.log.Logger;
import huolongluo.byw.util.Util;
/**
 * 带眼睛的edittext,有其他需求可自己拓展
 */
public class CommonEditText extends RelativeLayout {
    private static final String TAG = "CommonEditText";
    private Context context;
    private RelativeLayout rlt;
    private EditText etPwd;
    private ImageView ivEye;
    private CommonEditTextListener listener;
    private boolean isHideEye=false;

    public CommonEditText(Context context) {
        this(context,null);
    }

    public CommonEditText(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CommonEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        View view= LayoutInflater.from(context).inflate(R.layout.widget_common_edittext,this);
        initView(view);
        initAttrs(context, attrs, defStyleAttr);
    }
    public void setListener(CommonEditTextListener listener){
        this.listener=listener;
    }
    private void initView(View view){
        rlt = view.findViewById(R.id.rlt);
        etPwd = view.findViewById(R.id.etPwd);
        ivEye = view.findViewById(R.id.ivEye);

        ivEye.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivEye.isSelected()) {//隐藏
                    etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                ivEye.setSelected(!ivEye.isSelected());
                etPwd.setSelection(etPwd.getText().length());
            }
        });
        etPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!isHideEye){
                    if (TextUtils.isEmpty(s)) {
                        ivEye.setVisibility(GONE);
                    } else {
                        ivEye.setVisibility(VISIBLE);
                    }
                }
                if(listener!=null){
                    listener.onAfterTextChanged(s);
                }
            }
        });
    }
    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonEditText, defStyleAttr, 0);
        int indexCount = typedArray.getIndexCount();
        try {
            for (int i = 0; i < indexCount; i++) {
                initAttr(typedArray.getIndex(i), typedArray);
            }
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
        typedArray.recycle();
    }
    private void initAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.CommonEditText_widget_margin_horizontal) {//设置水平间距
            setMarginHorizontal(typedArray.getDimensionPixelSize(attr, Util.dp2px(context,26)));
        } else if (attr == R.styleable.CommonEditText_widget_textSize) {//设置edittext文本大小
            etPwd.setTextSize(TypedValue.COMPLEX_UNIT_PX,typedArray.getDimensionPixelSize(attr,Util.sp2px(13)));
        } else if (attr == R.styleable.CommonEditText_widget_hint) {//设置edittext文本hint
            etPwd.setHint(typedArray.getResourceId(attr,0));
        }
    }
    private void setMarginHorizontal(int margin){
        Logger.getInstance().debug(TAG,"CommonEditText_widget_margin_horizontal margin:"+margin);
        LayoutParams params= (LayoutParams) rlt.getLayoutParams();
        params.leftMargin= margin;
        params.rightMargin=margin;
        rlt.setLayoutParams(params);
    }
    public Editable getText(){
        return etPwd.getText();
    }
    public EditText getEt(){
        return etPwd;
    }
    public void hideEye(){
        isHideEye=true;
    }
    public void clearText(){
        etPwd.setText("");
    }

    public interface CommonEditTextListener{
        void onAfterTextChanged(Editable s);//输入框文本改变的回调
    }
}
