package huolongluo.byw.util;

import android.text.TextUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Pattern;


public final class NumberUtil {
    

    public NumberUtil() {
        super();
    }


    public static int toInt(String str) {
        return toInt(str, 0);
    }


    public static int toInt(String str, int defaultValue) {
        if(str == null || str.length() == 0) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static long toLong(String str) {
        return toLong(str, 0L);
    }

    private static long toLong(String str, long defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static float toFloat(String str) {
        return toFloat(str, 0.0f);
    }


    private static float toFloat(String str, float defaultValue) {
      if (str == null) {
          return defaultValue;
      }     
      try {
          return Float.parseFloat(str);
      } catch (NumberFormatException nfe) {
          return defaultValue;
      }
    }

    public static double toDouble(String str) {
        return toDouble(str, 0.0d);
    }

    private static double toDouble(String str, double defaultValue) {
      if (str == null) {
          return defaultValue;
      }
      try {
          return Double.parseDouble(str);
      } catch (NumberFormatException nfe) {
          return defaultValue;
      }
    }

    public static Float createFloat(String str) {
        if (str == null) {
            return null;
        }
        return Float.valueOf(str);
    }

    public static Double createDouble(String str) {
        if (str == null) {
            return null;
        }
        return Double.valueOf(str);
    }


    public static Integer createInteger(String str) {
        if (str == null) {
            return null;
        }
        // decode() handles 0xAABD and 0777 (hex and octal) as well.
        return Integer.decode(str);
    }


    public static Long createLong(String str) {
        if (str == null) {
            return null;
        }
        return Long.valueOf(str);
    }


    public static BigInteger createBigInteger(String str) {
        if (str == null) {
            return null;
        }
        return new BigInteger(str);
    }


    public static BigDecimal createBigDecimal(String str) {
        if (str == null) {
            return null;
        }
        // handle JDK1.3.1 bug where "" throws IndexOutOfBoundsException
        if (TextUtils.isEmpty(str)) {
            throw new NumberFormatException("A blank string is not a valid number");
        }  
        return new BigDecimal(str);
    }

    public static boolean isNum(String str){

        Pattern pattern = Pattern.compile("^-?[0-9]+");
        //数字
//非数字
        return pattern.matcher(str).matches();
    }

    public static boolean isNum1(String str){
        //带小数的
        Pattern pattern = Pattern.compile("^[-+]?[0-9]+(\\.[0-9]+)?$");

        //数字
//非数字
        return pattern.matcher(str).matches();
    }


    public static String addNum(String num) {
        if (TextUtils.isEmpty(num)) {
            return "0";
        }

        double add;
        int n = 0;
        int index = num.indexOf(".");
        if (index == -1) {
            add = 1;
        } else {
            n = num.length() - index - 1;
            add = 1.0 / Math.pow(10, n);
        }

        DecimalFormat decimalFormat = new DecimalFormat("###################.###########", new DecimalFormatSymbols(Locale.ENGLISH));
        double vol = MathHelper.round(MathHelper.add(num, Double.toString(add)), n);
        String ret =  decimalFormat.format(vol);
        if (ret.endsWith(".0")) {
            ret = ret.substring(0, ret.length() - 2);
        }
        return ret;
    }

    public static String minusNum(String num) {
        if (TextUtils.isEmpty(num)) {
            return "0";
        }

        if (num.equals("0")) {
            return "0";
        }

        double add;
        int n = 0;
        int index = num.indexOf(".");
        if (index == -1) {
            add = 1;
        } else {
            n = num.length() - index - 1;
            add = 1.0 / Math.pow(10, n);
        }

        DecimalFormat decimalFormat = new DecimalFormat("###################.###########", new DecimalFormatSymbols(Locale.ENGLISH));

        double vol = MathHelper.round(MathHelper.sub(num, Double.toString(add)), n);
        String ret =  decimalFormat.format(vol);
        if (ret.endsWith(".0")) {
            ret = ret.substring(0, ret.length() - 2);
        }

        return ret;
    }

    public static DecimalFormat getDecimal(int index) {
        DecimalFormat decimalFormat;
        switch (index) {
            case 0: decimalFormat = new DecimalFormat("###################", new DecimalFormatSymbols(Locale.ENGLISH)); break;
            case 1: decimalFormat = new DecimalFormat("##0.0", new DecimalFormatSymbols(Locale.ENGLISH)); break;
            case 2: decimalFormat = new DecimalFormat("##0.00", new DecimalFormatSymbols(Locale.ENGLISH)); break;
            case 3: decimalFormat = new DecimalFormat("##0.000", new DecimalFormatSymbols(Locale.ENGLISH)); break;
            case 4: decimalFormat = new DecimalFormat("##0.0000", new DecimalFormatSymbols(Locale.ENGLISH)); break;
            case 5: decimalFormat = new DecimalFormat("##0.00000", new DecimalFormatSymbols(Locale.ENGLISH)); break;
            case 6: decimalFormat = new DecimalFormat("##0.000000", new DecimalFormatSymbols(Locale.ENGLISH)); break;
            case 7: decimalFormat = new DecimalFormat("##0.0000000", new DecimalFormatSymbols(Locale.ENGLISH)); break;
            case 8: decimalFormat = new DecimalFormat("##0.00000000", new DecimalFormatSymbols(Locale.ENGLISH)); break;
            case 9: decimalFormat = new DecimalFormat("##0.000000000", new DecimalFormatSymbols(Locale.ENGLISH)); break;
            case 10: decimalFormat = new DecimalFormat("##0.0000000000", new DecimalFormatSymbols(Locale.ENGLISH)); break;
            default: decimalFormat = new DecimalFormat("###################.###########", new DecimalFormatSymbols(Locale.ENGLISH)); break;
        }
        return decimalFormat;
    }

}
