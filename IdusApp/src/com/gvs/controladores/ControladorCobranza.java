package com.gvs.controladores;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gvs.modelos.Cobranza;
import com.gvs.utilidades.BaseDeDatos;
import com.gvs.utilidades.Funciones;

public class ControladorCobranza {

	private Funciones funcion;
	private BaseDeDatos baseDeDatos;
	private SQLiteDatabase db;
	private Cursor cur;
	private Context context;
	
	public ControladorCobranza(Context contexto) {
		this.context = contexto;
		this.funcion = new Funciones();
		
	}	
	
	public List<Cobranza> buscarCobranzas() throws Exception{
		try{
			abrirBaseDeDatos();
			List<Cobranza> lista = new ArrayList<Cobranza>();
			String Sql = "SELECT * FROM COBRANZA WHERE ESTADO=0";
			cur = db.rawQuery(Sql, null);
			if (cur.getColumnCount()>0){
				while (cur.moveToNext()){
					 Cobranza cobranza=new Cobranza();
					 cobranza.setClase(cur.getString(cur.getColumnIndex("CLASE")));
					 cobranza.setTipo(cur.getString(cur.getColumnIndex("TIPO")));
					 cobranza.setSucursal(cur.getInt(cur.getColumnIndex("SUCURSAL")));
					 cobranza.setNumero_comprobante(cur.getInt(cur.getColumnIndex("NUMERO")));
					 cobranza.setNumero_recibo(cur.getInt(cur.getColumnIndex("RECIBO")));
					 cobranza.setImporte_pagado(cur.getDouble(cur.getColumnIndex("IMPORTEPAGADO")));
					 cobranza.setSaldo(cur.getDouble(cur.getColumnIndex("SALDO")));
					 cobranza.setFecha(cur.getLong(cur.getColumnIndex("FECHA")));
					 cobranza.setEstado(cur.getInt(cur.getColumnIndex("ESTADO")));
					 cobranza.setCodigo_cliente(cur.getInt(cur.getColumnIndex("CODIGOCLIENTE")));
					 cobranza.setCodigo_vendedor(cur.getInt(cur.getColumnIndex("CODIGOVENDEDOR")));
		     		 lista.add(cobranza);
				}
			}
			cur.close();
			cerrarBaseDeDatos();
			return lista;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public int buscarComprobantesPagados(int cliente,int periodo){
		try{
			abrirBaseDeDatos();
			Date fecha=new Date();	
			Date antes=new Date();          
            Calendar calendario_hasta = Calendar.getInstance();
            calendario_hasta.setTime(fecha);
            calendario_hasta.add(Calendar.DATE, -periodo);
            antes = calendario_hasta.getTime();
			String sql = "SELECT * FROM COBRANZA WHERE  CODIGOCLIENTE=" + cliente +" AND FECHA<"+fecha.getTime()+" AND FECHA>="+antes.getTime();
			cur = db.rawQuery(sql, null);
			int contador=0;
			if (cur.getColumnCount()>0){
				while (cur.moveToNext()){
					contador++;
				}
			}
			cur.close();
			cerrarBaseDeDatos();
			return contador;
		} catch (Exception e) {
			cur.close();
			cerrarBaseDeDatos();
			return 0;
		}
	}
	
	public void modificarCobranza(Cobranza gain){
		abrirBaseDeDatos();
		String sql = "UPDATE COBRANZA SET ESTADO=1 WHERE RECIBO="+gain.getNumero_recibo();
		db.execSQL(sql);
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
				Log.e("ControladorCobranza","--Base de datos abierta--");
				return true;
			} 
			
		}
		return false;
	}
}
