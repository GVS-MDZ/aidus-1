package com.gvs.idusapp;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.xmlpull.v1.XmlSerializer;
import com.gvs.controladores.ControladorInforme;
import com.gvs.controladores.ControladorPedido;
import com.gvs.controladores.ControladorUsuario;
import com.gvs.modelos.Log;
import com.gvs.modelos.Pedido;
import com.gvs.modelos.DetallePedido;
import com.gvs.utilidades.EnviarMail;
import com.gvs.utilidades.Funciones;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PedidoMailActivity extends Activity {
	private ListView list_view;
	private EditText txt_email,txt_usuario,txt_contrasena;
	private Button btn_enviar;
	private Dialog dialog_permiso;
	private TextView lbl_mensaje;
	private Bundle bun;
	private int empresa,vendedor,dia;
	private ProgressDialog dialog = null;
	private Funciones funcion;
	private Handler handler;
	private List<Pedido> lista;
	private ControladorUsuario controladorUsuario;
	private ControladorPedido controladorPedido;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedido_mail);
		try{
			list_view = (ListView) findViewById(R.id.lista_pedidos_pendientes);
			btn_enviar = (Button)findViewById(R.id.btn_enviar_mail);
			txt_email=(EditText)findViewById(R.id.txt_mail);
			/*controladorPedido = new ControladorPedido();
			controladorPedido.setContexto(PedidoMailActivity.this);*/
			controladorPedido = new ControladorPedido(PedidoMailActivity.this);
			bun = getIntent().getExtras();
			empresa = bun.getInt("EMPRESA");
			vendedor = bun.getInt("VENDEDOR");
			dia=bun.getInt("DIA");
			handler = new Handler();
			funcion = new Funciones(this);
			txt_email.getBackground().setColorFilter(Color.parseColor("#502b70"), PorterDuff.Mode.SRC_ATOP);
			btn_enviar.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					try{
						if(txt_email.getText().toString().trim().equals("")){
							Toast toast = Toast.makeText(getApplicationContext(),"Ingresar Email", Toast.LENGTH_SHORT);
							toast.show();
						}else{
							if(funcion.validarEmail(txt_email.getText().toString())){
							if (funcion.conexionInternet()) {
								 dialog_permiso=new Dialog(PedidoMailActivity.this);
								 dialog_permiso.requestWindowFeature(Window.FEATURE_NO_TITLE);
								 dialog_permiso.setContentView(R.layout.dialogo_permisos);
								 dialog_permiso.show();
								 txt_usuario = (EditText)dialog_permiso.findViewById(R.id.txt_usuario);
								 txt_contrasena = (EditText)dialog_permiso.findViewById(R.id.txt_contrasena);
								 lbl_mensaje=(TextView)dialog_permiso.findViewById(R.id.lbl_mensaje);
								 Button btn_editar_aceptar = (Button) dialog_permiso.findViewById(R.id.btn_aceptar);
							     btn_editar_aceptar.setOnClickListener(new OnClickListener() {				
										@SuppressLint("SimpleDateFormat")
										@Override
										public void onClick(View v) {
											try{
												controladorUsuario = new ControladorUsuario(PedidoMailActivity.this);
												if (controladorUsuario.validarUsuarioAdministrador(txt_usuario.getText().toString(), txt_contrasena.getText().toString())!=null) {
													ControladorInforme controlador_log=new ControladorInforme(PedidoMailActivity.this);
													Log log =new Log();
													log.setDescripcion("ENVIO MAIL");
													log.setTipo(39);
													log.setFecha(new Date().getTime());
													log.setEstado(0);
													log.setVendedor(bun.getInt("VENDEDOR"));
													controlador_log.guardarLog(log);
													dialog_permiso.dismiss();
													dialog = ProgressDialog.show(PedidoMailActivity.this,"Email","Enviando Pedidos...");
													Thread thread = new Thread() {
														public void run() {
															try {
																String fullPath = Environment.getExternalStorageDirectory()+ "/IdusApp";
														    	File dir = new File(fullPath);
														    	if (!dir.exists()) {
														    			dir.mkdirs();
														    	}
														    	SimpleDateFormat format=new SimpleDateFormat("ddMMyyyy");
														    	String nombre_archivo=empresa+"_vend"+vendedor+"_"+format.format(new Date())+".xml";
														    	File file = new File(fullPath,nombre_archivo);	
														    	FileOutputStream f = new FileOutputStream(file);
													    		XmlSerializer serializer = Xml.newSerializer();
													    		serializer.setOutput(f, "UTF-8");
												    		    serializer.startDocument(null, Boolean.valueOf(true));
												    		    serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
												    		    serializer.startTag(null, "comprobantes");
																controladorPedido.marcarParaEnviarPedidosMail(dia);
																List<Pedido> pedidos = controladorPedido.buscarPedidosParaEnviarMail(dia);
																
																for (Pedido pedido : pedidos) {
																		serializer.startTag(null, "pedido");
														    		        serializer.startTag(null, "empresa");
														    		        serializer.text(String.valueOf(empresa));
														    		        serializer.endTag(null, "empresa");
														    		        
														    		        serializer.startTag(null, "numero_original");
														    		        serializer.text(String.valueOf(pedido.getNumero_original()));
														    		        serializer.endTag(null, "numero_original");
														    		        
														    		        serializer.startTag(null, "fecha");
														    		        serializer.text(pedido.getFecha());
														    		        serializer.endTag(null, "fecha");
														    		        
														    		        serializer.startTag(null, "avance");
														    		        serializer.text(String.valueOf(pedido.getAvance()));
														    		        serializer.endTag(null, "avance");
														    		        
														    		        serializer.startTag(null, "vendedor");
														    		        serializer.text(String.valueOf(pedido.getCodigoVendedor()));
														    		        serializer.endTag(null, "vendedor");
														    		        
														    		        serializer.startTag(null, "cliente");
														    		        serializer.text(String.valueOf(pedido.getCodigoCliente()));
														    		        serializer.endTag(null, "cliente");
														    		        
														    		        serializer.startTag(null, "cantidad_items");
														    		        serializer.text(pedido.getItem());
														    		        serializer.endTag(null, "cantidad_items");
														    		        
														    		        serializer.startTag(null, "lugar");
														    		        serializer.text("1");
														    		        serializer.endTag(null, "lugar");
														    		        
														    		        serializer.startTag(null, "estado");
														    		        serializer.text("0");
														    		        serializer.endTag(null, "estado");
														    		        
														    		        serializer.startTag(null, "internet");
														    		        serializer.text(String.valueOf(pedido.getInternet()));
														    		        serializer.endTag(null, "internet");
														    		        
														    		        
														    		        serializer.startTag(null, "latitud");
														    		        serializer.text(String.valueOf(pedido.getLatitud()));
														    		        serializer.endTag(null, "latitud");
														    		        
														    		        
														    		        serializer.startTag(null, "longitud");
														    		        serializer.text(String.valueOf(pedido.getLongitud()));
														    		        serializer.endTag(null, "longitud");
														    		        
														    		        serializer.startTag(null, "precision");
														    		        serializer.text(String.valueOf(pedido.getPrecision()));
														    		        serializer.endTag(null, "precision");
														    		        
														    		        serializer.startTag(null, "proveedor");
														    		        serializer.text(String.valueOf(pedido.getProveedor()));
														    		        serializer.endTag(null, "proveedor");
														    		        
														    		        serializer.startTag(null, "observacion");
														    		        serializer.text(String.valueOf(pedido.getObservacion()));
														    		        serializer.endTag(null, "observacion");
														    		        
														    		        serializer.startTag(null, "fecha_despacho");
														    		        serializer.text(String.valueOf(pedido.getFechaEntrega()));
														    		        serializer.endTag(null, "fecha_despacho");
																		List<DetallePedido> items = controladorPedido.buscarItemsPorPedidoMail(Integer.parseInt(pedido.getNumero_original()));
																		for (DetallePedido item: items){
																				try{
																					serializer.startTag(null, "item");
																    		        serializer.startTag(null, "numero_pedido");
																    		        serializer.text(String.valueOf(item.getNumero()));
																    		        serializer.endTag(null, "numero_pedido");
																    		        
																    		        serializer.startTag(null, "numero_item");
																    		        serializer.text(String.valueOf(item.getItem()));
																    		        serializer.endTag(null, "numero_item");
																    		        
																    		        serializer.startTag(null, "articulo");
																    		        serializer.text(String.valueOf(item.getCodigo()));
																    		        serializer.endTag(null, "articulo");

																    		        serializer.startTag(null, "cantidad");
																    		        serializer.text(String.valueOf(item.getCantidad()));
																    		        serializer.endTag(null, "cantidad");
																    		        
																    		        serializer.startTag(null, "precio");
																    		        serializer.text(String.valueOf(item.getPrecio()));
																    		        serializer.endTag(null, "precio");
																    		        
																    		        serializer.startTag(null, "descuento");
																    		        serializer.text(String.valueOf(item.getDescuento()));
																    		        serializer.endTag(null, "descuento");
																    		        serializer.endTag(null, "item");
																				}catch(Exception ex){
																					 serializer.endTag(null, "item");
																					continue;
																				}
																		}
																		serializer.endTag(null, "pedido");
																}
																serializer.endTag(null,"comprobantes");
												    		    serializer.endDocument();
												    		    serializer.flush();
												    		    f.close();
																
																EnviarMail sender = new EnviarMail(getResources().getString(R.string.mailUsuario), getResources().getString(R.string.mailPassword));
												                sender.sendMail("AIDUS GVS", "LISTADO DE PEDIDOS",getResources().getString(R.string.mailUsuario),txt_email.getText().toString()+","+getResources().getString(R.string.mailUsuario),nombre_archivo);   
												               
																handler.post(new Runnable() {
																	@Override
																	public void run() {
																		txt_email.setText("");
																		Toast toast =Toast.makeText(getApplicationContext(),"Email enviado, verificar", Toast.LENGTH_SHORT);
																		toast.show();
																		buscarPedidos();
																		mostrarLista();	
																		dialog.dismiss();
																		dialog_permiso.dismiss();
																	}
																});
								
															} catch (Exception e) {
																handler.post(new Runnable() {
																	@Override
																	public void run() {
																		buscarPedidos();
																		mostrarLista();	
																		dialog.dismiss();
																		dialog_permiso.dismiss();
																	}
																});
															}
														}
													};
													thread.start();
												}else{
													lbl_mensaje.setText("Usuario Incorrecto");
												}
											}catch(Exception ex){
												Toast toast =Toast.makeText(getApplicationContext(),"Bug al generar mail", Toast.LENGTH_SHORT);
												toast.show();
											}
									    }
									});
									Button btn_editar_cancelar= (Button) dialog_permiso.findViewById(R.id.btn_cancelar);
									btn_editar_cancelar.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											dialog_permiso.dismiss();
										}
									});	
							} else {
								funcion.MostrarMensajeAceptar(PedidoMailActivity.this,"SIN INTERNET","No podra enviar email");
							}
						}else{
							Toast toast = Toast.makeText(getApplicationContext(),"Email Incorrecto", Toast.LENGTH_SHORT);
							toast.show();
						}
					}
				}catch(Exception ex){
					Toast toast =Toast.makeText(getApplicationContext(),"Bug al generar mail", Toast.LENGTH_SHORT);
					toast.show();
				}
				}
			});
		
			buscarPedidos();
			mostrarLista();
			realizarOperacion();
		}catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug al listar pedidos", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	@Override
	protected void onRestart() {
		try{
			buscarPedidos();
			mostrarLista();
			realizarOperacion();
		}catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug al listar pedidos", Toast.LENGTH_SHORT);
			toast.show();
		}
		super.onRestart();
	}
	
	private void buscarPedidos(){
		try{
			lista=controladorPedido.buscarPedidosXDiaMail(vendedor,dia);
		}catch(Exception ex){
			Toast toast =Toast.makeText(getApplicationContext(),"Bug al listar pedidos", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private void mostrarLista() {
		if(lista!=null){
			ArrayAdapter<Pedido> adapter = new MyListAdapter();
			list_view.setAdapter(adapter);
		}else{
			list_view.setAdapter(null);
		}	
	}
	
	private void realizarOperacion() {
		list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				try{
					Pedido order = lista.get(position);
					ControladorInforme controlador_log=new ControladorInforme(PedidoMailActivity.this);
					Log log =new Log();
					log.setDescripcion("PEDIDO MAIL - "+order.getNumero_original());
					log.setTipo(40);
					log.setFecha(new Date().getTime());
					log.setEstado(0);
					log.setVendedor(bun.getInt("VENDEDOR"));
					controlador_log.guardarLog(log);
					String[] dato01 = order.getNumero_original().toString().split("-");
					bun.putLong("NUMERO",Long.parseLong(dato01[0].toString().trim()));
					Intent intent = new Intent(PedidoMailActivity.this,DescripcionPedidoActivity.class);
					intent.putExtras(bun);
					startActivity(intent);
				}catch(Exception ex){
					Toast toast =Toast.makeText(getApplicationContext(),"Bug en el pedido seleccionado", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
	}
	
	private class MyListAdapter extends ArrayAdapter<Pedido> {

		public MyListAdapter() {
			super(PedidoMailActivity.this, R.layout.activity_pedido_cliente, lista);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder holder;
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.item_pedido, parent,false);
				holder = new ViewHolder();
				holder.image_operation=(ImageView) view.findViewById(R.id.img_estado_cliente);
				holder.lbl_name=(TextView) view.findViewById(R.id.lbl_numero_pedido);
				holder.lbl_address=(TextView) view.findViewById(R.id.lbl_fecha_pedido);
				holder.lbl_total=(TextView) view.findViewById(R.id.lbl_total);
				view.setTag(holder);
			}else{
				holder = (ViewHolder) view.getTag();
			}
			Pedido pedido = lista.get(position);
			holder.image_operation.setImageResource(pedido.getIcono());
			holder.lbl_name.setText(pedido.getNumero_original()+" - "+pedido.getNumero_final());
			holder.lbl_address.setText(pedido.getNombre_cliente());
			holder.lbl_total.setText(pedido.getFecha()+"- Total: "+funcion.formatDecimal(pedido.getTotal(),2)+" - Items:"+pedido.getItem());
			return view;
		}

	}
	
	static class ViewHolder {
		ImageView image_operation;
		TextView lbl_name;
		TextView lbl_address;
		TextView lbl_total;
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.pedido_mail, menu);
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
