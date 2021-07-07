package com.kk.taurus.avplayer.demo.pop;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.kk.taurus.avplayer.R;
import com.kk.taurus.avplayer.cover.HmGestureCover;
import com.kk.taurus.avplayer.play.DataInter;
import com.kk.taurus.avplayer.play.ReceiverGroupManager;
import com.kk.taurus.playerbase.assist.InterEvent;
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.player.IPlayer;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.render.AspectRatio;
import com.kk.taurus.playerbase.widget.BaseVideoView;

import static com.kk.taurus.avplayer.play.DataInter.ReceiverKey.KEY_GESTURE_COVER;

public class HmPopVideo implements LifecycleObserver, OnPlayerEventListener {
    private static final String TAG = "HmPopVideo";
    private ReceiverGroup mReceiverGroup;
    private BaseVideoView mVideoView;

    private boolean userPause;
    private boolean isLandscape;


    private final PopupWindow mPopupWindow;
    private int mMarginLeft;
    private int mMarginTop;
    private int mDefWidth;
    private int mDefHeight;
    private HmGestureCover mGestureCover;
    private int mScreenWidth;
    private int mScreenHeight;
    HmEgretVideoPlayer videoMessage;


    public HmPopVideo(Activity activity, View tagView, FrameLayout mRootView, HmEgretVideoPlayer videoPlayer) {
        Log.i(TAG, "HmPopVideo: 创建pop");
        this.videoMessage = videoPlayer;
        mScreenWidth = mRootView.getWidth();
        mScreenHeight = mRootView.getHeight();
        HmEgretVideoPlayer.EgretVideoPlayer data = videoPlayer.getData();
        mMarginLeft = data.getRect().get(0);
        mMarginTop = data.getRect().get(1);
        mDefWidth = data.getRect().get(2);
        mDefHeight = data.getRect().get(3);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.hm_dialog_float_video_layout, null, false);
        mPopupWindow = new PopupWindow(contentView, mDefWidth, mDefHeight, false);
        mPopupWindow.setAnimationStyle(0);
        // mPopupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        // mPopupWindow = new PopupWindow(contentView, mScreenWidth / 2, mScreenWidth / 2, false);
        // mPopupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, false);
        // popupWindow.setClippingEnabled(false);
        //穿透,必须在showAtLocation之前设置，不然无效
        // mPopupWindow.setTouchable(0 != videoPlayer.getData().getVideoControl());
        mPopupWindow.setTouchable(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x000000));
        //设置PopupWindow内容区域外的区域是否响应点击事件（true：响应；false：不响应【默认】）
        mPopupWindow.setOutsideTouchable(false);

        // mPopupWindow.showAtLocation(tagView, Gravity.LEFT, 0, 0);
        mPopupWindow.showAsDropDown(tagView, mMarginLeft, mMarginTop);
        mPopupWindow.setOnDismissListener(mOnDismissListener);
        mVideoView = contentView.findViewById(R.id.exoVideo);
        contentView.findViewById(R.id.iv_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });
        mVideoView.setOnPlayerEventListener(this);
        mVideoView.setEventHandler(onVideoViewEventHandler);
        if (0 != videoPlayer.getData().getVideoControl()) {
            //设置控制面板
            //设置播放器控制块
            mReceiverGroup = ReceiverGroupManager.get().getReceiverGroup(activity);
            mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
            mGestureCover = mReceiverGroup.getReceiver(KEY_GESTURE_COVER);
            mVideoView.setReceiverGroup(mReceiverGroup);
        }
        if (HmEgretVideoPlayer.FIT_MODE_FILL != videoMessage.getData().getFitMode()) {
            mVideoView.setAspectRatio(AspectRatio.AspectRatio_FILL_PARENT);
        } else {
            mVideoView.setAspectRatio(AspectRatio.AspectRatio_FIT_PARENT);
        }
        mVideoView.post(() -> {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
            layoutParams.width = mDefWidth;
            layoutParams.height = mDefHeight;
            // layoutParams.leftMargin = mMarginLeft;
            // layoutParams.topMargin = mMarginTop;
            // if (mGestureCover != null) {
            //     mGestureCover.updateRectInfo(mDefWidth, mDefHeight);
            // }
            mVideoView.setLayoutParams(layoutParams);
            mVideoView.setVisibility(View.VISIBLE);
            DataSource dataSource = new DataSource(data.getUrl());
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
        Log.i(TAG, "onResume: pop恢复");
        if (mVideoView == null) {
            return;
        }
        int state = mVideoView.getState();
        if (state == IPlayer.STATE_PLAYBACK_COMPLETE)
            return;
        if (mVideoView.isInPlaybackState()) {
            if (!userPause) {
                mVideoView.resume();
            }
        } else {
            mVideoView.rePlay(0);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause() {
        Log.i(TAG, "onPause: pop");
        if (mVideoView == null) {
            return;
        }
        int state = mVideoView.getState();
        Log.i(TAG, "onPause: state:" + state);
        if (state == IPlayer.STATE_PLAYBACK_COMPLETE
                || state == IPlayer.STATE_STOPPED
                || state == IPlayer.STATE_END
                || state == IPlayer.STATE_ERROR
        ) {
            Log.i(TAG, "onPause: complete or stopped");
            return;
        }
        if (mVideoView.isInPlaybackState()) {
            mVideoView.pause();
        } else {
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
                case DataInter.Event.EVENT_CODE_ERROR_SHOW:
                    mVideoView.stop();
                    break;
                case DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN:
                    updateVideo(!isLandscape);
                    break;
            }
        }

        @Override
        public void requestRetry(BaseVideoView videoView, Bundle bundle) {

        }
    };

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        Log.i(TAG, "onDestroy: pop");
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        if (eventCode == OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE) {

        }
    }

    public void dismissPop() {
        Log.i(TAG, "dismissPop: ");
        onDestroy();
        if (mPopupWindow != null) {
            //移除监听
            mPopupWindow.setOnDismissListener(null);
            mPopupWindow.dismiss();
        }
    }

    private void updateVideo(boolean landscape) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
        if (landscape) {
            //横屏，全屏处理
            // mPopupWindow.setWidth(mScreenWidth);
            // mPopupWindow.setHeight(mScreenHeight);
            layoutParams.width = mScreenWidth;//ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = mScreenHeight;//ViewGroup.LayoutParams.MATCH_PARENT;
            mPopupWindow.setAnimationStyle(0);
            mPopupWindow.update(0, 0, mScreenWidth, mScreenHeight);
            // layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            // layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            // layoutParams.setMargins(0, 0, 0, 0);
        } else {
            //todo 宽度恢复
            mPopupWindow.setAnimationStyle(0);
            mPopupWindow.update(mMarginLeft, mMarginTop, mDefWidth, mDefHeight);
            // mPopupWindow.up
            layoutParams.width = -1;
            layoutParams.height = -1;
            layoutParams.topMargin = 0;
            // layoutParams.leftMargin = mMarginLeft;
        }
        mVideoView.setLayoutParams(layoutParams);
        isLandscape = landscape;

    }
}
