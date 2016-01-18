package br.com.cast.ticket.app.util.qrcode;

import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;

final class ViewfinderResultPointCallback implements ResultPointCallback {

    private final ViewfinderView mViewfinderView;

    ViewfinderResultPointCallback(ViewfinderView viewfinderView) {
        this.mViewfinderView = viewfinderView;
    }

    public void foundPossibleResultPoint(ResultPoint point) {
        mViewfinderView.addPossibleResultPoint(point);
    }
}
