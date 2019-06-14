package com.testandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author by hs-johnny
 * Created on 2019/6/12
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final String TAG = "CameraPreview";
    private Camera mCamera;
    private SurfaceHolder mHolder;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Uri outputMediaFileUri;
    private String outputMediaFileType;
    private MediaRecorder mMediaRecorder;
    private float oldDist = 1f;
    private static final int PROCESS_WITH_HANDLER_THREAD = 1;
    private int processType = PROCESS_WITH_HANDLER_THREAD;
    private ProcessWithHandlerThread processWithHandlerThread;
    private Handler processFrameHandler;

    public CameraPreview(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        switch (processType){
            case PROCESS_WITH_HANDLER_THREAD:
                processWithHandlerThread = new ProcessWithHandlerThread("process frame");
                processFrameHandler = new Handler(processWithHandlerThread.getLooper(), processWithHandlerThread);
                break;
        }
    }

    public Camera getCameraInstance(){
        if (mCamera == null){
            try {
                CameraHandlerThread mThread = new CameraHandlerThread("camera thread");
                synchronized (mThread){
                    mThread.openCamera();
                }
            }catch (Exception e){
                Log.e(TAG, "camera is not available" );
            }
        }
        return mCamera;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = getCameraInstance();
        mCamera.setPreviewCallback(this);
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        int rotation = getDisplayOrientation();
        mCamera.setDisplayOrientation(rotation);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setRotation(rotation);
        mCamera.setParameters(parameters);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder.removeCallback(this);
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    private File getOutputMediaFile(int type){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists()){
            if (mediaStorageDir.mkdirs()){
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss").format(new Date());
        File mediaFile;
        if(type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            outputMediaFileType = "image/*";
        } else if (type == MEDIA_TYPE_VIDEO){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
            outputMediaFileType = "video/*";
        } else {
            return null;
        }
        outputMediaFileUri = Uri.fromFile(mediaFile);
        return mediaFile;
    }

    public Uri getOutputMediaFileUri() {
        return outputMediaFileUri;
    }

    public String getOutputMediaFileType() {
        return outputMediaFileType;
    }

    public void takePicture(final ImageView iv){
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (pictureFile == null){
                    Log.e(TAG, "Error creating media file, check storage permissions");
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();

                    iv.setImageURI(getOutputMediaFileUri());
                    camera.startPreview();
                }catch (Exception e){
                    Log.e(TAG, e.getMessage() );
                }
            }
        });
    }

    private boolean prepareVideoRecorder(){
        mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String prefVideoSize = preferences.getString("video_size", "");
        String[] split = prefVideoSize.split("x");
        mMediaRecorder.setVideoSize(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());

        int rotation = getDisplayOrientation();
        mMediaRecorder.setOrientationHint(rotation);
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void releaseMediaRecorder(){
        if (mMediaRecorder != null){
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }

    public boolean startRecording(){
        if(prepareVideoRecorder()){
            mMediaRecorder.start();
            return true;
        }else {
            releaseMediaRecorder();
        }
        return false;
    }

    public void stopRecording(ImageView iv){
        if(mMediaRecorder != null){
            mMediaRecorder.stop();
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(getOutputMediaFileUri().getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
            iv.setImageBitmap(thumbnail);
        }
        releaseMediaRecorder();
    }

    public boolean isRecording(){
        return mMediaRecorder != null;
    }

    public int getDisplayOrientation(){
        Display display = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation){
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
        int result = (info.orientation - degrees + 360) % 360;
        return result;
    }

    private static Rect calculateTapArea(float x, float y, float coefficient, int width, int height){
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) (x / width * 2000 - 1000);
        int centerY = (int) (y / height * 2000 - 1000);

        int halfAreaSize = areaSize / 2;
        RectF rectF = new RectF(clamp(centerX - halfAreaSize, -1000, 1000),
                clamp(centerY - halfAreaSize, -1000, 1000),
                clamp(centerX + halfAreaSize, -1000, 1000),
                clamp(centerY + halfAreaSize, -1000, 1000));
        return new Rect(Math.round(rectF.left), Math.round(rectF.top),
                Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private static int clamp(int x, int min, int max){
        if (x > max){
            return max;
        }
        if (x < min){
            return min;
        }
        return x;
    }

    private void handleFocus(MotionEvent event, Camera camera){
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        Rect focusRect = calculateTapArea(event.getX(), event.getY(), 1f, viewWidth, viewHeight);
        camera.cancelAutoFocus();
        Camera.Parameters parameters = camera.getParameters();
        if(parameters.getMaxNumFocusAreas() > 0){
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 800));
            parameters.setFocusAreas(focusAreas);
        }else {
            Log.e(TAG, "focus areas not supported");
        }
        final String currentFocusMode = parameters.getFocusMode();
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
        camera.setParameters(parameters);
        camera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                Camera.Parameters para = camera.getParameters();
                para.setFocusMode(currentFocusMode);
                camera.setParameters(para);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 1){
            handleFocus(event, mCamera);
        } else {
            switch (event.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = getFingerSpacing(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float newDist = getFingerSpacing(event);
                    if(newDist > oldDist){
                        handleZoom(true, mCamera);
                    }else if (newDist < oldDist){
                        handleZoom(false, mCamera);
                    }
                    oldDist = newDist;
                    break;
            }
        }
        return true;
    }

    private static float getFingerSpacing(MotionEvent event){
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        switch (processType){
            case PROCESS_WITH_HANDLER_THREAD:
                processFrameHandler.obtainMessage(ProcessWithHandlerThread.WHAT_PROCESS_FRAME, data).sendToTarget();
                break;
        }
    }

    private void handleZoom(boolean isZoomIn, Camera camera){
        Camera.Parameters parameters = camera.getParameters();
        if(parameters.isZoomSupported()){
            int maxZoom = parameters.getMaxZoom();
            int zoom = parameters.getZoom();
            if(isZoomIn && zoom < maxZoom){
                zoom ++ ;
            }else if(zoom > 0){
                zoom --;
            }
            parameters.setZoom(zoom);
            camera.setParameters(parameters);
        }else {
            Log.e(TAG, "zoom not supported");
        }
    }

    private void openCameraOriginal(){
        try {
            mCamera = Camera.open();
        }catch (Exception e){
            Log.e(TAG, "camera is not available");
        }
    }
    
    private class CameraHandlerThread extends HandlerThread{
        Handler handler;
        public CameraHandlerThread(String name) {
            super(name);
            start();
            handler = new Handler(getLooper());
        }
        
        synchronized void notifyCameraOpened(){
            notify();
        }
        
        void openCamera(){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    openCameraOriginal();
                    notifyCameraOpened();
                }
            });
            try {
                wait();
            }catch (Exception e){
                Log.e(TAG, "wait was interrupted");
            }
        }
    }
}
