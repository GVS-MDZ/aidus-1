package com.gvs.idusapp;

import java.util.Date;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gvs.conectores.ConectorCliente;
import com.gvs.conectores.ConectorGeneral;
import com.gvs.controladores.ControladorCliente;
import com.gvs.controladores.ControladorConfiguracion;
import com.gvs.controladores.ControladorInforme;
import com.gvs.modelos.Configuracion;
import com.gvs.modelos.Log;
import com.gvs.utilidades.Funciones;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GpsClienteActivity extends FragmentActivity implements LocationListener {

    private GoogleMap mapa;
    private TextView lbl_gps;
    private Button btn_guardar;
    private int empresa,codigoCliente;
    private Bundle bun;
    private String latitud,longitud;
	private Funciones funcion;
	private Configuracion configuracion;
	private ConectorGeneral conectorCliente;
	private Handler handler;
	private ControladorConfiguracion controladorConfiguracion;
	private ControladorCliente controladorClientes;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_cliente);
        try{
	        if (!isGooglePlayServicesAvailable()) {
	        	Toast toast = Toast.makeText(getApplicationContext(),"Bug google play service", Toast.LENGTH_SHORT);
				toast.show();
	            finish();
	        }else{
	        	latitud="";
	        	longitud="";
		        SupportMapFragment supportMapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		        mapa = supportMapFragment.getMap();
		        mapa.setMyLocationEnabled(true);
		    	lbl_gps=(TextView)findViewById(R.id.txt_gps);
		        btn_guardar = (Button) findViewById(R.id.btn_guardar);
		    	funcion = new Funciones();
			    bun = getIntent().getExtras();
				handler = new Handler();
				controladorClientes=new ControladorCliente(this);
				empresa = bun.getInt("EMPRESA");
				codigoCliente = bun.getInt("CODIGOCLIENTE");
				lbl_gps.setText(bun.getString("NOMBRECLIENTE"));
		        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		        Criteria criteria = new Criteria();
		        String bestProvider = locationManager.getBestProvider(criteria, true);
		        Location location = locationManager.getLastKnownLocation(bestProvider);
		        if (location != null) {
		            onLocationChanged(location);
		        }
		        controladorConfiguracion=new ControladorConfiguracion(this);
		        locationManager.requestLocationUpdates(bestProvider, 0, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    if (location != null) {
                    	 latitud=String.valueOf(location.getLatitude());
                         longitud=String.valueOf(location.getLongitude());
                     	 if (!latitud.equals("") && !longitud.equals("")) {	
	                         LatLng latLng = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
	                         mapa.addMarker(new MarkerOptions().position(latLng));
	                         mapa.moveCamera(CameraUpdateFactory.newLatLng(latLng));
	                         mapa.animateCamera(CameraUpdateFactory.zoomTo(15));
                         }else{
                        	 Toast toast = Toast.makeText(getApplicationContext(),"sin datos de gps", Toast.LENGTH_SHORT);
							 toast.show();
                         }
                    }
                }
		        
		        btn_guardar.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						try {		
							if (!latitud.equals("") && !longitud.equals("")) {	
								ControladorInforme controlador_log=new ControladorInforme(GpsClienteActivity.this);
								Log log =new Log();
								log.setDescripcion("GPS CLIENTE - "+codigoCliente);
								log.setTipo(30);
								log.setFecha(new Date().getTime());
								log.setEstado(0);
								log.setVendedor( bun.getInt("VENDEDOR"));
								controlador_log.guardarLog(log);
								
								controladorClientes.guardarDatosGps(Double.parseDouble(latitud.toString()),  Double.parseDouble(longitud.toString()), codigoCliente);
								configuracion =  controladorConfiguracion.buscarConfiguracion();
								Toast toast = Toast.makeText(getApplicationContext(),"Cliente localizado", Toast.LENGTH_SHORT);
								toast.show();
								conectorCliente = new ConectorCliente(configuracion, empresa, codigoCliente,Double.parseDouble(latitud.toString()), Double.parseDouble(longitud.toString()));						
								Thread thread = new Thread() {
									public void run() {
										handler.post(new Runnable() {
											@Override
											public void run() {	
													try {
														conectorCliente.correr(GpsClienteActivity.this);		
													} catch (Exception ex) {
														ex.getMessage();
													} 			
												}
											});		
									}
								};
								thread.start();
								
							} else {
								Toast toast = Toast.makeText(getApplicationContext(),"sin datos de gps", Toast.LENGTH_SHORT);
								toast.show();
							}
						} catch (Exception ex) {
							ex.getMessage();
						}
					}
				});
	        }
        }catch(Exception ex){
        	funcion.MostrarMensajeAceptar(this,"GPS","Bug en la localizacion del cliente");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        latitud=String.valueOf(latitude);
        longitud=String.valueOf(longitude);
        LatLng latLng = new LatLng(latitude, longitude);
        mapa.addMarker(new MarkerOptions().position(latLng));
        mapa.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mapa.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onProviderDisabled(String provider) {
     
    }

    @Override
    public void onProviderEnabled(String provider) {
      
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
       
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.gps_cliente, menu);
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
