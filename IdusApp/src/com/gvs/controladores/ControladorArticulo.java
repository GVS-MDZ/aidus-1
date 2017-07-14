package com.gvs.controladores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gvs.modelos.Articulo;
import com.gvs.utilidades.BaseDeDatos;
import com.gvs.utilidades.Funciones;

public class ControladorArticulo {
	
	private Funciones funcion;
	private BaseDeDatos baseDeDatos;
	private SQLiteDatabase db;
	private Cursor cur;
	private Context context;
	public ControladorArticulo(Context contexto){
		this.context = contexto;
		this.funcion = new Funciones();
	}
	
	public Articulo buscarPorCodigo(String codigo){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM ARTICULOS WHERE UPPER(CODIGO)=UPPER('" + codigo + "') AND HAB=1";
			cur = db.rawQuery(sql, null);
			if (cur.moveToFirst()){
				Articulo art = new Articulo();
				art.setCodigo(cur.getString(cur.getColumnIndex("CODIGO")));
				art.setDetalle(cur.getString(cur.getColumnIndex("NOMBRE")));
				art.setPrecio(cur.getDouble(cur.getColumnIndex("PRECIO")));
				art.setStock(cur.getDouble(cur.getColumnIndex("EXISTENCIA")));
				art.setRubro(cur.getInt(cur.getColumnIndex("CODIGORUBRO")));
				art.setSubRubro(cur.getInt(cur.getColumnIndex("CODIGOSUBRUBRO")));
				art.setMultiplo(cur.getInt(cur.getColumnIndex("MULTIPLO")));
				art.setOferta(cur.getDouble(cur.getColumnIndex("PRECIOOFERTA")));
				cur.close();
				cerrarBaseDeDatos();
				return art;
			} else {
				cur.close();
				cerrarBaseDeDatos();
				return null;
			}
		} catch (Exception e) {
			cur.close();
			cerrarBaseDeDatos();
			return null;
		}
	}
	
	public int buscarCombo(int codigo){
		abrirBaseDeDatos();
			int cantidad= 0;
			String sql = "SELECT * FROM COMBOS WHERE CODIGO=" + codigo ;
		    cur = db.rawQuery(sql, null);
			if (cur.getCount()>0){
				while(cur.moveToNext()){
					cantidad= cur.getInt(cur.getColumnIndex("CANTIDAD"));
			     }
			} else {
				cantidad=0;
			}
			cur.close();
			cerrarBaseDeDatos();
			return cantidad;
	}
	
	public List<Map<String, String>> buscarPorDetalle(String detalle){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM ARTICULOS WHERE NOMBRE LIKE '%" + detalle + "%' AND HAB=1";
			cur = db.rawQuery(sql, null);
			if (cur.getCount()>0){
				List<Map<String, String>> lista = new ArrayList <Map<String, String>>();
				Map<String, String> articulo;
				while (cur.moveToNext()){
					articulo = new HashMap<String, String>();
					articulo.put("DATO05", cur.getString(cur.getColumnIndex("NOMBRE")));
					articulo.put("DATO06", cur.getString(cur.getColumnIndex("CODIGO")) + " - Stock: " + cur.getDouble(cur.getColumnIndex("EXISTENCIA")) + " - Precio: " + funcion.formatDecimal(cur.getDouble(cur.getColumnIndex("PRECIO")),2));
					lista.add(articulo);
				}
				cur.close();
				cerrarBaseDeDatos();
				return lista;
			} else {
				cur.close();
				cerrarBaseDeDatos();
				return null;
			}
		} catch (Exception e) {
			cur.close();
			cerrarBaseDeDatos();
			return null;
		}
	}
	
	public List<Map<String, String>> buscarPorRubroSubro(int rubro, int subrubro,String detalle){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT * FROM ARTICULOS WHERE CODIGORUBRO="+rubro+" AND CODIGOSUBRUBRO="+subrubro+" AND NOMBRE LIKE'" + detalle + "%' AND HAB=1";
			cur = db.rawQuery(sql, null);
			if (cur.getCount()>0){
				List<Map<String, String>> lista = new ArrayList <Map<String, String>>();
				Map<String, String> articulo;
				while (cur.moveToNext()){
					articulo = new HashMap<String, String>();
					articulo.put("DATO05", cur.getString(cur.getColumnIndex("NOMBRE")));
					articulo.put("DATO06", cur.getString(cur.getColumnIndex("CODIGO")) + " - Stock: " + cur.getDouble(cur.getColumnIndex("EXISTENCIA")) + " - Precio: " + funcion.formatDecimal(cur.getDouble(cur.getColumnIndex("PRECIO")),2));
					lista.add(articulo);
				}
				cur.close();
				cerrarBaseDeDatos();
				return lista;
			} else {
				cur.close();
				cerrarBaseDeDatos();
				return null;
			}
		} catch (Exception e) {
			cur.close();
			cerrarBaseDeDatos();
			return null;
		}
	}
		
	public List<Map<String,String>> buscarIndispensables(int codigoCliente){
		try {
			abrirBaseDeDatos();
			String sql = "SELECT A.CODIGO,A.NOMBRE,T.CANTIDADVTA,T.CANTIDADOBJ,T.ACTUALIZACION FROM ARTINDISPENSABLES AS T "
					+ "INNER JOIN ARTICULOS AS A ON T.CODIGOARTICULO=A.CODIGO "
					+ "WHERE A.HAB=1 AND T.CANTIDADVTA<T.CANTIDADOBJ AND T.CODIGOCLIENTE=" + codigoCliente;
			cur = db.rawQuery(sql, null);
			if (cur.getCount()>0){
				List<Map<String, String>> lista = new ArrayList <Map<String, String>>();
				Map<String, String> indispensable;
				while (cur.moveToNext()){
					indispensable = new HashMap<String, String>();
					indispensable.put("DATO10",cur.getString(cur.getColumnIndex("CODIGO")) +"-"+ cur.getString(cur.getColumnIndex("NOMBRE")));
					indispensable.put("DATO11","Venta: " + cur.getDouble(cur.getColumnIndex("CANTIDADVTA")) +
					" - Objetivo: " + cur.getDouble(cur.getColumnIndex("CANTIDADOBJ")));
					lista.add(indispensable);
				}
				cur.close();
				cerrarBaseDeDatos();
				return lista;
			} else {
				cur.close();
				cerrarBaseDeDatos();
				return null;
			}					
		} catch (Exception ex){
			cur.close();
			cerrarBaseDeDatos();
			return null;
		}
	}

	public boolean esArticuloIndispensable(int codigoCliente,String codigoArticulo){
		boolean existe=false;
		abrirBaseDeDatos();
		String sql = "SELECT * FROM ARTINDISPENSABLES WHERE CODIGOARTICULO='"+codigoArticulo +
				"' AND CODIGOCLIENTE=" + codigoCliente+" AND CANTIDADVTA<CANTIDADOBJ";
		cur = db.rawQuery(sql, null);
		if (cur.getCount()>0){		
			while (cur.moveToNext()){
				existe=true;
				break;
			}
		}	
		cur.close();
		cerrarBaseDeDatos();
		return existe;
	}
	
	public List<Map<String,String>> buscarInidispensableCargado(int codigoCliente){
		try {
			abrirBaseDeDatos();
			String Sql = "SELECT A.CODIGO,A.NOMBRE,T.CANTIDADVTA,T.CANTIDADOBJ,T.ACTUALIZACION FROM ARTINDISPENSABLES AS T "
					+ "INNER JOIN ARTICULOS AS A ON T.CODIGOARTICULO=A.CODIGO "
					+ "WHERE A.HAB=1 AND T.CANTIDADVTA>0 AND T.CODIGOCLIENTE=" + codigoCliente;
			cur = db.rawQuery(Sql, null);
			if (cur.getCount()>0){
				List<Map<String, String>> lista = new ArrayList <Map<String, String>>();
				Map<String, String> indispensable ;
				while (cur.moveToNext()){
					indispensable = new HashMap<String, String>();
					indispensable.put("DATO10",cur.getString(cur.getColumnIndex("CODIGO")) +"-"+ cur.getString(cur.getColumnIndex("NOMBRE")));
					indispensable.put("DATO11","Venta: " + cur.getDouble(cur.getColumnIndex("CANTIDADVTA")) +
					" - Objetivo: " + cur.getDouble(cur.getColumnIndex("CANTIDADOBJ")));
					lista.add(indispensable);
				}
				cur.close();
				cerrarBaseDeDatos();
				return lista;
			} else {
				cur.close();
				cerrarBaseDeDatos();
				return null;
			}
					
		} catch (Exception ex){
			cur.close();
			cerrarBaseDeDatos();
			return null;
		}
	}
	
	public List<Map<String,String>> buscarLanzamientos(int codigoCliente) {
		try {
			abrirBaseDeDatos();
			String sql = "SELECT A.CODIGO,A.NOMBRE,T.CANTIDADVTA,T.CANTIDADOBJ,T.ACTUALIZACION FROM ARTLANZAMIENTOS AS T "
					+ "INNER JOIN ARTICULOS AS A ON T.CODIGOARTICULO=A.CODIGO "
					+ "WHERE A.HAB=1 AND T.CANTIDADVTA<T.CANTIDADOBJ AND T.CODIGOCLIENTE=" + codigoCliente;
			cur = db.rawQuery(sql, null);
			
			if (cur.getCount()>0){
				List<Map<String, String>> list = new ArrayList <Map<String, String>>();
				Map<String, String> lanzamiento ;
				while (cur.moveToNext()){
					lanzamiento = new HashMap<String, String>();
					lanzamiento.put("DATO10",cur.getString(cur.getColumnIndex("CODIGO")) +"-"+ cur.getString(cur.getColumnIndex("NOMBRE")));
					lanzamiento.put("DATO11", "Venta: " + cur.getDouble(cur.getColumnIndex("CANTIDADVTA")) +
					" - Objetivo: " + cur.getDouble(cur.getColumnIndex("CANTIDADOBJ")));
					list.add(lanzamiento);
				}
				cur.close();
				cerrarBaseDeDatos();
				return list;
			} else {
				cur.close();
				cerrarBaseDeDatos();
				return null;
			}
					
		} catch (Exception ex){
			cur.close();
			cerrarBaseDeDatos();
			return null;
		}
	}

	public boolean esArticuloLanzamiento(int codigoCliente,String codigoArticulo){
		boolean existe=false;
		abrirBaseDeDatos();
		String sql = "SELECT * FROM ARTLANZAMIENTOS WHERE CODIGOARTICULO='"+codigoArticulo +
				"' AND CODIGOCLIENTE=" + codigoCliente+" AND CANTIDADVTA<CANTIDADOBJ";
		cur = db.rawQuery(sql, null);
		if (cur.getCount()>0){		
			while (cur.moveToNext()){
				existe=true;
				break;
			}
		}	
		cur.close();
		cerrarBaseDeDatos();
		return existe;
	}
	
	public List<Map<String,String>> buscarLanzamientosCargados(int codigoCliente) {
		try {
			abrirBaseDeDatos();
			String Sql = "SELECT A.CODIGO,A.NOMBRE,T.CANTIDADVTA,T.CANTIDADOBJ,T.ACTUALIZACION FROM ARTLANZAMIENTOS AS T "
					+ "INNER JOIN ARTICULOS AS A ON T.CODIGOARTICULO=A.CODIGO "
					+ "WHERE A.HAB=1 AND T.CANTIDADVTA>0 AND T.CODIGOCLIENTE=" + codigoCliente;
			cur = db.rawQuery(Sql, null);
			
			if (cur.getCount()>0){
				List<Map<String, String>> list = new ArrayList <Map<String, String>>();
				while (cur.moveToNext()){
					Map<String, String> datum = new HashMap<String, String>();
					datum.put("DATO10",cur.getString(cur.getColumnIndex("CODIGO")) +"-"+ cur.getString(cur.getColumnIndex("NOMBRE")));
					datum.put("DATO11", "Venta: " + cur.getDouble(cur.getColumnIndex("CANTIDADVTA")) +
					" - Objetivo: " + cur.getDouble(cur.getColumnIndex("CANTIDADOBJ")));
					list.add(datum);
				}
				cur.close();
				cerrarBaseDeDatos();
				return list;
			} else {
				cur.close();
				cerrarBaseDeDatos();
				return null;
			}
					
		} catch (Exception ex){
			cur.close();
			cerrarBaseDeDatos();
			return null;
		}
	}
	
	public List<Map<String,String>> buscarSugeridos() {
		try {
			abrirBaseDeDatos();
			String Sql = "SELECT A.CODIGO,A.NOMBRE,S.MENSAJE,S.VENCIMIENTO FROM SUGERIDOS AS S "
					+ "INNER JOIN ARTICULOS AS A ON S.CODIGOARTICULO=A.CODIGO "
					+ "WHERE A.HAB=1";
			cur = db.rawQuery(Sql, null);
			if (cur.getCount()>0){
				List<Map<String, String>> list = new ArrayList <Map<String, String>>();
				while (cur.moveToNext()){
					Map<String, String> datum = new HashMap<String, String>();
					datum.put("DATO10", cur.getString(cur.getColumnIndex("CODIGO")) + "-" +cur.getString(cur.getColumnIndex("NOMBRE")));
					datum.put("DATO11", cur.getString(cur.getColumnIndex("MENSAJE")) + " / " +
					funcion.dateToString_ddmmyyyy(cur.getLong(cur.getColumnIndex("VENCIMIENTO"))));
					list.add(datum);
				}
				cur.close();
				cerrarBaseDeDatos();
				return list;
			} else {
				cur.close();
				cerrarBaseDeDatos();
				return null;
			}
					
		} catch (Exception ex){
			cur.close();
			cerrarBaseDeDatos();
			return null;
		}
	}
	
	public boolean esArticuloSugerido(String codigoArticulo){
		boolean existe=false;
		abrirBaseDeDatos();
		String Sql = "SELECT * FROM SUGERIDOS WHERE CODIGOARTICULO='"+codigoArticulo+"'";
		cur = db.rawQuery(Sql, null);
		if (cur.getCount()>0){		
			while (cur.moveToNext()){
				existe=true;
				break;
			}
		}	
		cur.close();
		cerrarBaseDeDatos();
		return existe;
	}
	
	public String precioPorLista(int codigoCliente, Articulo articulo){
		double precioCalculado=0;
		double porcentaje=0;
		int codigoDeLista;
		abrirBaseDeDatos();
		String sql = "SELECT CODIGOLISTAPRECIO FROM CLIENTES WHERE CODIGO=" + codigoCliente;
		cur = db.rawQuery(sql, null);
		if (cur.moveToNext()){
			codigoDeLista = cur.getInt(cur.getColumnIndex("CODIGOLISTAPRECIO"));
			if (codigoDeLista>0) {
				sql = "SELECT PORCENTAJE FROM LISTASPRECIO WHERE HAB=1 AND CODIGORUBRO=" + articulo.getRubro() + " AND CODIGOSUBRUBRO="+ articulo.getSubRubro() + " AND CODIGO=" + codigoDeLista + " AND CODIGOARTICULO='0'";
				cur = db.rawQuery(sql, null);
				if (cur.moveToNext()) {
					porcentaje = cur.getDouble(cur.getColumnIndex("PORCENTAJE"));					
				} else {
					sql = "SELECT PORCENTAJE FROM LISTASPRECIO WHERE HAB=1 AND CODIGORUBRO=" + articulo.getRubro() + " AND CODIGO=" + codigoDeLista + " AND CODIGOSUBRUBRO=0 AND CODIGOARTICULO='0'";
					cur = db.rawQuery(sql, null);
					if (cur.moveToNext()) {
						porcentaje = cur.getDouble(cur.getColumnIndex("PORCENTAJE"));					
					} else {
						sql = "SELECT PORCENTAJE FROM LISTASPRECIO WHERE HAB=1 AND CODIGOARTICULO='" + articulo.getCodigo() + "' AND CODIGO=" + codigoDeLista + " AND CODIGORUBRO=0 AND CODIGOSUBRUBRO=0";
						cur = db.rawQuery(sql, null);
						if (cur.moveToNext()) {
							porcentaje = cur.getDouble(cur.getColumnIndex("PORCENTAJE"));					
						} else {
							porcentaje=0;
						}
					}
				}
                if (porcentaje<0 && precioCalculado==0){
                    porcentaje = porcentaje*-1;
                    double descuento=(porcentaje * articulo.getPrecio())/100;
                    precioCalculado = articulo.getPrecio() - descuento;
                }
                if (porcentaje>0 && precioCalculado==0){
                	double aumento=(porcentaje * articulo.getPrecio())/100;
                    precioCalculado = articulo.getPrecio() + aumento;
                }
                if (porcentaje==0){
                	precioCalculado = articulo.getPrecio();
                }
                cur.close();
				cerrarBaseDeDatos();
                return funcion.formatDecimal(precioCalculado, 2);
			} else {
				cur.close();
				cerrarBaseDeDatos();
				return funcion.formatDecimal(articulo.getPrecio(), 2);
			}
		} else {
			cur.close();
			cerrarBaseDeDatos();
			return funcion.formatDecimal(articulo.getPrecio(), 2);
		}
	}
	
	public boolean controlaStock(int codigoRubro, int codigoSubRubro) throws Exception{
		try {	
			abrirBaseDeDatos();
			String sql = "SELECT * FROM SUBRUBROS WHERE CODIGO=" + codigoSubRubro + " AND CODIGORUBRO=" + codigoRubro;
			cur = db.rawQuery(sql, null);
			if (cur.moveToNext()) {
				if (cur.getInt(cur.getColumnIndex("CONTROLASTOCK"))==1) {
					cur.close();
					cerrarBaseDeDatos();
					return true;
				} else {
					cur.close();
					cerrarBaseDeDatos();
					return false;
				}
			} else {
				cur.close();
				cerrarBaseDeDatos();
				return false;
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
	
	public void modificarArticulo(Articulo articulo){
		abrirBaseDeDatos();
		String Sql = "UPDATE ARTICULOS SET PRECIO="+articulo.getPrecio()+", EXISTENCIA="+articulo.getExistencia()+" ,HAB="+articulo.getHab()+" WHERE CODIGO='"+articulo.getCodigo()+"'";
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
				Log.e("ControladorArticulo","--Base de datos abierta--");
				return true;
			} 
			
		}
		return false;
	}

}
