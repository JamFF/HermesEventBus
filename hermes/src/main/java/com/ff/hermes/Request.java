package com.ff.hermes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * description:
 * author: FF
 * time: 2019-07-06 10:44
 */
public class Request implements Parcelable {

    // 请求的对象，RequestBean对应的json字符串
    private String data;
    // 区分请求的对象类型（单例，对象）
    private int type;

    public Request(String data, int type) {
        this.data = data;
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public int getType() {
        return type;
    }

    protected Request(Parcel in) {
        data = in.readString();
        type = in.readInt();
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
        dest.writeInt(type);
    }
}
