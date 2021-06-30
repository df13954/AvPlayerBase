package com.kk.taurus.avplayer.demo;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.kk.taurus.avplayer.R;
import com.kk.taurus.avplayer.utils.DataUtils;

/**
 * dialog fragment中显示
 */
public class DemoDialogActivity extends AppCompatActivity {


    private ConstraintLayout rootView;
    private TextView textView;
    private Button button;
    private DemoPopVideo mPopVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_dialog);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
    }

    public void displayVideo(View view) {
        createVideoPopupWindow(DataUtils.VIDEO_URL_09);
    }

    private void createVideoPopupWindow(String url) {
        mPopVideo = new DemoPopVideo(this, url);
        //getLifecycle().addObserver(mPopVideo);
        PopupWindow popupWindow = mPopVideo.getPopupWindow();
        if (popupWindow != null) {
            popupWindow.showAtLocation(rootView, Gravity.TOP, 0, 0);
        }

    }


    private void initView() {
        rootView = (ConstraintLayout) findViewById(R.id.rootView);
        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DemoDialogActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });
    }
}