package br.com.cast.ticket.app.util.qrcode;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.zxing.BarcodeFormat;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

final class DecodeFormatManager {

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    private static final String TAG = DecodeFormatManager.class.getSimpleName();

    static final Vector<BarcodeFormat> QR_CODE_FORMATS;

    static {
        QR_CODE_FORMATS = new Vector<>();
        QR_CODE_FORMATS.add(BarcodeFormat.QR_CODE);
    }

    static Vector<BarcodeFormat> parseDecodeFormats(final Intent intent) {
        List<String> scanFormats = null;
        String scanFormatsString = intent.getStringExtra(Intents.Scan.FORMATS);
        if (scanFormatsString != null) {
            scanFormats = Arrays.asList(COMMA_PATTERN.split(scanFormatsString));
        }
        return parseDecodeFormats(scanFormats, intent.getStringExtra(Intents.Scan.MODE));
    }

    static Vector<BarcodeFormat> parseDecodeFormats(Uri inputUri) {
        List<String> formats = inputUri.getQueryParameters(Intents.Scan.FORMATS);
        if (formats != null && formats.size() == 1 && formats.get(0) != null) {
            formats = Arrays.asList(COMMA_PATTERN.split(formats.get(0)));
        }
        return parseDecodeFormats(formats, inputUri.getQueryParameter(Intents.Scan.MODE));
    }

    private static Vector<BarcodeFormat> parseDecodeFormats(Iterable<String> scanFormats,
                                                            String decodeMode) {
        if (scanFormats != null) {
            Vector<BarcodeFormat> formats = new Vector<>();
            try {
                for (String format : scanFormats) {
                    formats.add(BarcodeFormat.valueOf(format));
                }
                return formats;
            } catch (IllegalArgumentException iae) {
                Log.w(TAG, iae.getMessage());
            }
        }
        if (decodeMode != null) {
            if (Intents.Scan.QR_CODE_MODE.equals(decodeMode)) {
                return QR_CODE_FORMATS;
            }
        }
        return null;
    }
}
