package com.kk.taurus.avplayer.demo.pop;

import java.util.List;


public class HmEgretVideoPlayer extends HmBaseEgretMessage<HmEgretVideoPlayer.EgretVideoPlayer> {

    public static final int EVENT_START = 1;
    public static final int EVENT_PAUSE = 2;
    public static final int EVENT_STOP = 3;
    public static final int EVENT_SCREENSHOT = 4;
    public static final int EVENT_RESUME = 5;
    public static final int EVENT_SEEK = 6;
    public static final int EVENT_VOLUME = 7;

    public static final int LAYER_INDEX_TOP = 1;
    public static final int FIT_MODE_FILL = 1;


    public static class EgretVideoPlayer {
        /**
         * 视频尺寸适配模式,1 等比例不保证填满屏幕（可能留黑边） 2 等比例保证填满屏幕（不留黑边），默认1
         */
        private int fitMode = 1;

        /**
         * 0 无控制控件 1有控制控件（底部进度条等等工具），默认0
         */
        private int videoControl;

        /**
         * url : xxx.mp4
         * seek : 1
         * operType : 1
         */
        /**
         * 视频的url，为空表示操作当前播放的url
         * url可能是http协议
         * 也可以是file:///的本地路径
         */
        private String url;
        /**
         * 定位时间(毫秒)，默认0
         */
        private int seek;
        /**
         * 视频操作，1播放 2暂停 3停止 4截屏
         */
        private int operType;

        /**
         * 层级索引，0代表底部，1代表顶部，默认0
         */
        private int layerIndex;

        /**
         * base64，截图
         */
        private String fileData;

        private int uid;

        private float volume;

        public int getFitMode() {
            return fitMode;
        }

        public void setFitMode(int fitMode) {
            this.fitMode = fitMode;
        }

        public int getVideoControl() {
            return videoControl;
        }

        public void setVideoControl(int videoControl) {
            this.videoControl = videoControl;
        }

        public float getVolume() {
            return volume;
        }

        public void setVolume(float volume) {
            this.volume = volume;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        private List<Integer> rect;

        public List<Integer> getRect() {
            return rect;
        }

        public void setRect(List<Integer> rect) {
            this.rect = rect;
        }

        public String getFileData() {
            return fileData;
        }

        public void setFileData(String fileData) {
            this.fileData = fileData;
        }

        public int getLayerIndex() {
            return layerIndex;
        }

        public void setLayerIndex(int layerIndex) {
            this.layerIndex = layerIndex;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getSeek() {
            return seek;
        }

        public void setSeek(int seek) {
            this.seek = seek;
        }

        public int getOperType() {
            return operType;
        }

        public void setOperType(int operType) {
            this.operType = operType;
        }
    }
}