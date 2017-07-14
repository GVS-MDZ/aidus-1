package com.gvs.idusapp;

import java.util.List;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gvs.controladores.ControladorCliente;
import com.gvs.modelos.Cliente;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MapaClientesActivity extends Activity {
	
	private GoogleMap mapa;
	private Bundle bun;
	private List<Cliente>lista_cliente;
	private ControladorCliente busquedaCliente;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapa_clientes);
		try{
			bun = getIntent().getExtras();
			busquedaCliente=new ControladorCliente(this);
			lista_cliente=busquedaCliente.buscarClientesLocalizados(bun.getInt("DIA"), bun.getInt("VENDEDOR"));	
			if(lista_cliente!=null){
				for(Cliente item:lista_cliente){
					double lat = item.getLatitud();
					double log =item.getLongitud();
					String nombre=item.getNombre();
					String direccion=item.getDireccion();
					Location location = new Location("");
					location.setLatitude(lat);
					location.setLongitude(log);		  
					LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
					mapa = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
					mapa.addMarker(new MarkerOptions().position(newLatLng).title(nombre+"("+direccion+")"));	
					mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14.0f));
				}	
			}else{
				Toast toast = Toast.makeText(getApplicationContext(),"No existen clientes con gps", Toast.LENGTH_SHORT);
				toast.show();
				finish();
			}
		}catch(Exception ex){
			Toast toast = Toast.makeText(getApplicationContext(),"Bug en la localizacion de gps", Toast.LENGTH_SHORT);
			toast.show();
			finish();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mapa_cliente, menu);
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
