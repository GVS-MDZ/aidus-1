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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PedidosDiariosActivity extends Fragment {
	private ListView list_view;
	private Button btn_enviar;
	private Bundle bun ;
	private List<Pedido> lista;
	private Handler handler;
	private int empresa,vendedor,dia;
	private ProgressDialog dialog = null;
	private Funciones funcion;
	private ControladorConfiguracion controladorConfiguracion;
	private ControladorSincronizacion controladorSincronizacion;
	private ControladorPedido controladorPedido;
	private Configuracion configuracion;
	private Pedido pedido ;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View V = inflater.inflate(R.layout.activity_pedidos_diarios, container, false);	
		btn_enviar = (Button) V.findViewById(R.id.btn_enviar);
		list_view =  (ListView)V.findViewById(R.id.lista_pedido_dia); 		
		return V;
	}

	@Override
	public void onResume() {
		try{
			bun = getActivity().getIntent().getExtras();
			handler = new Handler();
			empresa = bun.getInt("EMPRESA");
			vendedor = bun.getInt("VENDEDOR");
			funcion=new Funciones(getActivity());
			dia=bun.getInt("DIA");
			controladorSincronizacion=new ControladorSincronizacion(getActivity());
		/*	controladorPedido=new ControladorPedido();
			controladorPedido.setContexto(getActivity());*/
			controladorPedido=new ControladorPedido(getActivity());
			controladorConfiguracion=new ControladorConfiguracion(getActivity());
			
			btn_enviar.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
				    try{
						configuracion = controladorConfiguracion.buscarConfiguracion();
						dialog = ProgressDialog.show(getActivity(),"Sincronizando","Enviando pedidos...");
						ControladorInforme controlador_log=new ControladorInforme(getActivity());
						Log log =new Log();
						log.setDescripcion("ENVIAR PEDIDOS");
						log.setTipo(8);
						log.setFecha(new Date().getTime());
						log.setEstado(0);
						log.setVendedor(vendedor);
						controlador_log.guardarLog(log);
						if (funcion.conexionInternet()) {	
							Thread thread = new Thread() {
								public void run() {
									try {
										controladorPedido.marcarParaEnviarPedidos(dia);
										List<Pedido> pedidos = controladorPedido.buscarPedidosParaEnviar(dia);
										for (Pedido pedido : pedidos) {
											try{
											ConectorGeneral conectorPedido = new ConectorPedido(configuracion,getActivity(), pedido,empresa);
										    conectorPedido.correr(getActivity());
										    controladorPedido.pedidoEnviadoCorrectamente(Integer.parseInt(pedido.getNumero_original()));
											}catch(Exception ex){
												controladorPedido.pedidoConErrorAlEnvio(Integer.parseInt(pedido.getNumero_original()));	
												continue;
											}
										}
										controladorSincronizacion.guardarSincronizacionParcial(empresa, vendedor);
										ConectorGeneral conectorStock = new ConectorActualizacionStock(configuracion, empresa, 1,getActivity());
										conectorStock.correr(getActivity());
										actualizarLista();
									} catch (Exception e) {
										actualizarLista();
									}
								}
							};
							thread.start();
						} else {
							funcion.MostrarMensajeAceptar(getActivity(),"SIN INTERNET","No podrá sincronizar hasta que esté conectado a internet");
						}
				    }catch(Exception ex){
				    	
				    }
				}
			});
				
			buscarPedidos();
			mostrarLista();
			realizarOperacion();
		}catch(Exception ex){
			Toast toast =Toast.makeText(getActivity(),"Bug en los pedidos del dia", Toast.LENGTH_SHORT);
			toast.show();
		}
		super.onResume();
	}
	
	private void buscarPedidos(){
		try{
			lista=controladorPedido.buscarPedidosXDia(vendedor,dia);
		}catch(Exception ex){
			Toast toast =Toast.makeText(getActivity(),"Bug al listar pedidos", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private void mostrarLista() {
		if(lista!=null){
			ArrayAdapter<Pedido> adapter = new MyListAdapter();
			list_view.setAdapter(adapter);
		}
	}
	
	private void actualizarLista(){
		handler.post(new Runnable() {
			@Override
			public void run() {
				buscarPedidos();
				mostrarLista();	
				dialog.dismiss();
			}
		});
	}
	
	private void realizarOperacion() {
		list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				try{					
					Pedido order = lista.get(position);
					ControladorInforme controlador_log=new ControladorInforme(getActivity());
					Log log =new Log();
					log.setDescripcion("PEDIDO DEL DIA - "+order.getNumero_original());
					log.setTipo(7);
					log.setFecha(new Date().getTime());
					log.setEstado(0);
					log.setVendedor(vendedor);
					controlador_log.guardarLog(log);				
					bun.putLong("NUMERO",Long.parseLong(order.getNumero_original()));
					Intent intent = new Intent(getActivity(),DescripcionPedidoActivity.class);
					intent.putExtras(bun);
					startActivity(intent);
				}catch(Exception ex){
					Toast toast =Toast.makeText(getActivity(),"Bug en el pedido seleccionado", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
	}
	
	private class MyListAdapter extends ArrayAdapter<Pedido> {

		public MyListAdapter() {
			super(getActivity(), R.layout.activity_pedidos_diarios, lista);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder holder;
			if (view == null) {
				view = getActivity().getLayoutInflater().inflate(R.layout.item_pedido, parent,false);
				holder = new ViewHolder();
				holder.image_operation=(ImageView) view.findViewById(R.id.img_estado_cliente);
				holder.lbl_name=(TextView) view.findViewById(R.id.lbl_numero_pedido);
				holder.lbl_address=(TextView) view.findViewById(R.id.lbl_fecha_pedido);
				holder.lbl_total=(TextView) view.findViewById(R.id.lbl_total);
				view.setTag(holder);
			}else{
				holder = (ViewHolder) view.getTag();
			}
			pedido = lista.get(position);
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
	
}
