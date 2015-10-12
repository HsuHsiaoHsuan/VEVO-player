package idv.hsu.vevoplayer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import idv.hsu.vevoplayer.ui.Fragment_PlaylistItems;
import idv.hsu.vevoplayer.ui.Fragment_Sub;
import idv.hsu.vevoplayer.ui.IOnFragmentInteractionListener;

public class PlayActivity extends AppCompatActivity implements IOnFragmentInteractionListener {
    private static final String TAG = PlayActivity.class.getSimpleName();
    private static final boolean D = true;

    public static final String PARAM_PLAYLISTID = "PLAYLISTID";
    public static final String PARAM_PLAYLIST_TITLE = "playlistTitle";

    private String playlistId;
    private String playlistTitle;

    Toolbar toolbar;

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
        setContentView(R.layout.activity_play);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        int navBarHeight = getNavigationBarHeight();
        findViewById(R.id.frag_container).setPadding(0, 0, 0, navBarHeight);
//        }

        if (!idDeviceOnline()) {
            Toast.makeText(this, "Please connect your device on Internet", Toast.LENGTH_LONG).show();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.frag_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            Intent intent = getIntent();
            playlistId = intent.getStringExtra(PARAM_PLAYLISTID);
            playlistTitle = intent.getStringExtra(PARAM_PLAYLIST_TITLE);
            Fragment_PlaylistItems sub =
                    Fragment_PlaylistItems.newInstance(
                            intent.getStringExtra(PARAM_PLAYLISTID));

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frag_container, sub).commit();
        }
    }

    @Override
    public void onFragmentInteraction(String id, String title) {

    }

    @Override
    public void setViewPager(ViewPager viewPager) {
    }

    @Override
    public void setSubTitle(String subTitle) {

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