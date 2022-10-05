package huolongluo.byw.util.noru;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by hy on 2018/8/8 0008.
 * 通用工具类
 */

public class NorUtils {


    public static void copeText(Context content, String text) {

        try {
            ClipboardManager copy = (ClipboardManager) content.getSystemService(Context.CLIPBOARD_SERVICE);
            copy.setText(text);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public static String getClipboardText(Context content) {

        ClipboardManager mClipboardManager = (ClipboardManager) content.getSystemService(CLIPBOARD_SERVICE);

        ClipData mClipData = mClipboardManager.getPrimaryClip();
        //获取到内荣
        if (mClipData != null) {
            ClipData.Item item = mClipData.getItemAt(0);
            return item.getText()==null?"":item.getText().toString();
        }


        return " ";
    }


    /**
     * 描述：是否是邮箱.
     *
     * @param str 指定的字符串
     * @return 是否是邮箱:是为true，否则false
     */
    public static Boolean isEmail(String str) {
        Boolean isEmail = false;
        String expr = "^[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
        if (str.matches(expr)) {
            isEmail = true;
        }
        return isEmail;
    }


    private static DecimalFormat[] formatsArr1 = new DecimalFormat[20];

    public static DecimalFormat NumberFormat1(int place) {
        if (formatsArr1[place] == null) {


            StringBuffer s = new StringBuffer();
            s.append("#0");

            if (place > 0) {
                s.append(".");
            }
            for (int i = 0; i < place; i++) {
                s.append("0");
            }
            formatsArr1[place] = new DecimalFormat(s.toString());
        }
        return formatsArr1[place];
    }


    private static DecimalFormat[] formatsArr = new DecimalFormat[20];

    //默认RoundingMode.HALF_EVEN:整数位若是奇数则四舍五入，若是偶数则五舍六入
    public static DecimalFormat NumberFormat(int place) {
        if (formatsArr[place] == null) {


            StringBuffer s = new StringBuffer();
            s.append("0");

            if (place > 0) {
                s.append(".");
            }
            for (int i = 0; i < place; i++) {
                s.append("0");
            }
            formatsArr[place] = new DecimalFormat(s.toString());
        }
        return formatsArr[place];
    }

    private static DecimalFormat[] formatsMode = new DecimalFormat[20];
    public static DecimalFormat NumberFormat(int place,RoundingMode mode) {
        if (formatsMode[place] == null) {


            StringBuffer s = new StringBuffer();
            s.append("0");

            if (place > 0) {
                s.append(".");
            }
            for (int i = 0; i < place; i++) {
                s.append("0");
            }
            DecimalFormat format = new DecimalFormat(s.toString());
            format.setRoundingMode(mode);
            formatsMode[place] = format;
        }
        return formatsMode[place];
    }

    private static DecimalFormat[] formatsArrNO = new DecimalFormat[20];
    //向零方向舍入的舍入模式。从不对舍弃部分前面的数字加 1（即截尾）。注意，此舍入模式始终不会增加计算值的绝对值 即截断
    public static DecimalFormat NumberFormatNo(int place) {
        if (formatsArrNO[place] == null) {


            StringBuffer s = new StringBuffer();
            s.append("0");

            if (place > 0) {
                s.append(".");
            }
            for (int i = 0; i < place; i++) {
                s.append("#");
            }
            DecimalFormat format = new DecimalFormat(s.toString());
            format.setRoundingMode(RoundingMode.DOWN);
            formatsArrNO[place] = format;
        }
        return formatsArrNO[place];
    }

    public static DecimalFormat NumberFormatFString(String pattern) {


        DecimalFormat format = new DecimalFormat(pattern);
        return format;
    }


    public static DecimalFormat twoDotFormat = new DecimalFormat("#0.00");

    public static DecimalFormat getTwoDotFormat() {
        return twoDotFormat;
    }


    private static DecimalFormat[] formatsArrd = new DecimalFormat[]{null, null, null, null, null, null, null, null, null, null,null,null,null,null,null,null,null,null,null,null};

    public static DecimalFormat NumberFormatd(int place) {
        if (formatsArrd[place] == null) {


            StringBuffer s = new StringBuffer();
            s.append("0");

            if (place > 0) {
                s.append(".");
            }
            for (int i = 0; i < place; i++) {
                s.append("0");
            }

            DecimalFormat format = new DecimalFormat(s.toString());
            format.setRoundingMode(RoundingMode.DOWN);

            formatsArrd[place] = format;
        }
        //  formatsArr[place]. setRoundingMode(RoundingMode.DOWN)

        // DecimalFormat nf = new DecimalFormat("##.##");
        //   Log.e("formatsArrd0","=  "+formatsArrd[0]);
        //   Log.e("formatsArrd1","=  "+formatsArrd[place]);

        return formatsArrd[place];
    }

    public static int getDigits(String price) {
        int i = price.indexOf('.');
        if (i >= 0) {
            return price.length() - i - 1;
        }
        return 0;
    }
}
