package com.android.coinw.biz.trade;

import android.content.Context;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.legend.common.util.ThemeUtil;

import huolongluo.byw.R;

public class SeekBarRelativeLayout extends RelativeLayout {
    private boolean mIsBuy = true;

    public SeekBarRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBarRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private SeekBar seekBar;
    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener;

    public void setProgress(int process) {
        if (seekBar != null) {
            seekBar.setProgress(process);
        }
    }

    public void setEnabled(boolean enabled) {
        if (seekBar != null) {
            seekBar.setEnabled(enabled);
        }
    }

    public int getProcess() {
        if (seekBar != null) {
            return seekBar.getProgress();
        }
        return 0;
    }

    long time = 0;
    boolean isClick = false;

    public void initSeekBar() {
        seekBar = (SeekBar) findViewById(R.id.seek_bar_relative_layout_seek_bar);
        if (seekBar != null) {
            seekBar.setOnTouchListener((view, motionEvent) -> {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time = System.currentTimeMillis();
                        isClick = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        if (System.currentTimeMillis() - time < 500) {
                            isClick = true;
                        }
                        break;
                }
                return false;
            });
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (isClick) {
                        Log.i("点击", "-------");
                        if (0 < progress && progress <= 12) {
                            clickSetProgress(seekBar, 0);
                            progress = 0;
                        } else if (progress > 12 && progress <= 25) {
                            clickSetProgress(seekBar, 25);
                            progress = 25;
                        } else if (progress > 25 && progress <= 37) {
                            clickSetProgress(seekBar, 25);
                            progress = 25;
                        } else if (progress > 37 && progress <= 50) {
                            clickSetProgress(seekBar, 50);
                            progress = 50;
                        } else if (progress > 50 && progress <= 62) {
                            clickSetProgress(seekBar, 50);
                            progress = 50;
                        } else if (progress > 62 && progress <= 75) {
                            clickSetProgress(seekBar, 75);
                            progress = 75;
                        } else if (progress > 75 && progress <= 87) {
                            clickSetProgress(seekBar, 75);
                            progress = 75;
                        } else if (progress > 87 && progress <= 100) {
                            clickSetProgress(seekBar, 100);
                            progress = 100;
                        }
                    } else {
                        Log.i("SeekBar", "滑动  ------- progress = "+progress);
                    }

                    if (onSeekBarChangeListener != null) {
                        onSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    if (onSeekBarChangeListener != null) {
                        onSeekBarChangeListener.onStartTrackingTouch(seekBar);
                    }
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (onSeekBarChangeListener != null) {
                        onSeekBarChangeListener.onStopTrackingTouch(seekBar);
                    }
                }
            });

            setSeekbarDrawable(mIsBuy);

        }
    }

    private void clickSetProgress(SeekBar seekBar, int progress) {
        tryInitSeekBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            seekBar.setProgress(progress, true);
        } else {
            seekBar.setProgress(progress);
        }
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        this.onSeekBarChangeListener = onSeekBarChangeListener;
    }

    public void updateSeekBar(boolean isBuy) {
        mIsBuy = isBuy;
        tryInitSeekBar();

        setSeekbarDrawable(isBuy);

    }

    private void setSeekbarDrawable(boolean isBuy) {
        Drawable seekBarColorDrawable = ThemeUtil.INSTANCE.getThemeDrawable(getContext(), isBuy ? R.attr.bg_seek_bar_buy : R.attr.bg_seek_bar_sell);
        Drawable[] drawables = new Drawable[2];
        drawables[0] = getResources().getDrawable(R.drawable.bg_seek_bar);
        drawables[1] = new ClipDrawable(seekBarColorDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        LayerDrawable newLayerDrawable = new LayerDrawable(drawables);
        newLayerDrawable.setId(0, android.R.id.background);
        newLayerDrawable.setId(1, android.R.id.progress);
        seekBar.setProgressDrawable(newLayerDrawable);

        seekBar.setThumb(ThemeUtil.INSTANCE.getThemeDrawable(getContext(), isBuy ? R.attr.ic_seek_thumb_buy : R.attr.ic_seek_thumb_sell));
        seekBar.setThumbOffset(0);
    }

    private void tryInitSeekBar() {
        //08-19 10:37:35.211 E/AndroidRuntime(20686): FATAL EXCEPTION: main
        //08-19 10:37:35.211 E/AndroidRuntime(20686): Process: huolongluo.byw, PID: 20686
        //08-19 10:37:35.211 E/AndroidRuntime(20686): java.lang.NullPointerException: Attempt to invoke virtual method 'void android.widget.SeekBar.setProgressDrawable(android.graphics.drawable.Drawable)' on a null object reference
        //08-19 10:37:35.211 E/AndroidRuntime(20686):     at com.android.coinw.biz.trade.SeekBarRelativeLayout.a(SeekBarRelativeLayout.java:21)
        //08-19 10:37:35.211 E/AndroidRuntime(20686):     at com.android.coinw.biz.trade.lever.view.TradeLeverLeftView.changeTag(TradeLeverLeftView.java:16)
        //08-19 10:37:35.211 E/AndroidRuntime(20686):     at com.android.coinw.biz.trade.lever.fragment.Trade1AbsFragment.clickSell(Trade1AbsFragment.java:2)
        //08-19 10:37:35.211 E/AndroidRuntime(20686):     at com.android.coinw.biz.trade.lever.fragment.Trade1AbsFragment.onClick(Trade1AbsFragment.java:5)
        if (seekBar == null) {
            initSeekBar();
        }
    }
}