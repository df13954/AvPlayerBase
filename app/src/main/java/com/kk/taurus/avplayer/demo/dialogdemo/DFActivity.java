package com.kk.taurus.avplayer.demo.dialogdemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.kk.taurus.avplayer.R;
import com.kk.taurus.avplayer.demo.FullDialogVideoFragment;
import com.kk.taurus.avplayer.demo.LocationInfo;
import com.kk.taurus.avplayer.demo.pop.HmEgretVideoPlayer;
import com.kk.taurus.avplayer.demo.pop.HmPopVideoTest;
import com.kk.taurus.avplayer.demo.pop.PopVideoDemo;
import com.kk.taurus.avplayer.utils.HmGsonUtils;

public class DFActivity extends AppCompatActivity {

    private FullDialogVideoFragment mFullDialogVideoFragment;
    private FrameLayout rootView;
    private RelativeLayout relativeLayout;
    private HmPopVideoTest mPopVideo;
    private View tagView;
    private PopVideoDemo mPopVideoDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dfactivity);
        initView();
        tagView.post(new Runnable() {
            @Override
            public void run() {
                displayPopDemo(null);
            }
        });
    }

    public void showFragment(View view) {
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.x = 0;
        locationInfo.y = 100;
        locationInfo.w = 640;
        locationInfo.h = 320;
        mFullDialogVideoFragment = FullDialogVideoFragment.newInstance(1000, "", locationInfo);
        mFullDialogVideoFragment.show(getSupportFragmentManager(), "testsfs");
    }

    @Override
    public void onBackPressed() {

        if (mFullDialogVideoFragment != null && mFullDialogVideoFragment.isVisible()) {
            mFullDialogVideoFragment.dismiss();
            return;
        }
        if (mPopVideo != null && mPopVideo.getPopupWindow() != null && mPopVideo.getPopupWindow().isShowing()) {
            mPopVideo.getPopupWindow().dismiss();
            getLifecycle().removeObserver(mPopVideo);
            mPopVideo = null;
            return;
        }
        if (mPopVideoDemo != null && mPopVideoDemo.getPopupWindow() != null && mPopVideoDemo.getPopupWindow().isShowing()) {
            mPopVideoDemo.getPopupWindow().dismiss();
            getLifecycle().removeObserver(mPopVideoDemo);
            mPopVideoDemo = null;
            return;
        }
        super.onBackPressed();
    }

    public void displayPop(View view) {
        //pop固定大小，且可以点击地下的控件
        HmEgretVideoPlayer bean = HmGsonUtils.jsonToBean(ms, HmEgretVideoPlayer.class);
        createVideoPopupWindow(bean);
    }

    private void createVideoPopupWindow(HmEgretVideoPlayer videoPlayer) {
        mPopVideo = new HmPopVideoTest(this, tagView, rootView, videoPlayer);
        getLifecycle().addObserver(mPopVideo);
    }

    private void initView() {
        rootView = (FrameLayout) findViewById(R.id.rootView);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        tagView = (View) findViewById(R.id.tag_view);
    }

    String ms = "{\n" +
            "        \"protocol\": \"100128\",\n" +
            "        \"data\": {\n" +
            "            \"uid\": 115,\n" +
            "            \"url\": \"https:\\/\\/mov.bn.netease.com\\/open-movie\\/nos\\/mp4\\/2017\\/05\\/31\\/SCKR8V6E9_hd.mp4\",\n" +
            "            \"seek\": 0,\n" +
            "            \"operType\": 1,\n" +
            "            \"layerIndex\": 1,\n" +
            "            \"videoControl\": 1,\n" +
            "            \"fitMode\": 2,\n" +
            "            \"rect\": [\n" +
            "                100,\n" +
            "                100,\n" +
            "                1000,\n" +
            "                619\n" +
            "            ]\n" +
            "        }\n" +
            "    }";

    public void testToast(View view) {
        // Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
        // PopupWindow popupWindow = mPopVideo.getPopupWindow();
        // if (popupWindow != null) {
        //     popupWindow.update(0, 0, 800, 400);
        // }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("HmPopVideo", "d f activity onDestroy: ");
        if (mFullDialogVideoFragment != null) {
            mFullDialogVideoFragment.dismiss();
        }

        if (mPopVideo != null && mPopVideo.getPopupWindow() != null) {
            mPopVideo.getPopupWindow().dismiss();
            getLifecycle().removeObserver(mPopVideo);
            mPopVideo = null;
        }
    }

    public void displayPopDemo(View view) {
        //pop固定大小，且可以点击地下的控件
        HmEgretVideoPlayer bean = HmGsonUtils.jsonToBean(ms, HmEgretVideoPlayer.class);
        videoPopupWindow(bean);
    }

    private void videoPopupWindow(HmEgretVideoPlayer videoPlayer) {
        mPopVideoDemo = new PopVideoDemo(this, tagView, rootView, videoPlayer);
        getLifecycle().addObserver(mPopVideoDemo);
    }
}