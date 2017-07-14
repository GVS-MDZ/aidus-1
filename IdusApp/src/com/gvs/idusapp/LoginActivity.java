package com.gvs.idusapp;

import java.util.Date;
import com.gvs.conectores.ConectorGeneral;
import com.gvs.conectores.ConectorUsuario;
import com.gvs.controladores.ControladorConfiguracion;
import com.gvs.controladores.ControladorInforme;
import com.gvs.controladores.ControladorSincronizacion;
import com.gvs.controladores.ControladorUsuario;
import com.gvs.controladores.ControladorCierreDeDia;
import com.gvs.modelos.Configuracion;
import com.gvs.modelos.Log;
import com.gvs.modelos.Usuario;
import com.gvs.utilidades.Funciones;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class LoginActivity extends Activity {
	
	private Button btnLogin;
	private EditText txt_usuario, txt_contrasena;
	private ProgressBar cargando;
	private Configuracion configuracion;
	private Funciones funcion;
	private ConectorGeneral conectorUsuario;
	private ControladorUsuario controladorUsuario;
	private ControladorSincronizacion controladorSincronizador;
	private ControladorCierreDeDia controladorFinDeDia;
	private Bundle bun;
	private ControladorConfiguracion controladorConfiguracion;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		bun = new Bundle();
		handler = new Handler();
		funcion=new Funciones(this);
		btnLogin = (Button) findViewById(R.id.btn_login);
		txt_usuario = (EditText) findViewById(R.id.txt_usuario);
		txt_contrasena = (EditText) findViewById(R.id.txt_contrasena);
		cargando=(ProgressBar)findViewById(R.id.cargando);
		
		cargando.getIndeterminateDrawable().setColorFilter(Color.parseColor("#a085bd"), android.graphics.PorterDuff.Mode.MULTIPLY);
		configuracion = new Configuracion();
		controladorUsuario = new ControladorUsuario(this);
		controladorSincronizador = new ControladorSincronizacion(this);
		controladorConfiguracion=new ControladorConfiguracion(this);
		txt_usuario.getBackground().setColorFilter(Color.parseColor("#a085bd"), PorterDuff.Mode.SRC_ATOP);
		txt_contrasena.getBackground().setColorFilter(Color.parseColor("#a085bd"), PorterDuff.Mode.SRC_ATOP);
		
		btnLogin.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				configuracion = controladorConfiguracion.buscarConfiguracion();
				conectorUsuario = new ConectorUsuario(configuracion,txt_usuario.getText().toString(), txt_contrasena.getText().toString(), LoginActivity.this);
				try {	
					if (funcion.conexionInternet()) {
						cargando.setVisibility(1);
						Thread thread = new Thread() {
							public void run() {
								handler.post(new Runnable() {
									@Override
									public void run() {
										try{			
											if (conectorUsuario.correr(LoginActivity.this)) {
												Usuario usuario = controladorUsuario.validarUsuario(txt_usuario.getText().toString(),txt_contrasena.getText().toString());
												if (usuario != null) {
													ControladorInforme controlador_log=new ControladorInforme(LoginActivity.this);
													Log log =new Log();
													log.setDescripcion("LOGIN");
													log.setTipo(1);
													log.setFecha(new Date().getTime());
													log.setEstado(0);
													log.setVendedor(usuario.getCodigo());
													controlador_log.guardarLog(log);
													
													controladorFinDeDia=new ControladorCierreDeDia(LoginActivity.this, usuario.getCodigo());
													boolean realizo_cierre=controladorFinDeDia.realizoCierreDiaAnterior();
													if(realizo_cierre){		
														controladorUsuario.iniciarSesion(txt_usuario.getText().toString(),txt_contrasena.getText().toString());
														bun.putInt("EMPRESA", usuario.getEmpresa());
														bun.putInt("VENDEDOR", usuario.getCodigo());								
														if (controladorSincronizador.realizoSincronizacionGeneral("T",usuario.getEmpresa(),usuario.getCodigo())) {	
															Intent intent = new Intent(getApplicationContext(),MenuDiaActivity.class);
															intent.putExtras(bun);
															startActivity(intent);
															finish();
														} else {
															Intent intent = new Intent(getApplicationContext(),SincronizadorActivity.class);
															bun.putString("SINCRONIZACION", "INICIO");
															intent.putExtras(bun);
															startActivity(intent);
															finish();
														}
												  }else{
													  cargando.setVisibility(View.INVISIBLE);
													  guardarFinDeDia(usuario);
												  }
												} else {
													cargando.setVisibility(View.INVISIBLE);
													funcion.MostrarMensajeAceptar(LoginActivity.this,"Acceso Denegado","El usuario o contraseña es incorrecta.");
												}
											} else {
												cargando.setVisibility(View.INVISIBLE);
												Usuario usuario = controladorUsuario.validarUsuario(txt_usuario.getText().toString(),txt_contrasena.getText().toString());
												if (usuario != null) {
													mensaje("Sin Conexión al Servidor","La información puede no estar actualizada, y los pedidos no se entregaron. \n"
																	+ "Esta acción es de su entera responsabilidad \n"
																	+ "¿Desea continuar?", usuario);
												} else {
													funcion.MostrarMensajeAceptar(LoginActivity.this,"Acceso Denegado","El usuario o contraseña es incorrecta.");
												}
											}
										}catch(Exception ex){
											cargando.setVisibility(View.INVISIBLE);
											Usuario usuario = controladorUsuario.validarUsuario(txt_usuario.getText().toString(), txt_contrasena.getText().toString());
											if (usuario != null) {
												mensaje("Sin Conexión al Servidor","La información puede no estar actualizada, y los pedidos no se entregaron. \n"
														+ "Esta acción es de su entera responsabilidad \n"
														+ "¿Desea continuar?", usuario);
											} else {
												funcion.MostrarMensajeAceptar(LoginActivity.this,"Acceso Denegado","El usuario o contraseña es incorrecta.");
											}
										}
									}
								});
							}
						};
						thread.start();
						
					} else {
						Usuario usuario = controladorUsuario.validarUsuario(txt_usuario.getText().toString(), txt_contrasena.getText().toString());
						if (usuario != null) {
							mensaje("Sin Internet","La información puede no estar actualizada, y los pedidos no se entregaron. \n"
											+ "Esta acción es de su entera responsabilidad \n"
											+ "¿Desea continuar?", usuario);
						} else {
							funcion.MostrarMensajeAceptar(LoginActivity.this,"Acceso Denegado","El usuario o contraseña es incorrecta.");
						}
					}
				
				} catch (Exception e) {
					Usuario usuario = controladorUsuario.validarUsuario(txt_usuario.getText().toString(), txt_contrasena.getText().toString());
					if (usuario != null) {
						mensaje("Sin Conexión al Servidor","La información puede no estar actualizada, y los pedidos no se entregaron. \n"
								+ "Esta acción es de su entera responsabilidad \n"
								+ "¿Desea continuar?", usuario);
					} else {
						funcion.MostrarMensajeAceptar(LoginActivity.this,"Acceso Denegado","El usuario o contraseña es incorrecta.");
					}
				}
			}
		});

	}
	
	private void mensaje(String titulo, String cadena, final Usuario usuario) {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setTitle(titulo);
		alertbox.setMessage(cadena);
		alertbox.setPositiveButton("Si", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				respuestaMensaje(true, usuario);
			}
		});
		alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				respuestaMensaje(false, null);
			}
		});
		alertbox.show();
	}
			
	private void respuestaMensaje(boolean respuesta, Usuario usuario) {
		if (respuesta) {
			Bundle bun = new Bundle();
			bun.putInt("EMPRESA", usuario.getEmpresa());
			bun.putInt("VENDEDOR", usuario.getCodigo());
			Intent intent = new Intent(LoginActivity.this,MenuDiaActivity.class);
			intent.putExtras(bun);
			finish();
			startActivity(intent);
		}
	}

	public void guardarFinDeDia(final Usuario usuario) {
		
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		alertbox.setTitle("Cierre de dia");
		alertbox.setMessage("La última vez no realizó el cierre de día obligatorio \n ¿Desea realizarlo?");
		
		alertbox.setPositiveButton("Si", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				try{
					ControladorInforme controlador_log=new ControladorInforme(LoginActivity.this);
					Log log =new Log();
					log.setDescripcion("CIERRE DIA ANTERIOR");
				    log.setTipo(2);
					log.setFecha(new Date().getTime());
					log.setEstado(0);
					log.setVendedor(usuario.getCodigo());	
					controlador_log.EliminarLog();
					controlador_log.guardarLog(log);
					controladorFinDeDia=new ControladorCierreDeDia(LoginActivity.this, usuario.getCodigo());
					controladorFinDeDia.realizarCierreDiaPendiente();						
					Intent intent = new Intent(getApplicationContext(),SincronizadorActivity.class);
					bun.putInt("EMPRESA", usuario.getEmpresa());
					bun.putInt("VENDEDOR", usuario.getCodigo());	
					bun.putString("SINCRONIZACION", "INICIO");
					intent.putExtras(bun);
					startActivity(intent);
					finish();
				}catch(Exception ex){
					
				}
			}
		});
		
		alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
		
		alertbox.show();
		
	}
	
	/*private boolean login(){
	Connection conectionManager = null;
	Statement st = null;
	ResultSet rs = null;
	boolean login=false;
	try {
		String usuario = "widus";
		String password = "qwerty12345/*";
		String url = "jdbc:mysql://gvs-mdz.dyndns.org:16101/mobileIdus";
		
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conectionManager = DriverManager.getConnection(url,usuario, password);
		if (!conectionManager.isClosed()) {
			st = conectionManager.createStatement();
			rs = st.executeQuery("SELECT U.ID,U.TIPO,U.NOMBRE,U.EMPRESA,V.CODIGO,U.NOMBRE,U.CONTRASENA FROM USUARIO AS U INNER JOIN VENDEDOR AS V ON U.CODIGO=V.ID WHERE U.NOMBRE='"+txt_usuario.getText().toString()+"' AND U.CONTRASENA="+txt_contrasena.getText().toString());
			while (rs.next()) {
				Usuario usu = new Usuario();
				usu.setId(rs.getInt("ID"));
				usu.setTipo(rs.getString("TIPO"));
				usu.setEmpresa(rs.getInt("EMPRESA"));
				usu.setCodigo(rs.getInt("CODIGO"));
				usu.setNombre(rs.getString("NOMBRE"));
				usu.setContrasena(rs.getString("CONTRASENA"));
				busquedaUsuario=new BusquedaUsuario(this);
				busquedaUsuario.modificarUsuarioConector(usu);
				login=true;
			}						
		}
	} catch (SQLException e) {
		e.getMessage();
	} catch (Exception ex) {
		 ex.getMessage();
	} finally {
		try {
			conectionManager.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	return login;
}*/
}


