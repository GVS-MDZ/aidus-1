package com.gvs.idusapp;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.gvs.controladores.ControladorConfiguracion;
import com.gvs.controladores.ControladorEncuesta;
import com.gvs.modelos.Configuracion;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class PreguntaEncuestaActivity extends Activity {
	
	private TextView lblCliente,lblNombre,lblPregunta,lblAyuda;
	private RadioButton radSi, radNo;
	private Button btnAceptar,btnSiguiente;
	private LocationManager locManager;
	private LocationListener locLis;	
	private Bundle bun;	
	private ControladorConfiguracion controladorConfiguracion;
	private ControladorEncuesta controladorEncuesta;
	private Configuracion configuracion;
	private String idEncuesta,proveedor;
	private int numeroItem;
	private double latitud,longitud,precision;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pregunta_encuesta);
		try{
			lblCliente = (TextView) findViewById(R.id.laEncuestaCodCliente);
			lblNombre = (TextView) findViewById(R.id.laEncuestaNombre);
			lblPregunta = (TextView) findViewById(R.id.laEncuestaPregunta);
			lblAyuda = (TextView) findViewById(R.id.laEncuestaAyuda);
			radSi = (RadioButton) findViewById(R.id.radEncuestaSi);
			radNo = (RadioButton) findViewById(R.id.radEncuestaNo);
			btnAceptar = (Button) findViewById(R.id.butEncuestaAceptar);
			btnSiguiente = (Button) findViewById(R.id.butEncuestaSiguiente);
			
			bun = getIntent().getExtras();	
			controladorConfiguracion=new ControladorConfiguracion(this);
			configuracion = controladorConfiguracion.buscarConfiguracion();
			SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
			String date=format.format(new Date());
			numeroItem=0;
			latitud=0;
			longitud=0;
			precision=0;
			proveedor="";
			idEncuesta =bun.getInt("EMPRESA") + "-" + bun.getInt("VENDEDOR") + "-" + bun.getInt("CODIGOCLIENTE")  + "-" + date;
			
			lblCliente.setText("Cliente:" + bun.getString("DATO01"));
			lblNombre.setText(bun.getString("NOMBREENCUESTA"));
			
			if (configuracion.getServicioGeo()==1){
				comenzarLocalizacion();
			}
			
			buscarPreguntaEncuesta(bun.getInt("NUMEROENCUESTA"), -1);
			
			btnAceptar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			
			btnSiguiente.setOnClickListener(new OnClickListener() {			
				@Override
				public void onClick(View v) {
						try {
							ContentValues contEncuesta = new ContentValues();
							contEncuesta.put("NUMERO", idEncuesta);
							contEncuesta.put("CODIGOCLIENTE", bun.getInt("CODIGOCLIENTE"));
							contEncuesta.put("CODIGOVENDEDOR", bun.getInt("VENDEDOR"));
							contEncuesta.put("NUMEROENCUESTA", bun.getInt("NUMEROENCUESTA"));
							contEncuesta.put("FECHA", new Date().getTime());
							contEncuesta.put("OBSERVACION", "");
							contEncuesta.put("LATITUD", latitud);
							contEncuesta.put("LONGITUD", longitud);
							contEncuesta.put("PRECISION", precision);
							contEncuesta.put("PROVEE", proveedor);
							contEncuesta.put("ESTADO",1);	
												
							ContentValues contRespuesta = new ContentValues();
							boolean respuesta= radSi.isChecked();
							numeroItem=numeroItem +1;
							contRespuesta.put("NUMERO", idEncuesta);
							contRespuesta.put("ITEM", numeroItem);
							contRespuesta.put("RESPUESTA", respuesta);
							contRespuesta.put("OBSERVACION", "");
							
							controladorEncuesta = new ControladorEncuesta(PreguntaEncuestaActivity.this);
							controladorEncuesta.grabarEncuesta(contEncuesta, contRespuesta);
							buscarPreguntaEncuesta(bun.getInt("NUMEROENCUESTA"),numeroItem);
						} catch (Exception ex) {
							Toast toast =Toast.makeText(getApplicationContext(),"Bug al guardar respuesta", Toast.LENGTH_SHORT);
							toast.show();
						}		
				}
			});
			
		} catch (Exception e){
			Toast toast =Toast.makeText(this,"Bug al listar encuestas", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private void buscarPreguntaEncuesta(final int numeroEncuesta,int numeroItem) {
		try{
			controladorEncuesta = new ControladorEncuesta(this);		
			String resp = controladorEncuesta.buscarNuevoItem(numeroEncuesta, numeroItem);		
			if (resp.equals("ef")) {
				finish();
			} else if (resp.equals("ev")) {
				Toast toast =Toast.makeText(this,"Bug encuesta vacia", Toast.LENGTH_SHORT);
				toast.show();
				finish();
			} else {
				String tex[] = resp.split("~");
				lblPregunta.setText("¿ "+tex[0].trim()+" ?");
				lblAyuda.setText(tex[1].trim());
				radSi.setChecked(true);
				radNo.setChecked(false);
			}
		} catch (Exception e){
			Toast toast =Toast.makeText(this,"Bug al buscar las preguntas", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private void comenzarLocalizacion(){
		try{
				locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				} else {
					if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
						locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					} 
				}
		} catch (Exception e){
			Toast toast =Toast.makeText(this,"Bug gps", Toast.LENGTH_SHORT);
			toast.show();
		}

		locLis = new LocationListener() {
				
				@Override
				public void onStatusChanged(String provider, int status, Bundle extras) {

					proveedor=provider;
				}
				
				@Override
				public void onProviderEnabled(String provider) {

				}
				
				@Override
				public void onProviderDisabled(String provider) {

				}
				
				@Override
				public void onLocationChanged(Location location) {
					latitud=location.getLatitude();
					longitud=location.getLongitude();
					precision=location.getAccuracy();
					proveedor=location.getProvider();
				}

			};
			
		if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
				locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, locLis);
		} else {
			if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100, locLis);
			} 
		}
			
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.pregunta_encuesta, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.cerrar_sesion){
			startActivity(new Intent(getBaseContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
			finish();
		}else if(id == R.id.sincronizar){
			Intent intent = new Intent(getApplicationContext(), SincronizadorActivity.class);
			bun.putString("SINCRONIZACION","GENERAL");
			intent.putExtras(bun);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

}
