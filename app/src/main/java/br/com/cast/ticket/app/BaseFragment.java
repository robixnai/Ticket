package br.com.cast.ticket.app;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import br.com.cast.ticket.util.AppUtil;

/**
 * Base navigation-drawer-based fragment.
 *
 * @author falvojr
 */
public abstract class BaseFragment<T extends AppCompatActivity> extends Fragment {

    public static final String TAG = BaseFragment.class.getSimpleName();

    /**
     * The fragment argument representing the section number for this fragment.
     */
    public static final String ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER";

    public BaseFragment() {
        super();
    }

    protected T getSpecificActivity() {
        return AppUtil.get(super.getActivity());
    }
}
