package com.atlas.airtool;

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
    private Intent mIntent;

    public AudioControl(Context context) {
        mContext = context;
    }

    public void mediaControl(int code) {
        Log.d(TAG, "mediaControl: " + keyCodeToString(code));
        mIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        KeyEvent value = new KeyEvent(
                SystemClock.currentThreadTimeMillis(),
                SystemClock.currentThreadTimeMillis(),
                ACTION_UP, code,
                0, 0, -1, 0, 0x0, 0x101);
        mIntent.putExtra(Intent.EXTRA_KEY_EVENT, value);
        mIntent.putExtra(SEND_BY_AUDIO_CONTROL, true);
        mContext.sendBroadcast(mIntent);
    }
}
