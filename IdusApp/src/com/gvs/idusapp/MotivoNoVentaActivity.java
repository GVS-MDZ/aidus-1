package com.gvs.idusapp;

import java.util.Date;
import java.util.List;
import com.gvs.controladores.ControladorInforme;
import com.gvs.controladores.ControladorNoAtendido;
import com.gvs.modelos.Log;
import com.gvs.modelos.Motivo;
import com.gvs.modelos.NoAtendido;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MotivoNoVentaActivity extends Activity {
	private ListView list_view;
	private List<Motivo>lista_motivo;
	private AlertDialog.Builder dialogo1;
	private TextView lbl_cliente;
	private Bundle bun ;	
	private int codigoMotivo,codigoCliente,codigoVendedor;
	private LocationManager locManager;
	private LocationListener locLis;	
	private double latitud,longitud,precision;
	private String proveedor="";
	private Location location;
	private ControladorNoAtendido controladorMotivo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_motivo_no_venta);
		try{
			lbl_cliente=(TextView)findViewById(R.id.lbl_cliente);
			list_view=(ListView)findViewById(R.id.lista_motivos);
			controladorMotivo=new ControladorNoAtendido();
			controladorMotivo.setContexto(MotivoNoVentaActivity.this);
			bun = getIntent().getExtras();
			lbl_cliente.setText(bun.getString("NOMBRECLIENTE"));
			codigoCliente = bun.getInt("CODIGOCLIENTE");
		    codigoVendedor = bun.getInt("VENDEDOR");
			comenzarLocalizacion();
			buscarMotivos();
			mostrarLista();
			realizarOperacion();
		}catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug al mostrar motivos", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	private void buscarMotivos(){
		try{
			lista_motivo=controladorMotivo.buscarMotivosNoVenta();
		}catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug al listar motivos", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private void mostrarLista() {
		if(lista_motivo!=null){
			ArrayAdapter<Motivo> adapter = new MyListAdapter();
			list_view.setAdapter(adapter);
		}
	}
	
	private void realizarOperacion(){
	
		list_view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,long arg3) {
					try{
						comenzarLocalizacion();
					    Motivo motivo=lista_motivo.get(position);
					    codigoMotivo=motivo.getCodigo();
					    dialogo1 = new AlertDialog.Builder(MotivoNoVentaActivity.this);
						dialogo1.setTitle("MOTIVO");
						dialogo1.setMessage("¿ "+motivo.getNombre()+" ?");
						dialogo1.setCancelable(false);
						dialogo1.setPositiveButton("Confirmar",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialogo1, int id) {
										guardar();
									}
								});
						dialogo1.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialogo1, int id) {
										
									}
								});
						dialogo1.show();
					}catch(Exception ex){
						Toast toast =Toast.makeText(getApplicationContext(),"Bug en el motivo seleccionado", Toast.LENGTH_SHORT);
						toast.show();
					}
			}
		});
	}

	private void guardar(){
		try {
			ControladorInforme controlador_log=new ControladorInforme(MotivoNoVentaActivity.this);
			Log log =new Log();
			log.setDescripcion("MOTIVO NO VENTA - "+codigoCliente+" - "+codigoMotivo);
			log.setTipo(27);
			log.setFecha(new Date().getTime());
			log.setEstado(0);
			log.setVendedor(codigoVendedor);
			controlador_log.guardarLog(log);	
			
			NoAtendido noatendido=new NoAtendido();
			noatendido.setCodigoCliente(codigoCliente);
			noatendido.setCodigoVendedor(codigoVendedor);
			noatendido.setFecha( new Date().getTime());
			noatendido.setCodigoMotivo(codigoMotivo);
			noatendido.setLatitud(latitud);
			noatendido.setLongitud(longitud);
			noatendido.setPresicion(precision);
			noatendido.setProveedor(proveedor);
			controladorMotivo.grabarNoAtendido(noatendido);
			Toast toast=Toast.makeText(this,"El motivo se guardo correctamente", Toast.LENGTH_SHORT);
			toast.show();
			finish();
			} catch (Exception e) {
				Toast toast=Toast.makeText(this,"Bug al guardar el motivo", Toast.LENGTH_SHORT);
				toast.show();
			}		
	}
	
	private class MyListAdapter extends ArrayAdapter<Motivo> {

		public MyListAdapter() {
			super(MotivoNoVentaActivity.this, R.layout.activity_motivo_no_venta, lista_motivo);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder holder;
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.item_motivo, parent,false);
				holder = new ViewHolder();
				holder.lbl_address=(TextView) view.findViewById(R.id.lbl_nombre_motivo);
				view.setTag(holder);
			}else{
				holder = (ViewHolder) view.getTag();
			}
			Motivo motivo = lista_motivo.get(position);	 
			holder.lbl_address.setText(motivo.getCodigo()+" - "+motivo.getNombre());		
			return view;
		}

	}

	static class ViewHolder {
		TextView lbl_address;
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
			e.printStackTrace();
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
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locLis);
			
			 if (locManager != null) {
			    	location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
               if (location != null) {
            		latitud=location.getLatitude();
    				longitud=location.getLongitude();
               }
           }
			
		} else {
			if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locLis);
				if (locManager != null) {
			    	location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                    	latitud=location.getLatitude();
        				longitud=location.getLongitude();
                    }
                }
				
			} 
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.motivo, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.cerrar_sesion) {
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
