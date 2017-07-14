package com.gvs.idusapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class FotoActivity extends Activity {

	private Bundle bun;
	private byte[] data;
	private ImageView imagen_view;
	private Button btn_compartir,btn_salir;
	private Bitmap bm ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.activity_foto);
			imagen_view=(ImageView)findViewById(R.id.foto);
			btn_compartir=(Button)findViewById(R.id.btn_compartir_foto);
			btn_salir=(Button)findViewById(R.id.btn_salir_foto);
			bun=getIntent().getExtras();
			data=bun.getByteArray("FOTO");

		    bm = BitmapFactory.decodeByteArray(data, 0, data.length);
			imagen_view.setImageBitmap(bm);
			
			btn_compartir.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					try{
						String fullPath = Environment.getExternalStorageDirectory()+ "/DCIM/Idus";
						File dir = new File(fullPath);
						if (!dir.exists()) {
							dir.mkdirs();
						}
						OutputStream fOut = null;
						File file = new File(fullPath, System.currentTimeMillis() + ".jpeg");
						fOut = new FileOutputStream(file);
						bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
						fOut.flush();
						fOut.close();
	
						ContentValues values = new ContentValues();
						values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis()+ ".jpeg");
						values.put(Images.Media.MIME_TYPE, "image/jpeg");
						values.put(Images.Media.TITLE, System.currentTimeMillis() + ".jpeg");
						values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
					    getApplicationContext().getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI,values);
					    Intent share = new Intent(Intent.ACTION_SEND);
					    share.setType("image/*");
					    Uri uri = Uri.fromFile(file);
					    share.putExtra(Intent.EXTRA_STREAM, uri);
					    startActivity(Intent.createChooser(share, "COMPARTIR"));
					} catch (FileNotFoundException e) {
						e.getMessage();
					} catch (IOException e) {
						e.getMessage();
					}
				}
			});
			
			btn_salir.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
				 finish();
				}
			});
			
			}catch(Exception ex){
				
			}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			imagen_view.setScaleType(ScaleType.FIT_CENTER);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			imagen_view.setScaleType(ScaleType.CENTER_CROP);
		}
	}

}
