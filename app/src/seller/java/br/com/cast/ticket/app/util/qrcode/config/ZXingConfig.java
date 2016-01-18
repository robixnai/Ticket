package br.com.cast.ticket.app.util.qrcode.config;

import java.io.Serializable;

public class ZXingConfig implements Serializable {
    private static final long serialVersionUID = -718816522243056216L;
    public static final String INTENT_KEY = "zxingConfig";
    public boolean copyToClipboard = false;
    public boolean vibrateOnDecoded = false;
    public boolean playBeepOnDecoded = true;
    public boolean useFrontLight = false;
    public boolean reverseImage = false;
}