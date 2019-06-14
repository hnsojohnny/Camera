package com.testandroid;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

/**
 * @author by hs-johnny
 * Created on 2019/6/14
 */
public class ProcessWithHandlerThread extends HandlerThread implements Handler.Callback {

    public static final int WHAT_PROCESS_FRAME = 1;
    private static final String TAG = "ProcessWithHandlerThread";

    public ProcessWithHandlerThread(String name) {
        super(name);
        start();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case WHAT_PROCESS_FRAME:
                byte[] frameData = (byte[]) msg.obj;
                processFrame(frameData);
                return true;
        }
        return false;
    }

    private void processFrame(byte[] frameData){
        Log.e(TAG, "test");
    }
}
