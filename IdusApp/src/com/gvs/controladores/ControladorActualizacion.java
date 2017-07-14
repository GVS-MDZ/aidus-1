package com.gvs.controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.gvs.modelos.ListaPrecio;

//import java.nio.channels.spi.AbstractInterruptibleChannel;

import com.gvs.utilidades.BaseDeDatos;
import com.gvs.utilidades.Funciones;

public class ControladorActualizacion {
	
	private Funciones funcion;
	private BaseDeDatos baseDeDatos;
	private SQLiteDatabase db;
	private Cursor cur;
	private Context context;
	private ContentValues contenido;
	public ControladorActualizacion(Context contexto){
		this.context=contexto;
		this.funcion = new Funciones();
	}
	
	
	
	synchronized public void guardarVendedor(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM VENDEDOR WHERE CODIGOEMPRESA="+ cont.getAsInteger("CODIGOEMPRESA") + " AND CODIGO="+ cont.getAsInteger("CODIGO");
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("VENDEDOR",cont,"CODIGOEMPRESA=" + cont.getAsInteger("CODIGOEMPRESA") + " AND CODIGO="+ cont.getAsInteger("CODIGO"), null);
			} else {
				db.insert("VENDEDOR", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally{
			
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
/*	synchronized public void guardarVendedores(ArrayList<String[]> cont){

		abrirBaseDeDatos();
		String sql = "SELECT * FROM VENDEDOR WHERE CODIGOEMPRESA="+ cont.getAsInteger("CODIGOEMPRESA") + " AND CODIGO="+ cont.getAsInteger("CODIGO");
		cur = db.rawQuery(sql, null);
		if (cur.moveToNext()) {
			db.update("VENDEDOR",cont,"CODIGOEMPRESA=" + cont.getAsInteger("CODIGOEMPRESA") + " AND CODIGO="+ cont.getAsInteger("CODIGO"), null);
		} else {
			db.insert("VENDEDOR", null, cont);
		}
		
		cur.close();
		cerrarBaseDeDatos();
		
	}*/
	
	
	
	synchronized public void guardarListaPrecio(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM LISTASPRECIO WHERE CODIGO=" + cont.getAsInteger("CODIGO") + " AND CODIGORUBRO=" +  cont.getAsInteger("CODIGORUBRO")+ " AND CODIGOSUBRUBRO=" +  cont.getAsInteger("CODIGOSUBRUBRO")+" AND CODIGOARTICULO='"+cont.getAsString("CODIGOARTICULO")+"'";
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("LISTASPRECIO", cont, "CODIGO=" + cont.getAsInteger("CODIGO")+ " AND CODIGORUBRO=" + cont.getAsInteger("CODIGORUBRO") + " AND CODIGOSUBRUBRO=" + cont.getAsInteger("CODIGOSUBRUBRO")+" AND CODIGOARTICULO='"+cont.getAsString("CODIGOARTICULO")+"'",	null);
			} else {
				db.insert("LISTASPRECIO", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally{
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void guardarListasDePrecios(ArrayList<ListaPrecio> listaPreciosParam){
		contenido = new ContentValues();
		contenido.clear();
		StringBuilder consulta; 
		int contadorTransaccion = 0;
		try {
			
			abrirBaseDeDatos();
			db.beginTransaction();
		
			for(ListaPrecio lista1 : listaPreciosParam){
			
				Log.e("CONTROLACTUALIZ",lista1.getCodigo() +","+ lista1.getCodigo_articulo());
					
					contenido.put("CODIGO",lista1.getCodigo());
					contenido.put("CODIGORUBRO", lista1.getCodigo_rubro());
					contenido.put("CODIGOSUBRUBRO",lista1.getCodigo_subrubro());
					contenido.put("CODIGOARTICULO",lista1.getCodigo_articulo());
					contenido.put("PORCENTAJE",lista1.getPorcentaje());
					contenido.put("HAB", lista1.getHab());
				
					
				consulta = new StringBuilder();
				consulta.append("SELECT * FROM LISTASPRECIO WHERE CODIGO=");
				consulta.append(contenido.getAsInteger("CODIGO"));
				consulta.append(" AND CODIGORUBRO=");
				consulta.append( contenido.getAsInteger("CODIGORUBRO"));
				consulta.append(" AND CODIGOSUBRUBRO=");
				consulta.append(  contenido.getAsInteger("CODIGOSUBRUBRO"));
				consulta.append(" AND CODIGOARTICULO='");
				consulta.append(contenido.getAsString("CODIGOARTICULO"));
				consulta.append("'");
				
				cur = db.rawQuery(consulta.toString(), null);
					if (cur.moveToFirst()) {
						db.update("LISTASPRECIO", contenido, "CODIGO=" + contenido.getAsInteger("CODIGO")+ " AND CODIGORUBRO=" + contenido.getAsInteger("CODIGORUBRO") + " AND CODIGOSUBRUBRO=" + contenido.getAsInteger("CODIGOSUBRUBRO")+" AND CODIGOARTICULO='"+contenido.getAsString("CODIGOARTICULO")+"'",	null);
					} else {
						db.insert("LISTASPRECIO", null, contenido);
					}
						if(contadorTransaccion== 1000){
							db.setTransactionSuccessful();
							db.endTransaction();
							db.beginTransaction();
							contadorTransaccion=0;
							
				
						}
						contadorTransaccion++;
						
					cur.close();
					contenido.clear();
				}
			
		} catch (Exception e) {
			Log.e("guardaListaPrecio",e.getMessage());
			
		}finally {
			
			db.setTransactionSuccessful();
			db.endTransaction();
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void guardarCliente(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM CLIENTES WHERE CODIGO="+ cont.getAsInteger("CODIGO");
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("CLIENTES",cont,"CODIGO=" + cont.getAsInteger("CODIGO"),null);
			} else {
				db.insert("CLIENTES", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally{
		
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void eliminarCliente(){
		try {
			abrirBaseDeDatos();
			String sql = "DELETE FROM CLIENTES";
			db.execSQL(sql);
		} catch (SQLException e) {
			
			e.printStackTrace();
		} finally {
			cerrarBaseDeDatos();
		}
		
	}
	
	synchronized public void eliminarVisitas(){
		try {
			abrirBaseDeDatos();
			String sql = "DELETE FROM VISITAS";
			db.execSQL(sql);
		} catch (SQLException e) {
			
			e.printStackTrace();
		} finally {
		cerrarBaseDeDatos();
		}
		
	}
	
	synchronized public void guardarVisita(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM VISITAS WHERE CODIGOCLIENTE="+ cont.getAsInteger("CODIGOCLIENTE");
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("VISITAS",cont,"CODIGOCLIENTE="+ cont.getAsInteger("CODIGOCLIENTE"),null);
			} else {
				db.insert("VISITAS", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
		
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void guardarRubro(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM RUBROS WHERE CODIGO="+ cont.getAsInteger("CODIGO");
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("RUBROS", cont,"CODIGO=" +cont.getAsInteger("CODIGO"),null);
			} else {
				db.insert("RUBROS", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
		
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void guardarSubrubro(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql= "SELECT * FROM SUBRUBROS WHERE CODIGO="+cont.getAsInteger("CODIGO")+ " AND CODIGORUBRO="+ cont.getAsInteger("CODIGORUBRO");
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("SUBRUBROS",cont,"CODIGO=" + cont.getAsInteger("CODIGO")+ " AND CODIGORUBRO="+cont.getAsInteger("CODIGORUBRO"),null);
			} else {
				db.insert("SUBRUBROS", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
		
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void eliminarArticulo(){
		try {
			abrirBaseDeDatos();
			String sql = "DELETE FROM ARTICULOS";
			db.execSQL(sql);
		} catch (SQLException e) {
			
			e.printStackTrace();
		} finally {
			cerrarBaseDeDatos();
		}		
	}
	
	synchronized public void guardarArticulo(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM ARTICULOS WHERE CODIGO='"+ cont.getAsString("CODIGO")+ "'";
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("ARTICULOS", cont,"CODIGO='" + cont.getAsString("CODIGO")+ "'", null);
			} else {
				db.insert("ARTICULOS", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
		
			cur.close();
			cerrarBaseDeDatos();
		}
		
	}
	
	synchronized public void guardarMotivo(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM MOTIVOS WHERE CODIGO="+ cont.getAsInteger("CODIGO");
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("MOTIVOS", cont,"CODIGO=" + cont.getAsInteger("CODIGO"),null);
			} else {
				db.insert("MOTIVOS", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			cur.close();
			cerrarBaseDeDatos();
		}
	
	}
	
	synchronized public void guardarSugerido(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM SUGERIDOS WHERE CODIGOARTICULO='"+ cont.getAsString("CODIGOARTICULO") + "'";
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("SUGERIDOS", cont, "CODIGOARTICULO='"+ cont.getAsString("CODIGOARTICULO") + "'",null);
			} else {
				db.insert("SUGERIDOS", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
		
			cur.close();
			cerrarBaseDeDatos();
		}
		
	}
	
	synchronized public void modificarConfiguracion(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM CONFIGURACION";
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("CONFIGURACION", cont,null, null);
			} else {
				db.insert("CONFIGURACION", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
		
			cur.close();
			cerrarBaseDeDatos();
		}
		
	}
	
	synchronized public void guardarPedido(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM PEDIDOSCABECERA WHERE CODIGOCLIENTE=-1";
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("PEDIDOSCABECERA", cont," CODIGOCLIENTE=-1", null);
			} else {
				db.insert("PEDIDOSCABECERA", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
		
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void eliminarArticuloImpresindible(){
		try {
			abrirBaseDeDatos();
			String sql = "DELETE FROM ARTINDISPENSABLES";
			db.execSQL(sql);
		} catch (SQLException e) {
			
			e.printStackTrace();
		} finally { 
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void guardarImpresindible(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM ARTINDISPENSABLES WHERE CODIGOCLIENTE="+ cont.getAsInteger("CODIGOCLIENTE")+
					    " AND CODIGOARTICULO='"+ cont.getAsString("CODIGOARTICULO") + "'";
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("ARTINDISPENSABLES",cont,"CODIGOCLIENTE="+ cont.getAsInteger("CODIGOCLIENTE")
						+ " AND CODIGOARTICULO='"+ cont.getAsString("CODIGOARTICULO")+ "'", null);
			} else {
				db.insert("ARTINDISPENSABLES", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
		
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void eliminarArticuloLanzamiento(){
		try {
			abrirBaseDeDatos();
			String sql = "DELETE FROM ARTLANZAMIENTOS";
			db.execSQL(sql);
		} catch (SQLException e) {
		
			e.printStackTrace();
		} finally {
			cerrarBaseDeDatos();
		}
		
	}
	
	synchronized public void guardarLanzamiento(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM ARTLANZAMIENTOS WHERE CODIGOCLIENTE="+ cont.getAsInteger("CODIGOCLIENTE")
					+ " AND CODIGOARTICULO='"+ cont.getAsString("CODIGOARTICULO") + "'";
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("ARTLANZAMIENTOS",cont,"CODIGOCLIENTE="+ cont.getAsInteger("CODIGOCLIENTE")
								+ " AND CODIGOARTICULO='"+ cont.getAsString("CODIGOARTICULO")+ "'", null);
			} else {
				db.insert("ARTLANZAMIENTOS", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
		
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void eliminarObjetivos(){
		try {
			abrirBaseDeDatos();
			String sql = "DELETE FROM OBJXVISITA";
			db.execSQL(sql);
		} catch (SQLException e) {
			
			e.printStackTrace();
		} finally {
			cerrarBaseDeDatos();
		}
		
	}
	
	synchronized public void guardarObjetivoVisita(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM OBJXVISITA WHERE CODIGOCLIENTE="
					+ cont.getAsInteger("CODIGOCLIENTE")+ " AND PERIODO='"+ cont.getAsString("PERIODO") + "'";
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("OBJXVISITA",cont,"CODIGOCLIENTE="+ cont.getAsInteger("CODIGOCLIENTE")
						+ " AND PERIODO='"+ cont.getAsString("PERIODO") + "'",null);
			} else {
				db.insert("OBJXVISITA", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
		
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void eliminarVentas(){
		try {
			abrirBaseDeDatos();
			String sql = "DELETE FROM VENTAXPDIAS";
			db.execSQL(sql);
		} catch (SQLException e) {
			
			e.printStackTrace();
		} finally {
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void guardarVentas(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM VENTAXPDIAS WHERE PERIODO='"+ cont.getAsString("PERIODO") + "' AND DIA="+ cont.getAsInteger("DIA");
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("VENTAXPDIAS",cont,"PERIODO='" + cont.getAsString("PERIODO")+ "' AND DIA="+ cont.getAsInteger("DIA"), null);
			} else {
				db.insert("VENTAXPDIAS", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
		
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void eliminarEncuestas(){
		try {
			abrirBaseDeDatos();
			String sqlEncuestaCabecera = "DELETE FROM ENCUESTACABECERA";
			String sqlEncuestaCuerpo = "DELETE FROM ENCUESTACUERPO";
			db.execSQL(sqlEncuestaCabecera);
			db.execSQL(sqlEncuestaCuerpo);
		} catch (SQLException e) {
			
			e.printStackTrace();
		} finally {
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void guardarEncuesta(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM ENCUESTACABECERA WHERE NUMERO="+ cont.getAsInteger("NUMERO");
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("ENCUESTACABECERA", cont, "NUMERO="+ cont.getAsInteger("NUMERO"), null);
			} else {
				db.insert("ENCUESTACABECERA", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			cur.close();
			cerrarBaseDeDatos();
		}
	} 
	
	synchronized public void guardarItemEncuesta(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM ENCUESTACUERPO WHERE NUMERO="+ cont.getAsInteger("NUMERO") + " AND ITEM="+ cont.getAsInteger("ITEM");
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("ENCUESTACUERPO",cont,"NUMERO=" + cont.getAsInteger("NUMERO")+ " AND ITEM="+ cont.getAsInteger("ITEM"), null);
			} else {
				db.insert("ENCUESTACUERPO", null, cont);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void eliminarComprobantes(){
		try {
			abrirBaseDeDatos();
			String sql= "DELETE FROM COMPROBANTESVENTAS";
			db.execSQL(sql);
		} catch (SQLException e) {
			
			e.printStackTrace();
		} finally {
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void guardarComprobante(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM COMPROBANTESVENTAS WHERE TIPO='"+ cont.getAsString("TIPO") + "' AND CLASE='"
					+ cont.getAsString("CLASE") + "' AND SUCURSAL="+ cont.getAsInteger("SUCURSAL")+ " AND NUMERO=" + cont.getAsInteger("NUMERO");
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("COMPROBANTESVENTAS",cont,"TIPO='" + cont.getAsString("TIPO")+ "' AND CLASE='"+ cont.getAsString("CLASE")
						+ "' AND SUCURSAL="+ cont.getAsInteger("SUCURSAL")+ " AND NUMERO="+ cont.getAsString("NUMERO"), null);
			} else {
				db.insert("COMPROBANTESVENTAS", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
		
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
	synchronized public void guardarCombo(ContentValues cont){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM COMBOS WHERE CODIGO="+ cont.getAsInteger("CODIGO");
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("COMBOS", cont,"CODIGO=" + cont.getAsInteger("CODIGO"),null);
			} else {
				db.insert("COMBOS", null, cont);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
		
			cur.close();
			cerrarBaseDeDatos();
		}
	
	}
	
	synchronized public void guardarSincronizacionTotal(ContentValues cont){
		try {
			abrirBaseDeDatos();
			db.insert("SYNCRO", null, cont);
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			cerrarBaseDeDatos();
		}
	}
	
synchronized private void abrirBaseDeDatos(){
		
		this.baseDeDatos = new BaseDeDatos(context.getApplicationContext(), funcion.BaseDeDatos(), null, funcion.Version());
		this.db = baseDeDatos.getWritableDatabase();
		
	}
	
synchronized private void cerrarBaseDeDatos(){
		db.close();
		baseDeDatos.close();
		
		SQLiteDatabase.releaseMemory();
	}
	
public boolean estaAbierto(){
	if(db != null){
		if (db.isOpen()) {
			Log.e("ControladorActualizacion","--Base de datos abierta--");
			return true;
		}  
	}	
	return false;
}
	
}
