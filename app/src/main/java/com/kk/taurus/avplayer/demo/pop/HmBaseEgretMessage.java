package com.kk.taurus.avplayer.demo.pop;


public class HmBaseEgretMessage<T> {

    private int protocol;
    private T data;

    public int getProtocol() {
        return protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" +
                "protocol=" + protocol +
                ", data=" + data +
                '}';
    }
}