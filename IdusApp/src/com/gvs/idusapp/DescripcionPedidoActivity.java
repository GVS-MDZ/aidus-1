package com.gvs.idusapp;

import java.util.Date;
import com.gvs.controladores.ControladorInforme;
import com.gvs.controladores.ControladorPedido;
import com.gvs.modelos.Log;
import com.gvs.modelos.Pedido;
import com.gvs.utilidades.Funciones;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class DescripcionPedidoActivity extends Activity {

	private TextView lbl_numero_pedido, lbl_numero_final, lbl_fecha,
			lbl_codigo_cliente, lbl_codigo_vendedor, lbl_cantidad_item,
			lbl_estado, lbl_fecha_enviado, lbl_internet, lbl_longitud,
			lbl_latitud, lbl_precision, lbl_proveedor, lbl_observacion;
	private Funciones funcion;
	private Bundle bun;
	private int estado = 0;
	private Pedido pedido;
	private ControladorPedido controladorPedidos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_descripcion_pedido);
		try{
		/*	controladorPedidos=new ControladorPedido();
			controladorPedidos.setContexto(DescripcionPedidoActivity.this);	*/
			controladorPedidos = new ControladorPedido(DescripcionPedidoActivity.this);

			funcion = new Funciones();
			lbl_numero_pedido = (TextView) findViewById(R.id.lbl_numero_pedido);
			lbl_numero_final = (TextView) findViewById(R.id.lbl_numero_final);
			lbl_fecha = (TextView) findViewById(R.id.lbl_fecha);
			lbl_codigo_cliente = (TextView) findViewById(R.id.lbl_codigo_cliente);
			lbl_codigo_vendedor = (TextView) findViewById(R.id.lbl_codigo_vendedor);
			lbl_cantidad_item = (TextView) findViewById(R.id.lbl_cantidad_item);
			lbl_estado = (TextView) findViewById(R.id.lbl_estado);
			lbl_fecha_enviado = (TextView) findViewById(R.id.lbl_fecha_enviado);
			lbl_internet = (TextView) findViewById(R.id.lbl_internet);
			lbl_longitud = (TextView) findViewById(R.id.lbl_longitud);
			lbl_latitud = (TextView) findViewById(R.id.lbl_latitud);
			lbl_precision = (TextView) findViewById(R.id.lbl_precision);
			lbl_proveedor = (TextView) findViewById(R.id.lbl_proveedor);
			lbl_observacion = (TextView) findViewById(R.id.lbl_observacion);		
		    bun = getIntent().getExtras();
			buscarDetallePedido();
		}catch(Exception ex){
			funcion.MostrarMensajeAceptar(this,"DETALLE FINAL","Bug en el detalle de pedido");
		}
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		try{
			buscarDetallePedido();	
		}catch(Exception ex){
			funcion.MostrarMensajeAceptar(this,"DETALLE FINAL","Bug en el detalle de pedido");
		}
	}
	
	synchronized private void buscarDetallePedido(){
		try{
			pedido=controladorPedidos.buscarDetallePedido(bun.getLong("NUMERO"));
			if(pedido!=null){
				lbl_numero_pedido.setText("NUMERO DE PEDIDO: "+ funcion.format(Integer.parseInt(pedido.getNumero_original()), 10));
				lbl_numero_final.setText("NUMERO FINAL: "+ funcion.format(Integer.parseInt(pedido.getNumero_final()), 10));
				lbl_fecha.setText("FECHA: "+ funcion.dateToString_yyyymmdd_hhmm(Long.parseLong(pedido.getFecha())));
				lbl_codigo_cliente.setText("CLIENTE: "+ funcion.format(pedido.getCodigoCliente(), 5));
				lbl_codigo_vendedor.setText("VENDEDOR: "+ funcion.format(pedido.getCodigoVendedor(), 3));
				lbl_cantidad_item.setText("CANTIDAD DE ITEMS: "+ funcion.format(Integer.parseInt(pedido.getItem()), 2));
				bun.putInt("ITEMS",Integer.parseInt(pedido.getItem()));
				String estadoD = "";
				estado = pedido.getEstado();
				switch (estado) {
				case 0:
					estadoD = "PEDIDO FINALIZADO Y SIN ENVIAR";
					break;
				case 1:
					estadoD = "ENVIADO CORRECTAMENTE";
					break;
				case 2:
					estadoD = "PEDIDO ANULADO";
					break;
				case 10:
					estadoD = "PEDIDO EDITABLE (SIN FINALIZAR)";
					break;
				case 11:
					estadoD = "PEDIDO LISTO PARA SER ENVIADO";
					break;
				case 12:
					estadoD = "PEDIDO CON ERROR DE ENVIO ";
					break;
				default:
					estadoD = "DESCONOCIDO / CONSULTAR";
					break;
				}
				lbl_estado.setText(estadoD);
				if (pedido.getFechaEntrega() > 0) {
					lbl_fecha_enviado.setText("ENVIADO EL: "+ funcion.dateToString_yyyymmdd_hhmm(pedido.getFechaEntrega()));
				} else {
					lbl_fecha_enviado.setText("SIN FECHA DE ENVIO");
				}
				lbl_internet.setText("CONECTADO POR: "+ pedido.getInternet());
				lbl_latitud.setText("LATITUD: "+ pedido.getLatitud());
				lbl_longitud.setText("LONGITUD: "+ pedido.getLongitud());
				lbl_precision.setText("PRECISION: "+ pedido.getPrecision());
				lbl_proveedor.setText("TOMADA POR: "+pedido.getProveedor());
				lbl_observacion.setText(pedido.getObservacion());
			}
		}catch(Exception ex){
			funcion.MostrarMensajeAceptar(this,"DETALLE FINAL","Bug en el detalle de pedido");
		}
	}
	
	private void mostrarConfirmacion(String titulo,String cadena,final int tipo){
        
        AlertDialog.Builder alertbox = new AlertDialog.Builder(DescripcionPedidoActivity.this);
        alertbox.setTitle(titulo);
        alertbox.setMessage(cadena);
        alertbox.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            	try{
	            	ControladorInforme controlador_log=new ControladorInforme(DescripcionPedidoActivity.this);
					Log log =new Log();
					log.setDescripcion("ANULAR -"+pedido.getNumero_original());
					log.setTipo(32);
					log.setFecha(new Date().getTime());
					log.setEstado(0);
					log.setVendedor(pedido.getCodigoVendedor());
					controlador_log.guardarLog(log);
	            	
					controladorPedidos.anularPedido(bun.getLong("NUMERO"));
					Toast toast =Toast.makeText(getApplicationContext(),"Pedido Anulado", Toast.LENGTH_SHORT);
					toast.show();
					finish();
            	}catch(Exception ex){
            		
            	}
            }
        });	 
        alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                
            }
        });
        alertbox.show();
}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.descripcion_pedido, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		try{
			switch (id) {
			case R.id.menu_editar: {
				if (estado == 10 || estado == 0 || estado == 12) {
					ControladorInforme controlador_log=new ControladorInforme(DescripcionPedidoActivity.this);
					Log log =new Log();
					log.setDescripcion("EDITAR");
					log.setTipo(31);
					log.setFecha(new Date().getTime());
					log.setEstado(0);
					log.setVendedor(pedido.getCodigoVendedor());
					controlador_log.guardarLog(log);
					Intent intent = new Intent(DescripcionPedidoActivity.this,RealizarPedidoActivity.class);
					bun.putInt("CODIGOCLIENTE",pedido.getCodigoCliente());
					intent.putExtras(bun);
					startActivity(intent);
				} else {
					funcion.MostrarMensajeAceptar(DescripcionPedidoActivity.this, "EDICION","No se puede editar el pedido");
				}
				break;
			}
			case R.id.menu_anular: {
				if (estado == 10 || estado == 0 || estado == 12) {
					mostrarConfirmacion("CONFIRMAR", "¿Está seguro de que quiere anular el pedido?", 2);
				} else {
					funcion.MostrarMensajeAceptar(DescripcionPedidoActivity.this, "ANULAR","No se puede Anular el pedido");
				}
				break;
			}
			case R.id.menu_finalizar: {
				if (estado == 10) {
					ControladorInforme controlador_log=new ControladorInforme(DescripcionPedidoActivity.this);
					Log log =new Log();
					log.setDescripcion("FINALIZAR -"+pedido.getNumero_original());
					log.setTipo(33);
					log.setFecha(new Date().getTime());
					log.setEstado(0);
					log.setVendedor(pedido.getCodigoVendedor());
					controlador_log.guardarLog(log);
					
					controladorPedidos.finalizarPedido(bun.getLong("NUMERO"));
					Toast toast =Toast.makeText(getApplicationContext(),"Pedido Finalizado", Toast.LENGTH_SHORT);
					toast.show();
					finish();
				} else {
					funcion.MostrarMensajeAceptar(DescripcionPedidoActivity.this, "FINALIZAR","No se puede Finalizar el pedido");
				}
				break;
			}
			case R.id.menu_items: {
				ControladorInforme controlador_log=new ControladorInforme(DescripcionPedidoActivity.this);
				Log log =new Log();
				log.setDescripcion("VER ITEM -"+pedido.getNumero_original());
				log.setTipo(34);
				log.setFecha(new Date().getTime());
				log.setEstado(0);
				log.setVendedor(pedido.getCodigoVendedor());
				controlador_log.guardarLog(log);
				Intent intent = new Intent(DescripcionPedidoActivity.this,ItemPedidoActivity.class);
				intent.putExtras(bun);
				startActivity(intent);
				break;
			}case R.id.cerrar_sesion: {
				startActivity(new Intent(getBaseContext(), MainActivity.class)
	            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
				finish();
				break;
			}case R.id.sincronizar:{
				Intent i = new Intent(getApplicationContext(), SincronizadorActivity.class);
				bun.putString("SINCRONIZACION","GENERAL");
				i.putExtras(bun);
				startActivity(i);
				break;
			}
			default:
				break;
			}
		}catch(Exception ex){
			
		}
		return super.onOptionsItemSelected(item);
	}
	
}
