package com.gvs.idusapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.gvs.controladores.ControladorArticulo;
import com.gvs.controladores.ControladorCliente;
import com.gvs.controladores.ControladorCobranza;
import com.gvs.controladores.ControladorComprobante;
import com.gvs.controladores.ControladorConfiguracion;
import com.gvs.controladores.ControladorInforme;
import com.gvs.controladores.ControladorPedido;
import com.gvs.controladores.ControladorCierreDeDia;
import com.gvs.modelos.Articulo;
import com.gvs.modelos.Configuracion;
import com.gvs.modelos.Informe;
import com.gvs.modelos.Log;
import com.gvs.modelos.ObjetivoVisita;
import com.gvs.utilidades.Funciones;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RealizarPedidoActivity extends Activity {
	
	private double latitud,longitud,precision;
	private String proveedor="";
	private long numeroPedido=0;
	private int request_code = 1;
	static final int DATE_DIALOG_ID = 0;
	private int mYear,mMonth,mDay,cantidadCombo,cantidad_contada,dia,codigoCliente;
	private String total="0";
	private Button btn_buscar,btn_agregar,btn_ver_item,btn_finalizar;
	private RadioGroup radio_group;
	private RadioButton radio_numerico,radio_alfanumerico,radio_detalle;
	private EditText txt_busqueda,txt_detalle,txt_cantidad,txt_descuento,txt_stock,txt_precio,txt_multiplo,txt_item_acumulado,txt_item_permitido,txt_fecha,txt_observacion;
	private TextView lbl_objetivo,lbl_total;
	private Articulo articulo;
	private Bundle bun ;
	private Funciones funcion ;
	private LocationManager locManager;
	private LocationListener locLis;
	private Configuracion configuracion;
	private Menu menu2;
	private boolean cliente_localizado=true;
	private boolean cliente_con_deuda=false;
	private Dialog dialog_gps,dialog_cobranza;
	private String ultima_factura;
	private Location location;
	private ObjetivoVisita objetivo;
	private int rubro,subrubro;
	private String detalle="";
	private boolean fecha_inicio_tomada=false;
	private long fecha_inicio;
	private int nuevoItem= 0;
	private ControladorPedido controladorPedido;
	private ControladorCobranza controladorCobranza;
	private ControladorComprobante controladorComprobante;
	private ControladorConfiguracion controladorConfiguracion;
	private ControladorArticulo controladorArticulo;
	private ControladorCliente controladorCliente;
	private final int REQUEST_ANALYTIC = 2;
	private Informe informe;
//	private Intent analyticPopUp;
	private ControladorInforme controladorInforme;
	private String accion,mejora,recomendacion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_realizar_pedido);
		try{
			bun= getIntent().getExtras();
			setTitle("Pedido: "+bun.getString("NOMBRECLIENTE"));
			btn_buscar=(Button)findViewById(R.id.btn_buscar_articulo);
			btn_agregar=(Button)findViewById(R.id.btn_agregar);
			btn_finalizar=(Button)findViewById(R.id.btn_finalizar);
			btn_ver_item=(Button)findViewById(R.id.btn_items);
			txt_busqueda=(EditText)findViewById(R.id.input_busqueda);
			txt_detalle=(EditText)findViewById(R.id.txt_detalle);
			txt_cantidad=(EditText)findViewById(R.id.txt_cantidad);
			txt_descuento=(EditText)findViewById(R.id.txt_descuento);
			txt_stock=(EditText)findViewById(R.id.txt_stock);
			txt_precio=(EditText)findViewById(R.id.txt_precio);
			lbl_objetivo=(TextView)findViewById(R.id.lbl_objetivo_pedido);
			lbl_total=(TextView)findViewById(R.id.lbl_total_pedido);
			txt_multiplo=(EditText)findViewById(R.id.txt_multiplo);
			txt_item_acumulado=(EditText)findViewById(R.id.txt_item_acumulado);
			txt_item_permitido=(EditText)findViewById(R.id.txt_item_permitido);
			txt_fecha=(EditText)findViewById(R.id.txt_fecha_pedido);
			txt_observacion=(EditText)findViewById(R.id.txt_observacion);
			txt_busqueda.setInputType(InputType.TYPE_CLASS_NUMBER);
			radio_group=(RadioGroup)findViewById(R.id.radio_tipo_texto);
			radio_numerico=(RadioButton)findViewById(R.id.radio_tipo_numerico);
			radio_alfanumerico=(RadioButton)findViewById(R.id.radio_tipo_alfa);
			radio_detalle=(RadioButton)findViewById(R.id.radio_detalle);
			funcion= new Funciones(this);
			//
		/*	controladorPedido = new ControladorPedido();
			controladorPedido.setContexto(getApplicationContext());
			*/
			controladorPedido = new ControladorPedido(RealizarPedidoActivity.this);
			controladorArticulo = new ControladorArticulo(RealizarPedidoActivity.this);
		    controladorCliente=new ControladorCliente(RealizarPedidoActivity.this);
		    controladorConfiguracion=new ControladorConfiguracion(RealizarPedidoActivity.this);
		    configuracion =  controladorConfiguracion.buscarConfiguracion();
		    controladorCobranza=new ControladorCobranza(RealizarPedidoActivity.this);
		    controladorComprobante=new ControladorComprobante(RealizarPedidoActivity.this);
		    controladorInforme = new ControladorInforme(RealizarPedidoActivity.this);
		    //
			numeroPedido=bun.getLong("NUMERO");
			dia=bun.getInt("DIA");
			total="0";	
		
			total=funcion.formatDecimal((controladorPedido.totalPedido(bun.getLong("NUMERO"))),2);
			lbl_total.setText("TOTAL: "+total);
			if(bun.getInt("ITEMS")>0){
				cantidad_contada=bun.getInt("ITEMS");
				bun.putInt("ITEM", controladorPedido.numeroItemsPorPedido(bun.getLong("NUMERO")));
			}
			txt_item_acumulado.setText(String.valueOf(bun.getInt("ITEM")));
	
			if (configuracion.getServicioGeo()==1){
				comenzarLocalizacion();
			}
	
			if (configuracion.getDescuento()==1){
				txt_descuento.setEnabled(true);
			} else {
				txt_descuento.setEnabled(false);
			}
			txt_item_permitido.setText(String.valueOf(configuracion.getCantidadItems()));		
			txt_precio.setEnabled(false);
			txt_precio.setText("0");
			txt_precio.setTextColor(Color.BLACK);
			txt_multiplo.setEnabled(false);
			txt_stock.setEnabled(false);
			txt_item_permitido.setEnabled(false);
			txt_item_acumulado.setEnabled(false);
			txt_item_permitido.setTextColor(Color.BLACK);
			txt_item_acumulado.setTextColor(Color.BLACK);
			txt_detalle.setEnabled(false);
			txt_detalle.setTextColor(Color.BLACK);
			txt_cantidad.setText("");
			txt_descuento.setText("0");
			txt_fecha.setText(funcion.dateToString_dd_mm_yyyy(new Date().getTime() + 86400000));
		    codigoCliente = bun.getInt("CODIGOCLIENTE");
		
			buscarObjetivoPorVisita();
			
			validarClientesConDeuda();		
			
			radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {		
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					if(radio_numerico.isChecked()==true){
						txt_busqueda.setInputType(InputType.TYPE_CLASS_NUMBER);
						txt_busqueda.setHint("Codigo");
					}else if(radio_alfanumerico.isChecked()==true){
						txt_busqueda.setInputType(InputType.TYPE_CLASS_TEXT);
						txt_busqueda.setHint("Codigo");
					}else if(radio_detalle.isChecked()==true){
						txt_busqueda.setInputType(InputType.TYPE_CLASS_TEXT);
						txt_busqueda.setHint("Detalle");
					}
				}
			});
			
			btn_buscar.setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					try {	
						if (configuracion.getServicioGeo()==1){
							comenzarLocalizacion();
						}
						rubro=0;
						subrubro=0;
						
						//RealizarPedidoActivity.this
						Log log =new Log();
						log.setDescripcion("BUSQUEDA ARTICULO - "+txt_busqueda.getText());
						log.setTipo(13);
						log.setFecha(new Date().getTime());
						log.setEstado(0);
						log.setVendedor(bun.getInt("VENDEDOR"));
						try {
							controladorInforme.guardarLog(log);
						} catch (Exception e) {
							android.util.Log.e("BuscarControladorInforme",e.getMessage());

						}
						if(cliente_localizado){
							if(!cliente_con_deuda){
								if((!txt_busqueda.getText().toString().trim().equals("")) || (rubro>0 && subrubro>0)){
										String codigo="";
										
										if (txt_busqueda.equals("")==false && codigo.equals("")) {
											codigo=txt_busqueda.getText().toString().trim();
										}	
										//buscar por codigo
										if (radio_numerico.isChecked()==true ||radio_alfanumerico.isChecked()==true ){
											//comprueba si ya fue cargado
												
												if (controladorPedido.buscarSiYaFueCargadoElArticulo(numeroPedido, codigo)){
													funcion.MostrarMensajeAceptar(RealizarPedidoActivity.this, "Items", "El Artículo ya fue ingresado");
													codigo="";
												}else{
													articulo = controladorArticulo.buscarPorCodigo(codigo);	
													if (articulo!=null){
														txt_detalle.setText(articulo.getDetalle());
														if (articulo.getOferta()>0){
															txt_precio.setText(funcion.formatDecimal(articulo.getOferta(), 2));
															txt_precio.setTextColor(Color.YELLOW);
														} else {
															txt_precio.setText(controladorArticulo.precioPorLista(bun.getInt("CODIGOCLIENTE"), articulo));
															txt_precio.setTextColor(Color.BLACK);
														}
														txt_stock.setText(String.valueOf(articulo.getStock()));
														if (articulo.getStock()<=0){
															txt_stock.setTextColor(Color.parseColor("#FF0000"));//red
														} else {
															txt_stock.setTextColor(Color.parseColor("#008000"));//green
														}
														txt_multiplo.setText(String.valueOf(articulo.getMultiplo()));
														txt_multiplo.setTextColor(Color.BLACK);
														txt_cantidad.requestFocus();	
													}else{
														txt_busqueda.setText("");
														txt_busqueda.setText("");
														txt_detalle.setText("");
														txt_precio.setText("0");
														txt_cantidad.setText("");
														txt_stock.setText("");
														txt_stock.setTextColor(Color.BLACK);
														txt_multiplo.setText("");
														txt_descuento.setText("0");
														funcion.MostrarMensajeAceptar(RealizarPedidoActivity.this, "Articulo", "El Articulo no existe");
													}
											}
									     //buscar por detalle
										} else {
											articulo=null;
											String detalles = txt_busqueda.getText().toString().trim();	
										//	String d=detalle;
										//	int a=rubro;
										//	int s=subrubro;
											
											if(detalle.equals("")){
												rubro=0;
												subrubro=0;
											}
											
											if(detalles.length()>0 && !detalles.equals(detalle)){
												rubro=0;
												subrubro=0;
											}
											if ((detalles.length()>0) || (rubro>0 && subrubro>0) ) {							
												Bundle bunBD = new Bundle();
												bunBD.putInt("EMPRESA", bun.getInt("EMPRESA"));
												bunBD.putInt("VENDEDOR", bun.getInt("VENDEDOR"));
												bunBD.putString("DETALLE", detalles);
												bunBD.putInt("RUBRO", rubro);
												bunBD.putInt("SUBRUBRO", subrubro);
												bunBD.putString("DETALLEBUSQUEDA",detalle);
												Intent intent = new Intent(RealizarPedidoActivity.this, BusquedaArticuloActivity.class);
												intent.putExtras(bunBD);
												startActivityForResult(intent, request_code);
											}
										}
									}
								 }else{
									 dialog_cobranza=new Dialog(RealizarPedidoActivity.this);
									 dialog_cobranza.requestWindowFeature(Window.FEATURE_NO_TITLE);
									 dialog_cobranza.setContentView(R.layout.dialog_cobranza);
									 dialog_cobranza.show();
									 TextView txt_mensaje=(TextView)dialog_cobranza.findViewById(R.id.txt_titulo_gps);
									 txt_mensaje.setText("DEUDA PENDIENTE "+ultima_factura);
									 Button btn_editar_aceptar = (Button) dialog_cobranza.findViewById(R.id.btn_aceptar_gps);
									 btn_editar_aceptar.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												finish();
											}
										});
									 Button btn_editar_cancelar= (Button) dialog_cobranza.findViewById(R.id.btn_cancelar_gps);
									 btn_editar_cancelar.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												dialog_cobranza.dismiss();
											}
										});
								 }				
							}else{
								   dialog_gps=new Dialog(RealizarPedidoActivity.this);
								   dialog_gps.requestWindowFeature(Window.FEATURE_NO_TITLE);
								   dialog_gps.setContentView(R.layout.dialogo_gps);
								   dialog_gps.show();
								   Button btn_editar_aceptar = (Button) dialog_gps.findViewById(R.id.btn_aceptar_gps);
								   btn_editar_aceptar.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											finish();
										}
									});
								   Button btn_editar_cancelar= (Button) dialog_gps.findViewById(R.id.btn_cancelar_gps);
								   btn_editar_cancelar.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											dialog_gps.dismiss();
										}
									});
							}
					} catch (Exception e) {		
						Toast toast =Toast.makeText(getApplicationContext(),"Bug al buscar articulo", Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			}); // Fin boton buscar
		    btn_agregar.setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					try{
							if (configuracion.getServicioGeo()==1){
								try {
									comenzarLocalizacion();
								} catch (Exception e) {
									android.util.Log.e("comenzarGeolocalizacion", e.getMessage());
								}
							}
							if(articulo!=null){
								Log log =new Log();
								log.setDescripcion("AGREGAR ITEM - "+articulo.getCodigo());
								log.setTipo(14);
								log.setFecha(new Date().getTime());
								log.setEstado(0);
								log.setVendedor(bun.getInt("VENDEDOR"));
								
								try {
									controladorInforme.guardarLog(log);
								} catch (Exception ex) {
									android.util.Log.e("controladorInforme",ex.getMessage());
								}	
								validarDatos();
								double cantidad = Double.parseDouble(txt_cantidad.getText().toString().trim());
								int multiplo = Integer.parseInt(txt_multiplo.getText().toString().trim());
								//verifica el multiplo
								if (calcularMultiplo(cantidad, multiplo)) {
								//verifica la cantiadad	
								if (Integer.parseInt(txt_cantidad.getText().toString().trim())<=0){
									funcion.MostrarMensajeAceptar(RealizarPedidoActivity.this, "Cantidad", "La catidad debe ser mayor que cero");					
								} else {
									//controla stock
									boolean controlaStock=false;
									if (articulo!=null){
										try {
											controlaStock = controladorArticulo.controlaStock(articulo.getRubro(), articulo.getSubRubro());
										} catch (Exception e) {
											android.util.Log.e( "ControladorArticuloAgreg",  e.getMessage());

										}
									}
									//controla cantidad > stock
									if (Double.parseDouble(txt_cantidad.getText().toString().trim()) > Double.parseDouble(txt_stock.getText().toString().trim()) && controlaStock) {
										funcion.MostrarMensajeAceptar(RealizarPedidoActivity.this, "CONTROL DE STOCK", "La cantidad ingresada es mayor a el stock disponible de este artículo");
									} else {
										//controla si articulo esta cargado
										if (txt_detalle.getText().toString().trim().equals("")){
											funcion.MostrarMensajeAceptar(RealizarPedidoActivity.this, "BUSQUEDA", "No hay ningun articulo cargado");
										} else {
											//controla vendedor
											if (bun.getInt("VENDEDOR")>0){
												//controla cliente
												if (bun.getInt("CODIGOCLIENTE")>0){
												    try{
												      cantidadCombo=controladorArticulo.buscarCombo(Integer.parseInt(articulo.getCodigo()));
												    }catch(Exception ex){
												      cantidadCombo=0;
												    }
													int cantidad_acumulada=Integer.parseInt(txt_item_acumulado.getText().toString().trim());
													int cantidad_suma=0;
													if(cantidad_acumulada==0 && cantidadCombo==0){
														 cantidad_suma=1;
													}else if(cantidad_acumulada==0 && cantidadCombo!=0){
														cantidad_suma=cantidadCombo;
													}else if(cantidad_acumulada!=0 && cantidadCombo==0){
														cantidad_suma=cantidad_acumulada+1;
													}
													else{
													    cantidad_suma=cantidad_acumulada+cantidadCombo;
													} 
													if (cantidad_suma <= configuracion.getCantidadItems()){
														if(!fecha_inicio_tomada){
															fecha_inicio=new Date().getTime();
															fecha_inicio_tomada=true;
														}
														cantidad_contada=cantidad_contada+1;
													    String cod=articulo.getCodigo();
													    String obs1=txt_observacion.getText().toString().replace("Obs:","");
														String obs2=obs1.replace("%"," porciento ");
														String observacion=obs2.replace("#"," numeral ");
														ContentValues contCab = new ContentValues();
														contCab.put("NUMERO", bun.getLong("NUMERO"));
														contCab.put("NUMEROFINAL", 0);
														contCab.put("FECHA", new Date().getTime());
														contCab.put("AVANCE", 0);
														contCab.put("CODIGOCLIENTE", bun.getInt("CODIGOCLIENTE"));
														contCab.put("CODIGOVENDEDOR", bun.getInt("VENDEDOR"));
														contCab.put("CANTIDADITEMS",cantidad_contada);
														contCab.put("ESTADO", 10);
														contCab.put("FECHAENVIADO", 0);
														contCab.put("LATITUD", latitud);
														contCab.put("LONGITUD", longitud);
														contCab.put("PRECISION", precision);
														contCab.put("PROVEE", proveedor);
														contCab.put("INTERNET",funcion.estadoRedes());
														contCab.put("OBS", observacion);
														contCab.put("DIA",dia);
														contCab.put("FECHAINICIO", fecha_inicio);
														try {
															contCab.put("FECHAENTREGA", funcion._stringToDate(txt_fecha.getText().toString()).getTime());
														} catch (Exception ex) {
															contCab.put("FECHAENTREGA", new Date().getTime() + 86400000);
														}
														ContentValues contCue = new ContentValues();
														contCue.put("NUMERO", bun.getLong("NUMERO"));
														contCue.put("NUMEROITEM", cantidad_contada);
														contCue.put("CODIGOARTICULO",cod);
														contCue.put("CANTIDAD", Double.parseDouble(txt_cantidad.getText().toString().trim()));
														double descuento=Double.parseDouble(txt_descuento.getText().toString());
														double descuento_articulo=0;
														if(descuento==99){
															descuento_articulo=99.99;
														}else{
															descuento_articulo=descuento;
														}
														/*double precio_descuento=(Double.parseDouble(txt_precio.getText().toString().trim())* descuento_articulo)/100;*/
														double precio=Double.parseDouble(txt_precio.getText().toString().trim());
														contCue.put("PRECIO",precio);
														contCue.put("ESTADO", 10);
														
														contCue.put("DESCUENTO", descuento_articulo);
														try {
															
															
															try {
																
																nuevoItem = controladorPedido.grabarPedido(contCab, contCue);
															} catch (Exception e) {
																android.util.Log.e("controladorPedidoGrabar", e.getMessage()+ "contCab " + contCab.getAsString("CODIGOCLIENTE") + "contCuePrecio" + contCue.getAsString("PRECIO"));
															}
															if(controladorArticulo.esArticuloLanzamiento(bun.getInt("CODIGOCLIENTE"), cod)){
																MenuItem m1=menu2.findItem(R.id.menu_lanzamientos);
															    m1.setIcon(R.drawable.amarillo);
															}
															if(controladorArticulo.esArticuloIndispensable(bun.getInt("CODIGOCLIENTE"), cod)){
																MenuItem m1=menu2.findItem(R.id.menu_imprescindibles);
															    m1.setIcon(R.drawable.amarillo);
															}
															if(controladorArticulo.esArticuloSugerido(cod)){
																MenuItem m1=menu2.findItem(R.id.menu_sugeridos);
															    m1.setIcon(R.drawable.amarillo);
															}
															controladorPedido.sumarCantidadImpresindibles(bun.getInt("CODIGOCLIENTE"), cod, Double.parseDouble(txt_cantidad.getText().toString().trim()));
															controladorPedido.sumarCantidadLanzamientos(bun.getInt("CODIGOCLIENTE"), cod, Double.parseDouble(txt_cantidad.getText().toString().trim()));
															if (nuevoItem>0){
																txt_busqueda.setText("");
																txt_busqueda.setText("");
																txt_detalle.setText("");
																txt_precio.setText("0");
																txt_cantidad.setText("");
																txt_stock.setText("");
																txt_stock.setTextColor(Color.BLACK);
																txt_multiplo.setText("");
																txt_descuento.setText("0");
																txt_item_acumulado.setText(String.valueOf(cantidad_suma));
																total=funcion.formatDecimal((controladorPedido.totalPedido(bun.getLong("NUMERO"))),2);
																lbl_total.setText("TOTAL: "+total);
																articulo=null;
																txt_busqueda.requestFocus();
																
																Toast toast =Toast.makeText(getApplicationContext(),"Articulo cargado", Toast.LENGTH_SHORT);
																toast.show();
															}
														} catch (Exception ex) {
															funcion.MostrarMensajeAceptar(RealizarPedidoActivity.this, "ERROR", "ERROR DE DISPOSITIVO: " + ex.getMessage());
														}
													} else {
														funcion.MostrarMensajeAceptar(RealizarPedidoActivity.this, "CANTIDAD DE ITEMS", "Ha llegado al límite de la cantidad de ítems, permitida");
													}
												} else {
													funcion.MostrarMensajeAceptar(RealizarPedidoActivity.this, "CLIENTE", "Error de pérdida de datos. no se puede identificar el cliente");
												}
											} else {
												funcion.MostrarMensajeAceptar(RealizarPedidoActivity.this, "VENDEDOR", "Error de pérdida de datos. no se puede identificar el cliente");
											}
										}
									}
								}
								} else {
									funcion.MostrarMensajeAceptar(RealizarPedidoActivity.this, "Múltiplo", "La cantidad ingresada debe ser múltiplo de: " + txt_multiplo.getText().toString());
								}
						}
					}catch(Exception ex){
						Toast toast =Toast.makeText(getApplicationContext(),ex.getMessage(), Toast.LENGTH_SHORT);
						toast.show();
					}
			  }				
			}); // FIN BOTON AGREGAR
		
		    btn_ver_item.setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					try{
						if(Double.parseDouble(total.toString())>0){
							Log log =new Log();
							log.setDescripcion("VER ITEMS");
							log.setTipo(15);
							log.setFecha(new Date().getTime());
							log.setEstado(0);
							log.setVendedor(bun.getInt("VENDEDOR"));
							controladorInforme.guardarLog(log);	
							bun.putString("CONSUL-PED", "");
							Intent intent = new Intent(RealizarPedidoActivity.this,ItemPedidoActivity.class);
							intent.putExtras(bun);
							startActivity(intent);
						}
					}catch(Exception ex){
						Toast toast =Toast.makeText(getApplicationContext(),"Bug al ver items", Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			});
		    
		    btn_finalizar.setOnClickListener(new OnClickListener() {	
		    	private int pedidosPendientes;
				@Override
				public void onClick(View v) {
					try {
						
						if (configuracion.getServicioGeo()==1){
							comenzarLocalizacion();
						}
						
						if(haveAnalytic()){
														
							informe = new Informe();
							informe.setCliente(codigoCliente);
							informe.setVendedor(bun.getInt("VENDEDOR"));
							informe.setAccion(accion);
							informe.setMejora(mejora);
							informe.setRecomendacion(recomendacion);
							informe.setEstado(0);
							informe.setFecha(new Date().getTime());
							try {
								controladorInforme.guardarInforme(informe);
							} catch (Exception e) {
								android.util.Log.e("controladorInforme",e.getMessage());
							}
						}
						
						if(Double.parseDouble(total)>0){
							Log log =new Log();
							log.setDescripcion("FINALIZAR PEDIDO");
							log.setTipo(16);
							log.setFecha(new Date().getTime());
							log.setEstado(0);
							log.setVendedor(bun.getInt("VENDEDOR"));
							try {
								controladorInforme.guardarLog(log);
							} catch (Exception e) {
								android.util.Log.e("controladorInforme",e.getMessage());

							}	
							try {
								controladorPedido.finalizarPedidoCompleto(bun.getLong("NUMERO"),txt_observacion.getText().toString(),funcion._stringToDate(txt_fecha.getText().toString()).getTime());
							} catch (Exception e) {
								android.util.Log.e("controladorPedido",e.getMessage());

							}
							ControladorCierreDeDia cierreDia;
							try {
								cierreDia = new ControladorCierreDeDia(RealizarPedidoActivity.this, bun.getInt("VENDEDOR"));
							    pedidosPendientes = cierreDia.devolverCantidadNoEnviados(dia);
							} catch (Exception e) {
								android.util.Log.e("controladorCierreDeDia",e.getMessage());

							}
							
							if (pedidosPendientes>=3) {
								Intent intent2=new Intent(RealizarPedidoActivity.this,PedidoClienteActivity.class);
								intent2.putExtras(bun);
								startActivity(intent2);
								Toast toast =Toast.makeText(getApplicationContext(),"Pedido finalizado correctamente", Toast.LENGTH_SHORT);
								toast.show();
								finish();
							}else{
								Toast toast =Toast.makeText(getApplicationContext(),"Pedido finalizado correctamente", Toast.LENGTH_SHORT);
								toast.show();
								finish();
							}		
						}
					} catch (Exception e) {
						Toast toast =Toast.makeText(getApplicationContext(),"Bug al finalizar pedido", Toast.LENGTH_SHORT);
						toast.show();
					}				
				}
			});
		
		    txt_fecha.setOnClickListener(new OnClickListener() {		
				@SuppressWarnings("deprecation")
				@Override
				public void onClick(View v) {
					showDialog(DATE_DIALOG_ID);
				}
			});
		    
		  /*  try{
		    	analyticPopUp = new Intent(RealizarPedidoActivity.this,AnalyticActivity.class);
		    	analyticPopUp.putExtras(bun);
		    	startActivityForResult(analyticPopUp, REQUEST_ANALYTIC);
		    	
		    }catch(Exception ex){
		    	Toast toast =Toast.makeText(this,"Bug al levantar estrategia", Toast.LENGTH_SHORT);
				toast.show();
		    }*/
		
		}catch(Exception ex){
			
			android.util.Log.e("Exception", ex.toString());
			Toast toast =Toast.makeText(this,"Bug en realizar pedidos", Toast.LENGTH_SHORT);
			toast.show();
		}
	}// onCREATE fin
	
	@Override
	protected void onRestart() {
			super.onRestart();
		 	int cantidad_acumulada;
			total=funcion.formatDecimal((controladorPedido.totalPedido(bun.getLong("NUMERO"))),2);
			lbl_total.setText("TOTAL: "+total);
		/*	if(bun.getInt("ITEMS")>0){
				cantidad_contada=bun.getInt("ITEMS");
			
				bun.putInt("ITEM", controladorPedido.numeroItemsPorPedido(bun.getLong("NUMERO")));
			}txt_item_acumulado.setText(String.valueOf(bun.getInt("ITEM")));*/
			
			txt_item_acumulado.setText(String.valueOf(bun.getInt("ITEM")));
			
			
			try{
				/*controladorPedido = new ControladorPedido(RealizarPedidoActivity.this);
				controladorArticulo = new ControladorArticulo(RealizarPedidoActivity.this);
				total="0";
				total=funcion.formatDecimal((controladorPedido.totalPedido(bun.getLong("NUMERO"))),2);
				*/
				
				try {
					cantidad_acumulada = controladorPedido.buscarCantidadAcumulada(bun.getLong("NUMERO"));	
					txt_item_acumulado.setText(String.valueOf(cantidad_acumulada));
				} catch (Exception e) {
					android.util.Log.e("RestartControladorPedido",e.getMessage());
				}
				
				/*lbl_total.setText("TOTAL: "+total); */
				List<Map<String, String>> list1 = new ArrayList <Map<String, String>>();
		    	List<Map<String, String>> list2 = new ArrayList <Map<String, String>>();
		    	List<Map<String, String>> list3 = new ArrayList <Map<String, String>>();
		    	try {
					list1=controladorArticulo.buscarLanzamientos(bun.getInt("CODIGOCLIENTE"));
					list2=controladorArticulo.buscarIndispensables(bun.getInt("CODIGOCLIENTE"));
					list3=controladorArticulo.buscarSugeridos();
				} catch (Exception e) {
					android.util.Log.e("RestartControladorArticulo",e.getMessage());

				}
		    	if(list1==null){
		    		 	MenuItem m1=menu2.findItem(R.id.menu_lanzamientos);
		    		    m1.setIcon(R.drawable.verde);
		    	}else{
		    		List<Map<String, String>> list4 = new ArrayList <Map<String, String>>();
		    		try {
						list4=controladorArticulo.buscarLanzamientosCargados(bun.getInt("CODIGOCLIENTE"));
					} catch (Exception e) {
						android.util.Log.e("RestartControladorArticulo",e.getMessage());

					}
		    		if(list4==null){
		    			MenuItem m1=menu2.findItem(R.id.menu_lanzamientos);
		       		 	m1.setIcon(R.drawable.rojo);
		    		}else{
		    			MenuItem m1=menu2.findItem(R.id.menu_lanzamientos);
		    			m1.setIcon(R.drawable.amarillo);
		    		}
		    		 
		    	}
		    	if(list2==null){
		    			MenuItem m2=menu2.findItem(R.id.menu_imprescindibles);
		    		    m2.setIcon(R.drawable.verde);
		    	}else{
		 			List<Map<String, String>> list4 = new ArrayList <Map<String, String>>();
		    		try {
						list4=controladorArticulo.buscarInidispensableCargado(bun.getInt("CODIGOCLIENTE"));
					} catch (Exception e) {
						android.util.Log.e("RestartControladorArticulo",e.getMessage());

					}
		    		if(list4==null){
		    			MenuItem m1=menu2.findItem(R.id.menu_imprescindibles);
		    			m1.setIcon(R.drawable.rojo);
		    		}else{
		    			MenuItem m1=menu2.findItem(R.id.menu_imprescindibles);
		    			m1.setIcon(R.drawable.amarillo);
		    		}
		    	}
		    	if(list3==null){
		    		 MenuItem m3=menu2.findItem(R.id.menu_sugeridos);
		    		 m3.setIcon(R.drawable.verde);
			   	}else{
			   		List<Map<String, String>> list4 = new ArrayList <Map<String, String>>();
		    		try {
						list4=controladorArticulo.buscarLanzamientosCargados(bun.getInt("CODIGOCLIENTE"));
					} catch (Exception e) {
						android.util.Log.e("RestartControladorArticulo",e.getMessage());

					}
		    		if(list4==null){
		    			MenuItem m1=menu2.findItem(R.id.menu_sugeridos);
		       		    m1.setIcon(R.drawable.rojo);
		    		}else{
		    			MenuItem m1=menu2.findItem(R.id.menu_sugeridos);
		       		    m1.setIcon(R.drawable.amarillo);
		    		}
			   	}
			}catch(Exception ex){
				android.util.Log.e("RestartMenuarticulos",ex.getMessage());

				Toast toast =Toast.makeText(this,"Bug en menu articulos", Toast.LENGTH_SHORT);
				toast.show();
			}
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
				
	    try{
			if ((requestCode == request_code) && (resultCode == RESULT_OK)){
	            articulo = controladorArticulo.buscarPorCodigo(data.getDataString());
	        	rubro=articulo.getRubro();
	        	subrubro=articulo.getSubRubro();
	        	detalle=txt_busqueda.getText().toString().trim();
	        	if (controladorPedido.buscarSiYaFueCargadoElArticulo(numeroPedido,articulo.getCodigo())){
					funcion.MostrarMensajeAceptar(RealizarPedidoActivity.this, "Control de Items", "El Artículo que desea ingresar, ya fue ingresado. Edite el pedido para cambair la cantidad");		
					articulo=null;
	        	}else{
				if (articulo!=null){
					txt_detalle.setText(articulo.getDetalle());
					if (articulo.getOferta()>0){
						txt_precio.setText(funcion.formatDecimal(articulo.getOferta(), 2));
						txt_precio.setTextColor(Color.YELLOW);
					} else {
						txt_precio.setText(controladorArticulo.precioPorLista(bun.getInt("CODIGOCLIENTE"), articulo));
						txt_precio.setTextColor(Color.BLACK);
					}
					txt_stock.setText(String.valueOf(articulo.getStock()));
					if (articulo.getStock()<=0){
						txt_stock.setTextColor(Color.parseColor("#FF0000"));//red
					} else {
						txt_stock.setTextColor(Color.parseColor("#008000"));//green
					}
					txt_multiplo.setText(String.valueOf(articulo.getMultiplo()));
					txt_multiplo.setTextColor(Color.BLACK);
					txt_cantidad.requestFocus();	
				}
			  }
			}
    	}catch(Exception ex){
    		
    		Toast toast =Toast.makeText(this,"Bug en la actualizacion", Toast.LENGTH_SHORT);
			toast.show();
    	}
	    
	    try{
			if ((requestCode == REQUEST_ANALYTIC) && (resultCode == RESULT_OK)){
					accion=data.getStringExtra("ACCION");
					mejora=data.getStringExtra("MEJORA");
					recomendacion=data.getStringExtra("RECOMENDACION");
			}
			if ((requestCode == REQUEST_ANALYTIC) && (resultCode == RESULT_CANCELED)){
				accion=null;
				mejora=null;
				recomendacion=null;
				
			}
			
    	}catch(Exception ex){
    		Toast toast =Toast.makeText(this,"Bug trayendo el analytic", Toast.LENGTH_SHORT);
			toast.show();
    	}
	    
	}
    	
		@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		try{	
	        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	        	if(Double.parseDouble(total)>0){
	        		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
	        		alertbox.setTitle("Advertencia");
	        		alertbox.setMessage("¿Desea salir sin finalizar el pedido?");
	        		alertbox.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface arg0, int arg1) {
	        				onBackPressed();
	        			}
	        		});
	        		alertbox.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface arg0, int arg1) {
	        			
	        			}
	        		});
	        		alertbox.show();
	        	
	        	
	        	}else{
	        		onBackPressed();
	        	}
	            return true;
	        }
		}catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug no puede validar el regreso", Toast.LENGTH_SHORT);
			toast.show();
		}
        return super.onKeyDown(keyCode, event);
    }
    
	@Override
	public void onBackPressed() {
    	super.onBackPressed();
  
	}
    
	protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
        	try {
            	mDay=Integer.parseInt(txt_fecha.getText().toString().substring(0, 2));
            	mMonth=Integer.parseInt(txt_fecha.getText().toString().substring(3,5))-1;
            	mYear=Integer.parseInt(txt_fecha.getText().toString().substring(6, 10));
                return new DatePickerDialog(RealizarPedidoActivity.this, mDateSetListener,mYear, mMonth, mDay);				
			} catch (Exception e) {
				Toast toast =Toast.makeText(this,"Bug dialogo fecha", Toast.LENGTH_SHORT);
				toast.show();
			}
        }
        return null;
    }
   
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear+1;
            mDay = dayOfMonth;
            txt_fecha.setText((mDay<10?"0": "")+mDay+"-"+(mMonth<10? "0": "")+mMonth+"-"+(mYear<10? "0": "")+mYear);
        }	
    };
	
    private void buscarObjetivoPorVisita(){
		try{
			objetivo=controladorCliente.buscarObjetivoPorVisita(codigoCliente);
			if(objetivo!=null){
				double objetivoActual=objetivo.getObjetivo_actual();
				double cantidadVisitas=objetivo.getCantidad_visitas();
				if (objetivoActual>0 && cantidadVisitas>0){
					double objetivoXVisita = (objetivoActual/cantidadVisitas)* 1.21;
					lbl_objetivo.setText("OBJETIVO: "+funcion.formatDecimal(objetivoXVisita, 1));
				}
			}else{
				lbl_objetivo.setText("OBJETIVO: 0.00");
			}
		}catch(Exception ex){
			android.util.Log.e("buscarObjetivoPorVisita",ex.getMessage());
			Toast toast =Toast.makeText(this,"Bug al buscar objetivos", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
    
    private void validarClientesConDeuda(){
		if(configuracion.getControl_deuda()==1){
			
			try {
				int cantidad=controladorComprobante.buscarComprobantesPorPeriodo(codigoCliente,configuracion.getDia_contol());
				if(cantidad>0){
				   int cantidad_cobranzas=controladorCobranza.buscarComprobantesPagados(codigoCliente,configuracion.getDia_contol());
				   if(cantidad_cobranzas>0){
					   cliente_con_deuda=false;
				   }else{
					    ultima_factura=controladorComprobante.buscarComprobantesMasAntiguo(codigoCliente, configuracion.getDia_contol());
					   cliente_con_deuda=true;
				   } 
				}else{
					cliente_con_deuda=false;
				}
			} catch (Exception e) {
				android.util.Log.e("validarClientesConDeuda", e.getMessage());
			}
		}else{
			cliente_con_deuda=false;
		}
		
	}
    
    private void validarDatos(){
    	if (txt_cantidad.getText().toString().trim().equals("")){
			txt_cantidad.setText("0");
		}
		if (txt_item_acumulado.getText().toString().trim().equals("")){
			txt_item_acumulado.setText("0");
		}
		if (txt_stock.getText().toString().trim().equals("")){
			txt_stock.setText("0");
		}
		if (txt_multiplo.getText().toString().trim().equals("")){
			txt_multiplo.setText("0");
		}
		if (txt_descuento.getText().toString().trim().equals("")){
			txt_descuento.setText("0");
		}
		if (Double.parseDouble(txt_descuento.getText().toString().trim())>99){
			txt_descuento.setText("99");
		}
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
			Toast toast =Toast.makeText(this,"Bug en la localizacion", Toast.LENGTH_SHORT);
			toast.show();
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
    
    private boolean haveAnalytic() {
		
    	if(accion!=null ){
    		if(accion.length()>0)
    			return true;
    	}else if(recomendacion!=null ){
    		if(recomendacion.length()>0)
    			return true;
    	}else if(mejora!=null ){
    		if(mejora.length()>0)
    			return true;
    	}    	
			return false;
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.realizar_pedido, menu);
		return true;
	}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	try{
    	controladorArticulo = new ControladorArticulo(RealizarPedidoActivity.this);
    	menu2=menu;
    	List<Map<String, String>> list1 = new ArrayList <Map<String, String>>();
    	List<Map<String, String>> list2 = new ArrayList <Map<String, String>>();
    	List<Map<String, String>> list3 = new ArrayList <Map<String, String>>();
    	list1=controladorArticulo.buscarLanzamientos(bun.getInt("CODIGOCLIENTE"));
    	list2=controladorArticulo.buscarIndispensables(bun.getInt("CODIGOCLIENTE"));
    	list3=controladorArticulo.buscarSugeridos();
    	if(list1==null){
    		 	MenuItem m1=menu.findItem(R.id.menu_lanzamientos);
    		    m1.setIcon(R.drawable.verde);
    	}else{
    		List<Map<String, String>> list4 = new ArrayList <Map<String, String>>();
    		list4=controladorArticulo.buscarLanzamientosCargados(bun.getInt("CODIGOCLIENTE"));
    		if(list4==null){
    			MenuItem m1=menu.findItem(R.id.menu_lanzamientos);
       		 	m1.setIcon(R.drawable.rojo);
    		}else{
    			MenuItem m1=menu.findItem(R.id.menu_lanzamientos);
    			m1.setIcon(R.drawable.amarillo);
    		}
    		 
    	}
    	if(list2==null){
    			MenuItem m2=menu.findItem(R.id.menu_imprescindibles);
    		    m2.setIcon(R.drawable.verde);
    	}else{
 			List<Map<String, String>> list4 = new ArrayList <Map<String, String>>();
    		list4=controladorArticulo.buscarInidispensableCargado(bun.getInt("CODIGOCLIENTE"));
    		if(list4==null){
    			MenuItem m1=menu.findItem(R.id.menu_imprescindibles);
    			m1.setIcon(R.drawable.rojo);
    		}else{
    			MenuItem m1=menu.findItem(R.id.menu_imprescindibles);
    			m1.setIcon(R.drawable.amarillo);
    		}
    	}
    	if(list3==null){
    		 MenuItem m3=menu.findItem(R.id.menu_sugeridos);
    		 m3.setIcon(R.drawable.verde);
	   	}else{
	   		List<Map<String, String>> list4 = new ArrayList <Map<String, String>>();
    		list4=controladorArticulo.buscarLanzamientosCargados(bun.getInt("CODIGOCLIENTE"));
    		if(list4==null){
    			MenuItem m1=menu.findItem(R.id.menu_sugeridos);
       		    m1.setIcon(R.drawable.rojo);
    		}else{
    			MenuItem m1=menu.findItem(R.id.menu_sugeridos);
       		    m1.setIcon(R.drawable.amarillo);
    		}
	   	}
    	}catch(Exception ex){
    		Toast toast =Toast.makeText(this,"Bug al preparar los menu", Toast.LENGTH_SHORT);
			toast.show();
    	}
    	return super.onPrepareOptionsMenu(menu);
    }
	
    @Override	
	public boolean onOptionsItemSelected(MenuItem item) {
		try{
	    	int id = item.getItemId();
			Intent intent;
			Log log =new Log();
			switch (id) {	
				case R.id.menu_lanzamientos:					
					log.setDescripcion("MENU LANZAMIENTOS");
					log.setTipo(17);
					log.setFecha(new Date().getTime());
					log.setEstado(0);
					log.setVendedor(bun.getInt("VENDEDOR"));
					controladorInforme.guardarLog(log);	
					intent = new Intent(RealizarPedidoActivity.this,LanzamientoActivity.class);
					intent.putExtras(bun);
					startActivityForResult(intent, request_code);
					break;
				case R.id.menu_imprescindibles:
					log.setDescripcion("MENU IMPRESCINDIBLES");
					log.setTipo(18);
					log.setFecha(new Date().getTime());
					log.setEstado(0);
					log.setVendedor(bun.getInt("VENDEDOR"));
					controladorInforme.guardarLog(log);	
					intent = new Intent(RealizarPedidoActivity.this,ImpresindibleActivity.class);
					intent.putExtras(bun);
					startActivityForResult(intent, request_code);
					break;
				case R.id.menu_sugeridos:
					log.setDescripcion("MENU SUGERIDOS");
					log.setTipo(19);
					log.setFecha(new Date().getTime());
					log.setEstado(0);
					log.setVendedor(bun.getInt("VENDEDOR"));
					controladorInforme.guardarLog(log);	
					intent = new Intent(RealizarPedidoActivity.this,SugeridoActivity.class);
					intent.putExtras(bun);
					startActivityForResult(intent, request_code);
					break;
				case R.id.menu_analytic:
					intent = new Intent(RealizarPedidoActivity.this, AnalyticActivity.class);
					bun.putString("ACCION", accion);
					bun.putString("MEJORA", mejora);
					bun.putString("RECOMENDACION", recomendacion);
					intent.putExtras(bun);
					startActivityForResult(intent, REQUEST_ANALYTIC);
					break;
				case R.id.cerrar_sesion:
					startActivity(new Intent(getBaseContext(), MainActivity.class)
		            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
					finish();
					break;
				case R.id.sincronizar:
					Intent i = new Intent(getApplicationContext(), SincronizadorActivity.class);
					bun.putString("SINCRONIZACION","GENERAL");
					i.putExtras(bun);
					startActivity(i);
					break;
				default:
					break;
			}
		}catch(Exception ex){
			
		}
		return super.onOptionsItemSelected(item);
	}
  
    @Override
	protected void onPause() {
    	super.onPause();
		if(controladorPedido.estaAbierto() || controladorCobranza.estaAbierto()|| controladorComprobante.estaAbierto() || controladorArticulo.estaAbierto() || controladorCliente.estaAbierto() || controladorInforme.estaAbierto() || controladorConfiguracion.estaAbierto())
		{
		android.util.Log.e("RealizarPedidoActivity", "onPause");	
		}
		
	}
}
