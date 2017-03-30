package com.atlas.airtool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import static android.view.KeyEvent.KEYCODE_MEDIA_NEXT;
import static com.atlas.airtool.AudioControl.SEND_BROADCAST_ONCE;

public class MediaReceiver extends BroadcastReceiver {
    private static final String TAG = "MediaReceiver";

    public static boolean isAirToolRunning = false;
    private AudioControl mAudioControl;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getBooleanExtra(AudioControl.SEND_BY_AUDIOCONTROL, false)) {
            return;
        }

        if (mAudioControl == null) {
            mAudioControl = new AudioControl(context);
        }

        KeyEvent keyEvent
                = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        int keyCode = keyEvent.getKeyCode();
        Log.d(TAG, "onReceive: " + keyEvent);

        boolean sendBroadcastOnce
                = intent.getBooleanExtra(SEND_BROADCAST_ONCE, false);
        if ((keyCode == 127 || keyCode == 126)
                && !isAirToolRunning
                && !sendBroadcastOnce) {
            mAudioControl.mediaControl(KEYCODE_MEDIA_NEXT, true);
        }
    }
}
