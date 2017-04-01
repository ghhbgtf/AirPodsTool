package com.atlas.AirPodsTool;

import android.animation.AnimatorInflater;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.atlas.AirPodsTool.MyView.CircleProgress;
import com.atlas.AirPodsTool.MyView.StyleToast;

import static android.media.AudioManager.STREAM_MUSIC;
import static android.view.KeyEvent.KEYCODE_MEDIA_NEXT;
import static android.view.KeyEvent.KEYCODE_MEDIA_PAUSE;
import static android.view.KeyEvent.KEYCODE_MEDIA_PLAY;
import static android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS;

public class MainActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";

    public static final int ENGROSS_AUDIO = 0x123;
    public static final int UN_ENGROSS_AUDIO = 0x124;
    public static final int REFRESH_BUTTON_AIRPODS = 0x125;
    public static final int REFRESH_BUTTON_PLAY_PAUSE = 0x126;

    private View root;
    private PopupWindow popupWindow;
    private CircleProgress circleProgress;
    private TextView tv_message;
    private StyleToast styleToast;
    private ImageButton mBtnPlayPause;
    private Button mBtnAirPods;

    private AudioManager mAudioManager;
    private ComponentName mComponent;
    private AudioControl mAudioControl;
    private Handler mHandler = new AirPodHandler();
    private PlayPauseReceiver mReceiver = new PlayPauseReceiver();

    private boolean released = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initAudio();

        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_MEDIA_BUTTON));
    }

    private SeekBar mSeekBar;

    private void initViews() {
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar_volume);
        mBtnAirPods = (Button) findViewById(R.id.btn_airpods);
        mBtnAirPods.setOnClickListener(this);
        mBtnAirPods.setOnLongClickListener(this);
        showInfo(R.string.re_init_audio_manager, 3000);

        ImageButton btnPrev = (ImageButton) findViewById(R.id.btn_previous);
        btnPrev.setOnClickListener(this);
        Drawable drawablePrev = btnPrev.getDrawable();
        if (drawablePrev instanceof Animatable) {
            ((Animatable) drawablePrev).start();
        }

        mBtnPlayPause = (ImageButton) findViewById(R.id.btn_play_pause);
        mBtnPlayPause.setOnClickListener(this);

        ImageButton btnNext = (ImageButton) findViewById(R.id.btn_next);
        btnNext.setOnClickListener(this);
        Drawable drawableNext = btnNext.getDrawable();
        if (drawableNext instanceof Animatable) {
            ((Animatable) drawableNext).start();
        }

        styleToast = new StyleToast(this);

        root = findViewById(R.id.root);
        View view = getLayoutInflater().inflate(R.layout.dialog_message, null);
        tv_message = (TextView) view.findViewById(R.id.tv_message);
        circleProgress = (CircleProgress) view.findViewById(R.id.progressBar);
        popupWindow = new PopupWindow(view,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(R.style.pop_anim_style);
        ObjectAnimator colorAnim
                = (ObjectAnimator) AnimatorInflater.loadAnimator(this,
                R.animator.animator_text_color);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setTarget(tv_message);
        colorAnim.start();
    }

    private void initAudio() {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mComponent = new ComponentName(this, MediaReceiver.class);
        mAudioControl = new AudioControl(this);
        mHandler.sendMessage(Message.obtain(mHandler,
                REFRESH_BUTTON_PLAY_PAUSE, mAudioManager.isMusicActive()));
        initSeekBar();
    }

    private void initSeekBar() {
        mSeekBar.setMax(mAudioManager.getStreamMaxVolume(STREAM_MUSIC));
        mSeekBar.setProgress(mAudioManager.getStreamVolume(STREAM_MUSIC));
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged: " + progress);
                mAudioManager.setStreamVolume(STREAM_MUSIC, progress, 0);
                mBtnAirPods.setText(String.format(
                        getString(R.string.adjust_volume), progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.sendMessageDelayed(Message.obtain(mHandler,
                        REFRESH_BUTTON_AIRPODS), 1500);

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !released) {
            releaseEngross();
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: " + v.getTag());
        int code;
        switch (v.getId()) {
            case R.id.btn_airpods:
                MediaReceiver.proxy = !MediaReceiver.proxy;
                mHandler.sendEmptyMessage(REFRESH_BUTTON_AIRPODS);
                return;
            //音乐控制
            case R.id.btn_previous:
                code = KEYCODE_MEDIA_PREVIOUS;
                break;
            case R.id.btn_play_pause:
                boolean musicActive = mAudioManager.isMusicActive();
                code = musicActive
                        ? KEYCODE_MEDIA_PAUSE
                        : KEYCODE_MEDIA_PLAY;
                mHandler.sendMessage(Message.obtain(mHandler,
                        REFRESH_BUTTON_PLAY_PAUSE, musicActive));
                break;
            case R.id.btn_next:
                code = KEYCODE_MEDIA_NEXT;
                break;
            default:
                return;
        }
        mAudioControl.mediaControl(code);
    }

    @Override
    public boolean onLongClick(View v) {
        releaseEngross();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    /**
     * 解除音乐播放器对 MEDIA_BUTTON 广播的独占,
     * 这样所有注册了 MEDIA_BUTTON 广播的接收器都会收到广播
     * 参考 http://www.cnblogs.com/mythou/p/3302347.html
     */
    private void releaseEngross() {
        mHandler.sendMessage(Message.obtain(mHandler, ENGROSS_AUDIO));
        mHandler.sendMessageDelayed(Message.obtain(mHandler, UN_ENGROSS_AUDIO), 1300);
    }

    private void showInfo(int resid) {
        showInfo(resid, 1500);
    }

    private void showInfo(int resid, long delayMillis) {
        mBtnAirPods.setText(resid);
        mHandler.sendMessageDelayed(Message.obtain(mHandler,
                REFRESH_BUTTON_AIRPODS), delayMillis);
    }

    private void showPopWindow(String title) {
        tv_message.setText(title);
        if (!popupWindow.isShowing()) {
            circleProgress.reset();
            circleProgress.startAnim();
            popupWindow.showAtLocation(root, Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private class AirPodHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ENGROSS_AUDIO:
                    Log.d(TAG, "handleMessage: " + "ENGROSS_AUDIO");
                    // FIXME: 2017/3/30 Deprecated
                    mAudioManager.registerMediaButtonEventReceiver(mComponent);
                    root.setVisibility(View.INVISIBLE);
                    showPopWindow("正在初始化...");
                    break;
                case UN_ENGROSS_AUDIO:
                    Log.d(TAG, "handleMessage: " + "UN_ENGROSS_AUDIO");
                    // FIXME: 2017/3/30 Deprecated
                    mAudioManager.registerMediaButtonEventReceiver(mComponent);
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                        root.setVisibility(View.VISIBLE);
                    }
                    styleToast.showToast("初始化成功");
                    released = true;
                    break;
                case REFRESH_BUTTON_AIRPODS:
                    mBtnAirPods.setText(MediaReceiver.proxy ? R.string.airpods_next
                            : R.string.airpods_play_pause);
                    break;
                case REFRESH_BUTTON_PLAY_PAUSE:
                    boolean musicActive;
                    if (msg.obj != null) {
                        musicActive = (boolean) msg.obj;
                    } else {
                        musicActive = mAudioManager.isMusicActive();
                    }
                    mBtnPlayPause.setImageResource(musicActive
                            ? R.drawable.ic_pause_white_24dp
                            : R.drawable.ic_play_arrow_white_24dp);
                    break;
            }
        }
    }

    private class PlayPauseReceiver extends BroadcastReceiver {
        private static final String TAG = "PlayPauseReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            KeyEvent keyEvent
                    = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            int keyCode = keyEvent.getKeyCode();
            Log.d(TAG, "onReceive: " + keyEvent.keyCodeToString(keyCode));
            switch (keyCode) {
                case KEYCODE_MEDIA_PAUSE:
                    mHandler.sendMessage(Message.obtain(mHandler,
                            REFRESH_BUTTON_PLAY_PAUSE, false/*pausing*/));
                    showInfo(R.string.media_pause);
                    break;
                case KEYCODE_MEDIA_PLAY:
                    mHandler.sendMessage(Message.obtain(mHandler,
                            REFRESH_BUTTON_PLAY_PAUSE, true/*playing*/));
                    showInfo(R.string.media_play);
                    break;
                case KEYCODE_MEDIA_NEXT:
                    mHandler.sendMessageDelayed(Message.obtain(mHandler,
                            REFRESH_BUTTON_PLAY_PAUSE), 1000);
                    showInfo(R.string.media_next);
                    break;
                case KEYCODE_MEDIA_PREVIOUS:
                    mHandler.sendMessageDelayed(Message.obtain(mHandler,
                            REFRESH_BUTTON_PLAY_PAUSE), 1000);
                    showInfo(R.string.media_prev);
                    break;
            }
        }
    }

    public void close(View view) {
        finish();
    }

}
