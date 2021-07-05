package com.kk.taurus.avplayer.demo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kk.taurus.avplayer.R;
import com.kk.taurus.avplayer.play.DataInter;
import com.kk.taurus.avplayer.play.ReceiverGroupManager;
import com.kk.taurus.avplayer.utils.DataUtils;
import com.kk.taurus.avplayer.utils.PUtil;
import com.kk.taurus.playerbase.assist.InterEvent;
import com.kk.taurus.playerbase.assist.OnVideoViewEventHandler;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;
import com.kk.taurus.playerbase.widget.BaseVideoView;


public class FullDialogVideoFragment extends DialogFragment implements
        OnPlayerEventListener {
    public static final String KEY_SCREENINFO = "screenInfo";
    private BaseVideoView mVideoView;
    private ReceiverGroup mReceiverGroup;
    private boolean isLandscape;
    private boolean hasStart;
    private LocationInfo mLocationInfo;

    public static FullDialogVideoFragment newInstance(long id, String name, LocationInfo locationInfo) {
        FullDialogVideoFragment dialog = new FullDialogVideoFragment();
        // 设置主题，这里只能通过xml方式设置主题，不能通过Java代码处理，因为这是getWindow还是null，
        // 而且window的几乎所有属性，都可以通过xml设置
        dialog.setStyle(STYLE_NORMAL, R.style.MyDialogTheme);
        // 设置触摸、点击弹窗外部不可关闭
        dialog.setCancelable(false);
        // 对于DialogFragment，设置外部传的参数，通过bundle设置，然后在onCreateView读取
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putString("name", name);
        bundle.putParcelable(KEY_SCREENINFO, locationInfo);
        // 把外部传进的参数放到bundle里， 在onCreateView里通过继续getArguments()读取参数，
        // 通过bundle来处理，是因为就算DialogFragment被重建了，也能恢复回来并初始化
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
            // 设置宽度为屏宽, 靠近屏幕底部。
            Window win = getDialog().getWindow();
            // 一定要设置Background，如果不设置，window属性设置无效
            win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            WindowManager.LayoutParams params = win.getAttributes();
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            // 这条就是控制点击背景的时候  如果被覆盖的view有点击事件那么就会直接触发(dialog消失并且触发背景下面view的点击事件)
            params.gravity = Gravity.CENTER;
            // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
            // if (getArguments() != null) {
            //     int id = (int) getArguments().getLong("id");
            //     params.width = id;// ViewGroup.LayoutParams.MATCH_PARENT;
            //     params.height = id;// ViewGroup.LayoutParams.WRAP_CONTENT;
            // }


            win.setAttributes(params);
        }
        return inflater.inflate(R.layout.dialog_fragment_full_layout, container, false);

    }

    private int w;
    private int h;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Bundle arguments = getArguments();
        if (arguments != null) {
            int id = (int) arguments.getLong("id");
            mLocationInfo = arguments.getParcelable(KEY_SCREENINFO);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(id, id);
            //view.setLayoutParams(lp);
            w = mLocationInfo.w;
            h = mLocationInfo.h;
        }
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //video
        mVideoView = view.findViewById(R.id.baseVideoView);
        mVideoView.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
                layoutParams.width = w;
                layoutParams.height = h;
                layoutParams.leftMargin = mLocationInfo.x;
                layoutParams.topMargin = mLocationInfo.y;
                mVideoView.setLayoutParams(layoutParams);
                //设置播放器控制块
                mReceiverGroup = ReceiverGroupManager.get().getReceiverGroup(getContext());
                mReceiverGroup.getGroupValue().putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
                mVideoView.setReceiverGroup(mReceiverGroup);
                mVideoView.setEventHandler(onVideoViewEventHandler);
                mVideoView.setOnPlayerEventListener(FullDialogVideoFragment.this);
                initPlay();
            }
        });
    }

    private boolean userPause;

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
                    // Toast.makeText(SmallVideoViewActivity.this, "全屏", Toast.LENGTH_SHORT).show();

                    updateVideo(!isLandscape);
                    break;
                case DataInter.Event.EVENT_CODE_ERROR_SHOW:
                    mVideoView.stop();
                    break;
            }
        }

        @Override
        public void requestRetry(BaseVideoView videoView, Bundle bundle) {
            if (PUtil.isTopActivity(getActivity())) {
                super.requestRetry(videoView, bundle);
            }
        }
    };


    private void initPlay() {
        if (!hasStart) {
            DataSource dataSource = new DataSource(DataUtils.VIDEO_URL_09);
            dataSource.setTitle("音乐和艺术如何改变世界");
            mVideoView.setDataSource(dataSource);
            mVideoView.start();
            hasStart = true;
        }
    }

    private void updateVideo(boolean landscape) {
        Log.i(TAG, "updateVideo: 触发横屏？:" + landscape);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
        if (landscape) {
            //横屏，全屏处理
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.setMargins(0, 0, 0, 0);
        } else {
            //16:9 160:90 320:180 640:360 w = 960;
            //             h = 540;
            layoutParams.width = w;//PUtil.getScreenW(this) / 2;
            layoutParams.height = h;//layoutParams.width / 2;
            layoutParams.topMargin = mLocationInfo.y;
            layoutParams.leftMargin = mLocationInfo.x;
            // RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) flTag.getLayoutParams();
            // layoutParams = params;
        }
        mVideoView.setLayoutParams(layoutParams);
        isLandscape = landscape;

    }

    @Override
    public void onPlayerEvent(int i, Bundle bundle) {

    }

    // @Override
    // public void onStart() {
    //     super.onStart();
    //     Window window = getDialog().getWindow();
    //     if (window != null) {
    //         window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    //     }
    // }

    @Override
    public void dismiss() {
        if (mVideoView != null) {
            Log.i(TAG, "dismiss: 停止video");
            mVideoView.stopPlayback();
        }
        super.dismiss();
    }

    private static final String TAG = "MyDialogFragment";
}
