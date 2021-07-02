package com.kk.taurus.avplayer.demo.dialogdemo;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.kk.taurus.avplayer.R;
import com.kk.taurus.avplayer.demo.MyDialogFragment;

public class DFActivity extends AppCompatActivity {

    private MyDialogFragment mMyDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfactivity);
    }

    public void showFragment(View view) {
        mMyDialogFragment = MyDialogFragment.newInstance(1000, "");
        mMyDialogFragment.show(getSupportFragmentManager(), "testsfs");
    }

    @Override
    public void onBackPressed() {

        if (mMyDialogFragment != null && mMyDialogFragment.isVisible()) {
            mMyDialogFragment.dismiss();
            return;
        }
        super.onBackPressed();
    }
}