package br.com.cast.ticket.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.cast.ticket.R;

/**
 * A simple {@link BaseFragment} subclass.
 *
 * @author falvojr
 */
public class AboutFragment extends BaseFragment<MainActivity> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_about, container, false);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        super.getSpecificActivity().onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
