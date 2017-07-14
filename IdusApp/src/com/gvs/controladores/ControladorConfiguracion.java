package com.gvs.controladores;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gvs.modelos.Configuracion;
import com.gvs.utilidades.BaseDeDatos;
import com.gvs.utilidades.Funciones;

public class ControladorConfiguracion {
	
	private Funciones funcion;
	private BaseDeDatos baseDeDatos;
	private SQLiteDatabase db;
	private Cursor cur;
	private Context context;
	
	public ControladorConfiguracion(Context contexto){
		this.context = contexto;
		this.funcion = new Funciones();
	}
	
synchronized public Configuracion buscarConfiguracion(){
		abrirBaseDeDatos();
		String sql = "SELECT * FROM CONFIGURACION";
		cur = db.rawQuery(sql, null);
		Configuracion configuracion=null;
		if (cur.moveToFirst()) {
			configuracion=new Configuracion();
			configuracion.setCantidadItems(Integer.parseInt(cur.getString(cur.getColumnIndex("CANTIDADITEMS"))));
			configuracion.setDiaCompleto(cur.getInt(cur.getColumnIndex("DIACOMPLETO")));
			configuracion.setMuestraSugeridos(cur.getInt(cur.getColumnIndex("MUESTRASUGERIDOS")));
			configuracion.setServicioGeo(cur.getInt(cur.getColumnIndex("SERVICIOGEO")));
			configuracion.setOtroDia(cur.getInt(cur.getColumnIndex("OTRODIA")));
			configuracion.setDescuento(cur.getInt(cur.getColumnIndex("DESCUENTO")));
			configuracion.setAiuds(cur.getInt(cur.getColumnIndex("AIDUS")));
			configuracion.setControl_deuda(cur.getInt(cur.getColumnIndex("CONTROLDEUDA")));
			configuracion.setDia_contol(cur.getInt(cur.getColumnIndex("DIASCONTROL")));
		}
		cur.close();
		cerrarBaseDeDatos();
		return configuracion;
	}

synchronized public void modificarConfiguracionCenector(Configuracion configuracion){
		abrirBaseDeDatos();
		String Sql = "UPDATE CONFIGURACION SET AIDUS="+ configuracion.getAiuds()+",CONTROLDEUDA="+configuracion.getControl_deuda()+",DIASCONTROL="+configuracion.getDia_contol();
		db.execSQL(Sql);
		cerrarBaseDeDatos();
	}
	
synchronized private void abrirBaseDeDatos (){
		
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
			Log.e("ControladorConfiguracion","--Base de datos abierta--");
			return true;
		} 
		
	}
	return false;
}
}
