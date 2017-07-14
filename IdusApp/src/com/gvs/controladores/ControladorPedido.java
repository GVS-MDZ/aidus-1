package com.gvs.controladores;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.gvs.idusapp.R;
import com.gvs.modelos.Articulo;
import com.gvs.modelos.Pedido;
import com.gvs.modelos.DetallePedido;
import com.gvs.utilidades.BaseDeDatos;
import com.gvs.utilidades.Funciones;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;

public class ControladorPedido {
	
	private Context context;
	private Funciones funcion;
	private BaseDeDatos baseDeDatos;
	private SQLiteDatabase db;
	private Cursor cur,cur2,cur3, curItems;
	private boolean llamadoExterno = true;
	
	public ControladorPedido(Context contexto) {
		
		this.funcion = new Funciones();
		this.context=contexto;	
		
	}
	
	
synchronized public List<Pedido> buscarPedidosXCliente(int dia,int codigoCliente, int codigoVendedor)throws Exception{
		try{
			abrirBaseDeDatos();
			Date fecha = new Date();
			Calendar calendario=Calendar.getInstance();
			calendario.setTime(fecha);
			calendario.add(Calendar.DAY_OF_YEAR,-30);
			Date fecha_mes=calendario.getTime();
			String hoyTexto = funcion.dateToString_yyyymmdd(fecha_mes.getTime());
			Date fechaINI = funcion.stringToDate(hoyTexto);
			String Sql = "SELECT A.CODIGOCLIENTE, NUMERO,NUMEROFINAL,FECHA,CANTIDADITEMS," +
					"(CASE WHEN ESTADO=0 THEN 'FINALIZADO' " +
					"WHEN ESTADO=10 THEN 'EDITADO' " +
					"WHEN ESTADO=11 THEN 'MARCADO' " + 
					"WHEN ESTADO=12 THEN 'ERROR' " +
					"WHEN ESTADO=13 THEN 'EMAIL' " +
					"WHEN ESTADO=1 THEN 'ENVIADO' " +
					"WHEN ESTADO=2 THEN 'ANULADO' END) AS ESTADO, C.NOMBRE "
					+ "FROM PEDIDOSCABECERA AS A INNER JOIN CLIENTES AS C ON A.CODIGOCLIENTE=C.CODIGO WHERE FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + " "
					+ "AND CODIGOCLIENTE=" + codigoCliente + " AND A.CODIGOVENDEDOR="+ codigoVendedor + " AND ESTADO<90 ORDER BY C.CODIGO ";
			cur = db.rawQuery(Sql, null);
			try {
				cur.moveToFirst();
			} catch (Exception e) {
				android.util.Log.e("CursorCTRLPEDIDOguardar ",e.getMessage());
			}
				
			
	
			List<Pedido> lista = new ArrayList <Pedido>();
			Pedido pedido;
			while (cur.isAfterLast() == false){
					pedido=new Pedido();
					String estado=cur.getString(cur.getColumnIndex("ESTADO"));
					pedido.setNumero_original(funcion.format(cur.getInt(cur.getColumnIndex("NUMERO")),10));
					pedido.setNumero_final(funcion.format(cur.getInt(cur.getColumnIndex("NUMEROFINAL")),10));
					pedido.setFecha(funcion.dateToString_yyyymmdd_hhmm(cur.getLong(cur.getColumnIndex("FECHA")))+" - "+estado);				
					pedido.setItem(funcion.format(cur.getInt(cur.getColumnIndex("CANTIDADITEMS")),2));
					pedido.setNombre_cliente(cur.getInt(cur.getColumnIndex("CODIGOCLIENTE"))+"-"+cur.getString(cur.getColumnIndex("NOMBRE")));
					if(estado.equals("FINALIZADO") || estado.equals("EDITADO") || estado.equals("MARCADO") ){
						pedido.setIcono(R.drawable.amarillo);
					}else if(estado.equals("ANULADO")){
						pedido.setIcono(R.drawable.naranja);
					}else if(estado.equals("ENVIADO")){
						pedido.setIcono(R.drawable.verde);
					}else if(estado.equals("EMAIL")){
						pedido.setIcono(R.drawable.azul);
					}else{
						pedido.setIcono(R.drawable.rojo);
					}
					llamadoExterno = false;
					List<DetallePedido> detalle=listarItemsDelPedidos(cur.getInt(cur.getColumnIndex("NUMERO")));
					double suma_articulos=0;
					if(detalle!=null){
						for(DetallePedido item:detalle){
							double pr=Double.parseDouble(item.getPrecio());
							double ca=Double.parseDouble(item.getCantidad());
							double importe_total_articulo=pr * ca;
							double descuento=importe_total_articulo *(Double.parseDouble(item.getDescuento())/100);
							 double total_articulo=importe_total_articulo-descuento;
							suma_articulos=suma_articulos+total_articulo;
							
						}
					}
					pedido.setTotal(Double.valueOf(suma_articulos));
					lista.add(pedido);
					cur.moveToNext();
			}
			
			return lista;	
		}catch(Exception ex){
			throw new Exception(ex.getMessage());
		}finally{
			cur.close();
			cerrarBaseDeDatos();
			}
	}
	
synchronized public List<Pedido> buscarPedidosXDia(int codigoVendedor,int dia)throws Exception{
		try{
			abrirBaseDeDatos();
		Date fecha = new Date();
		String hoyTexto = funcion.dateToString_yyyymmdd(fecha.getTime());
		Date fechaINI = funcion.stringToDate(hoyTexto);
		String Sql="";
		if(dia>0){
			Sql = "SELECT A.CODIGOCLIENTE,NUMERO,NUMEROFINAL,FECHA,CANTIDADITEMS," +
					"(CASE WHEN ESTADO=0 THEN 'FINALIZADO' " +
					"WHEN ESTADO=10 THEN 'EDITADO' " +
					"WHEN ESTADO=11 THEN 'MARCADO'"+ 
					"WHEN ESTADO=12 THEN 'ERROR' " +
					"WHEN ESTADO=13 THEN 'EMAIL' " +
					"WHEN ESTADO=1 THEN 'ENVIADO' " +
					"WHEN ESTADO=2 THEN 'ANULADO' END) AS ESTADO , C.NOMBRE"+
					" FROM PEDIDOSCABECERA AS A INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE INNER JOIN CLIENTES AS C ON V.CODIGOCLIENTE=C.CODIGO" +
					" WHERE A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + 
					" AND A.CODIGOVENDEDOR="+ codigoVendedor + " AND ESTADO<90 AND A.DIA= "+dia+" AND SUBSTR(V.DIAS," + dia +",1)='" + dia +"' ORDER BY C.CODIGO ";
		}else{
			Sql = "SELECT A.CODIGOCLIENTE,NUMERO,NUMEROFINAL,FECHA,CANTIDADITEMS," +
					"(CASE WHEN ESTADO=0 THEN 'FINALIZADO' " +
					"WHEN ESTADO=10 THEN 'EDITADO' " +
					"WHEN ESTADO=11 THEN 'MARCADO'"+ 
					"WHEN ESTADO=12 THEN 'ERROR' " +
					"WHEN ESTADO=13 THEN 'EMAIL' " +
					"WHEN ESTADO=1 THEN 'ENVIADO' " +
					"WHEN ESTADO=2 THEN 'ANULADO' END) AS ESTADO , C.NOMBRE"+
					" FROM PEDIDOSCABECERA AS A INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE INNER JOIN CLIENTES AS C ON V.CODIGOCLIENTE=C.CODIGO" +
					" WHERE A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + 
					" AND A.CODIGOVENDEDOR="+ codigoVendedor + " AND ESTADO<90  AND V.DIAS IN('0000000','8888888','9999999') ORDER BY C.CODIGO ";	
		}

		cur = db.rawQuery(Sql, null);
		List<Pedido> lista=null;
		if (cur.getCount()>0){
			lista = new ArrayList <Pedido>();
			cur.moveToFirst();
			while (cur.isAfterLast() == false){
				Pedido pedido=new Pedido();
				String estado=cur.getString(cur.getColumnIndex("ESTADO"));
				pedido.setNumero_original(funcion.format(cur.getInt(cur.getColumnIndex("NUMERO")),10));
				pedido.setNumero_final(funcion.format(cur.getInt(cur.getColumnIndex("NUMEROFINAL")),10));
				pedido.setFecha(funcion.dateToString_yyyymmdd_hhmm(cur.getLong(cur.getColumnIndex("FECHA")))+" - "+estado);				
				pedido.setItem(funcion.format(cur.getInt(cur.getColumnIndex("CANTIDADITEMS")),2));
				pedido.setNombre_cliente(cur.getInt(cur.getColumnIndex("CODIGOCLIENTE"))+"-"+cur.getString(cur.getColumnIndex("NOMBRE")));
				if(estado.equals("FINALIZADO") || estado.equals("EDITADO") || estado.equals("MARCADO")){
					pedido.setIcono(R.drawable.amarillo);
				}else if(estado.equals("ANULADO")){
					pedido.setIcono(R.drawable.naranja);
				}else if(estado.equals("ENVIADO")){
					pedido.setIcono(R.drawable.verde);
				}else if(estado.equals("EMAIL")){
					pedido.setIcono(R.drawable.azul);
				}
				else{
					pedido.setIcono(R.drawable.rojo);
				}
				llamadoExterno = false;
				List<DetallePedido> detalle=listarItemsDelPedidos(cur.getInt(cur.getColumnIndex("NUMERO")));
				double suma_articulos=0;
				if(detalle!=null){
					for(DetallePedido item:detalle){
						double pr=Double.parseDouble(item.getPrecio());
						double ca=Double.parseDouble(item.getCantidad());
						double importe_total_articulo=pr*ca;
						double descuento=importe_total_articulo*(Double.parseDouble(item.getDescuento())/100);
						double total_articulo=importe_total_articulo-descuento;
						suma_articulos=suma_articulos+total_articulo;
					}
				}
				pedido.setTotal(Double.valueOf(suma_articulos));
				lista.add(pedido);
				cur.moveToNext();
			}
		}
		
		return lista;
		}catch(Exception ex){
			throw new Exception(ex.getMessage());
		} finally {
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
synchronized public List<Pedido> buscarPedidosXDiaMail(int codigoVendedor,int dia)throws Exception{
		try{
			abrirBaseDeDatos();
		Date fecha = new Date();
		String hoyTexto = funcion.dateToString_yyyymmdd(fecha.getTime());
		Date fechaINI = funcion.stringToDate(hoyTexto);
		String Sql="";
		if(dia>0){
			Sql = "SELECT A.CODIGOCLIENTE,NUMERO,NUMEROFINAL,FECHA,CANTIDADITEMS," +
					"(CASE WHEN ESTADO=0 THEN 'FINALIZADO' " +
					"WHEN ESTADO=10 THEN 'EDITADO' " +
					"WHEN ESTADO=11 THEN 'MARCADO'"+ 
					"WHEN ESTADO=12 THEN 'ERROR' " +
					"WHEN ESTADO=13 THEN 'EMAIL' " +
					"WHEN ESTADO=1 THEN 'ENVIADO' " +
					"WHEN ESTADO=2 THEN 'ANULADO' END) AS ESTADO , C.NOMBRE"
					+ " FROM PEDIDOSCABECERA AS A INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE  INNER JOIN CLIENTES AS C ON V.CODIGOCLIENTE=C.CODIGO" +
					" WHERE A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + 
					" AND A.CODIGOVENDEDOR="+ codigoVendedor + " AND ESTADO IN(0,10,11,12,13) AND A.DIA= "+dia+" AND SUBSTR(V.DIAS," + dia +",1)='" + dia +"' ORDER BY C.CODIGO ";
		}else{
			Sql = "SELECT A.CODIGOCLIENTE,NUMERO,NUMEROFINAL,FECHA,CANTIDADITEMS," +
					"(CASE WHEN ESTADO=0 THEN 'FINALIZADO' " +
					"WHEN ESTADO=10 THEN 'EDITADO' " +
					"WHEN ESTADO=11 THEN 'MARCADO'"+ 
					"WHEN ESTADO=12 THEN 'ERROR' " +
					"WHEN ESTADO=13 THEN 'EMAIL' " +
					"WHEN ESTADO=1 THEN 'ENVIADO' " +
					"WHEN ESTADO=2 THEN 'ANULADO' END) AS ESTADO , C.NOMBRE"
					+ " FROM PEDIDOSCABECERA AS A INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE INNER JOIN CLIENTES AS C ON V.CODIGOCLIENTE=C.CODIGO" +
					" WHERE A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + 
					" AND A.CODIGOVENDEDOR="+ codigoVendedor + " AND ESTADO IN(0,10,11,12,13) AND A.DIA='0000000' ORDER BY C.CODIGO ";		
		}

		cur = db.rawQuery(Sql, null);
		List<Pedido> lista=null;
		if (cur.getCount()>0){
			lista = new ArrayList <Pedido>();
			cur.moveToFirst();
			while (cur.isAfterLast() == false){
				Pedido pedido=new Pedido();
				String estado=cur.getString(cur.getColumnIndex("ESTADO"));
				pedido.setNumero_original(funcion.format(cur.getInt(cur.getColumnIndex("NUMERO")),10));
				pedido.setNumero_final(funcion.format(cur.getInt(cur.getColumnIndex("NUMEROFINAL")),10));
				pedido.setFecha(funcion.dateToString_yyyymmdd_hhmm(cur.getLong(cur.getColumnIndex("FECHA")))+" - "+estado);				
				pedido.setItem(funcion.format(cur.getInt(cur.getColumnIndex("CANTIDADITEMS")),2));
				pedido.setNombre_cliente(cur.getInt(cur.getColumnIndex("CODIGOCLIENTE"))+"-"+cur.getString(cur.getColumnIndex("NOMBRE")));
				if(estado.equals("FINALIZADO") || estado.equals("EDITADO") || estado.equals("MARCADO")){
					pedido.setIcono(R.drawable.amarillo);
				}else if(estado.equals("ANULADO")){
					pedido.setIcono(R.drawable.naranja);
				}else if(estado.equals("ENVIADO")){
					pedido.setIcono(R.drawable.verde);
				}else if(estado.equals("EMAIL")){
					pedido.setIcono(R.drawable.azul);
				}
				else{
					pedido.setIcono(R.drawable.rojo);
				}	
				llamadoExterno = false;
				List<DetallePedido> detalle=listarItemsDelPedidos(cur.getInt(cur.getColumnIndex("NUMERO")));
				double suma_articulos=0;
				if(detalle!=null){
					for(DetallePedido item:detalle){
						double pr=Double.parseDouble(item.getPrecio());
						double ca=Double.parseDouble(item.getCantidad());
						double importe_total_articulo=pr*ca;
						double descuento=importe_total_articulo * (Double.parseDouble(item.getDescuento())/100);
						double total_articulo=importe_total_articulo-descuento;
						suma_articulos=suma_articulos+total_articulo;
					}
				}
				pedido.setTotal(Double.valueOf(suma_articulos));
				lista.add(pedido);
				cur.moveToNext();
			}	
		} 
		
		return lista;
		}catch(Exception ex){
			throw new Exception(ex.getMessage());
		} finally {
			cur.close();
			cerrarBaseDeDatos();}
	}
		
synchronized public List<Pedido> buscarPedidosPendientesDia(int codigoVendedor,int dia)throws Exception {
		try{
			abrirBaseDeDatos();
		Date fecha = new Date();
		String hoyTexto = funcion.dateToString_yyyymmdd(fecha.getTime());
		Date fechaINI = funcion.stringToDate(hoyTexto);
		String Sql="";
		if(dia>0){
		   Sql = "SELECT A.CODIGOCLIENTE,NUMERO,NUMEROFINAL,FECHA,CANTIDADITEMS," +
					"(CASE WHEN ESTADO=0 THEN 'FINALIZADO' " +
					"WHEN ESTADO=10 THEN 'EDITADO' " +
					"WHEN ESTADO=11 THEN 'MARCADO'"+ 
					"WHEN ESTADO=12 THEN 'ERROR' " +
					"WHEN ESTADO=1 THEN 'ENVIADO' " +
					"WHEN ESTADO=2 THEN 'ANULADO' END) AS ESTADO , C.NOMBRE FROM PEDIDOSCABECERA AS A INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE INNER JOIN CLIENTES AS C ON V.CODIGOCLIENTE=C.CODIGO" +
					" WHERE A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + 
					" AND A.DIA= "+dia+" AND A.CODIGOVENDEDOR="+ codigoVendedor + " AND ESTADO NOT IN(1,2,13) AND SUBSTR(V.DIAS," + dia +",1)='" + dia +"' ORDER BY C.CODIGO ";
		}else{
			 Sql = "SELECT A.CODIGOCLIENTE,NUMERO,NUMEROFINAL,FECHA,CANTIDADITEMS," +
						"(CASE WHEN ESTADO=0 THEN 'FINALIZADO' " +
						"WHEN ESTADO=10 THEN 'EDITADO' " +
						"WHEN ESTADO=11 THEN 'MARCADO'"+ 
						"WHEN ESTADO=12 THEN 'ERROR' " +
						"WHEN ESTADO=1 THEN 'ENVIADO' " +
						"WHEN ESTADO=2 THEN 'ANULADO' END) AS ESTADO , C.NOMBRE FROM PEDIDOSCABECERA AS A INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE INNER JOIN CLIENTES AS C ON V.CODIGOCLIENTE=C.CODIGO" +
						" WHERE A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + 
						" AND A.DIA= "+dia+" AND A.CODIGOVENDEDOR="+ codigoVendedor + " AND ESTADO NOT IN(1,2,13) AND V.DIAS IN('0000000','8888888','9999999') ORDER BY C.CODIGO ";
		}
		
		cur = db.rawQuery(Sql, null);
		List<Pedido> lista=null;
		if (cur.getCount()>0){
			 lista = new ArrayList <Pedido>();
			 cur.moveToFirst();
			while (cur.isAfterLast() == false){
				Pedido pedido=new Pedido();
				String estado=cur.getString(cur.getColumnIndex("ESTADO"));
				pedido.setNumero_original(funcion.format(cur.getInt(cur.getColumnIndex("NUMERO")),10));
				pedido.setNumero_final(funcion.format(cur.getInt(cur.getColumnIndex("NUMEROFINAL")),10));
				pedido.setFecha(funcion.dateToString_yyyymmdd_hhmm(cur.getLong(cur.getColumnIndex("FECHA")))+" - "+estado);				
				pedido.setItem(funcion.format(cur.getInt(cur.getColumnIndex("CANTIDADITEMS")),2));
				pedido.setNombre_cliente(cur.getInt(cur.getColumnIndex("CODIGOCLIENTE"))+"-"+cur.getString(cur.getColumnIndex("NOMBRE")));
				if(estado.equals("FINALIZADO") || estado.equals("EDITADO") || estado.equals("MARCADO")){
					pedido.setIcono(R.drawable.amarillo);
				}else if(estado.equals("ANULADO")){
					pedido.setIcono(R.drawable.naranja);
				}else if(estado.equals("ENVIADO")){
					pedido.setIcono(R.drawable.verde);
				}else{
					pedido.setIcono(R.drawable.rojo);
				}
				llamadoExterno = false;
				List<DetallePedido> detalle=listarItemsDelPedidos(cur.getInt(cur.getColumnIndex("NUMERO")));
				double suma_articulos=0;
				if(detalle!=null){
					for(DetallePedido item:detalle){
						double pr=Double.parseDouble(item.getPrecio());
						double ca=Double.parseDouble(item.getCantidad());
						double importe_total_articulo=pr*ca;
						double descuento=importe_total_articulo * (Double.parseDouble(item.getDescuento())/100);
						double total_articulo=importe_total_articulo-descuento;
						suma_articulos=suma_articulos+total_articulo;
					}
				}
				pedido.setTotal(Double.valueOf(suma_articulos));
				lista.add(pedido);
				cur.moveToNext();
			}
		}
		
		return lista;
		}catch(Exception ex){
			throw new Exception(ex.getMessage());
		}finally {
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
synchronized public boolean buscarSiYaFueCargadoElArticulo(long numeroPedido, String codigoArticulo) throws Exception{
		try {
			abrirBaseDeDatos();
			String Sql = "SELECT CODIGOARTICULO FROM PEDIDOSCUERPO "
					+ "WHERE NUMERO=" + numeroPedido + " AND CODIGOARTICULO='" + codigoArticulo + "' AND ESTADO<90";
			cur = db.rawQuery(Sql, null);
			if (cur.getCount()>0){
				
				return true;
			} else {
				
				return false;
			}
		} catch (Exception e) {
			throw new Exception("Error al buscar si está ingresado el artículo seleccionado: " + e.getMessage());
		} finally {
			cur.close();
			cerrarBaseDeDatos();
			}
	}
	
synchronized public int devolverCantidadVendidosXCliente(int dia, int codigoCliente) throws Exception{
		int cantidad=0;
		try {
			abrirBaseDeDatos();
			Date fecha = new Date();
			String hoyTexto = funcion.dateToString_yyyymmdd(fecha.getTime());
			Date fechaINI = funcion.stringToDate(hoyTexto);
			String Sql="";
			if(dia>0){
			 Sql = "SELECT COUNT(A.CODIGOCLIENTE),NUMERO FROM PEDIDOSCABECERA AS A "
					+ "INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE "
					+ "WHERE A.CODIGOCLIENTE=" + codigoCliente +" AND A.DIA= "+dia+
					" AND A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + 
					" AND A.ESTADO NOT IN(2) AND SUBSTR(V.DIAS," + dia +",1)='" + dia +"'"
					+" GROUP BY A.CODIGOCLIENTE";
			}else{
			 Sql = "SELECT COUNT(A.CODIGOCLIENTE),NUMERO FROM PEDIDOSCABECERA AS A "
						+ "INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE "
						+ "WHERE A.CODIGOCLIENTE=" + codigoCliente +" AND A.DIA= "+dia+
						" AND A.FECHA BETWEEN " + fechaINI.getTime() + " AND " + fecha.getTime() + 
						" AND A.ESTADO NOT IN(2) AND V.DIAS IN('0000000','8888888','9999999') "
						+" GROUP BY A.CODIGOCLIENTE";
			}
			cur = db.rawQuery(Sql, null);
			if (cur.getCount()>0) {
				cur.moveToFirst();
				while (cur.isAfterLast() == false){
					cantidad++;
					cur.moveToNext();
				}
			} else {
				cantidad=0;
			}
			
			return cantidad;			
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			cur.close();
			cerrarBaseDeDatos();
			}
	}
//////////////////////////////////////////////////			
synchronized public long numeroPedido(){
	long respuesta1 = 0;
	try {
			
			abrirBaseDeDatos();
			String sql = "SELECT MAX(NUMERO) FROM PEDIDOSCABECERA";
			cur = db.rawQuery(sql, null);
			cur.moveToFirst();
			if (cur.isAfterLast() == false) {
				if (cur.getString(0) != null) {
					respuesta1 = cur.getLong(0) + 1;
					
				} else {

					respuesta1= 1;
				}
			} else {

				respuesta1= 1;
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cur.close();
			cerrarBaseDeDatos();
		}
		return respuesta1;
	}

synchronized public int numeroItemsPorPedido(long numeroPedido){
		int cantidad_items = 0;
		try {
			if(llamadoExterno){
				abrirBaseDeDatos();
			}
			String sql = "SELECT * FROM PEDIDOSCUERPO WHERE NUMERO=" + numeroPedido + " AND ESTADO<90";
			curItems = db.rawQuery(sql, null);
			curItems.moveToFirst();
			String Sql2 = "SELECT * FROM COMBOS WHERE CODIGO=" + curItems.getInt(curItems.getColumnIndex("CODIGOARTICULO")) ;
			cur2 = db.rawQuery(Sql2, null);
			cantidad_items = 0;
			if (curItems.getCount()>0){
				curItems.moveToFirst();
				
			while (curItems.isAfterLast() == false){
				int cantidadCombo=0;
				int cantidad_acumulada=0;
				
				if (cur2.getCount()>0){
					cur2.moveToFirst();
					while(cur2.isAfterLast() == false){
						cantidadCombo= cur2.getInt(cur2.getColumnIndex("CANTIDAD"));
						cur2.moveToNext();
				     }
				} else{
					cantidad_acumulada=1;
				}
				
				int cantidad_suma=cantidad_acumulada+cantidadCombo;
				cantidad_items=cantidad_items+cantidad_suma;
				curItems.moveToNext();
			}
			}else{
				cantidad_items=0;
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			
			cur2.close();
			curItems.close();
			if(llamadoExterno){
						
				cerrarBaseDeDatos();
				
			}
			
		}
		llamadoExterno = true;
		return cantidad_items;
	}
	
synchronized public double totalPedido(long numeroPedido){
	double respuesta2 = 0;
	try {
			abrirBaseDeDatos();
			String sql = "SELECT SUM(CANTIDAD * PRECIO - PRECIO * CANTIDAD * DESCUENTO / 100) FROM PEDIDOSCUERPO WHERE NUMERO=" + numeroPedido + " AND ESTADO<90";
			cur = db.rawQuery(sql, null);
			cur.moveToFirst();
			if (cur.isAfterLast() == false){
				if (cur.getString(0)!=null){
					 respuesta2 = cur.getDouble(0);
								
				} 
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			cur.close();
			cerrarBaseDeDatos();
		}
		return respuesta2;
	}
///////////////	
synchronized public int grabarPedido(ContentValues contCab, ContentValues contCue) throws Exception{
		try {			
			abrirBaseDeDatos();
			String Sql = "";			
		
			Sql = "SELECT * FROM PEDIDOSCABECERA WHERE NUMERO=" + contCab.getAsLong("NUMERO");
			try {
				cur = db.rawQuery(Sql, null);
				
				cur.moveToFirst();
				if (cur.isAfterLast() == false) {
					
				int	lineas =	db.update("PEDIDOSCABECERA", contCab, "NUMERO=" + contCab.getAsLong("NUMERO"), null);
					android.util.Log.e("lineasCabecera",Integer.toString(lineas));
				} else {
				long lineas2 =	db.insert("PEDIDOSCABECERA", null, contCab);
				android.util.Log.e("lineasCabeceraInsert",Long.toString(lineas2));

				}
			} catch (Exception e) {
				android.util.Log.e("pedidoGrabarCur", e.getMessage());
			}
			
			
			Sql = "SELECT * FROM PEDIDOSCUERPO WHERE NUMERO=" + contCue.getAsLong("NUMERO") + " AND NUMEROITEM=" + contCue.getAsInteger("NUMEROITEM");
			try {
				cur2 = db.rawQuery(Sql, null);
				cur2.moveToFirst();
				if (cur2.isAfterLast() == false){
					db.update("PEDIDOSCUERPO", contCue, "NUMERO=" + contCue.getAsLong("NUMERO") + " AND NUMEROITEM=" + contCue.getAsInteger("NUMEROITEM"), null);
				} else {
				long lineas3 =	db.insert("PEDIDOSCUERPO", null, contCue);
				android.util.Log.e("PedidosCuerpoLineas",Long.toString(lineas3));
				}
			} catch (Exception e) {
				android.util.Log.e("pedidoGrabarCur2", e.getMessage());
			}
			
			int respuesta = 0;
			try {
				llamadoExterno = false;
				respuesta = numeroItemsPorPedido(contCab.getAsInteger("NUMERO"));
				
			} catch (Exception e) {
				android.util.Log.e("respuestaNumeroItems",e.getLocalizedMessage());
			}
		/*	cur2.close();
			cur.close();
			cerrarBaseDeDatos();*/
			return respuesta;
						
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally{
			cur2.close();
			cur.close();
			cerrarBaseDeDatos();
		}
	}
		
synchronized public boolean finalizarPedido(long numeroPedido){
		try {
			abrirBaseDeDatos();
			String Sql=""; 
			Sql = "SELECT COUNT(ESTADO) FROM PEDIDOSCUERPO WHERE NUMERO=" + numeroPedido + " AND ESTADO<90";
			cur = db.rawQuery(Sql, null);
			cur.moveToFirst();
			if (cur.isAfterLast() == false) {
				int cantidadItems = cur.getInt(0);
				if (cantidadItems>0) {
					Sql = "UPDATE PEDIDOSCABECERA SET CANTIDADITEMS=" + cantidadItems + " WHERE NUMERO=" + numeroPedido;
					db.execSQL(Sql);					
				}
				else{
					Sql = "UPDATE PEDIDOSCABECERA SET CANTIDADITEMS=" + cantidadItems + " WHERE NUMERO=" + numeroPedido;
					db.execSQL(Sql);	
				}
			}
			Sql = "UPDATE PEDIDOSCABECERA SET ESTADO=0 WHERE NUMERO=" + numeroPedido + " AND ESTADO<90";
			db.execSQL(Sql);
			Sql = "UPDATE PEDIDOSCUERPO SET ESTADO=0 WHERE NUMERO=" + numeroPedido + " AND ESTADO<90";
			db.execSQL(Sql);
			
			return true;
		} catch (SQLException e) {
			
			return false;
		}finally {
			cur.close();
			cerrarBaseDeDatos();
			}
	}

synchronized public boolean finalizarPedidoCompleto(long numeroPedido,String obs, long fecha){
		try {
			abrirBaseDeDatos();
			String obs1=obs.replace("Obs:","");
			String obs2=obs1.replace("%"," porciento ");
			String observacion=obs2.replace("#"," numeral ");
			String Sql=""; 
			Sql = "SELECT COUNT(ESTADO) FROM PEDIDOSCUERPO WHERE NUMERO=" + numeroPedido + " AND ESTADO<90";
			cur = db.rawQuery(Sql, null);
			cur.moveToFirst();
			if (cur.isAfterLast() == false) {
				int cantidadItems = cur.getInt(0);
				if (cantidadItems>0) {
					Sql = "UPDATE PEDIDOSCABECERA SET CANTIDADITEMS=" + cantidadItems + " WHERE NUMERO=" + numeroPedido;
					db.execSQL(Sql);					
				}
			}
			Sql = "UPDATE PEDIDOSCABECERA SET ESTADO=0,OBS='"+observacion+"',FECHAENTREGA="+fecha+",FECHAFIN="+new Date().getTime()+" WHERE NUMERO=" + numeroPedido + " AND ESTADO<90";
			db.execSQL(Sql);
			Sql = "UPDATE PEDIDOSCUERPO SET ESTADO=0 WHERE NUMERO=" + numeroPedido + " AND ESTADO<90";
			db.execSQL(Sql);
			
			return true;
		} catch (SQLException e) {
			
			return false;
		}finally {
			cur.close();
			cerrarBaseDeDatos();
			}
	}
	
synchronized public boolean anularPedido(long numeroPedido){
		try {
			abrirBaseDeDatos();
			String Sql=""; 
			Sql = "UPDATE PEDIDOSCABECERA SET ESTADO=2 WHERE NUMERO=" + numeroPedido + " AND ESTADO<90";
			db.execSQL(Sql);
			Sql = "UPDATE PEDIDOSCUERPO SET ESTADO=2 WHERE NUMERO=" + numeroPedido + " AND ESTADO<90";
			db.execSQL(Sql);
			
			
			return true;
		} catch (SQLException e) {
			
			
			return false;
		}finally {
			cerrarBaseDeDatos();
		}
	}

synchronized public boolean marcarParaEnviarPedidos(int dia) throws Exception{
		try {
			abrirBaseDeDatos();
			String Sql="";
			Sql = "UPDATE PEDIDOSCABECERA SET ESTADO=11 WHERE ESTADO=0 AND NUMEROFINAL=0 AND DIA= "+dia;
			db.execSQL(Sql);
			Sql = "UPDATE PEDIDOSCUERPO SET ESTADO=11 WHERE ESTADO=0";
			db.execSQL(Sql);
			
			return true;
		} catch (SQLException e) {
			throw new Exception("ERROR: Al intentar marcar el pedidos para ser enviados. " +
					"Póngase en contacto con la mesa de ayuda. Error: " + e.getMessage());
		} finally {
			cerrarBaseDeDatos();
		}
	}
	
synchronized public boolean marcarParaEnviarPedidos() throws Exception{
		try {
			abrirBaseDeDatos();
			String Sql="";
			Sql = "UPDATE PEDIDOSCABECERA SET ESTADO=11 WHERE ESTADO=0 AND NUMEROFINAL=0";
			db.execSQL(Sql);
			Sql = "UPDATE PEDIDOSCUERPO SET ESTADO=11 WHERE ESTADO=0";
			db.execSQL(Sql);
			
			return true;
		} catch (SQLException e) {
			throw new Exception("ERROR: Al intentar marcar el pedidos para ser enviados. " +
					"Pongase en contacto con la mesa de ayuda. Error: " + e.getMessage());
		}finally {
			cerrarBaseDeDatos();
		}
	}
	
synchronized public boolean marcarParaEnviarPedidosMail(int dia) throws Exception{
		try {
			abrirBaseDeDatos();
			String Sql="";
			Sql = "UPDATE PEDIDOSCABECERA SET ESTADO=13 WHERE ESTADO IN(0,12) AND NUMEROFINAL=0 AND DIA= "+dia;
			db.execSQL(Sql);
			Sql = "UPDATE PEDIDOSCUERPO SET ESTADO=13 WHERE ESTADO IN(0,12)";
			db.execSQL(Sql);
			
			return true;
		} catch (SQLException e) {
			throw new Exception("ERROR: Al intentar marcar el pedidos para ser enviados. " +
					"Póngase en contacto con la mesa de ayuda. Error: " + e.getMessage());
		}finally {
			cerrarBaseDeDatos();
		}
	}

synchronized public List<Pedido> buscarPedidosParaEnviar(int dia) throws Exception{
		try {			
			abrirBaseDeDatos();
			List<Pedido> pedidos = new ArrayList<Pedido>();
			String Sql ="";
			if(dia>0){
			 Sql = "SELECT * FROM PEDIDOSCABECERA AS A"
					+ " INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE  "
					+ " WHERE ESTADO IN(11,12) AND A.DIA= "+dia
					+ " AND SUBSTR(V.DIAS," + dia +",1)='" + dia +"'"
					+ " AND NUMEROFINAL=0 AND A.CODIGOCLIENTE>0 AND A.CODIGOVENDEDOR>0";
			}else{
				 Sql = "SELECT * FROM PEDIDOSCABECERA AS A"
							+ " INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE  "
							+ " WHERE ESTADO IN(11,12) AND A.DIA= "+dia
							+ " AND V.DIAS IN('0000000','8888888','9999999')"
							+ " AND NUMEROFINAL=0 AND A.CODIGOCLIENTE>0 AND A.CODIGOVENDEDOR>0";
			}
			
			cur = db.rawQuery(Sql, null);
			if (cur.getColumnCount()>0){
				cur.moveToFirst();
				while (cur.isAfterLast() == false){
					Pedido pedido = new Pedido();
					pedido.setNumero_original(String.valueOf(cur.getInt(cur.getColumnIndex("NUMERO"))));
					pedido.setFecha(String.valueOf(cur.getLong(cur.getColumnIndex("FECHA"))));
					pedido.setAvance(cur.getInt(cur.getColumnIndex("AVANCE")));
					pedido.setCodigoCliente(cur.getInt(cur.getColumnIndex("CODIGOCLIENTE")));
					pedido.setCodigoVendedor(cur.getInt(cur.getColumnIndex("CODIGOVENDEDOR")));
					pedido.setItem(String.valueOf(cur.getInt(cur.getColumnIndex("CANTIDADITEMS"))));
					pedido.setInternet(cur.getString(cur.getColumnIndex("INTERNET")));
					pedido.setLatitud(String.valueOf(cur.getDouble(cur.getColumnIndex("LATITUD"))));
					pedido.setLongitud(String.valueOf(cur.getDouble(cur.getColumnIndex("LONGITUD"))));
					pedido.setPrecision(String.valueOf(cur.getDouble(cur.getColumnIndex("PRECISION"))));
					pedido.setProveedor(cur.getString(cur.getColumnIndex("PROVEE")));
					pedido.setObservacion(cur.getString(cur.getColumnIndex("OBS")));
					pedido.setFechaEntrega(cur.getLong(cur.getColumnIndex("FECHAENTREGA")));
					pedido.setFechainicio(cur.getLong(cur.getColumnIndex("FECHAINICIO")));
					pedido.setFechafin(cur.getLong(cur.getColumnIndex("FECHAFIN")));
					pedidos.add(pedido);
					cur.moveToNext();
				}
			}
			
			return pedidos;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally {
			cur.close();
			cerrarBaseDeDatos();
			}
	}
	
synchronized public List<Pedido> buscarPedidosParaEnviar() throws Exception{
		try {			
			abrirBaseDeDatos();
			List<Pedido> pedidos = new ArrayList<Pedido>();
			String Sql ="";
			
			Sql = "SELECT * FROM PEDIDOSCABECERA AS A"
					+ " WHERE ESTADO IN(11,12)"
					+ " AND NUMEROFINAL=0 AND A.CODIGOCLIENTE>0 AND A.CODIGOVENDEDOR>0";
			
			cur = db.rawQuery(Sql, null);
			if (cur.getColumnCount()>0){
				cur.moveToFirst();
				while (cur.isAfterLast() == false){
					Pedido pedido = new Pedido();
					pedido.setNumero_original(String.valueOf(cur.getInt(cur.getColumnIndex("NUMERO"))));
					pedido.setFecha(String.valueOf(cur.getLong(cur.getColumnIndex("FECHA"))));
					pedido.setAvance(cur.getInt(cur.getColumnIndex("AVANCE")));
					pedido.setCodigoCliente(cur.getInt(cur.getColumnIndex("CODIGOCLIENTE")));
					pedido.setCodigoVendedor(cur.getInt(cur.getColumnIndex("CODIGOVENDEDOR")));
					pedido.setItem(String.valueOf(cur.getInt(cur.getColumnIndex("CANTIDADITEMS"))));
					pedido.setInternet(cur.getString(cur.getColumnIndex("INTERNET")));
					pedido.setLatitud(String.valueOf(cur.getDouble(cur.getColumnIndex("LATITUD"))));
					pedido.setLongitud(String.valueOf(cur.getDouble(cur.getColumnIndex("LONGITUD"))));
					pedido.setPrecision(String.valueOf(cur.getDouble(cur.getColumnIndex("PRECISION"))));
					pedido.setProveedor(cur.getString(cur.getColumnIndex("PROVEE")));
					pedido.setObservacion(cur.getString(cur.getColumnIndex("OBS")));
					pedido.setFechaEntrega(cur.getLong(cur.getColumnIndex("FECHAENTREGA")));
					pedido.setFechainicio(cur.getLong(cur.getColumnIndex("FECHAINICIO")));
					pedido.setFechafin(cur.getLong(cur.getColumnIndex("FECHAFIN")));
					pedidos.add(pedido);
					cur.moveToFirst();
				}
			}
			
			return pedidos;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally {
			cur.close();
			cerrarBaseDeDatos();
			}
	}
	
synchronized public List<Pedido> buscarPedidosParaEnviarMail(int dia) throws Exception{
		try {			
			abrirBaseDeDatos();
			List<Pedido> pedidos = new ArrayList<Pedido>();
			String Sql ="";
			if(dia>0){
				Sql = "SELECT * FROM PEDIDOSCABECERA AS A"
					+ " INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE  "
					+ " WHERE ESTADO IN(13,12) AND A.DIA= "+dia
					+ " AND SUBSTR(V.DIAS," + dia +",1)='" + dia +"'"
					+ " AND NUMEROFINAL=0 AND A.CODIGOCLIENTE>0 AND A.CODIGOVENDEDOR>0";
			}else{
				 Sql = "SELECT * FROM PEDIDOSCABECERA AS A"
							+ " INNER JOIN VISITAS AS V ON A.CODIGOCLIENTE=V.CODIGOCLIENTE  "
							+ " WHERE ESTADO IN(13,12) AND V.DIAS IN('0000000','8888888','9999999')"
							+ " AND NUMEROFINAL=0 AND A.CODIGOCLIENTE>0 AND A.CODIGOVENDEDOR>0";
			}
			cur = db.rawQuery(Sql, null);
			if (cur.getColumnCount()>0){
				cur.moveToFirst();
				while (cur.isAfterLast() == false){
					Pedido pedido = new Pedido();
					pedido.setNumero_original(String.valueOf(cur.getInt(cur.getColumnIndex("NUMERO"))));
					pedido.setFecha(String.valueOf(cur.getLong(cur.getColumnIndex("FECHA"))));
					pedido.setAvance(cur.getInt(cur.getColumnIndex("AVANCE")));
					pedido.setCodigoCliente(cur.getInt(cur.getColumnIndex("CODIGOCLIENTE")));
					pedido.setCodigoVendedor(cur.getInt(cur.getColumnIndex("CODIGOVENDEDOR")));
					pedido.setItem(String.valueOf(cur.getInt(cur.getColumnIndex("CANTIDADITEMS"))));
					pedido.setInternet(cur.getString(cur.getColumnIndex("INTERNET")));
					pedido.setLatitud(String.valueOf(cur.getDouble(cur.getColumnIndex("LATITUD"))));
					pedido.setLongitud(String.valueOf(cur.getDouble(cur.getColumnIndex("LONGITUD"))));
					pedido.setPrecision(String.valueOf(cur.getDouble(cur.getColumnIndex("PRECISION"))));
					pedido.setProveedor(cur.getString(cur.getColumnIndex("PROVEE")));
					pedido.setObservacion(cur.getString(cur.getColumnIndex("OBS")));
					pedido.setFechaEntrega(cur.getLong(cur.getColumnIndex("FECHAENTREGA")));
					pedidos.add(pedido);
					cur.moveToNext();
				}
			}
			
			return pedidos;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally {
			cur.close();
			cerrarBaseDeDatos();
			}
	}
	
synchronized public List<DetallePedido> buscarItemsPorPedido(int numero) throws Exception{
		try {
			abrirBaseDeDatos();
			List<DetallePedido> items = new ArrayList<DetallePedido>();
			String Sql = "SELECT * FROM PEDIDOSCUERPO WHERE NUMERO=" + numero+" AND (ESTADO=11 OR ESTADO=12)";
			cur = db.rawQuery(Sql, null);
			if (cur.getColumnCount()>0){
				cur.moveToFirst();
				while (cur.isAfterLast() == false){
					DetallePedido item = new DetallePedido();
					item.setNumero(cur.getInt(cur.getColumnIndex("NUMERO")));
					item.setCodigo(cur.getString(cur.getColumnIndex("CODIGOARTICULO")));
					item.setItem(String.valueOf(cur.getInt(cur.getColumnIndex("NUMEROITEM"))));
					item.setCantidad(String.valueOf(cur.getDouble(cur.getColumnIndex("CANTIDAD"))));
					item.setPrecio(String.valueOf(cur.getDouble(cur.getColumnIndex("PRECIO"))));
					item.setDescuento(String.valueOf(cur.getDouble(cur.getColumnIndex("DESCUENTO"))));
					items.add(item);
					cur.moveToNext();
				}
			}	
			
			return items;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally {
			cur.close();
			cerrarBaseDeDatos();
			}
	}
	
synchronized public List<DetallePedido> buscarItemsPorPedidoMail(int numero) throws Exception{
		try {
			abrirBaseDeDatos();
			List<DetallePedido> items = new ArrayList<DetallePedido>();
			String Sql = "SELECT * FROM PEDIDOSCUERPO WHERE NUMERO=" + numero+" AND ESTADO=13";
			cur = db.rawQuery(Sql, null);
			if (cur.getColumnCount()>0){
				cur.moveToFirst();
				while (cur.isAfterLast() == false){
					DetallePedido item = new DetallePedido();
					item.setNumero(cur.getInt(cur.getColumnIndex("NUMERO")));
					item.setCodigo(cur.getString(cur.getColumnIndex("CODIGOARTICULO")));
					item.setItem(String.valueOf(cur.getInt(cur.getColumnIndex("NUMEROITEM"))));
					item.setCantidad(String.valueOf(cur.getDouble(cur.getColumnIndex("CANTIDAD"))));
					item.setPrecio(String.valueOf(cur.getDouble(cur.getColumnIndex("PRECIO"))));
					item.setDescuento(String.valueOf(cur.getDouble(cur.getColumnIndex("DESCUENTO"))));
					items.add(item);
					cur.moveToNext();
				}
			}		
			
			return items;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally {
			cur.close();
			cerrarBaseDeDatos();
			}
	}
	
synchronized public boolean cambiarCantidadEnitem(double cantidad,double descuento,int cliente,DetallePedido item){
		try {
			abrirBaseDeDatos();
			ControladorArticulo busArt = new ControladorArticulo(context);
			Articulo art = busArt.buscarPorCodigo(item.getCodigo());
			double precio_art=0;
			if (art.getOferta()>0){
				precio_art=Double.parseDouble(funcion.formatDecimal(art.getOferta(), 2));
			} else {
				precio_art=Double.parseDouble(busArt.precioPorLista(cliente, art));
			}
			/*double precio_total_item = 0;
			precio_total_item = precio_art * cantidad;
			double precio_descuento= precio_total_item * (descuento/100);
			double precio=precio_total_item-precio_descuento;*/
			String Sql="UPDATE PEDIDOSCUERPO SET CANTIDAD=" + cantidad + ",DESCUENTO="+descuento+",PRECIO="+precio_art+" WHERE NUMERO=" + item.getNumero() + " AND NUMEROITEM=" + item.getItem(); 
			db.execSQL(Sql);
		
			return true;
		} catch (Exception e) {
			
			return false;
		}finally {
			cerrarBaseDeDatos();
		}
	}
	
synchronized public boolean borrarItemPedido(DetallePedido item){
		try {
			abrirBaseDeDatos();
			String Sql = "UPDATE PEDIDOSCUERPO SET ESTADO=99 WHERE NUMERO=" + item.getNumero() + " AND NUMEROITEM=" + item.getItem();
			db.execSQL(Sql);
			
			return true;
		} catch (Exception e) {	
			
			return false;
		}finally {
			cerrarBaseDeDatos();
		}
	}
	
synchronized public void sumarCantidadImpresindibles(int codigoCliente, String codigoArticulo, double cantidad) {
		try {
			abrirBaseDeDatos();
			String Sql = "UPDATE ARTINDISPENSABLES SET CANTIDADVTA=CANTIDADVTA+" + cantidad + 
					" WHERE CODIGOCLIENTE=" + codigoCliente + " AND CODIGOARTICULO='" + codigoArticulo + "'";
			db.execSQL(Sql);
		} catch (SQLException e) {
			e.getMessage();
		} finally {
			cerrarBaseDeDatos();
		}
		
	}
	
synchronized public void restarCantidadImpresindibles(int codigoCliente, String codigoArticulo, double cantidad) {
		try {
			abrirBaseDeDatos();
			String Sql = "UPDATE ARTINDISPENSABLES SET CANTIDADVTA=CANTIDADVTA-" + cantidad + 
					" WHERE CODIGOCLIENTE=" + codigoCliente + " AND CODIGOARTICULO='" + codigoArticulo + "'";
			db.execSQL(Sql);
		} catch (SQLException e) {
			e.getMessage();
		} finally {
			cerrarBaseDeDatos();
		}
		
	}

synchronized public void sumarCantidadLanzamientos(int codigoCliente, String codigoArticulo, double cantidad) {
		try {
			abrirBaseDeDatos();
			String Sql = "UPDATE ARTLANZAMIENTOS SET CANTIDADVTA=CANTIDADVTA+" + cantidad + 
					" WHERE CODIGOCLIENTE=" + codigoCliente + " AND CODIGOARTICULO='" + codigoArticulo + "'";
			db.execSQL(Sql);
		} catch (SQLException e) {
			e.getMessage();
		} finally {
			cerrarBaseDeDatos();
		}
		
	}
	
synchronized public void restarCantidadLanzamientos(int codigoCliente, String codigoArticulo, double cantidad) {
		try {
			abrirBaseDeDatos();
			String Sql = "UPDATE ARTLANZAMIENTOS SET CANTIDADVTA=CANTIDADVTA-" + cantidad + 
					" WHERE CODIGOCLIENTE=" + codigoCliente + " AND CODIGOARTICULO='" + codigoArticulo + "'";
			db.execSQL(Sql);
		} catch (SQLException e) {
			e.getMessage();
		} finally {
			cerrarBaseDeDatos();
		}
		
	}
	
synchronized public int buscarCantidadAcumulada(long numero){
			int cantidad = 0;
			try {
				abrirBaseDeDatos();
				String Sql = "SELECT * FROM PEDIDOSCUERPO WHERE NUMERO=" + numero+" AND ESTADO<90";
				cur = db.rawQuery(Sql, null);
				if (cur.getColumnCount()>0){
					cur.moveToFirst();
					while (cur.isAfterLast() == false){	
						String codigo=cur.getString(cur.getColumnIndex("CODIGOARTICULO"));
						llamadoExterno = false;
						int cant=buscarCombo(Integer.parseInt(codigo));
						if(cant>0){
							cantidad+=cant;
						}else{
							cantidad+=1;
						}
						cur.moveToNext();
					}
				}
			} catch (NumberFormatException e) {
				
				e.printStackTrace();
			}	finally {
				cur.close();
				cerrarBaseDeDatos();
			}
			return cantidad;
	}
	
synchronized public int buscarCombo(int codigo){
		int cantidad = 0;
		try {
			if(llamadoExterno){
				abrirBaseDeDatos();
			}
				
			String Sql = "SELECT * FROM COMBOS WHERE CODIGO=" + codigo ;
			cur3 = db.rawQuery(Sql, null);
			if (cur3.getCount()>0){
				cur3.moveToFirst();
				while(cur3.isAfterLast() == false){
					cantidad= cur3.getInt(cur3.getColumnIndex("CANTIDAD"));
					cur3.moveToNext();
			     }
			} else {
				cantidad=0;
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			cur3.close();
			if(llamadoExterno){
				cerrarBaseDeDatos();
			}
			llamadoExterno = true;
		}
		
		return cantidad;
	}

synchronized public List<DetallePedido>  listarItemsDelPedidos(long numero){
		List<DetallePedido> respuesta = null;
		try {
			if(llamadoExterno){
				abrirBaseDeDatos();
			}
			String Sql = "SELECT * FROM PEDIDOSCUERPO WHERE NUMERO=" + numero + " AND ESTADO<90 ORDER BY NUMEROITEM";
			 curItems = db.rawQuery(Sql, null);
			if (curItems.getCount()>0){
				List<DetallePedido>lista=new ArrayList<DetallePedido>();
				curItems.moveToFirst();
				while (curItems.isAfterLast() == false){
					ControladorArticulo busArt = new ControladorArticulo(context);
					String codigoArticulo = curItems.getString(curItems.getColumnIndex("CODIGOARTICULO")); 
					Articulo art = busArt.buscarPorCodigo(codigoArticulo);
					if (art!=null){
						DetallePedido detalle=new DetallePedido();
						detalle.setItem(funcion.format(curItems.getInt(curItems.getColumnIndex("NUMEROITEM")),3));
						detalle.setDescripcion(art.getDetalle());
						detalle.setCantidad(String.valueOf(curItems.getDouble(curItems.getColumnIndex("CANTIDAD"))));
						detalle.setDescuento(String.valueOf(curItems.getDouble(curItems.getColumnIndex("DESCUENTO"))));
						if(curItems.getDouble(curItems.getColumnIndex("DESCUENTO"))<10){
							detalle.setColor(Color.parseColor("#008000"));
						}else if(curItems.getDouble(curItems.getColumnIndex("DESCUENTO"))>=10 && curItems.getDouble(curItems.getColumnIndex("DESCUENTO"))<=22){
							detalle.setColor(Color.parseColor("#FF8C00"));
						}else{
							detalle.setColor(Color.RED);
						}
						detalle.setPrecio(funcion.formatDecimal(curItems.getDouble(curItems.getColumnIndex("PRECIO")),4));
						
						lista.add(detalle);
					}
					curItems.moveToNext();
				}
				
				respuesta = lista;
			} else {
				
				respuesta =  null;
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} 
		finally {
			curItems.close();
			if(llamadoExterno){				
					cerrarBaseDeDatos();
				}
		}
		llamadoExterno = true;
		return respuesta;
		
	}
	
synchronized public DetallePedido buscarItem(long numero, int item){
		DetallePedido respuesta = null;
		try {
			abrirBaseDeDatos();
			String Sql = "SELECT * FROM PEDIDOSCUERPO WHERE NUMERO=" + numero + " AND NUMEROITEM=" + item;
			cur = db.rawQuery(Sql, null);
			cur.moveToFirst();
			if (cur.isAfterLast() == false){
				
				DetallePedido detalle = new DetallePedido();
				detalle.setNumero(cur.getLong(cur.getColumnIndex("NUMERO")));
				detalle.setCodigo(cur.getString(cur.getColumnIndex("CODIGOARTICULO")));
				detalle.setCantidad(String.valueOf(cur.getDouble(cur.getColumnIndex("CANTIDAD"))));
				detalle.setDescuento((String.valueOf(cur.getDouble(cur.getColumnIndex("DESCUENTO")))));
				detalle.setPrecio(String.valueOf(cur.getDouble(cur.getColumnIndex("PRECIO"))));
				detalle.setItem(String.valueOf(cur.getInt(cur.getColumnIndex("NUMEROITEM"))));
				detalle.setEstado(cur.getInt(cur.getColumnIndex("ESTADO")));
				
				respuesta = detalle;
			} else {
				
				respuesta = null;
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			cur.close();
			cerrarBaseDeDatos();
		}
		return respuesta;
				
	}
	
synchronized public void pedidoEnviadoCorrectamente(int numero_pedido){
		try {
			abrirBaseDeDatos();
			String Sql = "UPDATE PEDIDOSCABECERA SET ESTADO=1 WHERE (ESTADO=11 OR ESTADO=12) AND NUMERO=" +numero_pedido;
			db.execSQL(Sql);
		} catch (SQLException e) {
			
			e.printStackTrace();
		} finally {
			cerrarBaseDeDatos();
		}
		
	}
	
synchronized public void pedidoConErrorAlEnvio(int numero_pedido){
		try {
			abrirBaseDeDatos();
			String Sql = "UPDATE PEDIDOSCABECERA SET ESTADO=12 WHERE ESTADO=11 AND NUMERO=" +  numero_pedido;
			db.execSQL(Sql);
			Sql = "UPDATE PEDIDOSCUERPO SET ESTADO=12 WHERE ESTADO=11 AND NUMERO=" +  numero_pedido;
			db.execSQL(Sql);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}	finally {
			cerrarBaseDeDatos();
		}
		
	}
	
synchronized public void eliminarItem(long numeroPedido){
		try {
			abrirBaseDeDatos();
			String Sql=""; 
			Sql = "SELECT COUNT(ESTADO) FROM PEDIDOSCUERPO WHERE NUMERO=" + numeroPedido + " AND ESTADO<90";
			cur = db.rawQuery(Sql, null);
			cur.moveToFirst();
			if (cur.isAfterLast() == false) {
				int cantidadItems = cur.getInt(0);
				if (cantidadItems>0) {
					Sql = "UPDATE PEDIDOSCABECERA SET CANTIDADITEMS=" + cantidadItems + " WHERE NUMERO=" + numeroPedido;
					db.execSQL(Sql);
				}else{
					Sql = "UPDATE PEDIDOSCABECERA SET CANTIDADITEMS=" + cantidadItems + " WHERE NUMERO=" + numeroPedido;
					db.execSQL(Sql);
				}
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		} finally {
			cur.close();
			cerrarBaseDeDatos();
		}
		
	}
	
synchronized public Pedido buscarDetallePedido(long numero_pedido)throws Exception{
		try{
			abrirBaseDeDatos();
			String Sql = "SELECT * FROM PEDIDOSCABECERA WHERE NUMERO="+numero_pedido;
			cur = db.rawQuery(Sql, null);
			Pedido pedido = null;
			cur.moveToFirst();
			if (cur.isAfterLast() == false) {
				pedido = new Pedido();
				pedido.setNumero_original(String.valueOf(cur.getInt(cur.getColumnIndex("NUMERO"))));
				pedido.setNumero_final(String.valueOf(cur.getInt(cur.getColumnIndex("NUMEROFINAL"))));
				pedido.setFecha(String.valueOf(cur.getLong(cur.getColumnIndex("FECHA"))));
				pedido.setAvance(cur.getInt(cur.getColumnIndex("AVANCE")));
				pedido.setCodigoCliente(cur.getInt(cur.getColumnIndex("CODIGOCLIENTE")));
				pedido.setCodigoVendedor(cur.getInt(cur.getColumnIndex("CODIGOVENDEDOR")));
				pedido.setItem(String.valueOf(cur.getInt(cur.getColumnIndex("CANTIDADITEMS"))));
				pedido.setInternet(cur.getString(cur.getColumnIndex("INTERNET")));
				pedido.setLatitud(String.valueOf(cur.getDouble(cur.getColumnIndex("LATITUD"))));
				pedido.setLongitud(String.valueOf(cur.getDouble(cur.getColumnIndex("LONGITUD"))));
				pedido.setPrecision(String.valueOf(cur.getDouble(cur.getColumnIndex("PRECISION"))));
				pedido.setProveedor(cur.getString(cur.getColumnIndex("PROVEE")));
				pedido.setObservacion(cur.getString(cur.getColumnIndex("OBS")));
				pedido.setFechaEntrega(cur.getLong(cur.getColumnIndex("FECHAENTREGA")));
				pedido.setEstado(cur.getInt(cur.getColumnIndex("ESTADO")));
		    }
			
			return pedido;
		}catch(Exception ex){
			throw new Exception(ex.getMessage());
		}finally{
			cur.close();
			cerrarBaseDeDatos();
		}
	}
	
synchronized public void modificarPedido(Pedido pedido){
		try {
			abrirBaseDeDatos();
			ContentValues cont = new ContentValues();
			cont.clear();
			cont.put("NUMEROFINAL", Integer.parseInt(pedido.getNumero_final()));
			cont.put("FECHAENVIADO", Long.valueOf(pedido.getFecha()));
			cont.put("ESTADO",1);
			db.update("PEDIDOSCABECERA", cont, "NUMERO=" + Integer.valueOf(pedido.getNumero_original()) + " AND (ESTADO=12 OR ESTADO=11)", null);
			cont.clear();
			cont.put("FECHAENVIADO", Long.valueOf(pedido.getFecha()));
			cont.put("ESTADO",1);
			db.update("PEDIDOSCUERPO", cont, "NUMERO=" + Integer.valueOf(pedido.getNumero_original()) + " AND (ESTADO=12 OR ESTADO=11)", null);
		} catch (NumberFormatException e) {
			
			e.printStackTrace();
		} finally {
			cerrarBaseDeDatos();
		}
		
	
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
			Log.e("ControladorPedido","--Base de datos abierta--");
			return true;
		} 
		
	}
	return false;
}

}
