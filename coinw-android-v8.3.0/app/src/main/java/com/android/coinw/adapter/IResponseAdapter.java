package com.android.coinw.adapter;
import java.io.IOException;
import java.io.Reader;
public interface IResponseAdapter {
    void dispatch(String url, Reader reader) throws Throwable;
    void dispatch(String url, IOException exception) throws Throwable;
}
