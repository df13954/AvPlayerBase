package com.kk.taurus.avplayer.demo;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.kk.taurus.avplayer.R;
import com.kk.taurus.avplayer.cover.PopCompleteCover;
import com.kk.taurus.avplayer.play.DataInter;
import com.kk.taurus.avplayer.play.ReceiverGroupManager;
import com.kk.taurus.playerbase.assist.InterEvent;
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import static com.kk.taurus.avplayer.play.DataInter.ReceiverKey.KEY_POP_COMPLETE_COVER;

public class DemoPopVideo implements LifecycleObserver, OnPlayerEventListener {
    private static final String TAG = "DemoPopVideo";
    private final ReceiverGroup mReceiverGroup;
    private final View mIvDismiss;
    private BaseVideoView mVideoView;

    private boolean userPause;
    private boolean isLandscape;


    private final PopupWindow mPopupWindow;

    public DemoPopVideo(Activity activity,  String videoUrl) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.hm_dialog_float_video_layout, null, false);
        mPopupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x000000));
        mPopupWindow.setOnDismissListener(mOnDismissListener);
        mVideoView = contentView.findViewById(R.id.exoVideo);
        mIvDismiss = contentView.findViewById(R.id.iv_dismiss);
        mIvDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });
        mReceiverGroup = ReceiverGroupManager.get().getReceiverPopGroup(activity);
        PopCompleteCover popCompleteCover = mReceiverGroup.getReceiver(KEY_POP_COMPLETE_COVER);
        if (popCompleteCover != null) {
            popCompleteCover.setITestCallBack(new PopCompleteCover.ITestCallBack() {
                @Override
                public void openTest() {
                    Toast.makeText(activity, "test", Toast.LENGTH_SHORT).show();
                }
            });
        }
        mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
        mVideoView.setReceiverGroup(mReceiverGroup);
        mVideoView.setEventHandler(onVideoViewEventHandler);
        mVideoView.setOnPlayerEventListener(this);

        mVideoView.post(() -> {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
            // int marginLeft = data.getRect().get(0);
            // int marginTop = data.getRect().get(1);
            // int w = data.getRect().get(2);
            // int h = data.getRect().get(3);
            // layoutParams.width = w;
            // layoutParams.leftMargin = marginLeft;
            // layoutParams.topMargin = marginTop;
            // layoutParams.height = h;
            mVideoView.setLayoutParams(layoutParams);
            mVideoView.setVisibility(View.VISIBLE);
            DataSource dataSource = new DataSource(videoUrl);
            mVideoView.setDataSource(dataSource);
            mVideoView.start();
        });
    }

    private PopupWindow.OnDismissListener mOnDismissListener = new PopupWindow.OnDismissListener() {
        @Override
        public void onDismiss() {
            onDestroy();
        }
    };

    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume() {
        Log.i(TAG, "OnLifecycleEvent onResume: ");
        // if (mVideoView == null) {
        //     return;
        // }
        // int state = mVideoView.getState();
        // if (state == IPlayer.STATE_PLAYBACK_COMPLETE) {
        //     Log.i(TAG, "onResume: complete");
        //     return;
        // }
        // if (mVideoView.isInPlaybackState()) {
        //     if (!userPause) {
        //         Log.i(TAG, "userPause onResume: resume");
        //         mVideoView.resume();
        //     }
        // } else {
        //     Log.i(TAG, "onResume: rePlay");
        //     mVideoView.rePlay(0);
        // }
    }

    //@OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause() {
        Log.i(TAG, "OnLifecycleEvent onPause: ");
        if (mVideoView == null) {
            return;
        }
        int state = mVideoView.getState();
        if (state == IPlayer.STATE_PLAYBACK_COMPLETE) {
            Log.i(TAG, "onPause: complete");
            return;
        }
        if (mVideoView.isInPlaybackState()) {
            Log.i(TAG, "onPause: pause");
            mVideoView.pause();
        } else {
            Log.i(TAG, "onPause: stop");
            mVideoView.stop();
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
                case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                    // setRequestedOrientation(isLandscape ?
                    //         ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                    //         ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    //全屏按钮点击后逻辑处理
                    // Toast.makeText(assist.getContext(), "全屏", Toast.LENGTH_SHORT).show();
                    if (isLandscape) {
                        updateVideo(false);
                    } else {
                        updateVideo(true);
                    }
                    break;
            }
        }

        @Override
        public void requestRetry(BaseVideoView videoView, Bundle bundle) {

        }
    };

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        if (mVideoView != null) {
            Log.i(TAG, "onDestroy: stop");
            mVideoView.stopPlayback();
        }
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE) {
            Log.i(TAG, "onPlayerEvent: 视频正常播放完成");

        }
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
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flTag.getLayoutParams();
            // layoutParams = params;
        }
        mVideoView.setLayoutParams(layoutParams);
        isLandscape = landscape;

    }

    public void dismissPop() {
        onDestroy();
        if (mPopupWindow != null) {
            //移除监听
            Log.i(TAG, "dismissPop: dismiss");
            mPopupWindow.setOnDismissListener(null);
            mPopupWindow.dismiss();
        }
    }
}
