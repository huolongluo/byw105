package huolongluo.byw.util.pwd;
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
//密码工具类
public class PwdUtils {
    public static final int MATCH_MAX_NUM=5;
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
                tvIntensity.setTextColor(ContextCompat.getColor(context,R.color.color_3DDD94));
                ivPwd1.setBackgroundColor(ContextCompat.getColor(context,R.color.color_3DDD94));
                ivPwd2.setBackgroundColor(ContextCompat.getColor(context,R.color.color_3DDD94));
                ivPwd3.setBackgroundColor(ContextCompat.getColor(context,R.color.color_3DDD94));
            }
            else if(checker.getMatchNum(pwd)>=2){//强度为中
                tvIntensity.setText(context.getString(R.string.middle));
                tvIntensity.setTextColor(ContextCompat.getColor(context,R.color.color_FFA800));
                ivPwd1.setBackgroundColor(ContextCompat.getColor(context,R.color.color_FFA800));
                ivPwd2.setBackgroundColor(ContextCompat.getColor(context,R.color.color_FFA800));
                ivPwd3.setBackgroundColor(ContextCompat.getColor(context,R.color.color_DBD9E2));
            }
            else{
                tvIntensity.setText(context.getString(R.string.weak));
                tvIntensity.setTextColor(ContextCompat.getColor(context,R.color.color_FF5857));
                ivPwd1.setBackgroundColor(ContextCompat.getColor(context,R.color.color_FF5857));
                ivPwd2.setBackgroundColor(ContextCompat.getColor(context,R.color.color_DBD9E2));
                ivPwd3.setBackgroundColor(ContextCompat.getColor(context,R.color.color_DBD9E2));
            }
        }
    }
    //统一处理密码规则的匹配和提示文案
    public static boolean isPwdRuleMatch(String pwd, PasswordChecker checker){
        if(!checker.isLength(pwd)){
            ToastUtils.showShortToast(R.string.pwd_rule_length);
            return false;
        }
        if(!checker.isContainNumber(pwd)){
            ToastUtils.showShortToast(R.string.pwd_rule_contain_number);
            return false;
        }
        if(!checker.isContainLowerCase(pwd)){
            ToastUtils.showShortToast(R.string.pwd_rule_contain_lower);
            return false;
        }
        if(!checker.isContainUpperCase(pwd)){
            ToastUtils.showShortToast(R.string.pwd_rule_contain_upper);
            return false;
        }
        return true;
    }
    //统一处理密码匹配和提示,（未处理交易密码）
    public static boolean isPwdMatch(String pwd, PasswordChecker checker){
        if(TextUtils.isEmpty(pwd)){
            ToastUtils.showShortToast(R.string.pwd_empty);
            return false;
        }
        if(!isPwdRuleMatch(pwd,checker)){
            return false;
        }
        return true;
    }
}
