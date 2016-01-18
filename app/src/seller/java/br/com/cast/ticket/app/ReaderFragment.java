package br.com.cast.ticket.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.cast.ticket.R;
import br.com.cast.ticket.app.util.qrcode.integrator.IntentIntegrator;

/**
 * A simple {@link BaseFragment} subclass.
 *
 * @author falvojr
 * @author maikotrindade
 */
public class ReaderFragment extends BaseFragment<MainActivity> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_reader, container, false);
        bindElements(view);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        super.getSpecificActivity().onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void bindElements(View view) {
        View btnScan = view.findViewById(R.id.btn_action_scan);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator.initiateScan(getActivity(), R.layout.scanner_code,
                        R.id.viewfinder_view, R.id.preview_view, true);
            }
        });
    }
}