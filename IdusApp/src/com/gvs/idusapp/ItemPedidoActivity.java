package com.gvs.idusapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.gvs.controladores.ControladorArticulo;
import com.gvs.controladores.ControladorConfiguracion;
import com.gvs.controladores.ControladorInforme;
import com.gvs.controladores.ControladorPedido;
import com.gvs.modelos.Articulo;
import com.gvs.modelos.Configuracion;
import com.gvs.modelos.Log;
import com.gvs.modelos.Pedido;
import com.gvs.utilidades.Funciones;
import com.gvs.modelos.DetallePedido;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemPedidoActivity extends Activity {
	
	private ListView lista_view;
	private long numeroPedido=0;
	private DetallePedido item=null;
	private List<DetallePedido> lista_item;
	private Configuracion configuracion;
	private Dialog dialog_editar,dialog_eliminar;
	private Bundle bun;
	private Articulo articulo;
	private DetallePedido detallePedido;
	private Pedido pedido;
	private double cantV;
	private ControladorConfiguracion controladorConfiguracion;
	private ControladorPedido controladorPedidos;
	private ControladorArticulo controladorArticulo;
	private double subTotal, descuentoItem;
	private Funciones funcion;
	
@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.activity_detalle_pedido);	
			lista_view = (ListView) findViewById(R.id.lista_item);
			lista_item = new ArrayList<DetallePedido>();
		/*	controladorPedidos = new ControladorPedido();
			controladorPedidos.setContexto(ItemPedidoActivity.this);*/
			controladorPedidos = new ControladorPedido(ItemPedidoActivity.this);
			controladorArticulo = new ControladorArticulo(ItemPedidoActivity.this);
			funcion = new Funciones();
		    item=new DetallePedido();
		    controladorConfiguracion=new ControladorConfiguracion(this);
		    configuracion =  controladorConfiguracion.buscarConfiguracion();
		    bun = getIntent().getExtras();
			numeroPedido = bun.getLong("NUMERO"); 
		    setTitle(String.valueOf(numeroPedido));
		    pedido=controladorPedidos.buscarDetallePedido(numeroPedido);
		    buscarDetalle();
		    mostrarLista();
		    realizarOperacion();	
		}catch(Exception ex){
			Toast toast =Toast.makeText(getApplicationContext(),"Bug al listar items", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private void buscarDetalle(){
		try{
			lista_item=controladorPedidos.listarItemsDelPedidos(numeroPedido);
		}catch(Exception ex){
			Toast toast =Toast.makeText(getApplicationContext(),"Bug al buscar Items", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	private void mostrarLista() {
	if(lista_item!=null){
			ArrayAdapter<DetallePedido> adapter = new MyListAdapter();
			lista_view.setAdapter(adapter);
		}
}
		
	private void realizarOperacion() {
		lista_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {	
				try{
					detallePedido = lista_item.get(position);		   
					int numeroItem = Integer.parseInt(detallePedido.getItem());
					item = controladorPedidos.buscarItem(numeroPedido, numeroItem);
					cantV=Double.parseDouble(item.getCantidad());
				}catch(Exception ex){
					Toast toast =Toast.makeText(getApplicationContext(),"Bug al seleccionar item", Toast.LENGTH_SHORT);
					toast.show();
				}
			}		
			});
	
		}

	private void eliminarItem(){
	try{
		if(detallePedido!=null){
		    dialog_eliminar=new Dialog(ItemPedidoActivity.this);
		    dialog_eliminar.requestWindowFeature(Window.FEATURE_NO_TITLE);
		    dialog_eliminar.setContentView(R.layout.dialogo_eliminar);
		    TextView txt_eliminar=(TextView)dialog_eliminar.findViewById(R.id.lbl_eliminar);
		    txt_eliminar.setText(detallePedido.getDescripcion());
		    
		    Button btn_eliminar_aceptar = (Button) dialog_eliminar.findViewById(R.id.btn_aceptar_eliminar1);
			btn_eliminar_aceptar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {	
					try{
						if(pedido.getEstado()==1){
							Toast toast =Toast.makeText(getApplicationContext(),"No se puede eliminar el articulo", Toast.LENGTH_SHORT);
							toast.show();
							dialog_eliminar.dismiss();
						}else{
							ControladorInforme controlador_log=new ControladorInforme(ItemPedidoActivity.this);
							Log log =new Log();
							log.setDescripcion("ELIMINAR ITEM PEDIDO - " +pedido.getNumero_original()+" - "+item.getCodigo());
							log.setTipo(20);
							log.setFecha(new Date().getTime());
							log.setEstado(0);
							log.setVendedor(pedido.getCodigoVendedor());
							controlador_log.guardarLog(log);	
							controladorPedidos.borrarItemPedido(item);
							controladorPedidos.restarCantidadLanzamientos(bun.getInt("CODIGOCLIENTE"),item.getCodigo(),Double.parseDouble(item.getCantidad()));
							controladorPedidos.restarCantidadImpresindibles(bun.getInt("CODIGOCLIENTE"),item.getCodigo(),Double.parseDouble(item.getCantidad()));	
							controladorPedidos.eliminarItem(numeroPedido);
							lista_item=controladorPedidos.listarItemsDelPedidos(numeroPedido);
							if(lista_item!=null){			
								Toast toast =Toast.makeText(getApplicationContext(),"Item Eliminado", Toast.LENGTH_SHORT);
								toast.show();	
								detallePedido=null;
								dialog_eliminar.dismiss();
								mostrarLista();						
							}else{
								finish();
							}
						}
					}catch(Exception ex){
					   Toast toast =Toast.makeText(getApplicationContext(),"Bug al eliminar item", Toast.LENGTH_SHORT);
					   toast.show();
					}
			   }
			});
		
			Button btn_eliminar_cancelar = (Button) dialog_eliminar.findViewById(R.id.btn_cancelar_eliminar1);
		    btn_eliminar_cancelar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try{
						dialog_eliminar.dismiss();
					}catch(Exception ex){
						Toast toast =Toast.makeText(getApplicationContext(),"Bug al cancelar eliminacion", Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			});
			dialog_eliminar.show();
			}
		}catch(Exception ex){
			Toast toast =Toast.makeText(getApplicationContext(),"Bug al querer eliminar item", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private void editarItem(){
	try{
		if(detallePedido!=null){
		    dialog_editar=new Dialog(ItemPedidoActivity.this);
		    dialog_editar.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog_editar.setContentView(R.layout.dialogo_editar);
			TextView txt_cantidad=(TextView)dialog_editar.findViewById(R.id.txt_cantidad_editado);
			TextView txt_descuento=(TextView)dialog_editar.findViewById(R.id.txt_descuento_editado);
			TextView txt_editar=(TextView)dialog_editar.findViewById(R.id.txt_editar);
			
			if (configuracion.getDescuento()==1){
				txt_descuento.setEnabled(true);
			}else {
				txt_descuento.setEnabled(false);
			}
			txt_editar.setText(detallePedido.getDescripcion());
			txt_cantidad.setText(String.valueOf(item.getCantidad()));
			txt_descuento.setText(String.valueOf(item.getDescuento()));
			txt_cantidad.requestFocus();
			Button btn_editar_aceptar = (Button) dialog_editar.findViewById(R.id.btn_aceptar_editar);
			btn_editar_aceptar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {	
					try{
						if(pedido.getEstado()==1){
							Toast toast =Toast.makeText(getApplicationContext(),"No se puede editar el articulo", Toast.LENGTH_SHORT);
							toast.show();
							dialog_editar.dismiss();
						}else{
							ControladorInforme controlador_log=new ControladorInforme(ItemPedidoActivity.this);
							Log log =new Log();
							log.setDescripcion("MODIFICAR ITEM PEDIDO - "+pedido.getNumero_original()+" - "+item.getCodigo());
							log.setTipo(21);
							log.setFecha(new Date().getTime());
							log.setEstado(0);
							log.setVendedor(pedido.getCodigoVendedor());
							controlador_log.guardarLog(log);	
							TextView txt_cantidad=(TextView)dialog_editar.findViewById(R.id.txt_cantidad_editado);
							TextView txt_descuento=(TextView)dialog_editar.findViewById(R.id.txt_descuento_editado);
							controladorPedidos.cambiarCantidadEnitem(Double.parseDouble(txt_cantidad.getText().toString().trim()),Double.parseDouble(txt_descuento.getText().toString().trim()),bun.getInt("CODIGOCLIENTE") ,item);
							double cantN=Double.parseDouble(txt_cantidad.getText().toString().trim());
							double result=0;
							if(cantN>cantV){
								result=cantN-cantV;
								controladorPedidos.sumarCantidadImpresindibles(bun.getInt("CODIGOCLIENTE"),  item.getCodigo(), result);
								controladorPedidos.sumarCantidadLanzamientos(bun.getInt("CODIGOCLIENTE"),  item.getCodigo(), result);
							}else{
								result=cantV-cantN;
								controladorPedidos.restarCantidadImpresindibles(bun.getInt("CODIGOCLIENTE"), item.getCodigo(), result);
								controladorPedidos.restarCantidadLanzamientos(bun.getInt("CODIGOCLIENTE"),  item.getCodigo(), result);
							}
							lista_item.removeAll(lista_item);
							lista_item=controladorPedidos.listarItemsDelPedidos(numeroPedido);					
						  	articulo = controladorArticulo.buscarPorCodigo(item.getCodigo());
						  	double cantidad=0;
						  	if(txt_cantidad.getText()==""){
						  		cantidad=0;
						  		txt_cantidad.setText("0");
						  	}else{
						      cantidad = Double.parseDouble(txt_cantidad.getText().toString().trim());
						  	}
						  	int multiplo = articulo.getMultiplo();
							if(calcularMultiplo(cantidad, multiplo)){
								if(Double.parseDouble(txt_descuento.getText().toString().trim())<0){
									TextView txt_mensaje=(TextView)dialog_editar.findViewById(R.id.txt_mensaje);
									txt_mensaje.setText("El descuento no puede ser menor que cero");			
								}else{
									Toast toast =Toast.makeText(getApplicationContext(),"Item modificado", Toast.LENGTH_SHORT);
									toast.show();	
									detallePedido=null;
									mostrarLista();
									dialog_editar.dismiss();
								}
							}else{
							 TextView txt_mensaje=(TextView)dialog_editar.findViewById(R.id.txt_mensaje);
							 txt_mensaje.setText("La cantidad no es multiplo de "+articulo.getMultiplo());			
							}
					  }	
					}catch(Exception ex){
						Toast toast =Toast.makeText(getApplicationContext(),"Bug al editar item", Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			});
			
			Button btn_editar_cancelar = (Button) dialog_editar.findViewById(R.id.btn_cancelar_editar);
			btn_editar_cancelar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try{
					dialog_editar.dismiss();
					}catch(Exception ex){
						Toast toast =Toast.makeText(getApplicationContext(),"Bug al cancelar eliminacion", Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			});
			
			dialog_editar.show();
			}
	}catch(Exception ex){
		Toast toast =Toast.makeText(getApplicationContext(),"Bug al querer editar item", Toast.LENGTH_SHORT);
		toast.show();
	}
}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.detalle_pedido, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem items) {
		int id = items.getItemId();
		if (id == R.id.editar_item) {
			editarItem();
		}else if(id == R.id.eliminar_item){
			eliminarItem();
		}else if(id == R.id.cerrar_sesion){
			startActivity(new Intent(getBaseContext(), MainActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
			finish();
		}else if(id == R.id.sincronizar){
			Intent i = new Intent(getApplicationContext(), SincronizadorActivity.class);
			bun.putString("SINCRONIZACION","GENERAL");
			i.putExtras(bun);
			startActivity(i);
		}
		return super.onOptionsItemSelected(items);
	}
	
	private boolean calcularMultiplo(Double cantidad,int multiplo) {
		int resto =0;
		if (multiplo>1) {
			resto = (int) (cantidad%multiplo);
		} else {
			resto=0;
		}
		if (resto==0) {
			return true;
		} else {
			return false;
		}
	}
	
	private class MyListAdapter extends ArrayAdapter<DetallePedido> {

		public MyListAdapter() {
			super(ItemPedidoActivity.this,R.layout.activity_detalle_pedido, lista_item);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder holder;
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.item_detalle_pedido, parent, false);
				holder = new ViewHolder();
				holder.lbl_descripcion=(TextView)view.findViewById(R.id.lbl_descripcion);
				holder.lbl_cantidad=(TextView) view.findViewById(R.id.lbl_cantidad);
				holder.lbl_descuento=(TextView)view.findViewById(R.id.lbl_descuento);
				holder.lbl_precio=(TextView)view.findViewById(R.id.lbl_precio);
				view.setTag(holder);
			}else{
				holder = (ViewHolder) view.getTag();
			}
			if(lista_item!=null){
				DetallePedido detail = lista_item.get(position);
				holder.lbl_descripcion.setText(detail.getItem()+" - "+detail.getDescripcion()); 
				holder.lbl_cantidad.setText("Cantidad: "+ detail.getCantidad());
				holder.lbl_descuento.setText("Descuento: "+detail.getDescuento()+" %");
				holder.lbl_descuento.setTextColor(detail.getColor());
				descuentoItem =( Double.parseDouble(detail.getPrecio()) * Double.parseDouble(detail.getCantidad())  )* (Double.parseDouble(detail.getDescuento()) / 100);
				subTotal = (Double.parseDouble(detail.getPrecio()) * Double.parseDouble(detail.getCantidad())) - descuentoItem;
				holder.lbl_precio.setText("Precio unitario: $" + detail.getPrecio() + "                Subtotal: $"+ funcion.formatDecimal(subTotal, 2) );
			}
			return view;
		}
	}

	static class ViewHolder {
		TextView lbl_descripcion;
		TextView lbl_cantidad;
		TextView lbl_descuento;
		TextView lbl_precio;
	}
	
}


