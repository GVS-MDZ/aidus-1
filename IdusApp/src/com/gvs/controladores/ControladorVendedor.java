package com.gvs.controladores;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gvs.utilidades.BaseDeDatos;
import com.gvs.utilidades.Funciones;

public class ControladorVendedor {

	private Funciones funcion;
	private BaseDeDatos baseDeDatos;
	private SQLiteDatabase db;
	private Cursor cur;
	private Context context;

	public ControladorVendedor(Context contexto) {
		
		this.context = contexto;
		this.funcion = new Funciones();
	}
	
	public String buscarObjetivoVendedor(int numeroEmpresa,int numeroVendedor) {
		abrirBaseDeDatos();
		String respuesta="";
		String sql = "SELECT COBREGULAR,COBBUENA,COBMUYBUENA FROM VENDEDOR WHERE CODIGOEMPRESA=" + numeroEmpresa + " AND CODIGO=" +numeroVendedor;
		cur = db.rawQuery(sql, null);
		if (cur.moveToNext()) {
			respuesta = cur.getInt(cur.getColumnIndex("COBREGULAR")) + "~" + cur.getInt(cur.getColumnIndex("COBBUENA")) + "~" + cur.getInt(cur.getColumnIndex("COBMUYBUENA"));			
		} else {
			respuesta="sd";
		}
		cur.close();
		cerrarBaseDeDatos();
		return respuesta;
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
				Log.e("ControladorVendedor","--Base de datos abierta--");
				return true;
			} 
			
		}
		return false;
	}
	
}
