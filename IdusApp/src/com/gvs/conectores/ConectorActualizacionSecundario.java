package com.gvs.conectores;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Date;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import com.gvs.controladores.ControladorActualizacion;
import com.gvs.modelos.Configuracion;

public class ConectorActualizacionSecundario extends ConectorGeneral {

	private int empresa,vendedor;
	private Context context;
	private String tipo;
	private ControladorActualizacion controladorActualizacion;
	private ContentValues cont;
	private String[] lista;
	
	public ConectorActualizacionSecundario(Configuracion conf, int empresa, int vendedor,String tipo,Context context) {
		super(conf);
		this.empresa = empresa;
		this.vendedor = vendedor;
		this.context = context;
		this.tipo=tipo;
	}

	@Override
	protected String CompletarURL() {
		return "buscarActualizaciones7?empresa=" + empresa + "&vendedor="+ vendedor;
	}

	@Override
	protected void procesarRespuesta(DataInputStream in)throws Exception {
		int tipoActualizacion = 0;
		String[] item;
		boolean bucle = true;
		controladorActualizacion=new ControladorActualizacion(context);
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
					tipoActualizacion = Integer.parseInt(item[0]);
					lista = item[1].split(";");	
					switch (tipoActualizacion) {
					case 1012:
						Log.e("IMPRESINDIBLE","IMPRESINDIBLE "+contador);
						guardarImprescindibles(lista);
						break;
					case 1013:
						Log.e("LANZAMIENTO","LANZAMIENTO "+contador);
						guardarLanzamientos(lista);
						break;
					case 1014:
						Log.e("OBJETIVO","OBJETIVO "+contador);
						guardarObjetivo(lista);
						break;
					case 1099:
						Log.e("FIN SINCRONIZACION","---");
						break;
					default:
						break;
					}				
				} catch (Exception ex) {
					contadorError++;
					Log.e("ERROR","------------------------------------- "+contadorError);
					continue;
				}
			}
			//controladorActualizacion.transaccionCorrectamente();
		} catch (Exception e) {
		   throw new Exception(e.getMessage());
		} finally {
			//controladorActualizacion.terminarTransaccion();
		   in.close();
		}
	}
	
	synchronized private void guardarImprescindibles(String[] lista){
		if (Double.parseDouble(lista[3]) < Double.parseDouble(lista[2])) {
			cont.clear();
			cont.put("CODIGOCLIENTE",Integer.parseInt(lista[0]));
			cont.put("CODIGOARTICULO", lista[1]);
			cont.put("CANTIDADOBJ",Double.parseDouble(lista[2]));
			cont.put("CANTIDADVTA",Double.parseDouble(lista[3]));
			cont.put("ACTUALIZACION", new Date().getTime());
			controladorActualizacion.guardarImpresindible(cont);
		}	
	}
	
	synchronized private void guardarLanzamientos(String[] lista){
		if (Double.parseDouble(lista[3]) < Double.parseDouble(lista[2])) {
			cont.clear();
			cont.put("CODIGOCLIENTE",Integer.parseInt(lista[0]));
			cont.put("CODIGOARTICULO",lista[1]);
			cont.put("CANTIDADOBJ",Double.parseDouble(lista[2]));
			cont.put("CANTIDADVTA",Double.parseDouble(lista[3]));
			cont.put("ACTUALIZACION",new Date().getTime());	
			controladorActualizacion.guardarLanzamiento(cont);
		}
	}
	
	synchronized private void guardarObjetivo(String[] lista){
		cont.clear();
		cont.put("CODIGOCLIENTE",Integer.parseInt(lista[0]));
		cont.put("PERIODO",lista[1]);
		cont.put("VTA_ANT",Double.parseDouble(lista[2]));
		cont.put("OBJ_ACT", Double.parseDouble(lista[3]));
		cont.put("CAN_VIS", Integer.parseInt(lista[4]));
		cont.put("VTA_ACT", Double.parseDouble(lista[5]));
		cont.put("FEC_ACT",Long.parseLong(lista[6]));
		cont.put("PORC", Double.parseDouble(lista[7]));
		controladorActualizacion.guardarObjetivoVisita(cont);
	}
	
	
}

