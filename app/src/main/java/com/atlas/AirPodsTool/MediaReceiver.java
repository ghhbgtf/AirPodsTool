package com.atlas.AirPodsTool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import static android.view.KeyEvent.ACTION_UP;
import static android.view.KeyEvent.KEYCODE_MEDIA_NEXT;
import static android.view.KeyEvent.KEYCODE_MEDIA_PAUSE;
import static android.view.KeyEvent.KEYCODE_MEDIA_PLAY;

public class MediaReceiver extends BroadcastReceiver {
    private static final String TAG = "MediaReceiver";

    private AudioControl mAudioControl;

    @Override
    public void onReceive(Context context, Intent intent) {
        /** 此广播有毒，扔掉
         * 否则 MEDIA_BUTTON 广播
         * 会在 AudioControl 和 MediaReceiver 之间无限循环
         * 超过光速你可能进入二次元
         */
        if (intent.getBooleanExtra(AudioControl.SEND_BY_AUDIO_CONTROL, false))
            return;

        if (mAudioControl == null)
            mAudioControl = new AudioControl(context);
        KeyEvent keyEvent
                = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        Log.d(TAG, "onReceive: " + keyEvent);
        int keyCode = keyEvent.getKeyCode();
        if (keyEvent.getAction() == ACTION_UP) {
            // 播放和暂停-->下一首
            if (keyCode == KEYCODE_MEDIA_PLAY
                    || keyCode == KEYCODE_MEDIA_PAUSE) {
                keyCode = KEYCODE_MEDIA_NEXT;
            }
            mAudioControl.mediaControl(keyCode);
        }
    }
}
