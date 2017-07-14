package com.gvs.controladores;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gvs.idusapp.R;
import com.gvs.modelos.Cliente;
import com.gvs.modelos.ObjetivoVisita;
import com.gvs.utilidades.BaseDeDatos;
import com.gvs.utilidades.Funciones;

public class ControladorCliente {
	
	private Context context;
	private Funciones funcion;
	private BaseDeDatos baseDeDatos;
	private SQLiteDatabase db;
	private Cursor cur;
	private String Sql="";
	private ControladorPedido controladorPedido;
	private ControladorNoAtendido controladorNoAtendido;
	
	public ControladorCliente(Context contexto){
		this.context=contexto;
		this.funcion = new Funciones();
	/*	controladorPedido = new ControladorPedido();
		controladorPedido.setContexto(context);*/
		controladorPedido = new ControladorPedido(this.context);
		controladorNoAtendido = new ControladorNoAtendido();
		controladorNoAtendido.setContexto(context);
	}
		
	public List<Cliente> buscarClientesActualizados() throws Exception{
		try{
			abrirBaseDeDatos();
			List<Cliente> lista = new ArrayList<Cliente>();
			Sql = "SELECT * FROM CLIENTES WHERE EDITADO=1";
			cur = db.rawQuery(Sql, null);
			Cliente client;
			if (cur.getColumnCount()>0){
				while (cur.moveToNext()){
					 client=new Cliente();
					 client.setCodigo(cur.getInt(cur.getColumnIndex("CODIGO")));
		     		 client.setLatitud(cur.getDouble(cur.getColumnIndex("LATITUD")));
		     		 client.setLongitud(cur.getDouble(cur.getColumnIndex("LONGITUD")));
		     		 lista.add(client);
				}
			}
			cur.close();
			cerrarBaseDeDatos();
			return lista;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public Cliente buscarCliente(int codigo){
		try{
			abrirBaseDeDatos();
			Sql = "SELECT * FROM CLIENTES WHERE CODIGO="+codigo;
			cur = db.rawQuery(Sql, null);
			Cliente cliente=null;
			if (cur.getColumnCount()>0){
				while (cur.moveToNext()){
					 cliente=new Cliente();
					 cliente.setCodigo(cur.getInt(cur.getColumnIndex("CODIGO")));
				     cliente.setNombre(cur.getString(cur.getColumnIndex("NOMBRE")));
				     cliente.setDireccion(cur.getString(cur.getColumnIndex("DOMICILIO")));
				     cliente.setCanal(cur.getString(cur.getColumnIndex("CANAL")));
				     cliente.setLocalidad(cur.getString(cur.getColumnIndex("LOCALIDAD")));
				     cliente.setObservaciones(cur.getString(cur.getColumnIndex("OBSERVACIONES")));
				     cliente.setProvincia(cur.getString(cur.getColumnIndex("PROVINCIA")));
				     cliente.setResponsabilidad(cur.getString(cur.getColumnIndex("RESPONSABILIDAD")));
				     cliente.setTelefono(cur.getString(cur.getColumnIndex("TELEFONO")));
				     cliente.setSaldo(cur.getDouble(cur.getColumnIndex("SALDO")));	
		     		 cliente.setLatitud(cur.getDouble(cur.getColumnIndex("LATITUD")));
		     		 cliente.setLongitud(cur.getDouble(cur.getColumnIndex("LONGITUD")));
				}
			}		
			cur.close();
			cerrarBaseDeDatos();
			
			return cliente;
		} catch (Exception e) {
			return null;
		}
	}

	public ObjetivoVisita buscarObjetivoPorVisita(int codigoCliente)throws Exception{
		try{
			abrirBaseDeDatos();
			Sql = "SELECT * FROM OBJXVISITA WHERE CODIGOCLIENTE=" + codigoCliente + " AND PERIODO='" + funcion.dateToString_MMYYYY(new java.util.Date().getTime()) + "'";
			cur = db.rawQuery(Sql, null);
			ObjetivoVisita objetivo=null;
			if (cur.getColumnCount()>0){
				while (cur.moveToNext()){
				objetivo=new ObjetivoVisita();
				objetivo.setCantidad_visitas(cur.getDouble(cur.getColumnIndex("CAN_VIS")));
				objetivo.setCodigo_cliente(codigoCliente);
				objetivo.setFecha_actualizacion(cur.getLong(cur.getColumnIndex("FEC_ACT")));
				objetivo.setObjetivo_actual(cur.getDouble(cur.getColumnIndex("OBJ_ACT")));
				objetivo.setPeriodo(cur.getString(cur.getColumnIndex("PERIODO")));
				objetivo.setPorcentajes(cur.getDouble(cur.getColumnIndex("PORC")));
				objetivo.setVenta_actual(cur.getDouble(cur.getColumnIndex("VTA_ACT")));
				objetivo.setVenta_anterior(cur.getDouble(cur.getColumnIndex("VTA_ANT")));
				}
			}
			cur.close();
			cerrarBaseDeDatos();
			return objetivo;
		}catch(Exception ex){
			throw new Exception(ex.getMessage());	
		}
	}
	
	public List<ObjetivoVisita> buscarObjetivosPorVisita(int dia,int vendedor)throws Exception{
		try {
			abrirBaseDeDatos();
			if(dia>0){
			 Sql = "SELECT C.CODIGO,C.NOMBRE,O.OBJ_ACT,O.CAN_VIS,O.PORC FROM CLIENTES AS C "
						+ "INNER JOIN VISITAS AS V ON C.CODIGO=V.CODIGOCLIENTE "
						+ "INNER JOIN OBJXVISITA AS O ON C.CODIGO=O.CODIGOCLIENTE "
						+ "WHERE C.CODIGOVENDEDOR="+ vendedor+ " AND SUBSTR(V.DIAS,"+dia+ ",1)='"+ dia+ "' "
						+ "AND C.CODIGOVENDEDOR=" + vendedor + " AND HAB=1";
			}else{
			 Sql = "SELECT C.CODIGO,C.NOMBRE,O.OBJ_ACT,O.CAN_VIS,O.PORC FROM CLIENTES AS C "
						+ "INNER JOIN VISITAS AS V ON C.CODIGO=V.CODIGOCLIENTE "
						+ "INNER JOIN OBJXVISITA AS O ON C.CODIGO=O.CODIGOCLIENTE "
						+ "WHERE C.CODIGOVENDEDOR="+ vendedor+ " AND V.DIAS IN('0000000','8888888','9999999') "
						+ "AND C.CODIGOVENDEDOR=" + vendedor + " AND HAB=1";
			}
			
			cur = db.rawQuery(Sql, null);
			List<ObjetivoVisita>lista=null;
			ObjetivoVisita objetivo;
			if (cur.getCount() > 0) {
				lista=new ArrayList<ObjetivoVisita>();
				while (cur.moveToNext()) {
					objetivo=new ObjetivoVisita();
					double objXVisita = 0;
					if (cur.getDouble(cur.getColumnIndex("CAN_VIS")) > 0) {
						objXVisita = cur.getDouble(cur.getColumnIndex("OBJ_ACT"))/ cur.getDouble(cur.getColumnIndex("CAN_VIS"));
					}
					objetivo.setCodigo_cliente(cur.getInt(cur.getColumnIndex("CODIGO")));
					objetivo.setNombre(cur.getString(cur.getColumnIndex("NOMBRE")));
					objetivo.setActual(" Objetivo actual=$"+ funcion.formatDecimal(cur.getDouble(cur.getColumnIndex("OBJ_ACT")),2));
					objetivo.setVisita(" Cantidad de visitas="+ cur.getDouble(cur.getColumnIndex("CAN_VIS")));
					objetivo.setObjetivo(" Objetivo por visita=$" + funcion.formatDecimal(objXVisita,2));
					objetivo.setPorcentaje(" Avance="+ cur.getDouble(cur.getColumnIndex("PORC"))+"%");
					lista.add(objetivo);
				}	
			}
			cur.close();
			cerrarBaseDeDatos();
			return lista;
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());	
		
		}
	}
	
	public List<Cliente> buscarClientes(int dia, int sOrden,int vendedor) throws Exception{	
		List<Cliente> lista = new ArrayList<Cliente>();
		String condicion = "";
		String orden = "";
		Cliente cliente;
		
		
		try {
			abrirBaseDeDatos();
			if (dia == 0) {
				condicion = " WHERE C.CODIGOVENDEDOR=" + vendedor + " AND V.DIAS IN('0000000','8888888','9999999') AND HAB=1";
			} else {
				condicion = " WHERE SUBSTR(V.DIAS," + dia + ",1)='" + dia+ "' AND C.CODIGOVENDEDOR=" + vendedor + " AND HAB=1";
			}
			switch (sOrden) {
			case 0:
				orden = " ORDER BY V.ORDEN";
				break;
			case 1:
				orden = " ORDER BY C.NOMBRE";
				break;
			case 2:
				orden = " ORDER BY C.LOCALIDAD,C.DOMICILIO";
				break;
			default:
				break;
			}
		
			Sql = "SELECT C.CODIGO,C.NOMBRE,C.DOMICILIO,C.LOCALIDAD,C.CODIGOLISTAPRECIO FROM CLIENTES AS C "
					+ "INNER JOIN VISITAS AS V ON C.CODIGO=V.CODIGOCLIENTE"+ condicion + orden;
			cur = db.rawQuery(Sql, null);
			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {
					cliente= new Cliente();
					cliente.setNombre(cur.getString(cur.getColumnIndex("NOMBRE")));
					cliente.setCodigo(cur.getInt(cur.getColumnIndex("CODIGO")));
					
					if (controladorPedido.devolverCantidadVendidosXCliente(dia,cur.getInt(cur.getColumnIndex("CODIGO"))) > 0) {
						cliente.setIcono(R.drawable.verde);
					} else {
						
						if (controladorNoAtendido.buscarClienteNoAtendido(cur.getInt(cur.getColumnIndex("CODIGO"))) > 0) {
							cliente.setIcono(R.drawable.rojo);
						} else {
							cliente.setIcono(R.drawable.amarillo);
						}
					}
					cliente.setDireccion(cur.getString(cur.getColumnIndex("DOMICILIO"))+ " ("+ cur.getString(cur.getColumnIndex("LOCALIDAD"))+ ")");
					lista.add(cliente);
				}
			}
			cur.close();
			cerrarBaseDeDatos();
			return lista;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} 
		
	}
	
	public int buscarClienteSinMotivosNoVenta(int dia,int vendedor)throws Exception{
		try {
			abrirBaseDeDatos();
			
			String condicion = "";
			if (dia == 0) {
				condicion = " WHERE C.CODIGOVENDEDOR=" + vendedor + " AND V.DIAS IN('0000000','8888888','9999999') AND HAB=1";
			} else {
				condicion = " WHERE SUBSTR(V.DIAS," + dia + ",1)='" + dia
						+ "' AND C.CODIGOVENDEDOR=" + vendedor + " AND HAB=1";
			}
			Sql = "SELECT C.CODIGO,C.NOMBRE,C.DOMICILIO,C.LOCALIDAD,C.CODIGOLISTAPRECIO FROM CLIENTES AS C "
					+ "INNER JOIN VISITAS AS V ON C.CODIGO=V.CODIGOCLIENTE"
					+ condicion;
			cur = db.rawQuery(Sql, null);
			int contador=0;
			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {
					
					if (controladorPedido.devolverCantidadVendidosXCliente(dia,cur.getInt(cur.getColumnIndex("CODIGO"))) > 0) {		
					} else {
						
						if (controladorNoAtendido.buscarClienteNoAtendido(cur.getInt(cur.getColumnIndex("CODIGO"))) > 0) {
						} else {
							contador=contador+1;
						}
					}	
				}
			}
			cur.close();
			cerrarBaseDeDatos();
			
			return contador;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} 
		
	}
	
	public List<Cliente> buscarClientesLocalizados(int dia,int vendedor) throws Exception{
		try{
			abrirBaseDeDatos();
			if(dia>0){
				 Sql = "SELECT * FROM CLIENTES AS C INNER JOIN VISITAS AS V ON C.CODIGO=V.CODIGOCLIENTE " +
						"WHERE SUBSTR(V.DIAS," + dia + ",1)='" + dia+"' AND C.CODIGOVENDEDOR=" + vendedor + " AND HAB=1 AND LATITUD !=0 AND LONGITUD!=0 ";
			}else{
				 Sql = "SELECT * FROM CLIENTES AS C INNER JOIN VISITAS AS V ON C.CODIGO=V.CODIGOCLIENTE " +
						"WHERE V.DIAS IN('0000000','8888888','9999999') AND C.CODIGOVENDEDOR=" + vendedor + " AND HAB=1 AND LATITUD !=0 AND LONGITUD!=0 ";
			}
			cur = db.rawQuery(Sql, null);
			List<Cliente> lista = new ArrayList<Cliente>();
			Cliente cliente;
			if (cur.getColumnCount()>0){
				while (cur.moveToNext()){
					 cliente=new Cliente();
					 cliente.setCodigo(cur.getInt(cur.getColumnIndex("CODIGO")));
				     cliente.setNombre(cur.getString(cur.getColumnIndex("NOMBRE")));
				     cliente.setDireccion(cur.getString(cur.getColumnIndex("DOMICILIO")));
				     cliente.setCanal(cur.getString(cur.getColumnIndex("CANAL")));
				     cliente.setLocalidad(cur.getString(cur.getColumnIndex("LOCALIDAD")));
				     cliente.setObservaciones(cur.getString(cur.getColumnIndex("OBSERVACIONES")));
				     cliente.setProvincia(cur.getString(cur.getColumnIndex("PROVINCIA")));
				     cliente.setResponsabilidad(cur.getString(cur.getColumnIndex("RESPONSABILIDAD")));
				     cliente.setTelefono(cur.getString(cur.getColumnIndex("TELEFONO")));
				     cliente.setSaldo(cur.getDouble(cur.getColumnIndex("SALDO")));	
		     		 cliente.setLatitud(cur.getDouble(cur.getColumnIndex("LATITUD")));
		     		 cliente.setLongitud(cur.getDouble(cur.getColumnIndex("LONGITUD")));
		     		 lista.add(cliente);
				}
			}
			cur.close();
			cerrarBaseDeDatos();
			return lista;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public void guardarDatosGps(double latitud,double longitud,int cliente)throws Exception{
		try{
			abrirBaseDeDatos();
			Sql = "UPDATE CLIENTES SET LATITUD="+ latitud + ",LONGITUD="+ longitud + ",EDITADO=1 WHERE CODIGO=" + cliente;
			db.execSQL(Sql);
			cerrarBaseDeDatos();
		}catch(Exception ex){
			throw new Exception(ex.getMessage());
		}
		
	}
	
	public void eliminarObjetivoVisita()throws Exception{
		try{
			abrirBaseDeDatos();
			Sql = "DELETE FROM OBJXVISITA";
			db.execSQL(Sql);
			cerrarBaseDeDatos();
		}catch(Exception ex){
			throw new Exception(ex.getMessage());
		}
		
	}
	
	public void guardarObjetivoVisita(ObjetivoVisita objetivo )throws Exception{
		try{
			abrirBaseDeDatos();
			ContentValues cont = new ContentValues();
			cont.clear();
			cont.put("CODIGOCLIENTE", objetivo.getCodigo_cliente());
			cont.put("PERIODO", objetivo.getPeriodo());
			cont.put("VTA_ANT", objetivo.getVenta_anterior());
			cont.put("OBJ_ACT", objetivo.getObjetivo_actual());
			cont.put("CAN_VIS", objetivo.getCantidad_visitas());
			cont.put("VTA_ACT", objetivo.getVenta_actual());
			cont.put("FEC_ACT", objetivo.getFecha_actualizacion());
			cont.put("PORC", objetivo.getPorcentaje());
	
			Sql = "SELECT * FROM OBJXVISITA WHERE CODIGOCLIENTE=" + objetivo.getCodigo_cliente() + " AND PERIODO='" + objetivo.getPeriodo() + "'";
			cur = db.rawQuery(Sql, null);
			if (cur.moveToFirst()) {
				db.update("OBJXVISITA", cont, "CODIGOCLIENTE="+ objetivo.getCodigo_cliente() + " AND PERIODO='" + objetivo.getPeriodo()+"'", null);
			} else {
				db.insert("OBJXVISITA", null, cont);
			}	
			cur.close();
			cerrarBaseDeDatos();
		}catch(Exception ex){
			throw new Exception(ex.getMessage());
		}
		
	}
	
	synchronized private void abrirBaseDeDatos(){
		
		this.baseDeDatos = new BaseDeDatos(context.getApplicationContext(), funcion.BaseDeDatos(), null, funcion.Version());
		this.db = baseDeDatos.getReadableDatabase();
	}
	
	synchronized private void cerrarBaseDeDatos(){
		db.close();
		baseDeDatos.close();
		
		SQLiteDatabase.releaseMemory();
	}
	
	public boolean estaAbierto(){
		if(db != null){
			if (db.isOpen()) {
				Log.e("ControladorCliente","--Base de datos abierta--");
				return true;
			} 
			
		}
		return false;
	}
	
}
