package br.com.cast.ticket.app.util.qrcode;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;

import java.util.ArrayList;
import java.util.List;

import br.com.cast.ticket.R;
import br.com.cast.ticket.app.util.qrcode.camera.CameraManager;

public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
    private static final long ANIMATION_DELAY = 80L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;

    private final Paint mPaint;
    private Bitmap mResultBitmap;
    private final int mMaskColor;
    private final int mResultColor;
    private final int mFrameColor;
    private final int mLaserColor;
    private final int mResultPointColor;
    private int mScannerAlpha;
    private List<ResultPoint> mPossibleResultPoints;
    private List<ResultPoint> mLastPossibleResultPoints;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every time in onDraw().
        mPaint = new Paint();
        Resources resources = getResources();
        mMaskColor = resources.getColor(R.color.qrcode_viewfinder_mask);
        mResultColor = resources.getColor(R.color.qrcode_result_view);
        mFrameColor = resources.getColor(R.color.qrcode_viewfinder_frame);
        mLaserColor = resources.getColor(R.color.qrcode_viewfinder_laser);
        mResultPointColor = resources.getColor(R.color.qrcode_possible_result_points);
        mScannerAlpha = 0;
        mPossibleResultPoints = new ArrayList<>(5);
        mLastPossibleResultPoints = null;
    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        mPaint.setColor(mResultBitmap != null ? mResultColor : mMaskColor);
        canvas.drawRect(0, 0, width, frame.top, mPaint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, mPaint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, mPaint);
        canvas.drawRect(0, frame.bottom + 1, width, height, mPaint);

        if (mResultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            mPaint.setAlpha(CURRENT_POINT_OPACITY);
            canvas.drawBitmap(mResultBitmap, null, frame, mPaint);
        } else {

            // Draw a two pixel solid black border inside the framing rect
            mPaint.setColor(mFrameColor);
            canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, mPaint);
            canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, mPaint);
            canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, mPaint);
            canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, mPaint);

            // Draw a red "laser scanner" line through the middle to show decoding is active
            mPaint.setColor(mLaserColor);
            mPaint.setAlpha(SCANNER_ALPHA[mScannerAlpha]);
            mScannerAlpha = (mScannerAlpha + 1) % SCANNER_ALPHA.length;
            int middle = frame.height() / 2 + frame.top;
            canvas.drawRect(frame.left + 2, middle - 1, frame.right - 1, middle + 2, mPaint);

            Rect previewFrame = CameraManager.get().getFramingRectInPreview();
            float scaleX = frame.width() / (float) previewFrame.width();
            float scaleY = frame.height() / (float) previewFrame.height();

            List<ResultPoint> currentPossible = mPossibleResultPoints;
            List<ResultPoint> currentLast = mLastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                mLastPossibleResultPoints = null;
            } else {
                mPossibleResultPoints = new ArrayList<>(5);
                mLastPossibleResultPoints = currentPossible;
                mPaint.setAlpha(CURRENT_POINT_OPACITY);
                mPaint.setColor(mResultPointColor);
                synchronized (currentPossible) {
                    for (ResultPoint point : currentPossible) {
                        canvas.drawCircle(frame.left + (int) (point.getX() * scaleX),
                                frame.top + (int) (point.getY() * scaleY),
                                6.0f, mPaint);
                    }
                }
            }
            if (currentLast != null) {
                mPaint.setAlpha(CURRENT_POINT_OPACITY / 2);
                mPaint.setColor(mResultPointColor);
                synchronized (currentLast) {
                    for (ResultPoint point : currentLast) {
                        canvas.drawCircle(frame.left + (int) (point.getX() * scaleX),
                                frame.top + (int) (point.getY() * scaleY),
                                3.0f, mPaint);
                    }
                }
            }

            // Request another update at the animation interval, but only repaint the laser line,
            // not the entire viewfinder mask.
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top, frame.right, frame.bottom);
        }
    }

    public void drawViewfinder() {
        mResultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        mResultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = mPossibleResultPoints;
        synchronized (point) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }
}
