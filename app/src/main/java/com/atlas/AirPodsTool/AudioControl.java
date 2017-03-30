package com.atlas.AirPodsTool;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.keyCodeToString;

public class AudioControl {
    private static final String TAG = "AudioControl";
    public static final String SEND_BY_AUDIO_CONTROL = "send_by_audio_control";

    private Context mContext;

    public AudioControl(Context context) {
        mContext = context;
    }

    /**
     * 发送 MEDIA_BUTTON 广播，远程控制音乐播放器
     * @param code 按键代码
     */
    public void mediaControl(int code) {
        Log.d(TAG, "mediaControl: " + keyCodeToString(code));
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        //在广播中下毒
        intent.putExtra(SEND_BY_AUDIO_CONTROL, true);
        KeyEvent value = new KeyEvent(
                SystemClock.currentThreadTimeMillis(),
                SystemClock.currentThreadTimeMillis(),
                ACTION_UP, code,
                0, 0, -1, 0, 0x0, 0x101);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, value);
        mContext.sendBroadcast(intent);
    }
}
