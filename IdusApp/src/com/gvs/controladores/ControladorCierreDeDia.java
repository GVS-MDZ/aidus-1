package com.gvs.controladores;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gvs.modelos.CierreDeDia;
import com.gvs.utilidades.BaseDeDatos;
import com.gvs.utilidades.Funciones;

public class ControladorCierreDeDia {
	
	private Funciones funcion;
	private int vendedor;
	private BaseDeDatos baseDeDatos;
	private SQLiteDatabase db;
	private Cursor cur;
	private Context context;
	
	public ControladorCierreDeDia(Context contexto, int vendedor){
		this.vendedor=vendedor;
		this.context = contexto;
		this.funcion = new Funciones();
	}

	public void guardarFinDeDia(int cantidad,int ventas,int noVentas,int noEnviados,double cobGral,double cobEfe)throws Exception{
		try {
			abrirBaseDeDatos();
			ContentValues cont = new ContentValues();
			Date fecha = new Date();
			cont.put("CODIGOVENDEDOR", vendedor);
			cont.put("FECHA",fecha.getTime());
			cont.put("CLIENTESDELDIA", cantidad);
			cont.put("CLIENTESATENDIDOS", ventas);
			cont.put("CLIENTESNOATENDIDOS", noVentas);
			cont.put("PEDIDOSSINENVIAR",noEnviados);
			cont.put("MOTIVOSSINENVIAR", 0);
			cont.put("COBGENERAL", cobGral);
			cont.put("COBEFECTIVA",cobEfe);
			cont.put("ESTADO", 0);	
			
			String sql = "SELECT * FROM FINESDEDIA WHERE CODIGOVENDEDOR=" + vendedor + " AND FECHA=" + fecha.getTime();
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				db.update("FINESDEDIA", cont, "CODIGOVENDEDOR=" + vendedor + " AND FECHA=" + fecha.getTime(), null);
			} else {
				db.insert("FINESDEDIA", null, cont);
			}
			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} 
		cur.close();
		cerrarBaseDeDatos();
	}
	
	public int devolverCantidadClientesDelDia(int dia) throws Exception{
		int cantidad=0;
		try {
			abrirBaseDeDatos();
			String sql="";
			if(dia>0){
				 sql = "SELECT COUNT(V.DIAS) FROM CLIENTES AS C " +
						"INNER JOIN VISITAS AS V ON C.CODIGO=V.CODIGOCLIENTE " +
						"WHERE C.CODIGOVENDEDOR=" + this.vendedor + 
						" AND SUBSTR(V.DIAS," + dia +",1)='" + dia +"' AND C.CODIGOVENDEDOR=" + vendedor + " AND HAB=1";		
			}else{
				 sql = "SELECT COUNT(V.DIAS) FROM CLIENTES AS C " +
						"INNER JOIN VISITAS AS V ON C.CODIGO=V.CODIGOCLIENTE " +
						"WHERE C.CODIGOVENDEDOR=" + vendedor+" AND V.DIAS IN('0000000','8888888','9999999') AND HAB=1";
			}	
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				cantidad = cur.getInt(0);
			} 
			cur.close();
			cerrarBaseDeDatos();
			return cantidad;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public int devolverCantidadVendidos(int dia) throws Exception{
		int cantidad=0;
		try {
			abrirBaseDeDatos();
			Date fecha = new Date();
			String hoy = funcion.dateToString_yyyymmdd(fecha.getTime());
			Date fechaINI = funcion.stringToDate(hoy);
			String sql="";
			if(dia>0){
				 sql = "SELECT COUNT(A.CODIGOCLIENTE),NUMERO FROM PEDIDOSCABECERA AS A "
							+ "INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE "
							+ "WHERE A.CODIGOVENDEDOR=" + vendedor +" AND A.DIA= "+dia+
							" AND A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + 
							" AND A.ESTADO NOT IN(2) AND SUBSTR(V.DIAS," + dia +",1)='" + dia +"'"
							+ "GROUP BY A.CODIGOCLIENTE";
				
		    }else{
		    	 sql = "SELECT COUNT(A.CODIGOCLIENTE),NUMERO FROM PEDIDOSCABECERA AS A "
							+ "INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE "
							+ "WHERE A.CODIGOVENDEDOR=" + vendedor +" AND A.DIA= "+dia+
							" AND A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + 
							" AND A.ESTADO NOT IN(2) AND V.DIAS IN('0000000','8888888','9999999') "
							+ "GROUP BY A.CODIGOCLIENTE";
		    }
			
			cur = db.rawQuery(sql, null);
			if (cur.getCount()>0) {
				while (cur.moveToNext()){
					cantidad++;
				}
			} 
			cur.close();
			cerrarBaseDeDatos();
			return cantidad;			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public int devolverCantidadVendidosFR(int dia) throws Exception{
		int cantidad=0;
		try {
			abrirBaseDeDatos();
			Date fecha = new Date();
			String hoy = funcion.dateToString_yyyymmdd(fecha.getTime());
			Date fechaINI = funcion.stringToDate(hoy);

			String sql = "SELECT COUNT(A.CODIGOCLIENTE),NUMERO FROM PEDIDOSCABECERA AS A "
					+ "INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE "
					+ "WHERE A.CODIGOVENDEDOR=" + vendedor + " " 
					+ "AND A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + " "  
					+ "AND A.ESTADO NOT IN(2) AND SUBSTR(V.DIAS," + dia +",1)='0' "
					+ "GROUP BY A.CODIGOCLIENTE";
			cur = db.rawQuery(sql, null);
			if (cur.getCount()>0) {
				while (cur.moveToNext()){
					cantidad++;
				}
			} 
			cur.close();
			cerrarBaseDeDatos();
			return cantidad;			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
		
	public int devolverCantidadPedidosTotal(int dia) throws Exception{
		int cantidad=0;
		try {
			abrirBaseDeDatos();
			Date fecha = new Date();
			String hoyTexto = funcion.dateToString_yyyymmdd(fecha.getTime());
			Date fechaINI = funcion.stringToDate(hoyTexto);
			String sql="";
			if(dia>0){
				sql = "SELECT A.NUMEROFINAL FROM PEDIDOSCABECERA AS A "
							+" WHERE A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() ;
				
		    }else{
		    	sql = "SELECT COUNT(A.NUMEROFINAL) FROM PEDIDOSCABECERA AS A "
							+" WHERE A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() ;
		    }
			
			cur = db.rawQuery(sql, null);
			if (cur.getCount()>0) {
				while (cur.moveToNext()){
					cantidad++;
				}
			} 
			cur.close();
			cerrarBaseDeDatos();
			return cantidad;			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
		
	public double devolverImportePedido(int dia) throws Exception{
		double cantidad=0;
		try {
			abrirBaseDeDatos();
			Date fecha = new Date();
			String hoyTexto = funcion.dateToString_yyyymmdd(fecha.getTime());
			Date fechaINI = funcion.stringToDate(hoyTexto);
			String Sql="";
			if(dia>0){
				 Sql = "SELECT SUM(B.CANTIDAD*B.PRECIO) FROM PEDIDOSCABECERA AS A "
							+ "INNER JOIN PEDIDOSCUERPO AS B ON A.NUMERO=B.NUMERO "
							+ "INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE "
							+ "WHERE A.CODIGOVENDEDOR=" + vendedor +
							" AND A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + 
							" AND A.ESTADO NOT IN(2) AND SUBSTR(V.DIAS," + dia +",1)='" + dia +"'";
			}else{
				 Sql = "SELECT SUM(B.CANTIDAD*B.PRECIO) FROM PEDIDOSCABECERA AS A "
							+ "INNER JOIN PEDIDOSCUERPO AS B ON A.NUMERO=B.NUMERO "
							+ "INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE "
							+ "WHERE A.CODIGOVENDEDOR=" + vendedor +
							" AND A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + 
							" AND A.ESTADO NOT IN(2) AND V.DIAS IN('0000000','8888888','9999999')";
			}
			
			cur = db.rawQuery(Sql, null);
			if (cur.moveToNext()) {
				cantidad = cur.getDouble(0);
			} else {
				cantidad=0;
			}
			cur.close();
			cerrarBaseDeDatos();
			return cantidad;			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public double devolverImportePedidoFR(int dia) throws Exception{
		double cantidad=0;
		try {
			abrirBaseDeDatos();
			Date fecha = new Date();
			String hoy = funcion.dateToString_yyyymmdd(fecha.getTime());
			Date fechaINI = funcion.stringToDate(hoy);

			String sql="SELECT SUM(B.CANTIDAD*B.PRECIO) FROM PEDIDOSCABECERA AS A "
					+ "INNER JOIN PEDIDOSCUERPO AS B ON A.NUMERO=B.NUMERO "
					+ "INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE "
					+ "WHERE A.CODIGOVENDEDOR=" + vendedor +
					" AND A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + 
					" AND A.ESTADO NOT IN(2) AND A.DIA !="+dia;
			cur = db.rawQuery(sql, null);
			if (cur.moveToNext()) {
				cantidad = cur.getDouble(0);
			}
			cur.close();
			cerrarBaseDeDatos();
			return cantidad;			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public int devolverCantidadNoVendidos(int dia) throws Exception{
		int cantidad=0;
		try {
			abrirBaseDeDatos();
			Date fecha = new Date();
			String hoyTexto = funcion.dateToString_yyyymmdd(fecha.getTime());
			Date fechaINI = funcion.stringToDate(hoyTexto);
			String sql="";
			if(dia>0){
				sql = "SELECT COUNT(N.CODIGOCLIENTE) FROM NOATENDIDOS AS N " +
						" INNER JOIN VISITAS AS V ON N.CODIGOCLIENTE=V.CODIGOCLIENTE" +
						" WHERE N.CODIGOVENDEDOR=" + vendedor +
						" AND N.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() +
						" AND SUBSTR(V.DIAS," + dia +",1)='" + dia +"'";
			}else{
				sql = "SELECT COUNT(N.CODIGOCLIENTE) FROM NOATENDIDOS AS N " +
						" INNER JOIN VISITAS AS V ON N.CODIGOCLIENTE=V.CODIGOCLIENTE" +
						" WHERE N.CODIGOVENDEDOR=" + vendedor +
						" AND N.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() +
						" AND V.DIAS IN('0000000','8888888','9999999')";
			}
			 
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				cantidad = cur.getInt(0);
			} 
			cur.close();
			cerrarBaseDeDatos();
			return cantidad;			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public int devolverCantidadNoEnviados(int dia) throws Exception{
		int cantidad=0;
		Date fecha = new Date();
		String hoy = funcion.dateToString_yyyymmdd(fecha.getTime());
		Date fechaINI = funcion.stringToDate(hoy);
		String sql="";
		try {
			abrirBaseDeDatos();
			if(dia>0){
				sql = "SELECT COUNT(A.CODIGOCLIENTE) FROM PEDIDOSCABECERA AS A "
						+"INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE "
						+"WHERE A.CODIGOVENDEDOR=" + vendedor +" AND A.DIA= "+dia+
						" AND A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + 
						" AND A.ESTADO NOT IN(1,2) AND SUBSTR(V.DIAS," + dia +",1)='" + dia +"'";
			}else{
				sql = "SELECT COUNT(A.CODIGOCLIENTE) FROM PEDIDOSCABECERA AS A "
						+"INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE "
						+"WHERE A.CODIGOVENDEDOR=" + vendedor +" AND A.DIA= "+dia+
						" AND A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + 
						" AND A.ESTADO NOT IN(1,2) AND V.DIAS IN('0000000','8888888','9999999')";
			}
		
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				cantidad = cur.getInt(0);
			}
			cur.close();
			cerrarBaseDeDatos();
			return cantidad;			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public boolean realizoCierreDiaAnterior(){
		try{
			abrirBaseDeDatos();
			Date fecha=new Date();
			String sql = "SELECT MAX(FECHA) FROM PEDIDOSCABECERA WHERE CODIGOVENDEDOR="+vendedor;
		    cur = db.rawQuery(sql, null);
			Long fecha_time=null;
			if (cur.moveToNext()){
				if (cur.getString(0)!=null){
					fecha_time= cur.getLong(0);
				}
			} 
			boolean ok=false;
			if(fecha_time!=null){
				String fecha_pedido = funcion.dateToString_yyyymmdd(fecha_time);
				Date fechaPedido = funcion.stringToDate(fecha_pedido);	
				String hoy = funcion.dateToString_yyyymmdd(fecha.getTime());
				Date fechaFin = funcion.stringToDate(hoy);
				
				if(!fechaPedido.equals(fechaFin)){
					String SqlFinDIA = "SELECT COUNT(*) FROM FINESDEDIA WHERE FECHA BETWEEN " + fecha_time + " AND " + fechaFin.getTime()+" ";
					Cursor cur2 = db.rawQuery(SqlFinDIA, null);
					cur2.moveToFirst();	
					int cantidad=cur2.getInt(0);
					cur2.close();				
					if(cantidad>0){
						ok= true;
					}
				}else{
					ok=true;
				}
				
		  }else{
			  ok=true;
		  }
			cur.close();
			cerrarBaseDeDatos();
		return ok;
		}catch(Exception ex){
			return false;
		}
	}
	
	public void realizarCierreDiaPendiente(){
		try {
			abrirBaseDeDatos();
			String SqlFecha = "SELECT MAX(FECHA) FROM PEDIDOSCABECERA WHERE CODIGOVENDEDOR="+vendedor;
			cur = db.rawQuery(SqlFecha, null);
			Long fecha_time=null;
			Date fecha=null;
			if (cur.moveToNext()){
				if (cur.getString(0)!=null){
					fecha_time= cur.getLong(0);
					fecha=new Date(fecha_time);
				}
			}
			
			String SqlDia = "SELECT DIA FROM PEDIDOSCABECERA WHERE CODIGOVENDEDOR="+vendedor+" AND FECHA >= " + fecha.getTime() +" GROUP BY DIA";
			Cursor cur2 = db.rawQuery(SqlDia, null);
			if(cur2.getCount()>0){
				while (cur2.moveToNext()){
					int dia=cur2.getInt(cur2.getColumnIndex("DIA"));	
					int cantidad = devolverCantidadClientesDelDiaPendiente(dia);
					int ventas = devolverCantidadVendidosPendiente(dia,fecha);
					int noVentas = devolverCantidadNoVendidosPendiente(dia,fecha);
					int noEnviados =devolverCantidadNoEnviadosPendiente(dia,fecha);
					double cobGral = (double)(ventas* 100)/(ventas + noVentas);
					double cobEfe = (double)(ventas*100)/cantidad;
				
					ContentValues cont = new ContentValues();		
					cont.put("CODIGOVENDEDOR", vendedor);
					cont.put("FECHA",fecha.getTime());
					cont.put("CLIENTESDELDIA", cantidad);
					cont.put("CLIENTESATENDIDOS", ventas);
					cont.put("CLIENTESNOATENDIDOS", noVentas);
					cont.put("PEDIDOSSINENVIAR",noEnviados);
					cont.put("MOTIVOSSINENVIAR", 0);
					cont.put("COBGENERAL", cobGral);
					cont.put("COBEFECTIVA",cobEfe);
					cont.put("ESTADO", 0);
					
				    db.insert("FINESDEDIA", null, cont);	
				}
				
			}
			cur2.close();
			cur.close();
			cerrarBaseDeDatos();
		} catch (Exception e) {
			e.getMessage();
		} 
			
			
				
	}
	
	public int devolverCantidadClientesDelDiaPendiente(int dia) throws Exception{
		int cantidad=0;
		try {
			abrirBaseDeDatos();
			String sql = "SELECT COUNT(V.DIAS) FROM CLIENTES AS C " +
					"INNER JOIN VISITAS AS V ON C.CODIGO=V.CODIGOCLIENTE " +
					"WHERE C.CODIGOVENDEDOR=" + this.vendedor + 
					" AND SUBSTR(V.DIAS," + dia +",1)='" + dia +"' AND C.CODIGOVENDEDOR=" + vendedor + " AND HAB=1";
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				cantidad = cur.getInt(0);
			} 
			cur.close();
			cerrarBaseDeDatos();
			return cantidad;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public int devolverCantidadVendidosPendiente(int dia,Date fecha_pendiente) throws Exception{
		int cantidad=0;
		try {
			Date fecha_actual = new Date();
			String hoy = funcion.dateToString_yyyymmdd(fecha_pendiente.getTime());
			Date fechaINI = funcion.stringToDate(hoy);
			abrirBaseDeDatos();
			String sql = "SELECT COUNT(A.CODIGOCLIENTE),NUMERO FROM PEDIDOSCABECERA AS A "
					+ "INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE "
					+ "WHERE A.CODIGOVENDEDOR=" + vendedor +" AND A.DIA= "+dia+
					" AND A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha_actual.getTime() + 
					" AND A.ESTADO NOT IN(2) AND SUBSTR(V.DIAS," + dia +",1)='" + dia +"'"
					+ "GROUP BY A.CODIGOCLIENTE";
			
			cur = db.rawQuery(sql, null);
			if (cur.getCount()>0) {
				while (cur.moveToNext()){
					cantidad++;
				}
			}
			cur.close();
			cerrarBaseDeDatos();
			return cantidad;			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	public int devolverCantidadNoVendidosPendiente(int dia,Date fecha_pendiente) throws Exception{
		int cantidad=0;
		try {
			
			Date fecha_actual = new Date();
			String hoy = funcion.dateToString_yyyymmdd(fecha_pendiente.getTime());
			Date fechaINI = funcion.stringToDate(hoy);
			abrirBaseDeDatos();
			String sql = "SELECT COUNT(N.CODIGOCLIENTE) FROM NOATENDIDOS AS N " +
					" INNER JOIN VISITAS AS V ON N.CODIGOCLIENTE=V.CODIGOCLIENTE" +
					" WHERE N.CODIGOVENDEDOR=" + vendedor +
					" AND N.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha_actual.getTime() +
					" AND SUBSTR(V.DIAS," + dia +",1)='" + dia +"'";
			
			cur = db.rawQuery(sql, null);
			if (cur.moveToNext()) {
				cantidad = cur.getInt(0);
			}
			cur.close();
			cerrarBaseDeDatos();
			return cantidad;			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public int devolverCantidadNoEnviadosPendiente(int dia,Date fecha_pendiente) throws Exception{
		int cantidad=0;
		try {
			Date fecha_actual = new Date();
			String hoy = funcion.dateToString_yyyymmdd(fecha_pendiente.getTime());
			Date fechaINI = funcion.stringToDate(hoy);
			abrirBaseDeDatos();
			String sql = "SELECT COUNT(A.CODIGOCLIENTE) FROM PEDIDOSCABECERA AS A "
						+ "INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE "
						+ "WHERE A.CODIGOVENDEDOR=" + vendedor +" AND A.DIA= "+dia+
						" AND A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha_actual.getTime() + 
						" AND A.ESTADO NOT IN(1,2) AND SUBSTR(V.DIAS," + dia +",1)='" + dia +"'";
			
			cur = db.rawQuery(sql, null);
			if (cur.moveToNext()) {
				cantidad = cur.getInt(0);
			}
			cur.close();
			cerrarBaseDeDatos();
			return cantidad;			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public boolean marcarParaEnviar() throws Exception {
		try {
			abrirBaseDeDatos();
			String sql = "UPDATE FINESDEDIA SET ESTADO=11 WHERE ESTADO=0";
			db.execSQL(sql);
			cerrarBaseDeDatos();
			return true;
			
		} catch (Exception e) {
			throw new Exception("ERROR:Al intentar marcar fines de día para ser enviados. " +
					"Póngase en contacto con la mesa de ayuda. Error: " + e.getMessage());
		}
	}
	
	public List<CierreDeDia> buscarNoAtendidosParaEnviar() throws Exception{
		try {
			abrirBaseDeDatos();
			List<CierreDeDia> cierres = new ArrayList<CierreDeDia>();
			String sql = "SELECT * FROM FINESDEDIA WHERE ESTADO=11";
			cur = db.rawQuery(sql, null);
			if (cur.getColumnCount()>0){
				CierreDeDia cierre;
				while (cur.moveToNext()){
					cierre = new CierreDeDia();
					cierre.setCodigoVendedor(cur.getInt(cur.getColumnIndex("CODIGOVENDEDOR")));
					cierre.setFecha(cur.getLong(cur.getColumnIndex("FECHA")));
					cierre.setClientesDelDia(cur.getInt(cur.getColumnIndex("CLIENTESDELDIA")));
					cierre.setClientesAtendidos(cur.getInt(cur.getColumnIndex("CLIENTESATENDIDOS")));
					cierre.setClientesNoAtendidos(cur.getInt(cur.getColumnIndex("CLIENTESNOATENDIDOS")));
					cierre.setPedidosSinEnviar(cur.getInt(cur.getColumnIndex("PEDIDOSSINENVIAR")));
					cierre.setMotivosSinEnviar(cur.getInt(cur.getColumnIndex("MOTIVOSSINENVIAR")));
					cierre.setCoberturaGeneral(cur.getDouble(cur.getColumnIndex("COBGENERAL")));
					cierre.setCoberturaEfectiva(cur.getDouble(cur.getColumnIndex("COBEFECTIVA")));
					cierre.setEstado(cur.getInt(cur.getColumnIndex("ESTADO")));
					cierres.add(cierre);
				}
			}
			cur.close();
			cerrarBaseDeDatos();
			return cierres;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public void modificarCierreDiaConector(CierreDeDia cierreDia){
		abrirBaseDeDatos();
		String sql = "SELECT * FROM FINESDEDIA WHERE CODIGOVENDEDOR=" + cierreDia.getCodigoVendedor() + " AND FECHA=" + cierreDia.getFecha();
		cur = db.rawQuery(sql, null);
		ContentValues cont = new ContentValues();
		if (cur.moveToNext()) {
			cont.put("ESTADO", 1);
			cont.put("FECHAENVIADO", new Date().getTime());
			cont.put("INTERNET",funcion.estadoRedes());
			db.update("FINESDEDIA", cont, "CODIGOVENDEDOR=" + cierreDia.getCodigoVendedor() + " AND FECHA=" + cierreDia.getFecha(),null);
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
				Log.e("ControladorCierreDeDia","--Base de datos abierta--");
				return true;
			} 
			
		}
		return false;
	}
}
