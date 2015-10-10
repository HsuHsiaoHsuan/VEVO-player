package idv.hsu.vevoplayer;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import idv.hsu.vevoplayer.ui.Fragment_Main;
import idv.hsu.vevoplayer.ui.IOnFragmentInteractionListener;

public class MainActivity extends AppCompatActivity implements IOnFragmentInteractionListener {

    Toolbar toolbar;
    private TabLayout mTabLayout;

    // get it from:
    // https://developers.google.com/apis-explorer/#p/youtube/v3/youtube.channels.list?part=snippet&forUsername=VEVO&_h=1&
    public static final String VEVO_CHANNEL_ID = "UC2pmfLm7iq6Ov1UwYrWYkZA";

    private boolean idDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int navBarHeight = getNavigationBarHeight();
            findViewById(R.id.frag_container).setPadding(0, 0, 0, navBarHeight);
//        }

        if (!idDeviceOnline()) {
            Toast.makeText(this, "Please connect your device on Internet", Toast.LENGTH_LONG).show();
        }

        if (findViewById(R.id.frag_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            Fragment_Main main = Fragment_Main.newInstance(VEVO_CHANNEL_ID);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frag_container, main).commit();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle("test");

        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.hide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setViewPager(ViewPager viewPager) {
        mTabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void setSubTitle(String subTitle) {
        toolbar.setSubtitle(subTitle);
    }

    private int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}
