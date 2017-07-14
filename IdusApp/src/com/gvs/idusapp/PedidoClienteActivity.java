package com.gvs.idusapp;

import java.util.Date;
import java.util.List;
import com.gvs.conectores.ConectorActualizacionStock;
import com.gvs.conectores.ConectorGeneral;
import com.gvs.conectores.ConectorPedido;
import com.gvs.controladores.ControladorConfiguracion;
import com.gvs.controladores.ControladorInforme;
import com.gvs.controladores.ControladorPedido;
import com.gvs.controladores.ControladorSincronizacion;
import com.gvs.modelos.Configuracion;
import com.gvs.modelos.Log;
import com.gvs.modelos.Pedido;
import com.gvs.utilidades.Funciones;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PedidoClienteActivity extends Activity {
	private ListView list_view;
	private TextView lbl_cliente;
	private Button btn_enviar;
	private Bundle bun;
	private int empresa,vendedor,dia;
	private ProgressDialog syncPrgDialog = null;
	private Funciones funcion;
	private Handler handler;
	private List<Pedido> lista;
	private ControladorPedido busPedido;
	private ControladorConfiguracion busquedaConfiguracion;
	private ControladorSincronizacion busquedaSincronizacion;
	private Configuracion configuracion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedido_cliente);
		try{
			list_view = (ListView) findViewById(R.id.lista_historico_cliente);
			btn_enviar = (Button)findViewById(R.id.btn_enviar2);
			lbl_cliente = (TextView) findViewById(R.id.lbl_cliente);
			
			funcion = new Funciones(this);
		/*	busPedido = new ControladorPedido();
			busPedido.setContexto(PedidoClienteActivity.this);*/
			busPedido = new ControladorPedido(PedidoClienteActivity.this);
			bun = getIntent().getExtras();
			busquedaSincronizacion=new ControladorSincronizacion(this);
			lbl_cliente.setText(bun.getString("NOMBRECLIENTE"));
			empresa = bun.getInt("EMPRESA");
			vendedor = bun.getInt("VENDEDOR");
			dia=bun.getInt("DIA");
			handler = new Handler();
			busquedaConfiguracion=new ControladorConfiguracion(this);
			
			btn_enviar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					    try{
						    configuracion =  busquedaConfiguracion.buscarConfiguracion();
							syncPrgDialog = ProgressDialog.show(PedidoClienteActivity.this,"Sincronizando","Enviando pedidos...");
							ControladorInforme controlador_log=new ControladorInforme(PedidoClienteActivity.this);
							Log log =new Log();
							log.setDescripcion("ENVIAR PEDIDO CLIENTE");
							log.setTipo(26);
							log.setFecha(new Date().getTime());
							log.setEstado(0);
							log.setVendedor(vendedor);
							controlador_log.guardarLog(log);
					if (funcion.conexionInternet()) {
								Thread thread = new Thread() {
									public void run() {
										try {
										 busPedido.marcarParaEnviarPedidos(dia);
											List<Pedido> pedidos = busPedido.buscarPedidosParaEnviar(dia);
											ConectorGeneral conectorGeneral;
											for (Pedido pedido : pedidos) {
												try{
													conectorGeneral = new ConectorPedido(configuracion,PedidoClienteActivity.this, pedido,empresa);
													conectorGeneral.correr(PedidoClienteActivity.this);
													busPedido.pedidoEnviadoCorrectamente(Integer.parseInt(pedido.getNumero_original()));
												}catch(Exception ex){
													busPedido.pedidoConErrorAlEnvio(Integer.parseInt(pedido.getNumero_original()));
													continue;
												}
											}
											busquedaSincronizacion.guardarSincronizacionParcial(empresa, vendedor);
											conectorGeneral = new ConectorActualizacionStock(configuracion, empresa, 1,PedidoClienteActivity.this);
											conectorGeneral.correr(PedidoClienteActivity.this);
											actualizarVista();
										} catch (Exception e) {
											actualizarVista();
										}
									}
								};
								thread.start();
							} else {
								Toast toast =Toast.makeText(getApplicationContext(),"Red no conectada, verifique internet", Toast.LENGTH_SHORT);
								toast.show();
							}
					    }catch(Exception ex){
							
						}
					}
				
				});
			
			buscarPedidos();
			mostrarLista();
			realizarOperacion();
		}catch(Exception ex){
			Toast toast =Toast.makeText(getApplicationContext(),"Bug al listar pedidos", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private void actualizarVista(){
		handler.post(new Runnable() {
			@Override
			public void run() {
				buscarPedidos();
				mostrarLista();	
				syncPrgDialog.dismiss();
			}
		});
	}

	@Override
 	protected void onRestart() {
		super.onRestart();
		buscarPedidos();
		mostrarLista();
		realizarOperacion();
		
	}
	
	synchronized private void buscarPedidos(){
		try{
			lista=busPedido.buscarPedidosXCliente(dia,bun.getInt("CODIGOCLIENTE"), bun.getInt("VENDEDOR"));
		}catch(Exception ex){
			Toast toast =Toast.makeText(getApplicationContext(),"Bug al listar pedidos", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private void mostrarLista() {
		if(lista!=null){
			ArrayAdapter<Pedido> adapter = new MyListAdapter();
			list_view.setAdapter(adapter);
		}
	}
	
	synchronized private void realizarOperacion() {
		list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				try{
					Pedido order = lista.get(position);
					bun.putLong("NUMERO",Long.parseLong(order.getNumero_original()));
					Intent intent = new Intent(PedidoClienteActivity.this,DescripcionPedidoActivity.class);
					intent.putExtras(bun);
					ControladorInforme controlador_log=new ControladorInforme(PedidoClienteActivity.this);
					Log log =new Log();
					log.setDescripcion("PEDIDO DEL CLIENTE - "+order.getCodigoCliente()+" - "+order.getNumero_original());
					log.setTipo(25);
					log.setFecha(new Date().getTime());
					log.setEstado(0);
					log.setVendedor(order.getCodigoVendedor());
					controlador_log.guardarLog(log);	
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
			super(PedidoClienteActivity.this, R.layout.activity_pedido_cliente, lista);
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
			Pedido order = lista.get(position);
			holder.image_operation.setImageResource(order.getIcono());
			holder.lbl_name.setText(order.getNumero_original()+" - "+order.getNumero_final());
			holder.lbl_address.setText(order.getNombre_cliente());
			holder.lbl_total.setText(order.getFecha()+" - Total: "+funcion.formatDecimal(order.getTotal(),2)+" - Items:"+order.getItem());
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
		getMenuInflater().inflate(R.menu.pedido_cliente, menu);
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
