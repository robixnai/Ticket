package br.com.cast.ticket.app.util.qrcode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;

import java.io.IOException;
import java.util.Vector;

import br.com.cast.ticket.R;
import br.com.cast.ticket.app.SellerTicketActivity;
import br.com.cast.ticket.app.util.qrcode.camera.CameraManager;
import br.com.cast.ticket.app.util.qrcode.config.ZXingConfig;

public final class ScannerCodeActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = ScannerCodeActivity.class.getSimpleName();

    private static final long INTENT_RESULT_DURATION = 1500L;

    private ScannerCodeActivityHandler mHandler;
    private ViewfinderView mViewfinderView;
    private TextView mStatusView;
    private boolean mHasSurface;
    private Vector<BarcodeFormat> mDecodeFormats;
    private String mCharacterSet;
    private InactivityTimer mInactivityTimer;
    private SoundManager mSoundManager;
    private ZXingConfig mConfig;

    ViewfinderView getViewfinderView() {
        return mViewfinderView;
    }

    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.scanner_code);

        Intent intent = getIntent();
        if (intent != null) {
            mConfig = (ZXingConfig) intent.getSerializableExtra(ZXingConfig.INTENT_KEY);
        }
        if (mConfig == null) {
            mConfig = new ZXingConfig();
        }

        CameraManager.init(getApplication(), mConfig);
        mViewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        mStatusView = (TextView) findViewById(R.id.status_view);
        mHandler = null;
        mHasSurface = false;
        mInactivityTimer = new InactivityTimer(this);
        mSoundManager = new SoundManager(this, mConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetStatusView();

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();

        if (mHasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        Intent intent = getIntent();
        mDecodeFormats = null;
        mCharacterSet = null;
        if (intent != null) {
            mCharacterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);
            // Scan the formats the intent requested, and return the result to the calling activity.
            mDecodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
            if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
                int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
                int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
                if (width > 0 && height > 0) {
                    CameraManager.get().setManualFramingRect(width, height);
                }
            }
        }

        mSoundManager.updatePrefs();
        mInactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHandler != null) {
            mHandler.quitSynchronously();
            mHandler = null;
        }
        mInactivityTimer.onPause();
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        mInactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
            // Handle these events so they don't launch the Camera app
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (!mHasSurface) {
            mHasSurface = true;
            initCamera(holder);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        mHasSurface = false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult The contents of the barcode.
     * @param barcode   A greyscale bitmap of the camera data which was decoded.
     */
    @SuppressWarnings("deprecation")
    public void handleDecode(Result rawResult, Bitmap barcode) {
        mInactivityTimer.onActivity();

        mSoundManager.playBeepSoundAndVibrate();
        drawResultPoints(barcode, rawResult);

        ParsedResult parsedResult = ResultParser.parseResult(rawResult);
        String type = parsedResult.getType().toString();

        mViewfinderView.drawResultBitmap(barcode);

        String resultContent = rawResult.toString();
        if (mConfig.copyToClipboard) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText(resultContent);
        }

        Intent intent = new Intent(getIntent().getAction());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intents.Scan.RESULT, resultContent);
        intent.putExtra(Intents.Scan.RESULT_FORMAT, rawResult.getBarcodeFormat().toString());
        intent.putExtra("TYPE", type);
        intent.putExtra("FORMAT", rawResult.getBarcodeFormat().toString());
        intent.setClass(getBaseContext(), SellerTicketActivity.class);
        byte[] rawBytes = rawResult.getRawBytes();
        if (rawBytes != null && rawBytes.length > 0) {
            intent.putExtra(Intents.Scan.RESULT_BYTES, rawBytes);
        }
        Message message = Message.obtain(mHandler, R.id.qrcode_return_scan_result);
        message.obj = intent;
        mHandler.sendMessageDelayed(message, INTENT_RESULT_DURATION);
        startActivity(intent);
    }

    /**
     * Superimpose a line for 1D or dots for 2D to highlight the key features of
     * the barcode.
     *
     * @param barcode   A bitmap of the captured image.
     * @param rawResult The decoded results which contains the points to draw.
     */
    private void drawResultPoints(Bitmap barcode, Result rawResult) {
        ResultPoint[] points = rawResult.getResultPoints();
        if (points != null && points.length > 0) {
            Canvas canvas = new Canvas(barcode);
            Paint paint = new Paint();
            paint.setColor(getResources().getColor(R.color.qrcode_result_image_border));
            paint.setStrokeWidth(3.0f);
            paint.setStyle(Paint.Style.STROKE);
            Rect border = new Rect(2, 2, barcode.getWidth() - 2, barcode.getHeight() - 2);
            canvas.drawRect(border, paint);

            paint.setColor(getResources().getColor(R.color.qrcode_result_points));
            if (points.length == 2) {
                paint.setStrokeWidth(4.0f);
                drawLine(canvas, paint, points[0], points[1]);
            } else if (points.length == 4
                    && (rawResult.getBarcodeFormat().equals(BarcodeFormat.UPC_A) || rawResult
                    .getBarcodeFormat().equals(BarcodeFormat.EAN_13))) {
                // Hacky special case -- draw two lines, for the barcode and metadata
                drawLine(canvas, paint, points[0], points[1]);
                drawLine(canvas, paint, points[2], points[3]);
            } else {
                paint.setStrokeWidth(10.0f);
                for (ResultPoint point : points) {
                    canvas.drawPoint(point.getX(), point.getY(), paint);
                }
            }
        }
    }

    private static void drawLine(Canvas canvas, Paint paint, ResultPoint a, ResultPoint b) {
        canvas.drawLine(a.getX(), a.getY(), b.getX(), b.getY(), paint);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            if (mHandler == null) {
                mHandler = new ScannerCodeActivityHandler(this, mDecodeFormats, mCharacterSet);
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe.getMessage());
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected error initializating camera. " + e.getMessage());
        }
    }

    private void resetStatusView() {
        mStatusView.setText(R.string.message_capture_code);
        mStatusView.setVisibility(View.VISIBLE);
        mViewfinderView.setVisibility(View.VISIBLE);
    }

    public void drawViewfinder() {
        mViewfinderView.drawViewfinder();
    }
}