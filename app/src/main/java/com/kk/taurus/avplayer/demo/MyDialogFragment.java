package com.kk.taurus.avplayer.demo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kk.taurus.avplayer.R;


public class MyDialogFragment extends DialogFragment {

    public static MyDialogFragment newInstance(long id, String name) {
        MyDialogFragment dialog = new MyDialogFragment();
        // 设置主题，这里只能通过xml方式设置主题，不能通过Java代码处理，因为这是getWindow还是null，
        // 而且window的几乎所有属性，都可以通过xml设置
        dialog.setStyle(STYLE_NORMAL, R.style.MyDialogTheme);
        // 设置触摸、点击弹窗外部不可关闭
        dialog.setCancelable(false);
        // 对于DialogFragment，设置外部传的参数，通过bundle设置，然后在onCreateView读取
        Bundle bundle = new Bundle();
        bundle.putLong("id", id);
        bundle.putString("name", name);
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
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(id, id);
            //view.setLayoutParams(lp);
        }
    }

    // @Override
    // public void onStart() {
    //     super.onStart();
    //     Window window = getDialog().getWindow();
    //     if (window != null) {
    //         window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    //     }
    // }
}
