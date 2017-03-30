package com.atlas.airtool;

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
        if (intent.getBooleanExtra(AudioControl.SEND_BY_AUDIO_CONTROL, false)) {
            return;
        }

        if (mAudioControl == null) {
            mAudioControl = new AudioControl(context);
        }

        KeyEvent keyEvent
                = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        int keyCode = keyEvent.getKeyCode();
        Log.d(TAG, "onReceive: " + keyEvent.toString());

        if (keyEvent.getAction() == ACTION_UP) {
            if (keyCode == KEYCODE_MEDIA_PLAY
                    || keyCode == KEYCODE_MEDIA_PAUSE) {
                keyCode = KEYCODE_MEDIA_NEXT;
            }
            mAudioControl.mediaControl(keyCode);
        }
    }
}
