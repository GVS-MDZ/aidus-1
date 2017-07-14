package com.gvs.controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gvs.modelos.Venta;
import com.gvs.utilidades.BaseDeDatos;
import com.gvs.utilidades.Funciones;

public class ControladorVentaPorDia {
	
	private Funciones funcion;
	private BaseDeDatos baseDeDatos;
	private SQLiteDatabase db;
	private Cursor cur;
	private Context context;
	
	public ControladorVentaPorDia(Context contexto){
		
		this.context = contexto;
		this.funcion = new Funciones();
	}
	
	public void eliminarVentas(){
		abrirBaseDeDatos();
		String sqlB = "DELETE FROM VENTAXPDIAS";
		db.execSQL(sqlB);
		cerrarBaseDeDatos();
	}
	
	public void modificarVentas(Venta ventas){
		abrirBaseDeDatos();
		ContentValues cont = new ContentValues();
		cont.clear();
		cont.put("PERIODO", ventas.getPeriodo());
		cont.put("DIA", ventas.getDia());
		cont.put("IMPORTE", ventas.getImporte());
		cont.put("ACTUALIZACION",ventas.getActualizacion() );
		
		String Sql = "SELECT * FROM VENTAXPDIAS WHERE PERIODO='" + ventas.getPeriodo() + "' AND DIA=" + ventas.getDia() ;
		cur = db.rawQuery(Sql, null);
		if (cur.moveToNext()) {
			db.update("VENTAXPDIAS", cont, "PERIODO='" + ventas.getPeriodo() + "' AND DIA=" + ventas.getDia(), null);
		} else {
			db.insert("VENTAXPDIAS", null, cont);
		}
		
		cur.close();
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
				Log.e("ControladorVentaPorDia","--Base de datos abierta--");
				return true;
			} 
			
		}
		return false;
	}
		
}
