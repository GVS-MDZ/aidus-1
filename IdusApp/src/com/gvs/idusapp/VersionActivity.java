package com.gvs.idusapp;

import com.gvs.utilidades.Funciones;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class VersionActivity extends Activity {

	private TextView version;
	private Funciones funcion;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_version);
		version=(TextView)findViewById(R.id.version);
		funcion=new Funciones();
		version.setText(funcion.getVersionAplicacion());
	}

	
}
