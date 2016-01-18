package br.com.cast.ticket.app.util.qrcode;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

final class InactivityTimer {

    private static final String TAG = InactivityTimer.class.getSimpleName();

    private static final int INACTIVITY_DELAY_SECONDS = 5 * 60;

    private final ScheduledExecutorService mInactivityTimer =
            Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
    private final Activity mActivity;
    private ScheduledFuture<?> mInactivityFuture = null;
    private final PowerStatusReceiver mPowerStatusReceiver = new PowerStatusReceiver();

    InactivityTimer(Activity activity) {
        this.mActivity = activity;
        onActivity();
    }

    void onActivity() {
        cancel();
        if (!mInactivityTimer.isShutdown()) {
            try {
                mInactivityFuture = mInactivityTimer.schedule(new FinishListener(mActivity),
                        INACTIVITY_DELAY_SECONDS,
                        TimeUnit.SECONDS);
            } catch (RejectedExecutionException ree) {
                // surprising, but could be normal if for some reason the implementation just doesn't
                // think it can scheduler again. Since this time-out is non-essential, just forget it
                Log.w(TAG, ree.getMessage());
            }
        }
    }

    public void onPause() {
        mActivity.unregisterReceiver(mPowerStatusReceiver);
    }

    public void onResume() {
        mActivity.registerReceiver(mPowerStatusReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void cancel() {
        if (mInactivityFuture != null) {
            mInactivityFuture.cancel(true);
            mInactivityFuture = null;
        }
    }

    void shutdown() {
        cancel();
        mInactivityTimer.shutdown();
    }

    private static final class DaemonThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        }
    }

    private final class PowerStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                if (intent.getIntExtra("plugged", -1) == 0) {
                    InactivityTimer.this.onActivity();
                } else {
                    InactivityTimer.this.cancel();
                }
            }
        }
    }
}
