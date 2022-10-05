package com.android.legend.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.blankj.utilcodes.utils.ToastUtils;

import huolongluo.byw.R;
import huolongluo.byw.util.PasswordChecker;
import huolongluo.byw.util.StringUtil;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
//输入验证的工具类
public class InputUtil {
    public static final int MATCH_MAX_NUM=5;
    /**
     * 验证手机号输入规则
     * @param phone
     * @param tvError 显示输入框下方的错误提示,需要显示toast时该值传空或使用重载，该值不为空则不显示toast
     * @return false 不符合规则
     */
    public static boolean checkInputPhone(Context context, String phone, TextView tvError){
//        if(TextUtils.isEmpty(phone)){
//            showError(context,context.getResources().getString(R.string.ple_in_pho),tvError);
//            return false;
//        }
//        if(!StringUtil.judgePhoneNums(phone)){
//            showError(context.getResources().getString(R.string.input_correct_phone),tvError);
//            return false;
//        }
        clearError(tvError);//输入框下方若显示了错误提示，规则符合需要清除该文案
        return true;
    }
    public static boolean checkInputEmail(Context context, String email, TextView tvError){
        if(TextUtils.isEmpty(email)){
//            showError(context,context.getResources().getString(R.string.ple_in_ema),tvError);
            clearError(tvError);
            return false;
        }
        if(!StringUtil.isEmail(email)){
            showError(context,context.getResources().getString(R.string.aa67),tvError);
            return false;
        }
        clearError(tvError);
        return true;
    }

    /**
     * 第一个密码输入框的验证,
     * @param pwd
     * @param tvError 可为null 显示输入框下方的错误提示,需要显示toast时该值传空或使用重载，该值不为空则不显示toast
     * @return
     */
    public static boolean checkInputPwd(Context context, String pwd, PasswordChecker checker, TextView tvError){
        if(!checkInputPwdIsNotEmpty(context,pwd,tvError)){
            return false;
        }
        if(!checker.isLength(pwd)){
            showError(context,context.getResources().getString(R.string.pwd_rule_length),tvError);
            return false;
        }
        if(!checker.isContainNumber(pwd)){
            showError(context,context.getResources().getString(R.string.pwd_rule_contain_number),tvError);
            return false;
        }
        if(!checker.isContainLowerCase(pwd)){
            showError(context,context.getResources().getString(R.string.pwd_rule_contain_lower),tvError);
            return false;
        }
        if(!checker.isContainUpperCase(pwd)){
            showError(context,context.getResources().getString(R.string.pwd_rule_contain_upper),tvError);
            return false;
        }
        clearError(tvError);
        return true;
    }
    //第二个密码输入框的验证
    public static boolean checkInputPwd(Context context, String pwd, String pwd2, PasswordChecker checker, TextView tvError){
        if(!TextUtils.equals(pwd,pwd2)){
            showError(context,context.getResources().getString(R.string.two_pwd_different),tvError);
            return false;
        }
        clearError(tvError);
        return true;
    }
    //只验证密码是否不为空
    public static boolean checkInputPwdIsNotEmpty(Context context, String pwd, TextView tvError){
        if(TextUtils.isEmpty(pwd)){
            showError(context,context.getResources().getString(R.string.pwd_empty),tvError);
            return false;
        }
        return true;
    }

//    public static boolean checkInviteCode(Context context, String code, TextView tvError){
//        if(!TextUtils.isEmpty(code) && !code.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])[0-9a-zA-Z]{8}$")){
//            showError(context,context.getResources().getString(R.string.input_invite_code_format),tvError);
//            return false;
//        }
//        clearError(tvError);
//        return true;
//    }

    /**
     * 根据type检查验证码的输入
//     * @param type SafeVerifyBottomDialogFragmentKt内的常量，暂时支持手机 邮箱和google的验证码
     * @return
     */
//    public static boolean checkInputCode(Context context, String code, int type){
//        String err="";
//        if(TextUtils.isEmpty(code)){
//            if(type== SafeVerifyBottomDialogFragmentKt.TYPE_PHONE){
//                err=context.getString(R.string.input_code_phone);
//            }else if(type== SafeVerifyBottomDialogFragmentKt.TYPE_EMAIL){
//                err=context.getString(R.string.input_code_email);
//            }else if(type== SafeVerifyBottomDialogFragmentKt.TYPE_GOOGLE){
//                err=context.getString(R.string.input_code_google);
//            }
//            ToastUtils.showShortToast(context,err);
//            return false;
//        }
//
//        return true;
//    }
    private static void showError(Context context, String error, TextView tvError){
        if(tvError==null){
            SnackBarUtils.ShowRed((Activity) context,error);
        }else{
            tvError.setText(error);
        }
    }
    private static void clearError(TextView tvError){
        if(tvError!=null){
            tvError.setText("");
        }
    }
    /**
     * 统一根据密码输入刷新强度ui
     * @param etPwd 密码输入框
     * @param clIntensity 强度的顶层布局，控制是否显示
     * @param tvIntensity 弱中强的文本
     * @param ivPwd1
     * @param ivPwd2
     * @param ivPwd3
     * @param checker 弱中强的验证工具对象
     */
    public static void refreshIntensity(Context context, EditText etPwd, ConstraintLayout clIntensity, TextView tvIntensity,
                                        ImageView ivPwd1, ImageView ivPwd2, ImageView ivPwd3, PasswordChecker checker) {
        String pwd=etPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            clIntensity.setVisibility(View.INVISIBLE);//无输入不显示
        } else {
            clIntensity.setVisibility(View.VISIBLE);
            if(checker.getMatchNum(pwd)>=MATCH_MAX_NUM){//强度为强
                tvIntensity.setText(context.getString(R.string.strong));
                ivPwd1.setBackgroundColor(ContextCompat.getColor(context,R.color.accent_main));
                ivPwd2.setBackgroundColor(ContextCompat.getColor(context,R.color.accent_main));
                ivPwd3.setBackgroundColor(ContextCompat.getColor(context,R.color.accent_main));
            }
            else if(checker.getMatchNum(pwd)>=2){//强度为中
                tvIntensity.setText(context.getString(R.string.middle));
                ivPwd1.setBackgroundColor(ContextCompat.getColor(context,R.color.accent_main));
                ivPwd2.setBackgroundColor(ContextCompat.getColor(context,R.color.accent_main));
                ivPwd3.setBackgroundColor(ContextCompat.getColor(context,R.color.color_1a000000));
            }
            else{
                tvIntensity.setText(context.getString(R.string.weak));
                ivPwd1.setBackgroundColor(ContextCompat.getColor(context,R.color.accent_main));
                ivPwd2.setBackgroundColor(ContextCompat.getColor(context,R.color.color_1a000000));
                ivPwd3.setBackgroundColor(ContextCompat.getColor(context,R.color.color_1a000000));
            }
        }
    }
}
