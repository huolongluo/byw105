package huolongluo.byw.util;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.mob.tools.utils.ResHelper;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import huolongluo.byw.log.Logger;
/**
 * Created by SLAN on 2016/11/28.
 */

public class StringUtil
{
    public static Spanned fromHtml(String str){
        if(TextUtils.isEmpty(str)){
            str="";
        }
        if(android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.N){
            return Html.fromHtml(str,Html.FROM_HTML_MODE_LEGACY);
        }else{
            return Html.fromHtml(str);
        }
    }
    //返回脱敏带有星号的文本
    public static String getHideStr(String str, int start, int end) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        String result="";
        String temp = "***********************************************";
        try {
            if (str.length() < end) {
                return str.substring(0, start) + temp.substring(0, (str.length() - start));
            }
            result= str.substring(0, start) + temp.substring(0, (end - start)) + str.substring(end);
        }catch (Exception e){
            e.printStackTrace();
            return temp.substring(0,end)+str.substring(end);
        }
        return result;
    }
    //统一获取脱敏的手机号
    public static String getHidePhone(String phone){
        return getHideStr(phone,3,7);
    }
    public static String getHideEmail(String email){
        int end=email.indexOf("@");
        return getHideStr(email,3,end);
    }
    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    public static boolean judgePhoneNums(String phoneNums)
    {
        return isMatchLength(phoneNums, 11) && isMobileNO(phoneNums);
    }
    public static boolean isEmail(String email){
        if (null==email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        String str = "^([a-zA-Z0-9_./d]+)@([a-zA-Z0-9_./d]+)$";
        Pattern p =  Pattern.compile(str);//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }


    public void isPhone(String phoneNums){
     char[] num=   phoneNums.toCharArray();
     for(char n:num){

     }
    }
    /**
     * 判断一个字符串的位数
     *
     * @param str
     * @param length
     * @return
     */
    private static boolean isMatchLength(String str, int length)
    {
        return !str.isEmpty() && str.length() == length;
    }

    /**
     * 验证手机格式
     */
    private static boolean isMobileNO(String mobileNums)
    {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String telRegex = "[1234567890][1234567890]\\d{9}";//
        // "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        return !TextUtils.isEmpty(mobileNums) && mobileNums.matches(telRegex);
    }

    public static int getLengthForInputStr(String inputStr)
    {

        int orignLen = inputStr.length();

        int resultLen = 0;

        String temp = null;

        for (int i = 0; i < orignLen; i++)
        {

            temp = inputStr.substring(i, i + 1);
            // 3 bytes to indicate chinese word,1 byte to indicate english word ,in utf-8 encode
            if (temp.getBytes(StandardCharsets.UTF_8).length == 3)
            {
                resultLen += 2;
            }
            else
            {
                resultLen++;
            }
        }
        return resultLen;
    }

    public static SpannableString getStringByColor(Context ctx, CharSequence content, @ColorRes int color) {
        if (content == null) {
            content = "";
        }
        SpannableString ssb = new SpannableString(content);
        ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(ctx, color)), 0, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    public static SpannableString getStringByColorValue(CharSequence content, int color) {
        if (content == null) {
            content = "";
        }
        SpannableString ssb = new SpannableString(content);
        ssb.setSpan(new ForegroundColorSpan(color), 0, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    public static SpannableString getStringBySize(CharSequence content, float size) {
        if (content == null) {
            content = "";
        }
        SpannableString ssb = new SpannableString(content);
        ssb.setSpan(new RelativeSizeSpan(size), 0, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    public static SpannableString getStringByImage(Context context, @DrawableRes int id) {
        String content = "$";
        SpannableString ssb = new SpannableString(content);
        ssb.setSpan(new ImageSpan(context, id, ImageSpan.ALIGN_BASELINE), 0, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ssb.setSpan(new VerticalCenterImageSpan(context, id), 0, content.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    public static SpannableString changePartTextSize(String text, int start, int end,int sizeSp){
        SpannableString span=new SpannableString(text);
        span.setSpan(new AbsoluteSizeSpan(Util.sp2px(sizeSp)),start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }
}

