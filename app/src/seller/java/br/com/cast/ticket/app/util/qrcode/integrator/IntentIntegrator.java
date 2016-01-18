package br.com.cast.ticket.app.util.qrcode.integrator;

import android.app.Activity;
import android.content.Intent;

import br.com.cast.ticket.app.util.qrcode.Intents;
import br.com.cast.ticket.app.util.qrcode.ScannerCodeActivity;

public final class IntentIntegrator {

    public static final int REQUEST_CODE = 12685;

    public static void initiateScan(Activity activity, int layoutResId, int viewFinderViewResId, int previewViewResId, boolean useFrontLight) {
        Intent intent = new Intent(activity, ScannerCodeActivity.class);
        intent.putExtra("layoutResId", layoutResId);
        intent.putExtra("viewFinderViewResId", viewFinderViewResId);
        intent.putExtra("previewViewResId", previewViewResId);
        intent.putExtra("useFrontLight", useFrontLight);
        activity.startActivity(intent);
    }

    public static IntentResult parseActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String contents = intent.getStringExtra(Intents.Scan.RESULT);
                String formatName = intent.getStringExtra(Intents.Scan.RESULT_FORMAT);
                return new IntentResult(contents, formatName);
            } else {
                return new IntentResult(null, null);
            }
        }
        return null;
    }
}
