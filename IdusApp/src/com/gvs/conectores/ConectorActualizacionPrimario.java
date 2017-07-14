package com.gvs.conectores;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.widget.Toast;

import com.gvs.modelos.Configuracion;
import com.gvs.modelos.ListaPrecio;
import com.gvs.modelos.Vendedor;
import com.gvs.controladores.ControladorActualizacion;
import com.gvs.utilidades.Funciones;

public class ConectorActualizacionPrimario extends ConectorGeneral {

	private int empresa,vendedor;
	private Context context;
	private String tipo;
	private ControladorActualizacion controladorActualizacion;
	private Funciones funcion ;
	private boolean verificaComprobantes = false;
	private ContentValues cont;
	private String[] lista;
	private ListaPrecio listaPreciosDatos = new ListaPrecio();
//	private ArrayList<Vendedor> listaVendedores = new ArrayList<Vendedor>();
	//	private Vendedor vendedorTemp;
	private ArrayList<ListaPrecio> listaPrecios = new ArrayList<ListaPrecio>();
	/*private ArrayList<String[]> listaClientes = new ArrayList<String[]>();
	private ArrayList<String[]> listaVisitas= new ArrayList<String[]>();
	private ArrayList<String[]> listaRubros= new ArrayList<String[]>();
	private ArrayList<String[]> listaSubRubros= new ArrayList<String[]>();
	private ArrayList<String[]> listaArticulos= new ArrayList<String[]>();
	private ArrayList<String[]> listaMotivos= new ArrayList<String[]>();
	private ArrayList<String[]> listaSugeridos= new ArrayList<String[]>();
	private ArrayList<String[]> listaConfiguracion= new ArrayList<String[]>();
	private ArrayList<String[]> listaPedidos= new ArrayList<String[]>();
	private ArrayList<String[]> listaVentas= new ArrayList<String[]>();
	private ArrayList<String[]> listaEncuestas= new ArrayList<String[]>();
	private ArrayList<String[]> listaItemEncuestas= new ArrayList<String[]>();
	private ArrayList<String[]> listaComprobantes= new ArrayList<String[]>();
	private ArrayList<String[]> listaCombos= new ArrayList<String[]>();*/
	
	public ConectorActualizacionPrimario(Configuracion conf, int empresa, int vendedor,String tipo,Context context) {
		super(conf);
		this.empresa = empresa;
		this.vendedor = vendedor;
		this.context = context;
		this.tipo=tipo;
	}
	
	@Override
	protected String CompletarURL() {
		return "buscarActualizaciones6?empresa=" + empresa + "&vendedor="+ vendedor;
	}

	@Override
	protected void procesarRespuesta(DataInputStream in)throws Exception {
		int tipoActualizacion = 0;
		String[] item;
		boolean bucle = true;
		controladorActualizacion=new ControladorActualizacion(context);
		funcion = new Funciones(context);
		cont=new ContentValues();
		ArrayList<String> lista_actualizaciones = new ArrayList<String>();
		
		try {
			//controladorActualizacion.iniciarTransaccion();
			while (bucle) {
				try {
					lista_actualizaciones.add(in.readUTF());
				} catch (Exception e) {
					bucle = false;
				}
			}
			in.close();
			Log.e("Fin de recepcion","### Fin de recepcion ###");
			if(tipo.equals("ACTUALIZACIONGENERAL")){
				controladorActualizacion.eliminarCliente();
				controladorActualizacion.eliminarArticulo();
				controladorActualizacion.eliminarVisitas();
				controladorActualizacion.eliminarArticuloImpresindible();
				controladorActualizacion.eliminarArticuloLanzamiento();
				controladorActualizacion.eliminarObjetivos();
				controladorActualizacion.eliminarVentas();
				controladorActualizacion.eliminarEncuestas();
				controladorActualizacion.eliminarComprobantes();
			}
			int contador=0;
			int contadorError=0;
			for (String item_actualizacion : lista_actualizaciones) {
				contador++;
				try {
					item= item_actualizacion.split("&&");
					try{
					tipoActualizacion = Integer.parseInt(item[0]);}
					catch(Exception e){
						Log.e("tipoActualizacion",e.getMessage());
					}
					lista = item[1].split(";");	
					switch (tipoActualizacion) {
					case 1001:
	//					listaVendedores.add(lista);
	//					Log.e("VENDEDOR","VENDEDOR "+contador);
						guardarVendedor(lista);
						break;
					case 1002:
						try {
						//	guardarListaPrecio(lista);
							listaPreciosDatos = new ListaPrecio();
							listaPreciosDatos.setCodigo(Integer.parseInt(lista[0]));
							listaPreciosDatos.setCodigo_rubro(Integer.parseInt(lista[1]));
							listaPreciosDatos.setCodigo_subrubro(Integer.parseInt(lista[2]));
							listaPreciosDatos.setCodigo_articulo(lista[3]);
							listaPreciosDatos.setPorcentaje(Double.parseDouble(lista[4]));
							listaPreciosDatos.setHab(Boolean.parseBoolean(lista[5]));
							listaPrecios.add(listaPreciosDatos);
						} catch (Exception e) {
							
							Log.e("PRUEBALISTAS", e.getMessage());
						}
					
						
						break;
					case 1003:
//						listaClientes.add(lista);
//						Log.e("CLIENTE","CLIENTE "+contador);
						guardarCliente(lista);
						break;
					case 1004:	
//						listaVisitas.add(lista);
//						Log.e("VISITA","VISITA "+contador);
						guardarVisita(lista);
						break;
					case 1005:
//						listaRubros.add(lista);
//						Log.e("RUBRO","RUBRO "+contador);
						guardarRubro(lista);
						break;
					case 1006:
//						listaSubRubros.add(lista);
//						Log.e("SUBRUBRO","SUBRUBRO "+contador);
						guardarSubrubro(lista);
						break;
					case 1007:
//						listaArticulos.add(lista);
//						Log.e("ARTICULO","ARTICULO "+contador);
						guardarArticulo(lista);
						break;
					case 1008:
//						listaMotivos.add(lista);
//						Log.e("MOTIVO","MOTIVO "+contador);
						guardarMotivo(lista);
						break;
					case 1009:
//						listaSugeridos.add(lista);
//						Log.e("SUGERIDO","SUGERIDO "+contador);
						guardarSugerido(lista);
						break;
					case 1010:
//						listaConfiguracion.add(lista);
//						Log.e("CONFIGURACION","CONFIGURACION "+contador);
						guardarConfiguracion(lista);
						break;
					case 1011:
//						listaPedidos.add(lista);
//						Log.e("PEDIDO","PEDIDO "+contador);
						guardarPedidos(lista);
						break;
					case 1015:
//						listaVentas.add(lista);
//						Log.e("VENTAS","VENTAS "+contador);
						guardarVentas(lista);
						break;
					case 1016:
//						listaEncuestas.add(lista);
//						Log.e("ENCUESTAS","ENCUESTAS "+contador);
						guardarEncuestas(lista);
						break;
					case 1017:
//						listaItemEncuestas.add(lista);
//						Log.e("ITEMENCUESTA","ITEMENCUESTA "+contador);
						guardarItemEncuesta(lista);		
						break;
					case 1018:
//						listaComprobantes.add(lista);
//						Log.e("COMPROBANTE","COMPROBANTE "+contador);
						guardarComprobantes(lista);
						break;
					case 1019:
						eliminarComprobantes();
						break;
					case 1020:
//						listaCombos.add(lista);
//						Log.e("COMBO","COMBO "+contador);
						guardarCombo(lista);
						break;
					case 1099:
//						Log.e("FIN SINCRONIZACION","---");
						if(contadorError==0){
							guardarSincronizacion();	
						}
						break;
					default:
						break;
					}
					
					
				} catch (Exception ex) {
					Log.e("excepcion", ex.getMessage());
					contadorError++;
					Log.e("ERROR","------------------------------------- "+contadorError);
					continue;
				}
			}
			
			try {
					controladorActualizacion.guardarListasDePrecios(listaPrecios);
				} catch (Exception e) {
					Log.e("controladorActualizacion",e.getMessage());
				}finally {
					listaPrecios.clear();
				}
			
			
			
		} catch (Exception e) {
			Log.e("excepcion", e.getMessage());
			throw new Exception(e.getMessage());
			

		} finally {
		   in.close();
		}
	}

synchronized private void guardarVendedor(String[] lista){
		try {
				
			cont.put("CODIGOEMPRESA", empresa);
			cont.put("CODIGO", Integer.parseInt(lista[0]));
			cont.put("NOMBRE", lista[1]);
			cont.put("PASS",lista[2]);
			cont.put("COBREGULAR", Integer.parseInt(lista[3]));
			cont.put("COBBUENA", Integer.parseInt(lista[4]));
			cont.put("COBMUYBUENA", Integer.parseInt(lista[5]));
		} catch (NumberFormatException e) {
			Log.e("guardaVendedor",e.getMessage());
		}
		controladorActualizacion.guardarVendedor(cont);
		cont.clear();	
	}
	
synchronized private void guardarListaPrecio(String[] lista){
		try {
			
			cont.put("CODIGO",Integer.parseInt(lista[0]));
			cont.put("CODIGORUBRO", Integer.parseInt(lista[1]));
			cont.put("CODIGOSUBRUBRO",Integer.parseInt(lista[2]));
			cont.put("CODIGOARTICULO",lista[3]);
			cont.put("PORCENTAJE",Double.parseDouble(lista[4]));
			cont.put("HAB", funcion.devolverBoolean(Boolean.parseBoolean(lista[5])));
		} catch (NumberFormatException e) {
			Log.e("guardaListaPrecio",e.getMessage());
		}	
		controladorActualizacion.guardarListaPrecio(cont);
		cont.clear();
	}


	
synchronized private void guardarCliente(String[] lista){
		try {
			
			cont.put("CODIGO", Integer.parseInt(lista[0]));
			if(Integer.parseInt(lista[0])==1610){Log.e("RegCliente","Codigo" + lista[0] + "Nombre: " +lista[1] + "  listaprecio : " + lista[7] + " gps:  " + lista[14]);}
			cont.put("NOMBRE", lista[1]);
			cont.put("DOMICILIO", lista[2]);
			cont.put("TELEFONO",lista[3]);
			cont.put("PROVINCIA", lista[4]);
			cont.put("LOCALIDAD", lista[5]);
			cont.put("OBSERVACIONES", lista[6]);
			cont.put("CODIGOLISTAPRECIO",Integer.parseInt(lista[7]));
			cont.put("SALDO", Double.parseDouble(lista[8]));
			cont.put("CODIGOVENDEDOR", this.vendedor);
			cont.put("HAB",funcion.devolverBoolean(Boolean.parseBoolean(lista[9])));
			cont.put("CANAL", lista[10]);					
			cont.put("RESPONSABILIDAD", lista[11]);
			cont.put("LATITUD", Double.parseDouble(lista[12]));
			cont.put("LONGITUD",Double.parseDouble(lista[13]));
			cont.put("HABILITARGPS", Integer.parseInt(lista[14]));
			cont.put("EDITADO",0);
		} catch (NumberFormatException e) {
			Log.e("guardarCliente",e.getMessage() + "codigo: " + Integer.parseInt(lista[0]) + " nombre: " +lista[1] + " codigolistaprecio: " + lista[7]);;
		}
		controladorActualizacion.guardarCliente(cont);
		cont.clear();
	}

synchronized private void guardarVisita(String[] lista){
		try {
			
			cont.put("DIAS",lista[0]);
			cont.put("HORA",lista[1]);
			cont.put("ORDEN",Integer.parseInt(lista[2]));
			cont.put("CODIGOCLIENTE",Integer.parseInt(lista[3]));
		} catch (NumberFormatException e) {
			Log.e("guardarVisita",e.getMessage());
		}
		controladorActualizacion.guardarVisita(cont);
		cont.clear();
	}
	
synchronized private void guardarRubro(String[] lista){
		try {
			
			cont.put("CODIGO",Integer.parseInt(lista[0]));
			cont.put("NOMBRE",lista[1]);
			cont.put("HAB", funcion.devolverBoolean(Boolean.parseBoolean(lista[2])));
		} catch (NumberFormatException e) {
			Log.e("guardarRubro",e.getMessage());
		}
		controladorActualizacion.guardarRubro(cont);
		cont.clear();
	}
	
synchronized private void guardarSubrubro(String[] lista ){
		try {
	
			cont.put("CODIGORUBRO", Integer.parseInt(lista[0]));
			cont.put("CODIGO",Integer.parseInt(lista[1]));
			cont.put("NOMBRE", lista[2]);
			cont.put("CONTROLASTOCK", funcion.devolverBoolean(Boolean.parseBoolean(lista[3])));
			cont.put("HAB",funcion.devolverBoolean(Boolean.parseBoolean(lista[4])));
		} catch (NumberFormatException e) {
			Log.e("guardaSubRubro",e.getMessage());
		}
		controladorActualizacion.guardarSubrubro(cont);
		cont.clear();
	}
	
synchronized private void guardarArticulo(String[] lista){
		try {
	
			cont.put("CODIGO", lista[0]);
			cont.put("CODIGORUBRO", Integer.parseInt(lista[1]));
			cont.put("CODIGOSUBRUBRO",Integer.parseInt(lista[2]));
			cont.put("NOMBRE",lista[3]);
			cont.put("PRECIO",Double.parseDouble(lista[4]));
			cont.put("EXISTENCIA",Double.parseDouble(lista[5]));
			cont.put("HAB", funcion.devolverBoolean(Boolean.parseBoolean(lista[6])));
			cont.put("MULTIPLO",Integer.parseInt(lista[7]));
			cont.put("PRECIOOFERTA",Double.parseDouble(lista[8]));
		} catch (NumberFormatException e) {
			Log.e("guardarArticulo",e.getMessage());
		}
		controladorActualizacion.guardarArticulo(cont);
		cont.clear();
	}
	
synchronized private void guardarMotivo(String[] lista){
		try {
			
			cont.put("CODIGO",Integer.parseInt(lista[0]));
			cont.put("NOMBRE",lista[1]);
		} catch (NumberFormatException e) {
			Log.e("guardarMotivo",e.getMessage());
		}
		controladorActualizacion.guardarMotivo(cont);
		cont.clear();
	}
	
synchronized private void guardarSugerido(String[] lista){
		try {
			
			cont.put("ORDEN",Integer.parseInt(lista[0]));
			cont.put("CODIGOARTICULO", lista[1]);
			cont.put("MENSAJE", lista[2]);
			cont.put("VENCIMIENTO", Long.parseLong(lista[3]));
			cont.put("EXIGIBLE", funcion.devolverBoolean(Boolean.parseBoolean(lista[4])));
		} catch (NumberFormatException e) {
			Log.e("guardaSugerido",e.getMessage());
		}
		controladorActualizacion.guardarSugerido(cont);
		cont.clear();
	}
	
synchronized private void guardarConfiguracion(String[] lista){
		int cantidadItems = Integer.parseInt(lista[0]);
		if (cantidadItems == 0) {cantidadItems = 25;}
		try {
			cont = new ContentValues();
			cont.put("PUERTO", 8080);
			cont.put("CANTIDADITEMS", cantidadItems);
			cont.put("DIACOMPLETO", Integer.parseInt(lista[1]));
			cont.put("MUESTRASUGERIDOS", Integer.parseInt(lista[2]));
			cont.put("OTRODIA", Integer.parseInt(lista[3]));
			cont.put("DESCUENTO", Integer.parseInt(lista[4]));
			cont.put("SERVICIOGEO", Integer.parseInt(lista[5]));
		} catch (NumberFormatException e) {
			Log.e("guardarConfiguracion",e.getMessage());
		}
		controladorActualizacion.modificarConfiguracion(cont);
		cont.clear();
	}
	
synchronized private void guardarPedidos(String[] lista){
		
		try {
			cont.put("NUMERO", lista[0]);
			cont.put("NUMEROFINAL", "0");
			cont.put("FECHA", "0");
			cont.put("AVANCE",0);
			cont.put("CODIGOCLIENTE", -1);
			cont.put("CODIGOVENDEDOR", 0);
			cont.put("CANTIDADITEMS", "0");
			cont.put("ESTADO", 0);
			cont.put("FECHAENVIADO",0);
			cont.put("LATITUD", "0");
			cont.put("LONGITUD","0");
			cont.put("PRECISION", "0");
			cont.put("PROVEE","");
			cont.put("INTERNET",funcion.estadoRedes());
		} catch (Exception e) {
			Log.e("guardarPedidos",e.getMessage());
		}
		controladorActualizacion.guardarPedido(cont);
		cont.clear();
	}
	
synchronized private void guardarVentas(String[] lista){
		
		try {
			cont.put("PERIODO", lista[0]);
			cont.put("DIA", Integer.parseInt(lista[1]));
			cont.put("IMPORTE", Double.parseDouble(lista[2]));
			cont.put("ACTUALIZACION",Long.parseLong(lista[3]));
		} catch (NumberFormatException e) {
			Log.e("guardarVentas",e.getMessage());
		}
		controladorActualizacion.guardarVentas(cont);
		cont.clear();
	}
	
synchronized private void guardarEncuestas(String[] lista){
		
		try {
			cont.put("NUMERO", Integer.parseInt(lista[0]));
			cont.put("NOMBRE",lista[1]);
			cont.put("FECHAINI",lista[2]);
			cont.put("FECHAFIN",lista[3]);
			cont.put("EXIGIBLE",funcion.devolverBoolean(Boolean.parseBoolean(lista[4])));
		} catch (NumberFormatException e) {
			Log.e("guardarEncuesta",e.getMessage());
		}
		controladorActualizacion.guardarEncuesta(cont);
		cont.clear();
	}

synchronized private void guardarItemEncuesta(String[] lista){
		
		try {
			cont.put("NUMERO", Integer.parseInt(lista[0]));
			cont.put("ITEM",Integer.parseInt(lista[1]));
			cont.put("PREGUNTA",lista[2]);
			cont.put("AYUDA",lista[3]);
		} catch (NumberFormatException e) {
			Log.e("guardarItemEncuesta",e.getMessage());
		}
		controladorActualizacion.guardarItemEncuesta(cont);	
		cont.clear();
	}
	
synchronized private void eliminarComprobantes(){
		if (verificaComprobantes == false) {
			controladorActualizacion.eliminarComprobantes();
			verificaComprobantes = true;
		}
	}
	
synchronized private void guardarComprobantes(String[] lista){
		
		try {
			cont.put("TIPO",lista[0]);
			cont.put("CLASE",lista[1]);
			cont.put("SUCURSAL", Integer.parseInt(lista[2]));
			cont.put("NUMERO", Integer.parseInt(lista[3]));
			cont.put("FECHA",lista[4]);
			cont.put("CODIGOCLIENTE",Integer.parseInt(lista[5]));
			cont.put("SALDO", lista[6]);
		} catch (NumberFormatException e) {
			Log.e("guardarComprobante",e.getMessage());
		}
		controladorActualizacion.guardarComprobante(cont);
		cont.clear();
	}
	
synchronized private void guardarCombo(String[] lista){	
		
		try {
			cont.put("CODIGO",Integer.parseInt(lista[0]));
			cont.put("CANTIDAD", Integer.parseInt(lista[1]));
		} catch (NumberFormatException e) {
			Log.e("guardarCombo",e.getMessage());
		}
		controladorActualizacion.guardarCombo(cont);
		cont.clear();
	}
	
synchronized private void guardarSincronizacion(){
		Date fechaInicio = new Date();
		
		try {
			cont.put("CODIGOEMPRESA", empresa);
			cont.put("CODIGOVENDEDOR",this.vendedor);
			cont.put("FECHAINICIO", fechaInicio.getTime());
			cont.put("FECHA", fechaInicio.getTime());
			cont.put("MODO", "T");
			cont.put("INTERNET",funcion.estadoRedes());
			cont.put("ESTADO", 0);
		} catch (Exception e) {
			Log.e("guardarSincronizacion",e.getMessage());
		}
		controladorActualizacion.guardarSincronizacionTotal(cont);
		cont.clear();
	}
}
