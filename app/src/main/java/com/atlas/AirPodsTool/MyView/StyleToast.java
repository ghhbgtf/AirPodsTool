package com.atlas.AirPodsTool.MyView;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.atlas.AirPodsTool.R;

/**
 * Created by Atlas on 16/1/12.
 * 显示自定义的toast
 * 并且防止重复显示yeah!
 */
public class StyleToast {

    private Context context;
    private Toast toast;
    private TextView textView;
    private WindowManager.LayoutParams mParams;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            if (toast != null) {
                toast.cancel();
            }
            toast = null;
        }
    };

    private int duration_ = Toast.LENGTH_SHORT;

    public StyleToast(Context context) {
        this.context = context;
        textView = new TextView(context);
        textView.setTextSize(17);
        textView.setTextColor(Color.WHITE);
        //设置圆角白色边框和背景黑色半透明
        textView.setBackground(context.getResources().getDrawable(R.drawable.corner_toast));
    }


    public void showToast(String string) {
        toast_makeText(string);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public void showToast(int resId) {
        toast_makeText(context.getString(resId));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public void showToast(String string, int duration_) {
        this.duration_ = duration_;
        toast_makeText(string);
        textView.setGravity(Gravity.LEFT);
    }

    public void showToast(int resId, int duration_) {
        this.duration_ = duration_;
        toast_makeText(context.getString(resId));
        textView.setGravity(Gravity.LEFT);
    }

    private void toast_makeText(String string) {
        handler.removeCallbacks(runnable);

        if (toast == null) {
            textView.setText(string);

            toast = new Toast(context);
            toast.setGravity(Gravity.CENTER, 0, -300);
            toast.setDuration(duration_);
            toast.setView(textView);
        } else {
            textView.setText(string);

            toast.setDuration(duration_);
            toast.setView(textView);
        }
        toast.show();

        if (duration_ == Toast.LENGTH_SHORT) {
            handler.postDelayed(runnable, 1000);
        } else {
            handler.postDelayed(runnable, 3000);
        }
    }

}
