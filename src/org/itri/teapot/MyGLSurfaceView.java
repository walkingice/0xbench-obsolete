package org.itri.teapot;

import android.content.Context;
import android.opengl.GLSurfaceView;

class MyGLSurfaceView extends GLSurfaceView {
    private TeapotRenderer mMyRenderer;

    public MyGLSurfaceView(Context context) {
		super(context);
    }
	
    @Override 
    public void setRenderer(GLSurfaceView.Renderer renderer) {
    	mMyRenderer = (TeapotRenderer) renderer;
    	super.setRenderer(renderer);
    }
    
    public void onSensorChanged(final float[] values) {
		 queueEvent(new Runnable() {
             // This method will be called on the rendering
             // thread:
             public void run() {
                 mMyRenderer.onSensorChanged(values);
             }}); 
	 }

	 public void setSensor(final int sensorId) {
		 queueEvent(new Runnable() {
             public void run() {
                 mMyRenderer.setSensor(sensorId);
             }}); 
	 }
}
