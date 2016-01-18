package br.com.cast.ticket.app;

import android.app.Application;
import android.content.Context;

/**
 * Created by falvojr on 8/7/2015.
 */
public class TicketApplication extends Application {

    private static Context sApplicationContext;

    public static Context getContext() {
        return sApplicationContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApplicationContext = super.getApplicationContext();
    }
}
