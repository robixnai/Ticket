package br.com.cast.ticket.app;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import br.com.cast.ticket.R;

/**
 * Seller navigation drawer menu enumeration.
 *
 * @author falvojr
 */
public enum NavigationDrawerMenu {
    SECTION_01(R.string.title_menu_tickets, TicketListFragment.class),
    SECTION_02(R.string.title_menu_config, ConfigFragment.class),
    SECTION_03(R.string.title_menu_about, AboutFragment.class),
    SECTION_04(R.string.title_menu_exit);

    private static final Map<Integer,NavigationDrawerMenu> sLookup = new HashMap<>();

    static {
        for(NavigationDrawerMenu menu : EnumSet.allOf(NavigationDrawerMenu.class)) {
            sLookup.put(menu.ordinal(), menu);
        }
    }

    private int mTitleRes;
    private Class<? extends Fragment>  mFragmentClass;

    private NavigationDrawerMenu(int titleRes, Class<? extends Fragment> fragmentClass) {
        mTitleRes = titleRes;
        mFragmentClass = fragmentClass;
    }

    private NavigationDrawerMenu(int titleRes) {
        mTitleRes = titleRes;
    }

    public int getTitleRes() {
        return mTitleRes;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return mFragmentClass;
    }

    public static NavigationDrawerMenu valueOf(int ordinal) {
        return sLookup.get(ordinal);
    }

    public static String[] titles(Context context) {
        final NavigationDrawerMenu[] values = values();
        final String[] titles = new String[values.length];
        for(int i = 0; i < values.length; i++) {
            titles[i] = context.getString(values[i].getTitleRes());
        }
        return titles;
    }
}
