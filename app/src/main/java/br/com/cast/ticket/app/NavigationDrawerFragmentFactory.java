package br.com.cast.ticket.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Factory for creation of specific fragments from {@link NavigationDrawerMenu} item.
 *
 * @author falvojr
 */
public final class NavigationDrawerFragmentFactory {

    public static final String TAG = NavigationDrawerFragmentFactory.class.getSimpleName();

    public static Fragment newInstance(NavigationDrawerMenu menu) {
        Fragment specificFragment = null;
        try {
            specificFragment = menu.getFragmentClass().newInstance();
            Bundle args = new Bundle();
            args.putInt(BaseFragment.ARG_SECTION_NUMBER, menu.ordinal());
            specificFragment.setArguments(args);
        } catch (Exception unexpectedException) {
            Log.e(TAG, unexpectedException.getMessage());
        }
        return specificFragment;
    }

}
