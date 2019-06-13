package com.testandroid;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

/**
 * @author by hs-johnny
 * Created on 2019/4/15
 */
@RunWith(RobolectricTestRunner.class)
public class SampleTest {

    private MainActivity activity;

    @Before
    public void setUp(){
        activity = Robolectric.buildActivity(MainActivity.class).create().get();
        //输出日志
        ShadowLog.stream = System.out;
    }

    @Test
    public void testFragment(){
        SimpleFragment fragment = new SimpleFragment();
        SupportFragmentTestUtil.startFragment(fragment);
        Assert.assertNotNull(fragment.getView());
    }

    @Test
    public void testReceiver(){

    }

}
