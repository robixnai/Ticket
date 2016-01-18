package br.com.cast.ticket.app.util.qrcode;

public final class Intents {
    private Intents() {
    }
    public static final class Scan {
        public static final String MODE = "SCAN_MODE";
        public static final String QR_CODE_MODE = "QR_CODE_MODE";
        public static final String FORMATS = "SCAN_FORMATS";
        public static final String CHARACTER_SET = "CHARACTER_SET";
        public static final String WIDTH = "SCAN_WIDTH";
        public static final String HEIGHT = "SCAN_HEIGHT";
        public static final String RESULT = "SCAN_RESULT";
        public static final String RESULT_FORMAT = "SCAN_RESULT_FORMAT";
        public static final String RESULT_BYTES = "SCAN_RESULT_BYTES";
        private Scan() {
        }
    }
}
