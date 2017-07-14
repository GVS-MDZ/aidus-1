package com.gvs.controladores;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.gvs.idusapp.R;
import com.gvs.modelos.Cobranza;
import com.gvs.modelos.Comprobante;
import com.gvs.utilidades.BaseDeDatos;
import com.gvs.utilidades.Funciones;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ControladorComprobante {
	
	private double total=0;
	private Funciones funcion;
	private BaseDeDatos baseDeDatos;
	private SQLiteDatabase db;
	private Cursor cur;
	private Context context;
	
	public ControladorComprobante(Context contexto) {	
		this.context = contexto;
		this.funcion = new Funciones();
	}
	
    public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public List<Comprobante> buscarComprobantesPorClientes(int cliente)throws Exception{
		this.total=0;
		try{
			abrirBaseDeDatos();
			String sql = "SELECT * FROM COMPROBANTESVENTAS WHERE CODIGOCLIENTE=" + cliente +" ORDER BY FECHA,CLASE,TIPO,SUCURSAL,NUMERO";
		    cur = db.rawQuery(sql, null);
			List<Comprobante> lista=new ArrayList<Comprobante>();
			Comprobante  comprobante;
			if (cur.getCount()>0){		
				while (cur.moveToNext()){
					comprobante=new Comprobante();
					comprobante.setClase(cur.getString(cur.getColumnIndex("CLASE")));
					comprobante.setTipo(cur.getString(cur.getColumnIndex("TIPO")));
					comprobante.setSucursal(cur.getInt(cur.getColumnIndex("SUCURSAL")));
					comprobante.setNumero(cur.getInt(cur.getColumnIndex("NUMERO")));
					comprobante.setComprobante(cur.getString(cur.getColumnIndex("CLASE")) + " - " 
					+ cur.getString(cur.getColumnIndex("TIPO"))+ " - " 
				    + funcion.autocompletarNumero(String.valueOf(cur.getInt(cur.getColumnIndex("SUCURSAL"))),4) + " - " 
				    + funcion.autocompletarNumero(String.valueOf(cur.getInt(cur.getColumnIndex("NUMERO"))),8));
					comprobante.setFecha(funcion.dateToString_yyyymmdd_hhmm(cur.getLong(cur.getColumnIndex("FECHA"))));
					comprobante.setEstado(cur.getInt(cur.getColumnIndex("ESTADO")));
					if(cur.getInt(cur.getColumnIndex("ESTADO"))==0){
						comprobante.setIcono(R.drawable.amarillo);
					}else{
						comprobante.setIcono(R.drawable.verde);
					}
					if(cur.getString(cur.getColumnIndex("CLASE")).equals("FT") || cur.getString(cur.getColumnIndex("CLASE")).equals("CI") || cur.getString(cur.getColumnIndex("CLASE")).equals("ND")){
						if(comprobante.getEstado()==0){
							String SqlCobranza = "SELECT * FROM COBRANZA WHERE CLASE='"+comprobante.getClase()+"' AND TIPO='"+comprobante.getTipo()+"' AND SUCURSAL="+comprobante.getSucursal()+" AND NUMERO="+comprobante.getNumero()+" AND ESTADO=0";
							Cursor cur2 = db.rawQuery(SqlCobranza, null);
							double pagado=0;
							if (cur2.getColumnCount()>0){
								while (cur2.moveToNext()){
						     		pagado=pagado+ cur2.getDouble(cur2.getColumnIndex("IMPORTEPAGADO"));
								}
							}
							cur2.close();
							total+=cur.getDouble(cur.getColumnIndex("SALDO"))-pagado;
							double calculo=cur.getDouble(cur.getColumnIndex("SALDO"))-pagado;
							comprobante.setSaldo(funcion.formatDecimal(calculo,2));
						}else{
							total+=cur.getDouble(cur.getColumnIndex("SALDO"));	
							comprobante.setSaldo(funcion.formatDecimal((cur.getDouble(cur.getColumnIndex("SALDO"))),2));
						}
					}else{
						total-=cur.getDouble(cur.getColumnIndex("SALDO"));
						comprobante.setSaldo(funcion.formatDecimal((cur.getDouble(cur.getColumnIndex("SALDO"))),2));
					}
				    lista.add(comprobante);
				}
			}
			cur.close();
			cerrarBaseDeDatos();
		return lista;
		}catch(Exception ex){
			throw new Exception(ex.getMessage());
		}
	}
	
	public int buscarComprobantesPorPeriodo(int cliente,int periodo){
		try{
			abrirBaseDeDatos();
			Date hoy=new Date();
			Date antes=new Date();          
            Calendar calendario = Calendar.getInstance();
            calendario.setTime(hoy);
            calendario.add(Calendar.DATE, -periodo);
            antes = calendario.getTime();
			String sql = "SELECT * FROM COMPROBANTESVENTAS WHERE  CODIGOCLIENTE=" + cliente +" AND CLASE IN ('FT','ND','NC','CI') AND FECHA<"+hoy.getTime()+" AND FECHA>="+antes.getTime();
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
	
	public String buscarComprobantesMasAntiguo(int cliente,int periodo){
		try{
			abrirBaseDeDatos();
			String comprobante="";
			Date hoy=new Date();
			Date antes=new Date();          
            Calendar calendario = Calendar.getInstance();
            calendario.setTime(hoy);
            calendario.add(Calendar.DATE, -periodo);
            antes = calendario.getTime();
			String Sql = "SELECT * FROM COMPROBANTESVENTAS WHERE  CODIGOCLIENTE=" + cliente +" AND CLASE IN ('FT','ND','NC','CI') AND FECHA<"+hoy.getTime()+" AND FECHA>="+antes.getTime()+" ORDER BY FECHA ASC LIMIT 1";
			cur = db.rawQuery(Sql, null);
			if (cur.getColumnCount()>0){
				while (cur.moveToNext()){
					comprobante=cur.getString(cur.getColumnIndex("CLASE"))+"-"+cur.getString(cur.getColumnIndex("TIPO"))+"-"+funcion.autocompletarNumero(String.valueOf(cur.getInt(cur.getColumnIndex("SUCURSAL"))),4)+"-"+funcion.autocompletarNumero(String.valueOf(cur.getInt(cur.getColumnIndex("NUMERO"))),8);
				}
			}
			cur.close();
			cerrarBaseDeDatos();
			return comprobante;
		} catch (Exception e) {
			cur.close();
			cerrarBaseDeDatos();
			return "";
		}
	}

	public void modificarComprobante(double saldo, Comprobante comprobante){
		 abrirBaseDeDatos();
		 String sql = "UPDATE COMPROBANTESVENTAS SET ESTADO=1,SALDO="+saldo+" WHERE CLASE='" +comprobante.getClase() + "' AND TIPO='"+ comprobante.getTipo() +"' AND SUCURSAL="+ comprobante.getSucursal() +" AND NUMERO="+ comprobante.getNumero() +"";
 		 db.execSQL(sql);
 		 cerrarBaseDeDatos();
	}
	
	public void guardarVoucher(Cobranza cobranza){
		abrirBaseDeDatos();
		String sql="INSERT INTO COBRANZA (CLASE,TIPO,SUCURSAL,NUMERO,FECHA,CODIGOCLIENTE,CODIGOVENDEDOR,RECIBO,SALDO,IMPORTEPAGADO,ESTADO) " +
	     		 " VALUES('"+cobranza.getClase()+"','"+cobranza.getTipo()+"',"+cobranza.getSucursal()+","+cobranza.getNumero_comprobante()+","+cobranza.getFecha()+"," +
	     		 ""+cobranza.getCodigo_cliente()+","+cobranza.getCodigo_vendedor()+","+cobranza.getNumero_recibo()+","+cobranza.getSaldo()+"," +
	     		 ""+cobranza.getImporte_pagado()+","+cobranza.getEstado()+")";
	   db.execSQL(sql);
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
				Log.e("ControladorComprobante","--Base de datos abierta--");
				return true;
			} 
			
		}
		return false;
	}
}
