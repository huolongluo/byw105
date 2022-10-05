package cn.baymax.android.keyboard;

import android.content.Context;
import android.text.Editable;

import java.util.List;

/**
 * Created by xud on 2017/3/2.
 */

public class NumberKeyboard extends BaseKeyboard {

    public static final int DEFAULT_NUMBER_XML_LAYOUT = R.xml.keyboard_number;

    private boolean enableDotInput = true;

    public ActionDoneClickListener mActionDoneClickListener;

    public NumberKeyboard(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }

    public NumberKeyboard(Context context, int xmlLayoutResId, int modeId, int width, int height) {
        super(context, xmlLayoutResId, modeId, width, height);
    }

    public NumberKeyboard(Context context, int xmlLayoutResId, int modeId) {
        super(context, xmlLayoutResId, modeId);
    }

    public NumberKeyboard(Context context, int layoutTemplateResId, CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }

    public void setActionDoneClickListener(ActionDoneClickListener actionDoneClickListener) {
        mActionDoneClickListener = actionDoneClickListener;
    }

    public void setEnableDotInput(boolean enableDotInput) {
        this.enableDotInput = enableDotInput;
    }


    public void aaa(String ss) {
        KeyStyle keyStyle = getKeyStyle();
        List<Key> keyList = getKeys();

        for (Key key : keyList) {
            if (key.label != null && key.label.toString() != null) {
                if (key.label.toString().equals(mContext.getString(R.string.buy_d)) || key.label.toString().equals(mContext.getString(R.string.sell_d))) {
                    key.label = ss;
                    // key.iconPreview=mContext.getResources().getDrawable(R.drawable.trade_bg1);
                }

            }


        }


    }


    @Override
    public boolean handleSpecialKey(int primaryCode, int[] keyCodes) {
        Editable editable = getEditText().getText();
        int start = getEditText().getSelectionStart();
        //小数点
        if (primaryCode == 46) {
            if (!enableDotInput) {
                return true;
            }
            if (!editable.toString().contains(".")) {
                if (!editable.toString().startsWith(".")) {
                    editable.insert(start, Character.toString((char) primaryCode));
                } else {
                    editable.insert(start, "0" + (char) primaryCode);
                }
            }
            return true;
        }
        if (primaryCode == -4) {
            if (mActionDoneClickListener != null) {
                mActionDoneClickListener.onActionDone("false");
            } else {
                hideKeyboard();
            }

            return true;
        } else if (primaryCode == -41) {
            if (mActionDoneClickListener != null) {
                mActionDoneClickListener.onActionDone("true");
            } else {
                hideKeyboard();
            }
            return true;
        }
        return false;
    }

    public interface ActionDoneClickListener {
        void onActionDone(CharSequence charSequence);
    }
}
