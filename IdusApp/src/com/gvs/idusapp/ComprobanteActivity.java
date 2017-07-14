package com.gvs.idusapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import com.gvs.controladores.ControladorComprobante;
import com.gvs.controladores.ControladorInforme;
import com.gvs.modelos.Cobranza;
import com.gvs.modelos.Log;
import com.gvs.modelos.Comprobante;
import com.gvs.utilidades.Funciones;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ComprobanteActivity extends Activity {
	
	private Bundle bun;
	private ListView list_view;
	private TextView lbl_cliente,lbl_total,lbl_comprobante,txt_importe,txt_saldo,txt_recibo,lbl_mensaje;
	private Funciones funcion; 
	private Dialog dialog_cobranza;
	private List<Comprobante> lista;
	private int codigoCliente,codigoVendedor;
	private ControladorComprobante controladorComprobante;
	private Comprobante comprobante;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_comprobantes);
		 try{
			 list_view=(ListView)findViewById(R.id.lista_comprobantes);
			 lbl_cliente=(TextView)findViewById(R.id.lbl_cliente);
			 lbl_total=(TextView)findViewById(R.id.lbl_total_cliente);
			 funcion=new Funciones();
			 bun = getIntent().getExtras();
		     controladorComprobante = new ControladorComprobante(ComprobanteActivity.this);
			 handler = new Handler();
			 codigoVendedor=bun.getInt("VENDEDOR");
			 codigoCliente = bun.getInt("CODIGOCLIENTE");
			 lbl_cliente.setText(bun.getString("NOMBRECLIENTE"));  
		     buscarComprobantes();
		     mostrarLista();
		     realizarOperacion();
			 lbl_total.setText("$ "+funcion.formatDecimal((controladorComprobante.getTotal()),2));
		 }catch(Exception ex){
			 Toast toast =Toast.makeText(this,"Bug al listar comprobantes", Toast.LENGTH_SHORT);
			 toast.show();
		 }
	}
	
	@Override
	protected void onRestart() {
		 try{
			buscarComprobantes();
			mostrarLista();
		 }catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug al listar comprobantes", Toast.LENGTH_SHORT);
			toast.show();
		 }
		super.onRestart();
	}
	
	private void buscarComprobantes(){
		try{
			lista=controladorComprobante.buscarComprobantesPorClientes(codigoCliente);
		}catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug al listar comprobantes", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private void mostrarLista() {
		if(lista!=null){
			ArrayAdapter<Comprobante> adapter = new MyListAdapter();
			list_view.setAdapter(adapter);
			
		}
	}
	
	private void realizarOperacion() {
		list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				try{
					comprobante = lista.get(position);
					 if(comprobante.getClase().equals("AC") || comprobante.getClase().equals("RC")){
						 funcion.MostrarMensajeAceptar(ComprobanteActivity.this, "Cobranza", "No se puede realizar la cobranza");					
					 }else{
						 dialog_cobranza=new Dialog(ComprobanteActivity.this);
						 dialog_cobranza.setCancelable(false);
						 dialog_cobranza.requestWindowFeature(Window.FEATURE_NO_TITLE);
						 dialog_cobranza.setContentView(R.layout.dialogo_cobranza);
						 lbl_comprobante=(TextView)dialog_cobranza.findViewById(R.id.lbl_comprobante_cobranza);
						 txt_recibo=(TextView)dialog_cobranza.findViewById(R.id.txt_recibo);
						 txt_importe=(TextView)dialog_cobranza.findViewById(R.id.txt_importe);
						 txt_saldo=(TextView)dialog_cobranza.findViewById(R.id.txt_saldo);
						 lbl_mensaje=(TextView)dialog_cobranza.findViewById(R.id.lbl_mensaje);
						 lbl_comprobante.setText(comprobante.getComprobante());			
						 txt_recibo.setText("");
						 txt_importe.setText("");
						 lbl_mensaje.setText("");
						 txt_saldo.setText("$ "+comprobante.getSaldo());
						 txt_saldo.setTextColor(Color.RED);
						 
						 Button btn_editar_aceptar = (Button) dialog_cobranza.findViewById(R.id.btn_aceptar_editar);				 
						 btn_editar_aceptar.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									try{
										if(txt_recibo.getText().toString().trim().equals("")){
											lbl_mensaje.setText("Ingrese el n° de recibo");
											txt_recibo.requestFocus();
										}else{
											if(txt_importe.getText().toString().trim().equals("")){
												lbl_mensaje.setText("Ingrese un importe");
												txt_importe.requestFocus();
									        }else{
									         if(Double.parseDouble(txt_importe.getText().toString())> Double.parseDouble(comprobante.getSaldo())){
									        	 lbl_mensaje.setText("El importe no debe ser mayor que el saldo");
									             txt_importe.requestFocus();
									         }else{
									        	 ControladorInforme controlador_log=new ControladorInforme(ComprobanteActivity.this);
												 Log log =new Log();
												 log.setDescripcion("COBRANZA - "+comprobante.getSucursal()+"-"+comprobante.getClase()+"-"+comprobante.getTipo()+"-"+comprobante.getNumero());
												 log.setTipo(29);
												 log.setFecha(new Date().getTime());
												 log.setEstado(0);
												 log.setVendedor(codigoVendedor);
												 controlador_log.guardarLog(log);
									        	 lbl_mensaje.setText("Guardando la cobranza....");
									     		 double saldo=Double.parseDouble(comprobante.getSaldo())-Double.parseDouble(txt_importe.getText().toString());
									     		 controladorComprobante.modificarComprobante(saldo, comprobante);
									     		 Cobranza gain=new Cobranza();
									     		 gain.setClase(comprobante.getClase());
									     		 gain.setTipo(comprobante.getTipo());
									     		 gain.setSucursal(comprobante.getSucursal());
									     		 gain.setNumero_comprobante(comprobante.getNumero());
									     		 gain.setNumero_recibo(Integer.parseInt(txt_recibo.getText().toString()));
									     		 gain.setImporte_pagado(Double.parseDouble(txt_importe.getText().toString()));
									     		 gain.setSaldo(Double.parseDouble(comprobante.getSaldo()));
									     		 gain.setFecha(new Date().getTime());
									     		 gain.setEstado(0);
									     		 gain.setCodigo_cliente(codigoCliente);
									     		 gain.setCodigo_vendedor(codigoVendedor);
									     		 controladorComprobante.guardarVoucher(gain);
												 Thread thread = new Thread() {
													public void run() {
														try {					
															handler.post(new Runnable() {
																@Override
																public void run() {	
																	try{
																		buscarComprobantes();
															     		mostrarLista();
															     		lbl_total.setText("$ "+funcion.formatDecimal((controladorComprobante.getTotal()),2));
															        	dialog_cobranza.dismiss();
															        	Toast toast =Toast.makeText(getApplicationContext(),"Cobranza realizada correctamente", Toast.LENGTH_SHORT);
																	    toast.show();
																	}catch(Exception ex){
																		Toast toast =Toast.makeText(getApplicationContext(),"Bug al listar comprobantes", Toast.LENGTH_SHORT);
																	    toast.show();
																	    dialog_cobranza.dismiss();
																    }
																}
															});
														} catch (Exception e) {
															handler.post(new Runnable() {
																@Override
																public void run() {	
																	buscarComprobantes();
														     		mostrarLista();
														     		lbl_total.setText("$ "+funcion.formatDecimal((controladorComprobante.getTotal()),2));
																	dialog_cobranza.dismiss();
																	Toast toast = Toast.makeText(getApplicationContext(),"Bug al enviar la cobranza",Toast.LENGTH_SHORT);
																	toast.show();
																}
															});													
														}
													}
												};
												thread.start();
									         }
									        }
									}	
								 }catch(Exception ex){
									 Toast toast =Toast.makeText(getApplicationContext(),"Bug al cargar cobranza", Toast.LENGTH_SHORT);
								     toast.show();
								 }
								}						
							});
									
						 Button btn_editar_cancelar = (Button) dialog_cobranza.findViewById(R.id.btn_cancelar_editar);
						 btn_editar_cancelar.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									dialog_cobranza.dismiss();
								}
							});
						
						 dialog_cobranza.show();				
				    }
				}catch(Exception ex){
					Toast toast =Toast.makeText(getApplicationContext(),"Bug en el comprobante seleccionado", Toast.LENGTH_SHORT);
					toast.show();
				}
		}
		
		});
	}
	
	private class MyListAdapter extends ArrayAdapter<Comprobante> {

		public MyListAdapter() {
			super(ComprobanteActivity.this, R.layout.activity_comprobantes, lista);
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder holder;
			try {
				if (view == null) {	
					view = getLayoutInflater().inflate(R.layout.item_comprobante, parent,false);
					holder = new ViewHolder();
					holder.image_operation=(ImageView) view.findViewById(R.id.img_estado_voucher);
					holder.lbl_comprobante=(TextView) view.findViewById(R.id.lbl_comprobante);
					holder.lbl_fecha = (TextView) view.findViewById(R.id.lbl_fecha_comprobante);
					holder.lbl_saldo= (TextView) view.findViewById(R.id.lbl_saldo);
					view.setTag(holder);
				}else{
					holder = (ViewHolder) view.getTag();
				}
				Comprobante comprobante = lista.get(position);
				Calendar c = Calendar.getInstance();
				Calendar fechaInicio = new GregorianCalendar();
				SimpleDateFormat f=new SimpleDateFormat("dd/MM/yyyy");
				String a=comprobante.getFecha();
				Date fec=f.parse(a);		
				fechaInicio.setTime(fec);
				Calendar fechaFin = new GregorianCalendar();	 
				fechaFin.setTime(new Date());
				c.setTimeInMillis(fechaFin.getTime().getTime() - fechaInicio.getTime().getTime()); 
				int dias=c.get(Calendar.DAY_OF_YEAR);
				holder.image_operation.setImageResource(comprobante.getIcono());
				holder.lbl_comprobante.setText(comprobante.getComprobante());
				holder.lbl_fecha.setText(comprobante.getFecha()+" - Antiguedad: "+dias+" dias");
				holder.lbl_saldo.setText(comprobante.getSaldo());	
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return view;
		}

	}
	
	static class ViewHolder {
		ImageView image_operation;
		TextView lbl_comprobante;
		TextView lbl_fecha ;
		TextView lbl_saldo;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.comprobante, menu);
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
