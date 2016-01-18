package br.com.cast.ticket.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.cast.ticket.R;
import br.com.cast.ticket.entity.Ticket;
import br.com.cast.ticket.entity.TicketType;
import br.com.cast.ticket.util.AppUtil;

/**
 * A simple {@link BaseFragment} subclass.
 *
 * @author falvojr
 */
public class TicketListFragment extends BaseFragment<MainActivity> {

    private static final int REQUEST_CODE_TICKET = 1;

    private View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_ticket_list, container, false);
        bindElements();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initViewPagerAndTabs();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        super.getSpecificActivity().onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void initViewPagerAndTabs() {
        final Ticket ticket = new Ticket();
        final ViewPager viewPager = (ViewPager) mView.findViewById(R.id.viewPager);
        final PagerAdapter pagerAdapter = new PagerAdapter(getFragmentManager());
        final TabLayout tabLayout = (TabLayout) mView.findViewById(R.id.tabLayout);

        if (viewPager.getAdapter() == null) {
            pagerAdapter.addFragment(TicketListPagerFragment.createInstance(ticket.getAll(TicketType.BUS)), getString(R.string.tab_bus));
            pagerAdapter.addFragment(TicketListPagerFragment.createInstance(ticket.getAll(TicketType.SUBWAY)), getString(R.string.tab_subway));
            viewPager.setAdapter(pagerAdapter);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    private void bindElements() {
        FloatingActionButton floatNewTicket = AppUtil.get(mView.findViewById(R.id.floatNewTicket));
        floatNewTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent goToPurcheseTicket = new Intent(mView.getContext(), TicketActivity.class);
                startActivityForResult(goToPurcheseTicket, REQUEST_CODE_TICKET);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_TICKET) {
            if (resultCode == Activity.RESULT_OK)
                Toast.makeText(mView.getContext(), R.string.message_buyer_ticket_success, Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    static class PagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

}
