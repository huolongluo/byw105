package huolongluo.byw.util;
import android.text.TextUtils;

import java.util.LinkedHashSet;
import java.util.Set;
/**
 * 密码验证规则
 */
public class PasswordChecker {
    //8--32位，包含大小写字母、数字和特殊符号
    private boolean upperCase = true; // 包含大写字母
    private boolean lowerCase = true; // 包含小写字母
    private boolean letter = false; // 包含字母
    private boolean digit = true; // 包含数字
    private boolean special = false; // 包含特殊字符
    private Set<Character> specialCharSet = null; // 特殊字符集合
    private int minLength = 8; // 最小长度
    private int maxLength = 32; // 最大长度

    public PasswordChecker() {
        this.specialCharSet = defaultSpecialCharSet();
    }

    /**
     * 密码符合规则，返回true
     */
    public boolean check(String password) {
        if (password == null || password.length() < this.minLength || password.length() > this.maxLength) {
            // 长度不符合
            return false;
        }
        boolean containUpperCase = false;
        boolean containLowerCase = false;
        boolean containLetter = false;
        boolean containDigit = false;
        boolean containSpecial = false;
        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                containUpperCase = true;
                containLetter = true;
            } else if (Character.isLowerCase(ch)) {
                containLowerCase = true;
                containLetter = true;
            } else if (Character.isDigit(ch)) {
                containDigit = true;
            } else if (this.specialCharSet.contains(ch)) {
                containSpecial = true;
            } else {
                // 非法字符
                return false;
            }
        }
        if (this.upperCase && !containUpperCase) {
            return false;
        }
        if (this.lowerCase && !containLowerCase) {
            return false;
        }
        if (this.letter && !containLetter) {
            return false;
        }
        if (this.digit && !containDigit) {
            return false;
        }
        if (this.special && !containSpecial) {
            return false;
        }
        return true;
    }
    public boolean isLength(String password){
        if (password == null || password.length() < this.minLength || password.length() > this.maxLength) {
            return false;
        }
        return true;
    }
    public boolean isContainNumber(String password){
        boolean containDigit = false;
        for (char ch : password.toCharArray()) {
            if (Character.isDigit(ch)) {
                containDigit = true;
            }
        }
        return containDigit;
    }
    public boolean isContainLowerCase(String password){
        boolean containLowerCase = false;
        for (char ch : password.toCharArray()) {
            if (Character.isLowerCase(ch)) {
                containLowerCase = true;
            }
        }
        return containLowerCase;
    }
    public boolean isContainUpperCase(String password){
        boolean containUpperCase = false;
        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                containUpperCase = true;
            }
        }
        return containUpperCase;
    }

    //获取输入的密码满足条件的数量，最大为5
    public int getMatchNum(String password){
        int matchNum=0;
        if(TextUtils.isEmpty(password)||password.length()==0){
            return matchNum;
        }
        boolean isLength=false;
        boolean containUpperCase = false;
        boolean containLowerCase = false;
        boolean containDigit = false;
        boolean containSpecial = false;


        if(password.length()>=minLength){
            isLength=true;
        }
        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                containUpperCase = true;
            } else if (Character.isLowerCase(ch)) {
                containLowerCase = true;
            } else if (Character.isDigit(ch)) {
                containDigit = true;
            } else if (this.specialCharSet.contains(ch)) {
                containSpecial = true;
            }
        }
        if(isLength){
            matchNum++;
        }
        if(containUpperCase){
            matchNum++;
        }
        if(containLowerCase){
            matchNum++;
        }
        if(containDigit){
            matchNum++;
        }
        if(containSpecial){
            matchNum++;
        }
        return matchNum;
    }

    public static Set<Character> defaultSpecialCharSet() {
        Set<Character> specialChars = new LinkedHashSet<>();
        // 键盘上能找到的符号
        specialChars.add(Character.valueOf('~'));
        specialChars.add(Character.valueOf('`'));
        specialChars.add(Character.valueOf('!'));
        specialChars.add(Character.valueOf('@'));
        specialChars.add(Character.valueOf('#'));
        specialChars.add(Character.valueOf('$'));
        specialChars.add(Character.valueOf('%'));
        specialChars.add(Character.valueOf('^'));
        specialChars.add(Character.valueOf('&'));
        specialChars.add(Character.valueOf('*'));
        specialChars.add(Character.valueOf('('));
        specialChars.add(Character.valueOf(')'));
        specialChars.add(Character.valueOf('-'));
        specialChars.add(Character.valueOf('_'));
        specialChars.add(Character.valueOf('+'));
        specialChars.add(Character.valueOf('='));
        specialChars.add(Character.valueOf('{'));
        specialChars.add(Character.valueOf('['));
        specialChars.add(Character.valueOf('}'));
        specialChars.add(Character.valueOf(']'));
        specialChars.add(Character.valueOf('|'));
        specialChars.add(Character.valueOf('\\'));
        specialChars.add(Character.valueOf(':'));
        specialChars.add(Character.valueOf(';'));
        specialChars.add(Character.valueOf('"'));
        specialChars.add(Character.valueOf('\''));
        specialChars.add(Character.valueOf('<'));
        specialChars.add(Character.valueOf(','));
        specialChars.add(Character.valueOf('>'));
        specialChars.add(Character.valueOf('.'));
        specialChars.add(Character.valueOf('?'));
        specialChars.add(Character.valueOf('/'));
        return specialChars;
    }
}
