package com.gvs.controladores;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gvs.modelos.Sincronizacion;
import com.gvs.utilidades.BaseDeDatos;
import com.gvs.utilidades.Funciones;

public class ControladorSincronizacion {
	
	private Context context;
	private Funciones funcion;
	private BaseDeDatos baseDeDatos;
	private SQLiteDatabase db;
	private Cursor cur;
		
	public ControladorSincronizacion(Context contexto){
		this.context=contexto;
		funcion=new Funciones(context);
	}
	
	public void guardarSincronizacionParcial(int empresa,int vendedor){
		
		ContentValues cont = new ContentValues();	
		Date fecha = new Date();
		cont.put("CODIGOEMPRESA", empresa);
		cont.put("CODIGOVENDEDOR", vendedor);
		cont.put("FECHAINICIO", fecha.getTime());
		cont.put("FECHA", fecha.getTime());
		cont.put("MODO", "R");
		cont.put("INTERNET", funcion.estadoRedes());
		cont.put("ESTADO", 0);
		db.insert("SYNCRO", null, cont);
	}
	
	@SuppressLint("SimpleDateFormat")
	public boolean realizoSincronizacionGeneral(String tipo, int empresa,int vendedor) {
		try{
			abrirBaseDeDatos();
			String sql = "SELECT * FROM SYNCRO WHERE CODIGOEMPRESA="+ empresa + " AND CODIGOVENDEDOR=" + vendedor + " AND MODO='"+tipo+"'";
			cur = db.rawQuery(sql, null);
			int contador=0;
			if (cur.getColumnCount()>0){
				while (cur.moveToNext()){
					SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
					String fecha=format.format(new Date());
					Date fecha_sincro=new Date(cur.getLong(cur.getColumnIndex("FECHA")));
					String sincro=format.format(fecha_sincro);
					if(sincro.equals(fecha)){
						contador++;
					}
			     } 
			}
			if(contador>0){
				cur.close();
				cerrarBaseDeDatos();
				return true;
			}else{
				cur.close();
				cerrarBaseDeDatos();
				return false;
			}
		}catch(Exception ex){
			return false;
		}	
	}
	
	public boolean marcarParaEnviar() throws Exception{
		try {
			abrirBaseDeDatos();
			String Sql = "UPDATE SYNCRO SET ESTADO=11 WHERE ESTADO=0";
			db.execSQL(Sql);
			cerrarBaseDeDatos();
			return true;
		} catch (SQLException e) {
			throw new Exception("ERROR:Al intentar marcar sincronismos para ser enviados. " +
					"Pongase en contacto con la mesa de ayuda. Error: " + e.getMessage());
		}
	}
	
	public List<Sincronizacion> buscarSincronizacionesParaEnviar() throws Exception{
		try {
			abrirBaseDeDatos();
			List<Sincronizacion> lista = new ArrayList<Sincronizacion>();
			String Sql = "SELECT * FROM SYNCRO WHERE ESTADO=11";
			cur = db.rawQuery(Sql, null);
			if (cur.getColumnCount()>0){
				while (cur.moveToNext()){
					Sincronizacion syncro = new Sincronizacion();
					syncro.setCodigoEmpresa(cur.getInt(cur.getColumnIndex("CODIGOEMPRESA")));
					syncro.setCodigoVendedor(cur.getInt(cur.getColumnIndex("CODIGOVENDEDOR")));
					syncro.setFechaIncio(cur.getLong(cur.getColumnIndex("FECHAINICIO")));
					syncro.setFecha(cur.getLong(cur.getColumnIndex("FECHA")));
					syncro.setModo(cur.getString(cur.getColumnIndex("MODO")));
					syncro.setInternet(cur.getString(cur.getColumnIndex("INTERNET")));
					lista.add(syncro);
				}
			}
			cur.close();
			cerrarBaseDeDatos();
			return lista;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public void modificarSincronizacion(Sincronizacion syncro){
		abrirBaseDeDatos();
		String Sql = "UPDATE SYNCRO SET ESTADO=1 WHERE FECHA=" + syncro.getFecha();
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
			Log.e("ControladorSincronizacion","--Base de datos abierta--");
			return true;
		} 
			
		}
		return false;
	}
	
}
