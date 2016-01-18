package br.com.cast.ticket.app.util.qrcode.integrator;

public final class IntentResult {

    private final String mContents;
    private final String mFormatName;

    IntentResult(String contents, String formatName) {
        this.mContents = contents;
        this.mFormatName = formatName;
    }

    public String getContents() {
        return mContents;
    }

    public String getFormatName() {
        return mFormatName;
    }

    @Override
    public String toString() {
        return "IntentResult [Contents=" + mContents + ", FormatName=" + mFormatName + "]";
    }
}
