package com.gvs.idusapp;

import com.gvs.controladores.ControladorUsuario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private ControladorUsuario controladorUsuario;
	private ImageView imagen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.activity_main);
			controladorUsuario = new ControladorUsuario(this);
			controladorUsuario.guardarUsuario();
			imagen=(ImageView)findViewById(R.id.img_software);
			
			imagen.setOnClickListener(new View.OnClickListener() {

				   public void onClick(View v) {
					   Intent intent = new Intent(MainActivity.this, LoginActivity.class);
					 
					   startActivity(intent);
				   }        
			});
			
		}catch(Exception ex){
			ex.getMessage();
		}
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.version:
			Intent intent = new Intent(getApplication(), VersionActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
