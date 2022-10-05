package huolongluo.byw.reform.c2c.oct;
import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.Toast;

import huolongluo.byw.log.Logger;
public class LengthFilter implements InputFilter {
    private final int mMax;
    private String msg;
    private Context context;

    public LengthFilter(Context context, int max, String msg) {
        this.context = context;
        this.mMax = max;
        this.msg = msg;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int keep = mMax - (dest.length() - (dend - dstart));
            if (keep <= 0) {
                //这里，用来给用户提示
                Toast.makeText(this.context, this.msg, Toast.LENGTH_SHORT).show();
                return "";
            } else if (keep >= end - start) {
                return null; // keep original
            } else {
                keep += start;
                if (Character.isHighSurrogate(source.charAt(keep - 1))) {
                    --keep;
                    if (keep == start) {
                        return "";
                    }
                }
                return source.subSequence(start, keep);
            }
        } catch (Throwable t) {
            Logger.getInstance().error(t);
        }
        return "";
    }

    /**
     * @return the maximum length enforced by this input filter
     */
    public int getMax() {
        return mMax;
    }
}
