package com.gvs.controladores;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import org.apache.http.impl.io.AbstractSessionInputBuffer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gvs.modelos.Motivo;
import com.gvs.modelos.NoAtendido;
import com.gvs.utilidades.BaseDeDatos;
import com.gvs.utilidades.Funciones;

public class ControladorNoAtendido {
	
	private Funciones funcion;
	private BaseDeDatos baseDeDatos;
	private SQLiteDatabase db;
	private Cursor cur;
	private Context context;
	
	public ControladorNoAtendido() {		
		
		this.funcion = new Funciones();
	}
	
	public void setContexto(Context contexto){
		this.context = contexto;
	}
	
	public void grabarNoAtendido(NoAtendido noatendido) throws Exception{
		try {			
			abrirBaseDeDatos();
			String Sql = "SELECT * FROM NOATENDIDOS WHERE CODIGOCLIENTE=" + noatendido.getCodigoCliente() + " AND FECHA=" + noatendido.getFecha();
			cur = db.rawQuery(Sql, null);
			ContentValues cont = new ContentValues();
			if (cur.moveToNext()) {	
				cont.put("CODIGOCLIENTE", noatendido.getCodigoCliente());
				cont.put("CODIGOVENDEDOR", noatendido.getCodigoVendedor());
				cont.put("FECHA",noatendido.getFecha());
				cont.put("CODIGOMOTIVO", noatendido.getCodigoMotivo());
				cont.put("LATITUD", noatendido.getLatitud());
				cont.put("LONGITUD", noatendido.getLongitud());
				cont.put("PRECISION", noatendido.getPresicion());
				cont.put("PROVEE", noatendido.getProveedor());
				cont.put("ESTADO", 0);
				db.update("NOATENDIDOS", cont, "CODIGOCLIENTE=" + cont.getAsInteger("CODIGOCLIENTE") + " AND FECHA=" + cont.getAsLong("FECHA"), null);
			} else {
				cont.put("CODIGOCLIENTE", noatendido.getCodigoCliente());
				cont.put("CODIGOVENDEDOR", noatendido.getCodigoVendedor());
				cont.put("FECHA",noatendido.getFecha());
				cont.put("CODIGOMOTIVO", noatendido.getCodigoMotivo());
				cont.put("LATITUD", noatendido.getLatitud());
				cont.put("LONGITUD", noatendido.getLongitud());
				cont.put("PRECISION", noatendido.getPresicion());
				cont.put("PROVEE", noatendido.getProveedor());
				cont.put("ESTADO", 0);
				db.insert("NOATENDIDOS", null, cont);
			}
			cur.close();
			cerrarBaseDeDatos();
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}
	
	public boolean marcarParaEnviar() throws Exception{
		try { 
			abrirBaseDeDatos();
			String Sql = "UPDATE NOATENDIDOS SET ESTADO=11 WHERE ESTADO=0";
			db.execSQL(Sql);
			cerrarBaseDeDatos();
			return true;
		} catch (SQLException e) {
			throw new Exception("ERROR:Al intentar marcar el no atendidos para ser enviados. " +
					"Pongase en contacto con la mesa de ayuda. Error: " + e.getMessage());
		}
	}
	
	public List<NoAtendido> buscarNoAtendidosParaEnviar() throws Exception{
		try {
			abrirBaseDeDatos();
			List<NoAtendido> lista = new ArrayList<NoAtendido>();
			String Sql = "SELECT * FROM NOATENDIDOS WHERE ESTADO=11";
		    cur = db.rawQuery(Sql, null);
			if (cur.getColumnCount()>0){
				NoAtendido noAtendido;
				while (cur.moveToNext()){
					noAtendido = new NoAtendido();
					noAtendido.setFecha(cur.getLong(cur.getColumnIndex("FECHA")));
					noAtendido.setCodigoVendedor(cur.getInt(cur.getColumnIndex("CODIGOVENDEDOR")));
					noAtendido.setCodigoCliente(cur.getInt(cur.getColumnIndex("CODIGOCLIENTE")));
					noAtendido.setCodigoMotivo(cur.getInt(cur.getColumnIndex("CODIGOMOTIVO")));
					noAtendido.setLatitud(cur.getDouble(cur.getColumnIndex("LATITUD")));
					noAtendido.setLongitud(cur.getDouble(cur.getColumnIndex("LONGITUD")));
					noAtendido.setPresicion(cur.getDouble(cur.getColumnIndex("PRECISION")));
					noAtendido.setProveedor(cur.getString(cur.getColumnIndex("PROVEE")));
					lista.add(noAtendido);
				}
			}
			cur.close();
			cerrarBaseDeDatos();
			return lista;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		
	}
	
	public int buscarClienteNoAtendido(int codigoCliente){		
		try {
			abrirBaseDeDatos();
			int cantidad=0;
			Date fecha = new Date();
			String hoyTexto = funcion.dateToString_yyyymmdd(fecha.getTime());
			Date fechaINI = funcion.stringToDate(hoyTexto);
			String Sql = "SELECT * FROM NOATENDIDOS " +
					"WHERE CODIGOCLIENTE=" + codigoCliente+" AND FECHA>="+fechaINI.getTime();
			cur = db.rawQuery(Sql, null);
			while (cur.moveToNext()){
				cantidad++;
			}
			cur.close();
			cerrarBaseDeDatos();
			return cantidad;			
		} catch (Exception e) {
			return 0;
		}
	}
	
	public List<Motivo> buscarMotivosNoVenta()throws Exception{
		try{
			abrirBaseDeDatos();
			String Sql = "SELECT * FROM MOTIVOS ORDER BY NOMBRE";
			cur = db.rawQuery(Sql, null);
			List<Motivo> lista=null;
			if (cur.getCount() > 0) {
			    lista=new ArrayList<Motivo>();
				Motivo motivo=null;
				while (cur.moveToNext()) {
					motivo=new Motivo();
					motivo.setCodigo(cur.getInt(cur.getColumnIndex("CODIGO")));
					motivo.setNombre(cur.getString(cur.getColumnIndex("NOMBRE")));
					lista.add(motivo);
				}
			}
			cur.close();
			cerrarBaseDeDatos();
			return lista;
		}catch(Exception ex){
			throw new Exception(ex.getMessage());
		}
		
	}
	
	public void modificarNoAtendido(NoAtendido noAtendido){
		abrirBaseDeDatos();
		String Sql = "UPDATE NOATENDIDOS SET ESTADO=1 WHERE FECHA=" + noAtendido.getFecha();
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
				Log.e("ControladorNoAtendido","--Base de datos abierta--");
				return true;
			}
			
		}
		return false;
	}

}
