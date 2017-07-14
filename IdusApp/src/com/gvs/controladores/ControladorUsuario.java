package com.gvs.controladores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gvs.modelos.Usuario;
import com.gvs.utilidades.BaseDeDatos;
import com.gvs.utilidades.Funciones;

public class ControladorUsuario {
	
	private Funciones funcion;
	private BaseDeDatos baseDeDatos;
	private SQLiteDatabase db;
	private Cursor cur;
	private Context context;
	
	public ControladorUsuario(Context contexto) {
		this.context = contexto;
		this.funcion = new Funciones();
	}
	
	public Usuario validarUsuario(String usu, String pass) {
		abrirBaseDeDatos();
		String sql = "SELECT * FROM USUARIO WHERE NOMBRE='" + usu + "' AND PASS='" + pass + "'";
		cur = db.rawQuery(sql, null);
		if (cur.moveToNext()) {
			Usuario usuario = new Usuario();
			usuario.setCodigo(cur.getInt(cur.getColumnIndex("CODIGOVENDEDOR")));
			usuario.setEmpresa(cur.getInt(cur.getColumnIndex("CODIGOEMPRESA")));
			usuario.setTipo("vendedor");
			usuario.setSesion(cur.getInt(cur.getColumnIndex("SESION")));
			cur.close();
			cerrarBaseDeDatos();
			return usuario;
		} else {
			cur.close();
			cerrarBaseDeDatos();
			return null;
		}
	}
	
	public Usuario validarUsuarioAdministrador(String usu, String pass) {
		abrirBaseDeDatos();
		String sql = "SELECT * FROM USUARIO WHERE NOMBRE='" + usu + "' AND PASS='" + pass + "' AND CODIGO=1 ";
		cur = db.rawQuery(sql, null);
		if (cur.moveToNext()) {
			Usuario usuario = new Usuario();
			usuario.setCodigo(cur.getInt(cur.getColumnIndex("CODIGOVENDEDOR")));
			usuario.setEmpresa(cur.getInt(cur.getColumnIndex("CODIGOEMPRESA")));
			usuario.setSesion(cur.getInt(cur.getColumnIndex("SESION")));
			cur.close();
			cerrarBaseDeDatos();
			return usuario;
		} else {
			cur.close();
			cerrarBaseDeDatos();
			return null;
		}
	}
	
	public void iniciarSesion(String usu, String pass){
		abrirBaseDeDatos();
		String sql = "UPDATE USUARIO SET SESION=1 WHERE NOMBRE='" + usu + "' AND PASS='"+ pass +"'";
		db.execSQL(sql);
		cerrarBaseDeDatos();
	}
	
	public void guardarUsuario(){
		try{	
			abrirBaseDeDatos();
			String sql = "SELECT NOMBRE,PASS FROM USUARIO WHERE CODIGO=1";
			cur = db.rawQuery(sql, null);
			
			if (cur.moveToNext()){
				ContentValues cont = new ContentValues();
				String pass=funcion.getPassAdministrator();
				cont.put("PASS",pass);
				db.update("USUARIO", cont, "CODIGO=1",null);				
			} else {
				ContentValues cont = new ContentValues();
				cont.put("CODIGO", 1);
				cont.put("NOMBRE", "administrator");
				cont.put("PASS",funcion.getPassAdministrator());
				db.insert("USUARIO", null,cont);
			}
			
			} catch(Exception e) {
				e.printStackTrace();
				}
		cur.close();
		cerrarBaseDeDatos();
	 }

	public void modificarUsuario(Usuario usu ){
		abrirBaseDeDatos();
		ContentValues cont = new ContentValues();
		String sql = "SELECT * FROM USUARIO WHERE CODIGO='" + usu.getId()+ "'";
		cur = db.rawQuery(sql, null);
		if (cur.moveToNext()) {
			cont.put("NOMBRE", usu.getNombre());
			cont.put("PASS", usu.getContrasena());
			cont.put("CODIGOEMPRESA", usu.getEmpresa());
			cont.put("CODIGOVENDEDOR", usu.getCodigo());
			db.update("USUARIO", cont, "CODIGO=" + usu.getId() + "", null);
		} else {
			cont.put("CODIGO", usu.getId());
			cont.put("NOMBRE", usu.getNombre());
			cont.put("PASS", usu.getContrasena());
			cont.put("CODIGOEMPRESA", usu.getEmpresa());
			cont.put("CODIGOVENDEDOR", usu.getCodigo());
			db.insert("USUARIO", null, cont);
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
				Log.e("ControladorUsuario","--Base de datos abierta--");
				return true;
			} 
			
		}
		return false;
	}
}
