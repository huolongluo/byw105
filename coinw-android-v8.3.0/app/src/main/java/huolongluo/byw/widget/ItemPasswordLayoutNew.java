package huolongluo.byw.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import huolongluo.byw.R;


/**
 * Created by LS on 2018/6/28.
 */

public class ItemPasswordLayoutNew extends RelativeLayout {
    private EditText editText;
    public ImageView[] imageViews;//使用一个数组存储密码框
    public View[] views;
    private StringBuffer stringBuffer = new StringBuffer();//存储密码字符
    private int count = 6;
    private String strPassword;//密码字符串
    private TextView[] textViews;
    private char[] pwd;

    public ItemPasswordLayoutNew(Context context) {
        this(context,null);
    }

    public ItemPasswordLayoutNew(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ItemPasswordLayoutNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        imageViews = new ImageView[6];
        View view = View.inflate(context, R.layout.layout_password_new,this);
//        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/GOTHAM-LIGHT.TTF");

        editText = findViewById(R.id.item_edittext);
        textViews = new TextView[6];
        imageViews[0] = findViewById(R.id.item_password_iv1);
        imageViews[1] = findViewById(R.id.item_password_iv2);
        imageViews[2] = findViewById(R.id.item_password_iv3);
        imageViews[3] = findViewById(R.id.item_password_iv4);
        imageViews[4] = findViewById(R.id.item_password_iv5);
        imageViews[5] = findViewById(R.id.item_password_iv6);

        textViews[0] = findViewById(R.id.tv_pwd_item1);
        textViews[1] = findViewById(R.id.tv_pwd_item2);
        textViews[2] = findViewById(R.id.tv_pwd_item3);
        textViews[3] = findViewById(R.id.tv_pwd_item4);
        textViews[4] = findViewById(R.id.tv_pwd_item5);
        textViews[5] = findViewById(R.id.tv_pwd_item6);

        views = new View[6];
        views[0] = findViewById(R.id.view1);
        views[1] = findViewById(R.id.view2);
        views[2] = findViewById(R.id.view3);
        views[3] = findViewById(R.id.view4);
        views[4] = findViewById(R.id.view5);
        views[5] = findViewById(R.id.view6);
//        textViews[0].setTypeface(typeface);
//        textViews[1].setTypeface(typeface);
//        textViews[2].setTypeface(typeface);
//        textViews[3].setTypeface(typeface);
//        textViews[4].setTypeface(typeface);
//        textViews[5].setTypeface(typeface);

        editText.setCursorVisible(false);//将光标隐藏
        setListener();
    }

    private void setListener() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (stringBuffer.length() > 6){
                    stringBuffer.deleteCharAt(stringBuffer.length()-1);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (stringBuffer.length() > 6){
                    stringBuffer.deleteCharAt(stringBuffer.length()-1);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //重点   如果字符不为""时才进行操作
                if (!editable.toString().equals("")) {
//                    if (editable.toString().length() <= 6) {
                    if (stringBuffer.length() > 6) {
                        //当密码长度大于5位时edittext置空
                        editText.setText("");
                        stringBuffer.deleteCharAt(stringBuffer.length()-1);
                        return;
                    } else {
//                        if (editable.toString().length()>6){
//
//                        }else {
                        //将文字添加到StringBuffer中
                        stringBuffer.append(editable);
                        editText.setText("");//添加后将EditText置空  造成没有文字输入的错局
                        Log.e("TAG", "afterTextChanged: stringBuffer is " + stringBuffer);
                        count = stringBuffer.length();//记录stringbuffer的长度
                        strPassword = stringBuffer.toString();
                        Log.e("TAG", "afterTextChanged: strPassword is " + strPassword);
                        pwd = stringBuffer.toString().toCharArray();//将字符串转为字符数组

                        if (stringBuffer.length() == 6) {
                            //文字长度位6   则调用完成输入的监听
//                            editText.setInputType(InputType.TYPE_NULL);
                            if (inputCompleteListener != null) {
                                inputCompleteListener.inputComplete();
                            }
                        }
                        for (int i = 0; i < stringBuffer.length(); i++) {
                            if (stringBuffer.length() > 6) {
//                                editText.setFocusable(false);
                            } else {
                                imageViews[i].setImageResource(R.mipmap.ic_circle_white);
                                textViews[i].setText(pwd[i] + "");
                                views[i].setBackgroundColor(getResources().getColor(R.color.color_A0A0A0));
                            }
                        }
                    }
                }
//                }
//                }
            }
        });
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
//                    Log.e("TAG", "afterTextChanged: stringBuffer is "+stringBuffer);
                    if (onKeyDelete()) return true;
                    return true;
                }
                return false;
            }
        });
    }

    public void clearText(){

        stringBuffer.delete(0,stringBuffer.length());
        for (int i =0;i<6;i++){
            imageViews[i].setImageResource(R.mipmap.ic_circle_no_pwd);
            textViews[i].setText("");
            views[i].setBackgroundColor(getResources().getColor(R.color.color_A0A0A0));
        }
    }

    public boolean onKeyDelete() {
        if (count==0){
            count = 6;
            return true;
        }
        if (stringBuffer.length()>0){
            //删除相应位置的字符
            stringBuffer.delete((count-1),count);
            count--;
            Log.e("TAG", "afterTextChanged: stringBuffer is "+stringBuffer);
            strPassword = stringBuffer.toString();
//            imageViews[stringBuffer.length()].setImageResource(R.drawable.ic_circle_no_pwd);
            textViews[stringBuffer.length()].setText("");
            views[stringBuffer.length()].setBackgroundColor(getResources().getColor(R.color.color_A0A0A0));

        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    private InputCompleteListener inputCompleteListener;

    public void setInputCompleteListener(InputCompleteListener inputCompleteListener) {
        this.inputCompleteListener = inputCompleteListener;
    }

    public interface InputCompleteListener{
        void inputComplete();
    }

    public EditText getEditText() {
        return editText;
    }

    /**
     * 获取密码
     * @return
     */
    public String getStrPassword() {
        return strPassword;
    }

    public void setContent(String content){
        editText.setText(content);
    }
}