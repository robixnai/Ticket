package br.com.cast.ticket.app.util.qrcode.camera;

import android.graphics.Bitmap;

import com.google.zxing.LuminanceSource;

public final class PlanarYUVLuminanceSource extends LuminanceSource {

    private final byte[] mYuvData;
    private final int mDataWidth;
    private final int mDataHeight;
    private final int mLeft;
    private final int mTop;

    public PlanarYUVLuminanceSource(byte[] yuvData, int dataWidth, int dataHeight, int left, int top,
                                    int width, int height, boolean reverseHorizontal) {
        super(width, height);

        if (left + width > dataWidth || top + height > dataHeight) {
            throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
        }

        this.mYuvData = yuvData;
        this.mDataWidth = dataWidth;
        this.mDataHeight = dataHeight;
        this.mLeft = left;
        this.mTop = top;
        if (reverseHorizontal) {
            reverseHorizontal(width, height);
        }
    }

    @Override
    public byte[] getRow(int y, byte[] row) {
        if (y < 0 || y >= getHeight()) {
            throw new IllegalArgumentException("Requested row is outside the image: " + y);
        }
        int width = getWidth();
        if (row == null || row.length < width) {
            row = new byte[width];
        }
        int offset = (y + mTop) * mDataWidth + mLeft;
        System.arraycopy(mYuvData, offset, row, 0, width);
        return row;
    }

    @Override
    public byte[] getMatrix() {
        int width = getWidth();
        int height = getHeight();

        // If the caller asks for the entire underlying image, save the copy and give them the
        // original data. The docs specifically warn that result.length must be ignored.
        if (width == mDataWidth && height == mDataHeight) {
            return mYuvData;
        }

        int area = width * height;
        byte[] matrix = new byte[area];
        int inputOffset = mTop * mDataWidth + mLeft;

        // If the width matches the full width of the underlying data, perform a single copy.
        if (width == mDataWidth) {
            System.arraycopy(mYuvData, inputOffset, matrix, 0, area);
            return matrix;
        }

        // Otherwise copy one cropped row at a time.
        byte[] yuv = mYuvData;
        for (int y = 0; y < height; y++) {
            int outputOffset = y * width;
            System.arraycopy(yuv, inputOffset, matrix, outputOffset, width);
            inputOffset += mDataWidth;
        }
        return matrix;
    }

    @Override
    public boolean isCropSupported() {
        return true;
    }

    public Bitmap renderCroppedGreyscaleBitmap() {
        int width = getWidth();
        int height = getHeight();
        int[] pixels = new int[width * height];
        byte[] yuv = mYuvData;
        int inputOffset = mTop * mDataWidth + mLeft;

        for (int y = 0; y < height; y++) {
            int outputOffset = y * width;
            for (int x = 0; x < width; x++) {
                int grey = yuv[inputOffset + x] & 0xff;
                pixels[outputOffset + x] = 0xFF000000 | (grey * 0x00010101);
            }
            inputOffset += mDataWidth;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private void reverseHorizontal(int width, int height) {
        byte[] yuvData = this.mYuvData;
        for (int y = 0, rowStart = mTop * mDataWidth + mLeft; y < height; y++, rowStart += mDataWidth) {
            int middle = rowStart + width / 2;
            for (int x1 = rowStart, x2 = rowStart + width - 1; x1 < middle; x1++, x2--) {
                byte temp = yuvData[x1];
                yuvData[x1] = yuvData[x2];
                yuvData[x2] = temp;
            }
        }
    }
}
