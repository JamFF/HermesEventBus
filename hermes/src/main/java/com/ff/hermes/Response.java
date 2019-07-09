package com.ff.hermes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * description:
 * author: FF
 * time: 2019-07-06 10:44
 */
public class Response implements Parcelable {

    private String data;

    public Response(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    protected Response(Parcel in) {
        data = in.readString();
    }

    public static final Creator<Response> CREATOR = new Creator<Response>() {
        @Override
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
    }
}
