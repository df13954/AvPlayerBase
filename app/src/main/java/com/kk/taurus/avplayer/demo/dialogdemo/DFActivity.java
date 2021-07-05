package com.kk.taurus.avplayer.demo.dialogdemo;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.kk.taurus.avplayer.R;
import com.kk.taurus.avplayer.demo.FullDialogVideoFragment;
import com.kk.taurus.avplayer.demo.LocationInfo;

public class DFActivity extends AppCompatActivity {

    private FullDialogVideoFragment mFullDialogVideoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfactivity);
    }

    public void showFragment(View view) {
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.x=0;
        locationInfo.y=100;
        locationInfo.w=640;
        locationInfo.h=320;
        mFullDialogVideoFragment = FullDialogVideoFragment.newInstance(1000, "",locationInfo);
        mFullDialogVideoFragment.show(getSupportFragmentManager(), "testsfs");
    }

    @Override
    public void onBackPressed() {

        if (mFullDialogVideoFragment != null && mFullDialogVideoFragment.isVisible()) {
            mFullDialogVideoFragment.dismiss();
            return;
        }
        super.onBackPressed();
    }
}