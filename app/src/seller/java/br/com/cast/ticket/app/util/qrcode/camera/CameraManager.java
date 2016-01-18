package br.com.cast.ticket.app.util.qrcode.camera;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.view.SurfaceHolder;

import java.io.IOException;

import br.com.cast.ticket.app.util.qrcode.config.ZXingConfig;

public final class CameraManager {

    private static final int MIN_FRAME_WIDTH = 600;
    private static final int MIN_FRAME_HEIGHT = 600;
    private static final int MAX_FRAME_WIDTH = 1020;
    private static final int MAX_FRAME_HEIGHT = 820;
    private static CameraManager mCameraManager;
    static final int SDK_INT = Build.VERSION.SDK_INT;

    private final CameraConfigurationManager configManager;
    @SuppressWarnings("deprecation")
    private Camera mCamera;
    private Rect mFramingRect;
    private Rect mFramingRectInPreview;
    private boolean mInitialized;
    private boolean mPreviewing;
    private final boolean mUseOneShotPreviewCallback;
    private ZXingConfig mConfig;
    /**
     * Preview frames are delivered here, which we pass on to the registered handler. Make sure to
     * clear the handler so it will only receive one message.
     */
    private final PreviewCallback previewCallback;

    /**
     * Autofocus callbacks arrive here, and are dispatched to the Handler which requested them.
     */
    private final AutoFocusCallback autoFocusCallback;

    /**
     * Initializes this static object with the Context of the calling Activity.
     *
     * @param context The Activity which wants to use the mCamera.
     */
    public static void init(Context context, ZXingConfig config) {
        if (mCameraManager == null) {
            mCameraManager = new CameraManager(context, config);
        }
    }

    /**
     * Gets the CameraManager singleton instance.
     *
     * @return A reference to the CameraManager singleton.
     */
    public static CameraManager get() {
        return mCameraManager;
    }

    private CameraManager(Context context, ZXingConfig config) {

        this.configManager = new CameraConfigurationManager(context);
        this.mConfig = config;

        // Camera.setOneShotPreviewCallback() has a race condition in Cupcake, so we use the older
        // Camera.setPreviewCallback() on 1.5 and earlier. For Donut and later, we need to use
        // the more efficient one shot callback, as the older one can swamp the system and cause it
        // to run out of memory. We can't use SDK_INT because it was introduced in the Donut SDK.
        mUseOneShotPreviewCallback = Build.VERSION.SDK_INT > 3; // 3 = Cupcake

        previewCallback = new PreviewCallback(configManager, mUseOneShotPreviewCallback);
        autoFocusCallback = new AutoFocusCallback();
    }

    /**
     * Opens the mCamera driver and initializes the hardware parameters.
     *
     * @param holder The surface object which the mCamera will draw preview frames into.
     * @throws IOException Indicates the mCamera driver failed to open.
     */
    @SuppressWarnings("deprecation")
    public void openDriver(SurfaceHolder holder) throws IOException {
        if (mCamera == null) {
            mCamera = Camera.open();
            if (mCamera == null) {
                throw new IOException();
            }
        }
        mCamera.setPreviewDisplay(holder);
        if (!mInitialized) {
            mInitialized = true;
            configManager.initFromCameraParameters(mCamera);
        }
        configManager.setDesiredCameraParameters(mCamera);

        if (mConfig.useFrontLight) {
            FlashlightManager.enableFlashlight();
        }
    }

    /**
     * Closes the Camera driver if still in use.
     */
    public void closeDriver() {
        if (mCamera != null) {
            FlashlightManager.disableFlashlight();
            mCamera.release();
            mCamera = null;

            // Make sure to clear these each time we close the mCamera, so that any scanning rect
            // requested by intent is forgotten.
            mFramingRect = null;
            mFramingRectInPreview = null;
        }
    }

    /**
     * Asks the Camera hardware to begin drawing preview frames to the screen.
     */
    public void startPreview() {
        if (mCamera != null && !mPreviewing) {
            mCamera.startPreview();
            mPreviewing = true;
        }
    }

    /**
     * Tells the Camera to stop drawing preview frames.
     */
    public void stopPreview() {
        if (mCamera != null && mPreviewing) {
            if (!mUseOneShotPreviewCallback) {
                mCamera.setPreviewCallback(null);
            }
            mCamera.stopPreview();
            previewCallback.setHandler(null, 0);
            autoFocusCallback.setHandler(null, 0);
            mPreviewing = false;
        }
    }

    /**
     * A single preview frame will be returned to the handler supplied. The data will arrive as byte[]
     * in the message.obj field, with width and height encoded as message.arg1 and message.arg2,
     * respectively.
     *
     * @param handler The handler to send the message to.
     * @param message The what field of the message to be sent.
     */
    public void requestPreviewFrame(Handler handler, int message) {
        if (mCamera != null && mPreviewing) {
            previewCallback.setHandler(handler, message);
            if (mUseOneShotPreviewCallback) {
                mCamera.setOneShotPreviewCallback(previewCallback);
            } else {
                mCamera.setPreviewCallback(previewCallback);
            }
        }
    }

    /**
     * Asks the Camera hardware to perform an autofocus.
     *
     * @param handler The Handler to notify when the autofocus completes.
     * @param message The message to deliver.
     */
    public void requestAutoFocus(Handler handler, int message) {
        if (mCamera != null && mPreviewing) {
            autoFocusCallback.setHandler(handler, message);
            //Log.d(TAG, "Requesting auto-focus callback");
            mCamera.autoFocus(autoFocusCallback);
        }
    }

    /**
     * Calculates the framing rect which the UI should draw to show the user where to place the
     * barcode. This target helps with alignment as well as forces the user to hold the device
     * far enough away to ensure the image will be in focus.
     *
     * @return The rectangle to draw on screen in window coordinates.
     */
    public Rect getFramingRect() {
        if (mFramingRect == null) {
            if (mCamera == null) {
                return null;
            }
            Point screenResolution = configManager.getScreenResolution();
            int width = screenResolution.x * 3 / 4;
            if (width < MIN_FRAME_WIDTH) {
                width = MIN_FRAME_WIDTH;
            } else if (width > MAX_FRAME_WIDTH) {
                width = MAX_FRAME_WIDTH;
            }
            int height = screenResolution.y * 3 / 4;
            if (height < MIN_FRAME_HEIGHT) {
                height = MIN_FRAME_HEIGHT;
            } else if (height > MAX_FRAME_HEIGHT) {
                height = MAX_FRAME_HEIGHT;
            }
            int leftOffset = (screenResolution.x - width) / 2;
            int topOffset = (screenResolution.y - height) / 2;
            mFramingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
        }
        return mFramingRect;
    }

    /**
     * Like {@link #getFramingRect} but coordinates are in terms of the preview frame,
     * not UI / screen.
     */
    public Rect getFramingRectInPreview() {
        if (mFramingRectInPreview == null) {
            Rect rect = new Rect(getFramingRect());
            Point cameraResolution = configManager.getCameraResolution();
            Point screenResolution = configManager.getScreenResolution();
            rect.left = rect.left * cameraResolution.x / screenResolution.x;
            rect.right = rect.right * cameraResolution.x / screenResolution.x;
            rect.top = rect.top * cameraResolution.y / screenResolution.y;
            rect.bottom = rect.bottom * cameraResolution.y / screenResolution.y;
            mFramingRectInPreview = rect;
        }
        return mFramingRectInPreview;
    }

    /**
     * Allows third party apps to specify the scanning rectangle dimensions, rather than determine
     * them automatically based on screen resolution.
     *
     * @param width  The width in pixels to scan.
     * @param height The height in pixels to scan.
     */
    public void setManualFramingRect(int width, int height) {
        Point screenResolution = configManager.getScreenResolution();
        if (width > screenResolution.x) {
            width = screenResolution.x;
        }
        if (height > screenResolution.y) {
            height = screenResolution.y;
        }
        int leftOffset = (screenResolution.x - width) / 2;
        int topOffset = (screenResolution.y - height) / 2;
        mFramingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
        mFramingRectInPreview = null;
    }

    /**
     * A factory method to build the appropriate LuminanceSource object based on the format
     * of the preview buffers, as described by Camera.Parameters.
     *
     * @param data   A preview frame.
     * @param width  The width of the image.
     * @param height The height of the image.
     * @return A PlanarYUVLuminanceSource instance.
     */
    public PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, int width, int height) {
        Rect rect = getFramingRectInPreview();
        int previewFormat = configManager.getPreviewFormat();
        String previewFormatString = configManager.getPreviewFormatString();

        switch (previewFormat) {
            // This is the standard Android format which all devices are REQUIRED to support.
            // In theory, it's the only one we should ever care about.
            case PixelFormat.YCbCr_420_SP:
                // This format has never been seen in the wild, but is compatible as we only care
                // about the Y channel, so allow it.
            case PixelFormat.YCbCr_422_SP:
                return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                        rect.width(), rect.height(), mConfig.reverseImage);
            default:
                // The Samsung Moment incorrectly uses this variant instead of the 'sp' version.
                // Fortunately, it too has all the Y data up front, so we can read it.
                if ("yuv420p".equals(previewFormatString)) {
                    return new PlanarYUVLuminanceSource(data, width, height, rect.left, rect.top,
                            rect.width(), rect.height(), mConfig.reverseImage);
                }
        }
        throw new IllegalArgumentException("Unsupported picture format: " +
                previewFormat + '/' + previewFormatString);
    }
}
