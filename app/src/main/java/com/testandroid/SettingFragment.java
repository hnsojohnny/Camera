package com.testandroid;

import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author by hs-johnny
 * Created on 2019/6/12
 */
public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static Camera mCamera;
    private static Camera.Parameters mParameters;
    public static final String KEY_PREF_PREV_SIZE = "preview_size";
    public static final String KEY_PREF_PIC_SIZE = "picture_size";
    public static final String KEY_PREF_VIDEO_SIZE = "video_size";
    public static final String KEY_PREF_FLASH_MODE = "flash_mode";
    public static final String KEY_PREF_FOCUS_MODE = "focus_mode";
    public static final String KEY_PREF_WHITE_BALANCE = "white_balance";
    public static final String KEY_PREF_SCENE_MODE = "screne_mode";
    public static final String KEY_PREF_GPS_DATA = "gps_data";
    public static final String KEY_PREF_EXPOS_COMP = "exposure_compensation";
    public static final String KEY_PREF_JPEG_QUALITY = "jpeg_quality";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        loadSupportedPreviewSize();
        loadSupportedPictureSize();
        loadSupportedVideoSize();
        loadSupportedFlashMode();
        loadSupportedFocusMode();
        loadSupportedWhiteBalance();
        loadSupportedSceneMode();
        loadSupportedExposeCompenstation();
        initSummary(getPreferenceScreen());
    }

    public static void passCamera(Camera camera){
        mCamera = camera;
        mParameters = camera.getParameters();
    }

    /**
     * 设置预览照片的屏幕大小
     */
    private void loadSupportedPreviewSize(){
        cameraSizeListToListPreference(mParameters.getSupportedPreviewSizes(), KEY_PREF_PREV_SIZE);
    }

    /**
     * 设置照片大小
     */
    private void loadSupportedPictureSize(){
        cameraSizeListToListPreference(mParameters.getSupportedPictureSizes(), KEY_PREF_PIC_SIZE);
    }

    /**
     * 设置影像大小
     */
    private void loadSupportedVideoSize(){
        cameraSizeListToListPreference(mParameters.getSupportedVideoSizes(), KEY_PREF_VIDEO_SIZE);
    }

    /**
     * 设置闪光灯
     */
    private void loadSupportedFlashMode(){
        stringListToListPreference(mParameters.getSupportedFlashModes(), KEY_PREF_FLASH_MODE);
    }

    /**
     * 设置对焦模式
     */
    private void loadSupportedFocusMode(){
        stringListToListPreference(mParameters.getSupportedFocusModes(), KEY_PREF_FOCUS_MODE);
    }

    /**
     * 设置白平衡
     */
    private void loadSupportedWhiteBalance(){
        stringListToListPreference(mParameters.getSupportedWhiteBalance(), KEY_PREF_WHITE_BALANCE);
    }

    /**
     * 设置场景
     */
    private void loadSupportedSceneMode(){
        stringListToListPreference(mParameters.getSupportedSceneModes(), KEY_PREF_SCENE_MODE);
    }

    /**
     * 动态加载曝光补偿
     */
    private void loadSupportedExposeCompenstation(){
        int minExposComp = mParameters.getMinExposureCompensation();
        int maxExposComp = mParameters.getMaxExposureCompensation();
        List<String> exposComp = new ArrayList<>();
        for (int value = minExposComp; value <= maxExposComp; value++){
            exposComp.add(Integer.toString(value));
        }
        stringListToListPreference(exposComp, KEY_PREF_EXPOS_COMP);
    }

    private void cameraSizeListToListPreference(List<Camera.Size> list, String key){
        List<String> stringList = new ArrayList<>();
        for (Camera.Size size : list){
            String stringSize = size.width + "x" + size.height;
            stringList.add(stringSize);
        }
        stringListToListPreference(stringList, key);
    }

    private void stringListToListPreference(List<String> list, String key){
        if(list != null && list.size() > 0){
            CharSequence[] charseq = list.toArray(new CharSequence[list.size()]);
            ListPreference listPreference = (ListPreference) getPreferenceScreen().findPreference(key);
            listPreference.setEntries(charseq);
            listPreference.setEntryValues(charseq);
        }
    }

    public static void setDefault(SharedPreferences sharedPreferences){
        String valPreviewSize = sharedPreferences.getString(KEY_PREF_PREV_SIZE, null);
        if(valPreviewSize == null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_PREF_PREV_SIZE, getDefaultPreviewSize());
            editor.putString(KEY_PREF_PIC_SIZE, getDefaultPictureSize());
            editor.putString(KEY_PREF_VIDEO_SIZE, getDefaultVideoSize());
            editor.putString(KEY_PREF_FOCUS_MODE, getDefaultFocusMode());
            editor.apply();
        }
    }

    private static String getDefaultPreviewSize(){
        Camera.Size previewSize = mParameters.getPreviewSize();
        return previewSize.width + "x" + previewSize.height;
    }

    private static String getDefaultPictureSize(){
        Camera.Size pictureSize = mParameters.getPictureSize();
        return pictureSize.width + "x" + pictureSize.height;
    }

    private static String getDefaultVideoSize(){
        Camera.Size videoSize = mParameters.getPreferredPreviewSizeForVideo();
        return videoSize.width + "x" + videoSize.height;
    }

    private static String getDefaultFocusMode(){
        List<String> supportedFocusModes = mParameters.getSupportedFocusModes();
        if(supportedFocusModes.contains("continuous-picture")){
            return "continuous-picture";
        }
        return "continuous-video";
    }

    public static void init(SharedPreferences sharedPreferences){
//        setPreviewSize(sharedPreferences.getString(KEY_PREF_PREV_SIZE, ""));
//        setPictureSize(sharedPreferences.getString(KEY_PREF_PIC_SIZE, ""));
//        setFlashMode(sharedPreferences.getString(KEY_PREF_FLASH_MODE, ""));
//        setFocusMode(sharedPreferences.getString(KEY_PREF_FOCUS_MODE, ""));
//        setWhiteBalance(sharedPreferences.getString(KEY_PREF_WHITE_BALANCE, ""));
//        setSceneMode(sharedPreferences.getString(KEY_PREF_SCENE_MODE, ""));
//        setExposComp(sharedPreferences.getString(KEY_PREF_EXPOS_COMP, ""));
//        setJpegQuality(sharedPreferences.getString(KEY_PREF_JPEG_QUALITY, ""));
//        setGpsData(sharedPreferences.getBoolean(KEY_PREF_GPS_DATA, false));
        mCamera.stopPreview();
        mCamera.setParameters(mParameters);
        mCamera.startPreview();
    }

    private static void setPreviewSize(String value){
        String[] split = value.split("x");
        mParameters.setPreviewSize(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    private static void setPictureSize(String value) {
        String[] split = value.split("x");
        mParameters.setPictureSize(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    private static void setFocusMode(String value) {
        mParameters.setFocusMode(value);
    }

    private static void setFlashMode(String value) {
        mParameters.setFlashMode(value);
    }

    private static void setWhiteBalance(String value) {
        mParameters.setWhiteBalance(value);
    }

    private static void setSceneMode(String value) {
        mParameters.setSceneMode(value);
    }

    private static void setExposComp(String value) {
        mParameters.setExposureCompensation(Integer.parseInt(value));
    }

    private static void setJpegQuality(String value) {
        mParameters.setJpegQuality(Integer.parseInt(value));
    }

    private static void setGpsData(Boolean value) {
        if (value.equals(false)) {
            mParameters.removeGpsData();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreSummary(findPreference(key));
        switch (key){
            case KEY_PREF_PREV_SIZE:
                setPreviewSize(sharedPreferences.getString(key, ""));
                break;
            case KEY_PREF_PIC_SIZE:
                setPictureSize(sharedPreferences.getString(key, ""));
                break;
            case KEY_PREF_FOCUS_MODE:
                setFocusMode(sharedPreferences.getString(key, ""));
                break;
            case KEY_PREF_FLASH_MODE:
                setFlashMode(sharedPreferences.getString(key, ""));
                break;
            case KEY_PREF_WHITE_BALANCE:
                setWhiteBalance(sharedPreferences.getString(key, ""));
                break;
            case KEY_PREF_SCENE_MODE:
                setSceneMode(sharedPreferences.getString(key, ""));
                break;
            case KEY_PREF_EXPOS_COMP:
                setExposComp(sharedPreferences.getString(key, ""));
                break;
            case KEY_PREF_JPEG_QUALITY:
                setJpegQuality(sharedPreferences.getString(key, ""));
                break;
            case KEY_PREF_GPS_DATA:
                setGpsData(sharedPreferences.getBoolean(key, false));
                break;
        }
        mCamera.startPreview();
        mCamera.setParameters(mParameters);
        mCamera.startPreview();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    private static void initSummary(Preference preference){
        if(preference instanceof PreferenceGroup){
            PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
            for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++){
                initSummary(preferenceGroup.getPreference(i));
            }
        }else {
            updatePreSummary(preference);
        }
    }

    private static void updatePreSummary(Preference preference){
        if(preference instanceof ListPreference){
            preference.setSummary(((ListPreference) preference).getEntry());
        }
    }
}
