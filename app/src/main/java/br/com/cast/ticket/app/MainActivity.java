package br.com.cast.ticket.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import br.com.cast.ticket.R;

/**
 * Activity that manage the {@link BaseFragment}'s.
 *
 * @author falvojr
 */
public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private OnBaseActivityResult baseActivityResult;
    public static final int BASE_RESULT_RCODE = 111;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * Restore to activity login
     */
    static public boolean ACTIVITY_BACK_LOGIN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Remove shadow below action bar
        getSupportActionBar().setElevation(0);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getBaseActivityResult() != null && requestCode == BASE_RESULT_RCODE) {
            getBaseActivityResult().onBaseActivityResult(requestCode, resultCode, data);
            setBaseActivityResult(null);
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        final NavigationDrawerMenu menu = NavigationDrawerMenu.valueOf(position);
        if (menu.getFragmentClass() == null || ACTIVITY_BACK_LOGIN==true) {
            final Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            // update the main content by replacing fragments
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, NavigationDrawerFragmentFactory.newInstance(menu)).commit();
        }
    }

    public void onSectionAttached(int position) {
        NavigationDrawerMenu menu = NavigationDrawerMenu.valueOf(position);
        mTitle = getString(menu.getTitleRes());
    }

    private void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen if the drawer is not
            // showing. Otherwise, let the drawer decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        // TODO Specific menus here!
        return super.onOptionsItemSelected(item);
    }

    public interface OnBaseActivityResult{
        void onBaseActivityResult(int requestCode, int resultCode, Intent data);
    }

    public OnBaseActivityResult getBaseActivityResult() {
        return baseActivityResult;
    }

    public void setBaseActivityResult(OnBaseActivityResult baseActivityResult) {
        this.baseActivityResult = baseActivityResult;
    }
}
