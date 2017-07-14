package com.gvs.controladores;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.gvs.idusapp.R;
import com.gvs.modelos.Encuesta;
import com.gvs.modelos.RespuestaEncuesta;
import com.gvs.utilidades.BaseDeDatos;
import com.gvs.utilidades.Funciones;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ControladorEncuesta {

	private Funciones funcion;
	private BaseDeDatos baseDeDatos;
	private SQLiteDatabase db;
	private Cursor cur;
	private Context context;
	
	public ControladorEncuesta(Context contexto){
		this.context = contexto;
		this.funcion = new Funciones();
		}
	
	@SuppressLint("SimpleDateFormat")
	public List<Encuesta> buscarEncuestas(int cliente,int vendedor)throws Exception {
		try{
			abrirBaseDeDatos();
			SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
			String fecha=format.format(new Date());
			Date fec=format.parse(fecha);
			long fechaActual =fec.getTime();
			String sqlEncuesta = "SELECT * FROM ENCUESTACABECERA WHERE FECHAFIN>=" + fechaActual + " AND FECHAINI<=" + fechaActual;
			cur = db.rawQuery(sqlEncuesta, null);
			List<Encuesta>lista=new ArrayList<Encuesta>();
			Encuesta encuesta;
			if (cur.getCount()>0) {
				while (cur.moveToNext()){
					encuesta=new Encuesta();
					encuesta.setNumero(Integer.parseInt(funcion.format(cur.getInt(cur.getColumnIndex("NUMERO")),4)));
					encuesta.setDescripcion(cur.getString(cur.getColumnIndex("NOMBRE")));
					encuesta.setFecha_inicio(funcion.dateToString_yyyymmdd_hhmm(cur.getLong(cur.getColumnIndex("FECHAINI"))));
					encuesta.setFecha_fin(funcion.dateToString_yyyymmdd_hhmm(cur.getLong(cur.getColumnIndex("FECHAFIN"))));
					String sqlTomaEncuesta="SELECT * FROM TOMAENCUESTACABECERA WHERE NUMEROENCUESTA='" + cur.getInt(cur.getColumnIndex("NUMERO")) + "' AND CODIGOCLIENTE="+cliente+" AND CODIGOVENDEDOR="+vendedor;
					Cursor cur2 = db.rawQuery(sqlTomaEncuesta, null);
					encuesta.setIcono(R.drawable.amarillo);
					while(cur2.moveToNext()){
						encuesta.setIcono(R.drawable.verde);		
					}
					cur2.close();
					lista.add(encuesta);
				}
			}
			cur.close();
			cerrarBaseDeDatos();
		return lista;	
		}catch(Exception ex){
			throw new Exception(ex.getMessage());
		}
	}
	
	public String buscarNuevoItem(int numeroEncuesta,int numeroItem) {
		abrirBaseDeDatos();
		String respuesta="";
		String sql = "SELECT ITEM,PREGUNTA,AYUDA FROM ENCUESTACUERPO WHERE NUMERO=" + numeroEncuesta + " AND ITEM>" + numeroItem + " ORDER BY ITEM LIMIT 1 ";
		cur = db.rawQuery(sql, null);
		if (cur.moveToNext()) {
			respuesta = cur.getString(cur.getColumnIndex("PREGUNTA")) + "~" + cur.getString(cur.getColumnIndex("AYUDA")) + "~" + cur.getInt(cur.getColumnIndex("ITEM"));			
		} else {
			if (numeroItem>0) {
				respuesta="ef";
			} else {
				respuesta="ev";
			}
		}
		cur.close();
		cerrarBaseDeDatos();
		return respuesta;
	}
	
	public void grabarEncuesta(ContentValues conEncuesta,ContentValues conEncuestaItem) throws Exception{
		try {
			abrirBaseDeDatos();
			String SqlEnsuestaCabecera= "SELECT * FROM TOMAENCUESTACABECERA WHERE NUMERO='" + conEncuesta.getAsString("NUMERO") + "' AND NUMEROENCUESTA="+Integer.parseInt(conEncuesta.getAsString("NUMEROENCUESTA"));
			cur = db.rawQuery(SqlEnsuestaCabecera, null);
			if (cur.moveToNext()==false) {				
				db.insert("TOMAENCUESTACABECERA", null, conEncuesta);
			}
			cur.close();
			String SqlEncuestaCuerpo = "SELECT * FROM TOMAENCUESTACUERPO WHERE NUMERO='" + conEncuestaItem.getAsString("NUMERO") + "' AND ITEM=" + conEncuestaItem.getAsInteger("ITEM");
			cur = db.rawQuery(SqlEncuestaCuerpo, null);
			if (cur.moveToNext()==false) {
				db.insert("TOMAENCUESTACUERPO", null, conEncuestaItem);
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
			String Sql = "UPDATE TOMAENCUESTACABECERA SET ESTADO=11 WHERE ESTADO=1";
			db.execSQL(Sql);
			cerrarBaseDeDatos();
			return true;
		} catch (SQLException e) {
			throw new Exception("ERROR:Al intentar marcar el no atendidos para ser enviados. " +
					"Pongase en contacto con la mesa de ayuda. Error: " + e.getMessage());
		}
	}	

	public List<RespuestaEncuesta> buscarRespuestasParaEnviar() throws Exception{
		try {
			abrirBaseDeDatos();
			List<RespuestaEncuesta> respuestas = new ArrayList<RespuestaEncuesta>();
			String Sql = "SELECT * FROM TOMAENCUESTACABECERA WHERE ESTADO=11";
			cur = db.rawQuery(Sql, null);
			if (cur.getCount()>0){
				while (cur.moveToNext()){
					RespuestaEncuesta resp = new RespuestaEncuesta();
					resp.setId(cur.getString(cur.getColumnIndex("NUMERO")));
					resp.setNumero(cur.getInt(cur.getColumnIndex("NUMEROENCUESTA")));
					resp.setCodigoCliente(cur.getInt(cur.getColumnIndex("CODIGOCLIENTE")));
					resp.setFecha(cur.getLong(cur.getColumnIndex("FECHA")));
					resp.setObservacion(cur.getString(cur.getColumnIndex("OBSERVACION")));
					resp.setLatitud(cur.getString(cur.getColumnIndex("LATITUD")));
					resp.setLongitud(cur.getString(cur.getColumnIndex("LONGITUD")));
					resp.setPrecision(cur.getString(cur.getColumnIndex("PRECISION")));
					resp.setProveedor(cur.getString(cur.getColumnIndex("PROVEE")));
					Sql = "SELECT ITEM,RESPUESTA FROM TOMAENCUESTACUERPO WHERE NUMERO='" + resp.getId() + "'";
					Cursor cur01 = db.rawQuery(Sql, null);
					if (cur01.getColumnCount()>0) {
						String respItem="";
						while (cur01.moveToNext()) {
							if (respItem.equals("")) {
								respItem=cur01.getInt(cur01.getColumnIndex("ITEM")) + ";"+ cur01.getInt(cur01.getColumnIndex("RESPUESTA")); 
							} else {
								respItem=respItem + "~" +cur01.getInt(cur01.getColumnIndex("ITEM")) + ";"+ cur01.getInt(cur01.getColumnIndex("RESPUESTA"));
							}
						}
						resp.setRespuestas(respItem);
					}					
					respuestas.add(resp);
				}
			}
			cur.close();
			cerrarBaseDeDatos();
			return respuestas;
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}
	
	public void modificarRespuesta(RespuestaEncuesta respuesta){
		abrirBaseDeDatos();
		String Sql = "UPDATE TOMAENCUESTACABECERA SET ESTADO=1 WHERE NUMERO='" + respuesta.getId() + "'";
		db.execSQL(Sql);
		cerrarBaseDeDatos();
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
				Log.e("ControladorEncuesta","--Base de datos abierta--");
				return true;
			} 
			
		}
		return false;
	}

}
