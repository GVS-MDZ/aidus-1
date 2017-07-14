package com.gvs.controladores;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.gvs.modelos.Informe;
import com.gvs.modelos.Log;
import com.gvs.utilidades.BaseDeDatos;
import com.gvs.utilidades.Funciones;

public class ControladorInforme {
	
	private Funciones funcion;
	private BaseDeDatos baseDeDatos;
	private SQLiteDatabase db;
	private Cursor cur;
	private Context context;
	
	public ControladorInforme(Context contexto){
		this.context = contexto;
		this.funcion = new Funciones();
	}
	
synchronized public void guardarInforme(Informe informe )throws Exception{
		try{
			abrirBaseDeDatos();
			String Sql = "SELECT * FROM INFORME WHERE CODIGOVENDEDOR=" +informe.getVendedor() + " AND CODIGOCLIENTE=" + informe.getCliente() + " AND FECHA="+informe.getFecha();
			cur = db.rawQuery(Sql, null);
			ContentValues cont = new ContentValues();
			cont.clear();
			cont.put("CODIGOVENDEDOR", informe.getVendedor());
			cont.put("CODIGOCLIENTE", informe.getCliente());
			cont.put("ACCION", informe.getAccion());
			cont.put("RECOMENDACION", informe.getRecomendacion());
			cont.put("MEJORA", informe.getMejora());
			cont.put("FECHA", informe.getFecha());
			cont.put("ESTADO", informe.getEstado());
			if (cur.moveToFirst()) {
				db.update("INFORME", cont, "CODIGOVENDEDOR=" +informe.getVendedor() +" AND CODIGOCLIENTE=" + informe.getCliente() + " AND FECHA="+informe.getFecha(), null);
			} else {
				db.insert("INFORME", null, cont);
			}	
			cur.close();
			cerrarBaseDeDatos();
		}catch(Exception ex){
			throw new Exception(ex.getMessage());
		}
			
	}	
	
synchronized public List<Informe> buscarInformes()throws Exception{
		try {
			abrirBaseDeDatos();
			List<Informe> lista = new ArrayList<Informe>();
			String Sql = "SELECT * FROM INFORME WHERE ESTADO=0";
			cur = db.rawQuery(Sql, null);
			if (cur.getColumnCount()>0){
				while (cur.moveToNext()){
					Informe informe = new Informe();
					informe.setVendedor(cur.getInt(cur.getColumnIndex("CODIGOVENDEDOR")));
					informe.setCliente(cur.getInt(cur.getColumnIndex("CODIGOCLIENTE")));
					informe.setAccion(cur.getString(cur.getColumnIndex("ACCION")));
					informe.setMejora(cur.getString(cur.getColumnIndex("MEJORA")));
					informe.setRecomendacion(cur.getString(cur.getColumnIndex("RECOMENDACION")));
					informe.setFecha(cur.getLong(cur.getColumnIndex("FECHA")));					
					lista.add(informe);
				}
			}
			cur.close();
			cerrarBaseDeDatos();
			return lista;
		} catch (Exception e) {
			cur.close();
			cerrarBaseDeDatos();
			throw new Exception(e.getMessage());
		}
	}
	
	synchronized public void guardarLog(Log log )throws Exception{
		try{
			abrirBaseDeDatos();
			String Sql = "SELECT * FROM LOG WHERE CODIGOVENDEDOR=" +log.getVendedor() + " AND FECHA="+log.getFecha();
			cur = db.rawQuery(Sql, null);
			ContentValues cont = new ContentValues();
			cont.clear();
			cont.put("CODIGOVENDEDOR", log.getVendedor());
			cont.put("TIPO", log.getTipo());
			cont.put("DESCRIPCION", log.getDescripcion());
			cont.put("FECHA", log.getFecha());
			cont.put("ESTADO", log.getEstado());
			if (cur.moveToFirst()) {
				db.update("LOG", cont, "CODIGOVENDEDOR=" +log.getVendedor() + " AND FECHA="+log.getFecha(), null);
			} else {
				db.insert("LOG", null, cont);
			}		
		}catch(Exception ex){
			throw new Exception(ex.getMessage());
		}
		cur.close();
		cerrarBaseDeDatos();
		
	}
	
	public void EliminarLog(){
		abrirBaseDeDatos();
		String Sql = "DELETE FROM LOG WHERE ESTADO=1";
		db.execSQL(Sql);
		cerrarBaseDeDatos();
	}
	
synchronized public List<Log> buscarLogs()throws Exception{
		try {
			abrirBaseDeDatos();
			List<Log> lista = new ArrayList<Log>();
			String Sql = "SELECT * FROM LOG WHERE ESTADO=0";
			cur = db.rawQuery(Sql, null);
			if (cur.getColumnCount()>0){
				while (cur.moveToNext()){
					Log log = new Log();
					log.setVendedor(cur.getInt(cur.getColumnIndex("CODIGOVENDEDOR")));
					log.setDescripcion(cur.getString(cur.getColumnIndex("DESCRIPCION")));
					log.setTipo(cur.getInt(cur.getColumnIndex("TIPO")));
					log.setFecha(cur.getLong(cur.getColumnIndex("FECHA")));					
					lista.add(log);
				}
			}
			cur.close();
			cerrarBaseDeDatos();
			return lista;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
synchronized public void modificarLog(Log log){
		abrirBaseDeDatos();
		String Sql = "UPDATE LOG SET ESTADO=1 WHERE FECHA=" + log.getFecha();
		db.execSQL(Sql);
		cerrarBaseDeDatos();
	}
	
synchronized public void modificarInforme(Informe informe){
		abrirBaseDeDatos();
		String Sql = "UPDATE INFORME SET ESTADO=1 WHERE FECHA=" + informe.getFecha();
		db.execSQL(Sql);
		cerrarBaseDeDatos();
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
			android.util.Log.e("ControladorInforme","--Base de datos abierta--");
			return true;
		} 
		
	}
	return false;
}
	
}
