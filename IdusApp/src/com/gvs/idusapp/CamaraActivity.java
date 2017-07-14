package com.gvs.idusapp;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class CamaraActivity extends Activity implements SurfaceHolder.Callback, Camera.ShutterCallback, Camera.PictureCallback {
	private Button btn_foto;
	private Camera camera;
	private SurfaceView preview;
	private boolean flash = false;
	private int tipoCamara = 0;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_camara);
		preview = (SurfaceView) findViewById(R.id.preview);
		btn_foto=(Button)findViewById(R.id.btn_foto);
		preview.getHolder().addCallback(this);
		preview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		camera = Camera.open();

		String modo = camera.getParameters().getFlashMode();
		if (modo.equals(Camera.Parameters.FLASH_MODE_ON)) {
			flash = true;
		}
		
		btn_foto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					onSnapClick();
				}catch(Exception ex){
					Toast toast =Toast.makeText(CamaraActivity.this,"error "+ex.getMessage(), Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		camera.stopPreview();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		camera.release();
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void onCancelClick(View v) {
		finish();
	}

	public void onSnapClick() {
		try {	
			Camera.Parameters parametros = camera.getParameters();
			if (flash) {
				parametros.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
			}
			camera.setParameters(parametros);
			camera.takePicture(null, null, CamaraActivity.this);
		} catch (Exception ex) {
			new Exception(ex.getMessage());
		} 
	}

	@Override
	public void onShutter() {
	
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		try {
			Intent intent=new Intent(CamaraActivity.this,FotoActivity.class);
			Bundle bunFoto=new Bundle();
			bunFoto.putByteArray("FOTO",data);
			intent.putExtras(bunFoto);
			startActivity(intent);	
		} catch (Exception e) {
			Toast toast =Toast.makeText(CamaraActivity.this,"Bug con la camara "+e.getMessage(), Toast.LENGTH_SHORT);
			toast.show();
		} 
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		actualizarCamara();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera.setPreviewDisplay(preview.getHolder());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	public void actualizarCamara() {
		try{
			Camera.CameraInfo info = new Camera.CameraInfo();
			Camera.getCameraInfo(tipoCamara, info);
			Camera.Parameters parametros = camera.getParameters();
			List<Camera.Size> sizes = parametros.getSupportedPreviewSizes();
			Camera.Size size = sizes.get(tipoCamara);
			parametros.setPreviewSize(size.width, size.height);
			WindowManager winManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			int rotation = winManager.getDefaultDisplay().getRotation();
			int degrees = 0;
			switch (rotation) {
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
			int cameraRotationOffset = info.orientation;
	
			int result;
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				result = (info.orientation + degrees) % 360;
				result = (360 - result) % 360;
			} else {
				result = (info.orientation - degrees + 360) % 360;
			}
			int rotate = 0;
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				rotate = (360 + cameraRotationOffset + degrees) % 360;
			} else {
				rotate = (360 + cameraRotationOffset - degrees) % 360;
			}
			parametros.setRotation(rotate);
			camera.setDisplayOrientation(result);
			camera.setParameters(parametros);
			camera.setPreviewDisplay(preview.getHolder());
			camera.startPreview();
		}catch(Exception ex){
			ex.getMessage();
		}
	}
}
