package org.itri.teapot;

import org.zeroxlab.benchmark.Tester;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

class MyGLSurfaceView extends GLSurfaceView {
    private TeapotRenderer mMyRenderer;
    private Tester mTester;
    private float xspeed;
    private float yspeed;
    private float zspeed;

    public MyGLSurfaceView(Context context) {
        super(context);
    }

    @Override 
    public void setRenderer(GLSurfaceView.Renderer renderer) {
        mMyRenderer = (TeapotRenderer) renderer;
        super.setRenderer(renderer);
    }
}
