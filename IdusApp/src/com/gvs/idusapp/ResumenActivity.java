package com.gvs.idusapp;

import java.util.Date;

import com.gvs.controladores.ControladorInforme;
import com.gvs.controladores.ControladorVendedor;
import com.gvs.controladores.ControladorCierreDeDia;
import com.gvs.modelos.Log;
import com.gvs.utilidades.Funciones;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ResumenActivity extends Fragment{
	private Button btn_cliente;
	private TextView lbl_programadas, lbl_fuera_ruta, lbl_efectivas,
			lbl_no_efectivas, lbl_realizadas, lbl_drop_size, lbl_pedidos,
			lbl_pedido_fuera_ruta, lbl_ruta_actual, lbl_ruta_objetivo,
			lbl_visita_actual, lbl_visita_objetivo, lbl_gps, lbl_latitud,
			lbl_longitud,lbl_cantidad_total_clientes,lbl_monto_total_pedidos,lbl_cantidad_total_pedidos;
	private Bundle bun;
	private int empresa, vendedor, dia;
	private Funciones funcion;
	private LocationManager locManager;
	private LocationListener locLis;
	private Location location;
	private Handler handler;
	private ControladorCierreDeDia controladorFinDeDia;
	private ControladorVendedor controladorVendedor;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View V = inflater.inflate(R.layout.activity_resumen, container, false);
		setHasOptionsMenu(true);
		return V;
	}
	
	@Override
	public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);
		lbl_programadas = (TextView) getActivity().findViewById(R.id.lbl_programadas);
		lbl_fuera_ruta = (TextView) getActivity().findViewById(R.id.lbl_fuera_ruta);
		lbl_efectivas = (TextView) getActivity().findViewById(R.id.lbl_efectivas);
		lbl_no_efectivas = (TextView) getActivity().findViewById(R.id.lbl_no_efectivas);
		lbl_realizadas = (TextView) getActivity().findViewById(R.id.lbl_realizadas);
		lbl_drop_size = (TextView) getActivity().findViewById(R.id.lbl_drop_size);
		lbl_pedidos = (TextView) getActivity().findViewById(R.id.lbl_pedidos);
		lbl_pedido_fuera_ruta = (TextView) getActivity().findViewById(R.id.lbl_pedido_fuera_ruta);
		lbl_ruta_actual = (TextView) getActivity().findViewById(R.id.lbl_ruta_actual);
		lbl_ruta_objetivo = (TextView) getActivity().findViewById(R.id.lbl_ruta_objetivo);
		lbl_visita_actual = (TextView) getActivity().findViewById(R.id.lbl_visita_actual);
		lbl_visita_objetivo = (TextView) getActivity().findViewById(R.id.lbl_visita_objetivo);
		lbl_gps = (TextView) getActivity().findViewById(R.id.lbl_gps);
		lbl_latitud = (TextView) getActivity().findViewById(R.id.lbl_latitud);
		lbl_longitud = (TextView) getActivity().findViewById(R.id.lbl_longitud);
		btn_cliente = (Button) getView().findViewById(R.id.btn_clientes);
		lbl_cantidad_total_clientes=(TextView)getView().findViewById(R.id.lbl_cantidad_total_clientes);
		lbl_monto_total_pedidos=(TextView)getView().findViewById(R.id.lbl_monto_total_pedidos);
		lbl_cantidad_total_pedidos=(TextView)getView().findViewById(R.id.lbl_cantidad_total_pedidos);
	}
	
	@Override
	public void onResume() {
		try{
			handler = new Handler();
			bun = getActivity().getIntent().getExtras();
			controladorVendedor = new ControladorVendedor(getActivity());
			funcion = new Funciones();
			vendedor = bun.getInt("VENDEDOR");
			empresa = bun.getInt("EMPRESA");
			dia = bun.getInt("DIA");
			cargarDatos(empresa, vendedor, getActivity(), dia);
			
			btn_cliente.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							comenzarLocalizacion();
							if(lbl_gps.getText().toString().equals("Si")){
								try{	
									ControladorInforme controlador_log=new ControladorInforme(getActivity());
									Log log =new Log();
									log.setDescripcion("BOTON CLIENTES");
									log.setTipo(5);
									log.setFecha(new Date().getTime());
									log.setEstado(0);
									log.setVendedor(vendedor);
									controlador_log.guardarLog(log);
								}catch(Exception ex){
									
								}
								Intent intent = new Intent(getActivity(), ClienteActivity.class);
								intent.putExtras(bun);
								startActivity(intent);
							}else{
								Toast toast =Toast.makeText(getActivity(),"Activar GPS", Toast.LENGTH_SHORT);
								toast.show();
							}	
						}
					});
				}
			});
			
		}catch(Exception ex){
			Toast toast =Toast.makeText(getActivity(),"Bub en el resumen", Toast.LENGTH_SHORT);
			toast.show();
		}
		super.onResume();
	}
	
	private void cargarDatos(int empresa, int vendedor, Context cont, int dia) throws Exception {
		try {
			controladorFinDeDia = new ControladorCierreDeDia(cont,vendedor);
			int cantCliDia = controladorFinDeDia.devolverCantidadClientesDelDia(dia);
			int cantVEfect = controladorFinDeDia.devolverCantidadVendidos(dia);
			int cantVEfectFR = controladorFinDeDia.devolverCantidadVendidosFR(dia);
			int cantVnEfec = controladorFinDeDia.devolverCantidadNoVendidos(dia);
			int totalVista = cantVEfect + cantVnEfec;
			double importePedido = controladorFinDeDia.devolverImportePedido(dia);
			double importePedidoFR = controladorFinDeDia.devolverImportePedidoFR(dia);
			String respuesta = controladorVendedor.buscarObjetivoVendedor(empresa,vendedor);
			
			int cantidad_total_clientes=cantVEfect+cantVEfectFR;	
			int cantidad_total_pedidos=controladorFinDeDia.devolverCantidadPedidosTotal(dia);
			double importe_total_pedidos=importePedido+importePedidoFR;
			lbl_cantidad_total_clientes.setText(String.valueOf(cantidad_total_clientes));
			lbl_monto_total_pedidos.setText(String.valueOf(funcion.formatDecimal(importe_total_pedidos, 2)));
			lbl_cantidad_total_pedidos.setText(String.valueOf(cantidad_total_pedidos));
			
			if (respuesta.equals("sd") == false) {
				String tex[];
				tex = respuesta.split("~");
				lbl_ruta_objetivo.setText(tex[0]);
				lbl_visita_objetivo.setText(tex[1]);
			}
			lbl_programadas.setText(String.valueOf(cantCliDia));
			lbl_efectivas.setText(String.valueOf(cantVEfect));
			lbl_no_efectivas.setText(String.valueOf(cantVnEfec));
			lbl_realizadas.setText(String.valueOf(totalVista));
			lbl_pedidos.setText(String.valueOf(funcion.formatDecimal(importePedido, 2)));
			lbl_fuera_ruta.setText(String.valueOf(cantVEfectFR));
			lbl_pedido_fuera_ruta.setText(String.valueOf(funcion.formatDecimal(importePedidoFR, 2)));

			if (cantCliDia > 0 && cantVEfect > 0) {
				double efectividadRuta = ((cantVEfect * 100) / cantCliDia);
				double efectividadVist = ((cantVEfect * 100) / totalVista);
				double dropSize = (importePedido / cantCliDia);
				lbl_ruta_actual.setText(String.valueOf(funcion.formatDecimal(efectividadRuta, 2)));
				lbl_visita_actual.setText(String.valueOf(funcion.formatDecimal(efectividadVist, 2)));
				lbl_drop_size.setText(String.valueOf(funcion.formatDecimal(dropSize, 2)));
			}
			handler.post(new Runnable() {
				@Override
				public void run() {
					comenzarLocalizacion();
				}
			});
		} catch (Exception e) {
			throw new Exception("Bug en la carga del resumen");
		}

	}
	
	private void comenzarLocalizacion(){
		try{
			locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
			if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			} else {
				if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
					locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				} 
			}
		} catch (Exception e){
			
		}

		locLis = new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {

			}
			
			@Override
			public void onProviderEnabled(String provider) {
				lbl_gps.setText("Si");
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				lbl_gps.setText("No");
			}
			
			@Override
			public void onLocationChanged(Location location) {	
				lbl_latitud.setText(String.valueOf(location.getLatitude()));
				lbl_longitud.setText(String.valueOf(location.getLongitude()));
			}

		};
		
		if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locLis);
			
			 if (locManager != null) {
			    	location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
               if (location != null) {
            	   lbl_latitud.setText(String.valueOf(location.getLatitude()));
   				lbl_longitud.setText(String.valueOf(location.getLongitude()));
               }
           }
			 lbl_gps.setText("Si");
			
		} else {
			lbl_gps.setText("No");
			if (locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locLis);
				if (locManager != null) {
			    	location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                    	lbl_latitud.setText(String.valueOf(location.getLatitude()));
        				lbl_longitud.setText(String.valueOf(location.getLongitude()));
                    }
                }			
			} 
		}	
	}
}
