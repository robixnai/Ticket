package br.com.cast.ticket.app.util.qrcode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Vector;

import br.com.cast.ticket.R;
import br.com.cast.ticket.app.util.qrcode.camera.CameraManager;

public final class ScannerCodeActivityHandler extends Handler {

    private static final String TAG = ScannerCodeActivityHandler.class.getSimpleName();

    private final ScannerCodeActivity mActivity;
    private final DecodeThread mDecodeThread;
    private State mState;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    ScannerCodeActivityHandler(ScannerCodeActivity activity, Vector<BarcodeFormat> decodeFormats,
                               String characterSet) {
        this.mActivity = activity;
        mDecodeThread = new DecodeThread(activity, decodeFormats, characterSet,
                new ViewfinderResultPointCallback(activity.getViewfinderView()));
        mDecodeThread.start();
        mState = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case R.id.qrcode_auto_focus:
                if (mState == State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, R.id.qrcode_auto_focus);
                }
                break;
            case R.id.qrcode_decode_succeeded:
                mState = State.SUCCESS;
                Bundle bundle = message.getData();
                Bitmap barcode = bundle == null ? null : (Bitmap) bundle
                        .getParcelable(DecodeThread.BARCODE_BITMAP);
                mActivity.handleDecode((Result) message.obj, barcode);
                break;
            case R.id.qrcode_decode_failed:
                // We're decoding as fast as possible, so when one decode fails, start another.
                mState = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(),
                        R.id.qrcode_decode);
                break;
            case R.id.qrcode_return_scan_result:
                mActivity.setResult(Activity.RESULT_OK, (Intent) message.obj);
                mActivity.finish();
                break;
        }
    }

    public void quitSynchronously() {
        mState = State.DONE;
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(mDecodeThread.getHandler(), R.id.qrcode_quit);
        quit.sendToTarget();
        try {
            mDecodeThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }

        removeMessages(R.id.qrcode_decode_succeeded);
        removeMessages(R.id.qrcode_decode_failed);
    }

    private void restartPreviewAndDecode() {
        if (mState == State.SUCCESS) {
            mState = State.PREVIEW;
            CameraManager.get()
                    .requestPreviewFrame(mDecodeThread.getHandler(), R.id.qrcode_decode);
            CameraManager.get().requestAutoFocus(this, R.id.qrcode_auto_focus);
            mActivity.drawViewfinder();
        }
    }
}