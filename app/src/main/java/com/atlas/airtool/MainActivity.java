package com.atlas.airtool;

import android.animation.AnimatorInflater;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import static android.view.KeyEvent.KEYCODE_MEDIA_NEXT;
import static android.view.KeyEvent.KEYCODE_MEDIA_PAUSE;
import static android.view.KeyEvent.KEYCODE_MEDIA_PLAY;
import static android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    public static final int ENGROSS_AUDIO = 0x123;
    public static final int UN_ENGROSS_AUDIO = 0x124;

    private Button btn_re_engross;
    private StyleToast styleToast;
    public View root;
    private TextView tv_message;
    private PopupWindow popupWindow;
    private CircleProgress circleProgress;

    private AudioManager mAudioManager;
    private ComponentName mComponent;
    private AudioControl mAudioControl;

    private Handler mHandler = new AirPodHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initAudio();
    }

    private void initViews() {
        Button btn_previous = (Button) findViewById(R.id.btn_previous);
        Button btn_play = (Button) findViewById(R.id.btn_play);
        Button btn_pause = (Button) findViewById(R.id.btn_pause);
        Button btn_next = (Button) findViewById(R.id.btn_next);
        btn_re_engross = (Button) findViewById(R.id.btn_re_engross_audio);

        btn_previous.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_re_engross.setOnClickListener(this);

        styleToast = new StyleToast(this);

        root = findViewById(R.id.root);
        View view = getLayoutInflater().inflate(R.layout.dialog_message, null);
        tv_message = (TextView) view.findViewById(R.id.tv_message);
        circleProgress = (CircleProgress) view.findViewById(R.id.progressBar);
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        ObjectAnimator colorAnim = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.animator_text_color);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setTarget(tv_message);
        colorAnim.start();
    }

    private void initAudio() {
        mAudioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);
        mComponent = new ComponentName(this, MediaReceiver.class);
        mAudioControl = new AudioControl(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            releaseEngross();
        }
    }

    private void releaseEngross() {
        mHandler.sendMessage(Message.obtain(mHandler, ENGROSS_AUDIO));
        mHandler.sendMessageDelayed(Message.obtain(mHandler, UN_ENGROSS_AUDIO), 1300);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: " + v.getContentDescription());
        int code = KEYCODE_MEDIA_NEXT;
        switch (v.getId()) {
            //初始化按钮，解除音乐播放器对 MEDIA_BUTTON 广播的独占
            case R.id.btn_re_engross_audio:
                releaseEngross();
                return;
            //音乐控制四骑士
            case R.id.btn_previous:
                code = KEYCODE_MEDIA_PREVIOUS;
                break;
            case R.id.btn_play:
                code = KEYCODE_MEDIA_PLAY;
                break;
            case R.id.btn_pause:
                code = KEYCODE_MEDIA_PAUSE;
                break;
            case R.id.btn_next:
                code = KEYCODE_MEDIA_NEXT;
                break;
        }
        mAudioControl.mediaControl(code);
    }

    private class AirPodHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ENGROSS_AUDIO:
                    Log.d(TAG, "handleMessage: " + "ENGROSS_AUDIO");
                    mAudioManager.registerMediaButtonEventReceiver(mComponent);
                    showPopWindow("正在初始化...");
                    break;
                case UN_ENGROSS_AUDIO:
                    Log.d(TAG, "handleMessage: " + "UN_ENGROSS_AUDIO");
                    mAudioManager.registerMediaButtonEventReceiver(mComponent);
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                    styleToast.showToast("初始化成功");
                    break;
            }
        }
    }

    private void showPopWindow(String title) {
        tv_message.setText(title);
        if (!popupWindow.isShowing()) {
            circleProgress.reset();
            circleProgress.startAnim();
            popupWindow.showAtLocation(root, Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

}
