package com.kk.taurus.avplayer.ui;

import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.kk.taurus.avplayer.R;
import com.kk.taurus.avplayer.adapter.SettingAdapter;
import com.kk.taurus.avplayer.play.DataInter;
import com.kk.taurus.avplayer.play.ReceiverGroupManager;
import com.kk.taurus.avplayer.utils.DataUtils;
import com.kk.taurus.avplayer.utils.PUtil;
import com.kk.taurus.playerbase.assist.InterEvent;
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.widget.BaseVideoView;

public class SmallVideoViewActivity extends AppCompatActivity implements
        OnPlayerEventListener {

    private BaseVideoView mVideoView;
    private ReceiverGroup mReceiverGroup;

    private boolean userPause;
    private boolean isLandscape;
    private int margin;

    private boolean hasStart;
    private RecyclerView mRecycler;
    private SettingAdapter mAdapter;
    private FrameLayout flTag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_video_view);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);

        flTag = (FrameLayout)findViewById(R.id.fl_tag);
        mVideoView = findViewById(R.id.baseVideoView);
        mRecycler = findViewById(R.id.setting_recycler);

        margin = 0;// PUtil.dip2px(this, 2);

        // updateVideo(false);

        mReceiverGroup = ReceiverGroupManager.get().getReceiverGroup(this);
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
        mVideoView.setReceiverGroup(mReceiverGroup);
        mVideoView.setEventHandler(onVideoViewEventHandler);
        mVideoView.setOnPlayerEventListener(this);

//        mVideoView.setVolume(0f, 0f);
    }

    private void initPlay() {
        if (!hasStart) {
            DataSource dataSource = new DataSource(DataUtils.VIDEO_URL_09);
            dataSource.setTitle("音乐和艺术如何改变世界");
            mVideoView.setDataSource(dataSource);
            mVideoView.start();
            hasStart = true;
        }
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:

                break;
        }
    }

    private OnVideoViewEventHandler onVideoViewEventHandler = new OnVideoViewEventHandler() {
        @Override
        public void onAssistHandle(BaseVideoView assist, int eventCode, Bundle bundle) {
            super.onAssistHandle(assist, eventCode, bundle);
            switch (eventCode) {
                case InterEvent.CODE_REQUEST_PAUSE:
                    userPause = true;
                    break;
                case DataInter.Event.EVENT_CODE_REQUEST_BACK:
                    // if(isLandscape){
                    //     setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    // }else{
                    //     finish();
                    // }
                    break;
                case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                    // setRequestedOrientation(isLandscape ?
                    //         ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    //         ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    //全屏按钮点击后逻辑处理
                    Toast.makeText(SmallVideoViewActivity.this, "全屏", Toast.LENGTH_SHORT).show();
                    updateVideo(true);
                    break;
                case DataInter.Event.EVENT_CODE_ERROR_SHOW:
                    mVideoView.stop();
                    break;
            }
        }

        @Override
        public void requestRetry(BaseVideoView videoView, Bundle bundle) {
            if (PUtil.isTopActivity(SmallVideoViewActivity.this)) {
                super.requestRetry(videoView, bundle);
            }
        }
    };

    private void replay() {
        mVideoView.setDataSource(new DataSource(DataUtils.VIDEO_URL_09));
        mVideoView.start();
    }

    private void updateVideo(boolean landscape) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
        if (landscape) {
            //横屏，全屏处理
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.setMargins(0, 0, 0, 0);
        } else {
            //16:9 160:90 320:180 640:360
            layoutParams.width = 640;//PUtil.getScreenW(this) / 2;
            layoutParams.height = 360;//layoutParams.width / 2;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flTag.getLayoutParams();
            layoutParams = params;
        }
        mVideoView.setLayoutParams(layoutParams);
        isLandscape = landscape;

    }

    @Override
    public void onBackPressed() {
        if (isLandscape) {
            updateVideo(false);
            return;
        }
        super.onBackPressed();
    }

    // @Override
    // public void onConfigurationChanged(Configuration newConfig) {
    //     super.onConfigurationChanged(newConfig);
    //     if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
    //         isLandscape = true;
    //         updateVideo(true);
    //     }else{
    //         isLandscape = false;
    //         updateVideo(false);
    //     }
    //     mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_IS_LANDSCAPE, isLandscape);
    // }

    @Override
    protected void onPause() {
        super.onPause();
        int state = mVideoView.getState();
        if (state == IPlayer.STATE_PLAYBACK_COMPLETE)
            return;
        if (mVideoView.isInPlaybackState()) {
            mVideoView.pause();
        } else {
            mVideoView.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int state = mVideoView.getState();
        if (state == IPlayer.STATE_PLAYBACK_COMPLETE)
            return;
        if (mVideoView.isInPlaybackState()) {
            if (!userPause)
                mVideoView.resume();
        } else {
            mVideoView.rePlay(0);
        }
        initPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

}
