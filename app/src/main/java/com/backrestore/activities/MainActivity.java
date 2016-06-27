package com.backrestore.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.backrestore.R;
import com.backrestore.SlidingTabLayout;
import com.backrestore.adapters.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView buildModel = (TextView) findViewById(R.id.build_model);
        if (buildModel != null) buildModel.setText(Build.MODEL);

        getDeviceSize();
        setViewPager();
    }

    private void setViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(viewPager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            mSlidingTabLayout.setElevation(2);
    }

    public void getDeviceSize() {
        final long SIZE_KB = 1024L;
        final long SIZE_MB = SIZE_KB * SIZE_KB;

        long availableSpace = -1L;
        /*StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        availableSpace = stat.getAvailableBlocks() * (long) stat.getBlockSize();

        TextView mFilesCount = (TextView) findViewById(R.id.files_count);
        mFilesCount.setText(String.format("%sMB", String.valueOf(availableSpace / SIZE_MB)));*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

}
