package com.gvs.idusapp;

import java.util.Collection;
import java.util.List;
import com.gvs.conectores.ConectorActualizacionPrimario;
import com.gvs.conectores.ConectorActualizacionSecundario;
import com.gvs.conectores.ConectorCliente;
import com.gvs.conectores.ConectorCobranza;
import com.gvs.conectores.ConectorControlDeuda;
import com.gvs.conectores.ConectorFinDeDia;
import com.gvs.conectores.ConectorGeneral;
import com.gvs.conectores.ConectorInforme;
import com.gvs.conectores.ConectorLog;
import com.gvs.conectores.ConectorNoAtendido;
import com.gvs.conectores.ConectorPedido;
import com.gvs.conectores.ConectorRespuestaEncuesta;
import com.gvs.conectores.ConectorSincronizador;
import com.gvs.conectores.ConectorVendedor;
import com.gvs.controladores.ControladorCliente;
import com.gvs.controladores.ControladorCobranza;
import com.gvs.controladores.ControladorConfiguracion;
import com.gvs.controladores.ControladorEncuesta;
import com.gvs.controladores.ControladorInforme;
import com.gvs.controladores.ControladorNoAtendido;
import com.gvs.controladores.ControladorPedido;
import com.gvs.controladores.ControladorSincronizacion;
import com.gvs.controladores.ControladorCierreDeDia;
import com.gvs.modelos.CierreDeDia;
import com.gvs.modelos.Cliente;
import com.gvs.modelos.Configuracion;
import com.gvs.modelos.Cobranza;
import com.gvs.modelos.Informe;
import com.gvs.modelos.Log;
import com.gvs.modelos.NoAtendido;
import com.gvs.modelos.Pedido;
import com.gvs.modelos.RespuestaEncuesta;
import com.gvs.modelos.Sincronizacion;
import com.gvs.utilidades.Funciones;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

public class SincronizadorActivity extends Activity {

	private int empresa, vendedor;
	private Funciones funcion;
	private Configuracion configuracion;
	private Bundle bun;
	private Handler handler;
	private ConectorGeneral conectorGeneral;
	private ControladorConfiguracion controladorConfiguracion;
	private ControladorSincronizacion controladorSincronizacion;
	private ControladorNoAtendido controladorNoAtendido;
	private ControladorPedido trbPedidos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sincronizador);
		try { 
			bun = getIntent().getExtras();
			empresa = bun.getInt("EMPRESA");
			vendedor = bun.getInt("VENDEDOR");
		handler = new Handler();
			funcion = new Funciones(this);
			controladorConfiguracion = new ControladorConfiguracion(this);
			controladorSincronizacion=new ControladorSincronizacion(this);
			controladorNoAtendido = new ControladorNoAtendido();
			controladorNoAtendido.setContexto(SincronizadorActivity.this); 
			/*trbPedidos = new ControladorPedido();
			trbPedidos.setContexto(SincronizadorActivity.this);*/
			trbPedidos = new ControladorPedido(SincronizadorActivity.this);
			configuracion = controladorConfiguracion.buscarConfiguracion();
			bun.putInt("EMPRESA", empresa);
			bun.putInt("VENDEDOR", vendedor);
							
			if (funcion.conexionInternet()) {
		Thread thread = new Thread() {
				public void run() {
						try {
							if(controladorSincronizacion.realizoSincronizacionGeneral("T", empresa, vendedor)){
								conectorGeneral = new ConectorActualizacionPrimario(configuracion,empresa, vendedor,"ACTUALIZACIONGENERAL", SincronizadorActivity.this);
								if (conectorGeneral.correr(SincronizadorActivity.this)) {
									conectorGeneral = new ConectorActualizacionSecundario(configuracion,empresa, vendedor,"ACTUALIZACIONPENDIENTE", SincronizadorActivity.this);
									if (conectorGeneral.correr(SincronizadorActivity.this)) {
										enviarDatos();
									}
								}
							}else{
								conectorGeneral = new ConectorActualizacionPrimario(configuracion,empresa, vendedor,"ACTUALIZACIONGENERAL", SincronizadorActivity.this);				
								if (conectorGeneral.correr(SincronizadorActivity.this)) {
									conectorGeneral = new ConectorActualizacionSecundario(configuracion,empresa, vendedor,"ACTUALIZACIONPENDIENTE", SincronizadorActivity.this);
									if (conectorGeneral.correr(SincronizadorActivity.this)) {
										enviarDatos();
									}
								}					
							}
							if (bun.getString("SINCRONIZACION").equals("GENERAL")) {
								finish();
							} else {
								Intent intent = new Intent(SincronizadorActivity.this,MenuDiaActivity.class);
								intent.putExtras(bun);
								startActivity(intent);
								finish();
							}
						} catch (Exception e) {
							handler.post(new Runnable() {
								@Override
								public void run() {
									finish();
								}
							});
					}
				}
				};
				thread.start();
			} else {
				finish();
				 Toast toast = Toast.makeText(getApplicationContext(),"Sincronizacion no realizada", Toast.LENGTH_SHORT);
				 toast.show();
				 
			}
		} catch (Exception ex) {
			 finish();
			 Toast toast = Toast.makeText(getApplicationContext(),"Bug en la sincronizacion", Toast.LENGTH_SHORT);
			 toast.show();
		}
	}	
	
synchronized private void enviarDatos()throws Exception{
		try{
			controladorNoAtendido.marcarParaEnviar();
			Collection<NoAtendido> noAtendidos = controladorNoAtendido.buscarNoAtendidosParaEnviar();
			for (NoAtendido noAtendido : noAtendidos) {
				conectorGeneral = new ConectorNoAtendido(configuracion, SincronizadorActivity.this,empresa, noAtendido);
				conectorGeneral.correr(SincronizadorActivity.this);
			}
			
			ControladorCierreDeDia controladorCierreDia = new ControladorCierreDeDia(SincronizadorActivity.this, vendedor);
			controladorCierreDia.marcarParaEnviar();
			List<CierreDeDia> cierres = controladorCierreDia.buscarNoAtendidosParaEnviar();
			for (CierreDeDia cierre : cierres) {
				conectorGeneral = new ConectorFinDeDia(configuracion, SincronizadorActivity.this,empresa, cierre);
				conectorGeneral.correr(SincronizadorActivity.this);
			}
			
			ControladorSincronizacion trbSyncro = new ControladorSincronizacion(SincronizadorActivity.this);
			trbSyncro.marcarParaEnviar();
			List<Sincronizacion> syncros = trbSyncro.buscarSincronizacionesParaEnviar();
			for (Sincronizacion syncro : syncros) {
				conectorGeneral = new ConectorSincronizador(configuracion, SincronizadorActivity.this,empresa, syncro);
				conectorGeneral.correr(SincronizadorActivity.this);
			}
			
			ControladorEncuesta controladorEncuesta = new ControladorEncuesta(SincronizadorActivity.this);
			controladorEncuesta.marcarParaEnviar();
			List<RespuestaEncuesta> respuestas = controladorEncuesta.buscarRespuestasParaEnviar();
			for (RespuestaEncuesta resp : respuestas) {
				conectorGeneral = new ConectorRespuestaEncuesta(configuracion, SincronizadorActivity.this,empresa, vendedor, resp);
				conectorGeneral.correr(SincronizadorActivity.this);
			}
			
			ControladorCobranza controladorCobranza = new ControladorCobranza(SincronizadorActivity.this);
			List<Cobranza> list_gain = controladorCobranza.buscarCobranzas();
			for (Cobranza gain : list_gain) {
				conectorGeneral = new ConectorCobranza(configuracion, SincronizadorActivity.this,empresa, gain);
				conectorGeneral.correr(SincronizadorActivity.this);
			}
			
			ControladorCliente controladorCliente = new ControladorCliente(SincronizadorActivity.this);
			List<Cliente> list_client = controladorCliente.buscarClientesActualizados();
			for (Cliente client : list_client) {
				conectorGeneral = new ConectorCliente(configuracion, empresa,client.getCodigo(),client.getLatitud(),client.getLongitud());
				conectorGeneral.correr(SincronizadorActivity.this);
			}
	
			trbPedidos.marcarParaEnviarPedidos();
			List<Pedido> pedidos = trbPedidos.buscarPedidosParaEnviar();
			for (Pedido pedido : pedidos) {
				try {
					conectorGeneral = new ConectorPedido(configuracion,SincronizadorActivity.this, pedido,empresa);
					conectorGeneral.correr(SincronizadorActivity.this);
					trbPedidos.pedidoEnviadoCorrectamente(Integer.parseInt(pedido.getNumero_original()));
				} catch (Exception ex) {
					trbPedidos.pedidoConErrorAlEnvio(Integer.parseInt(pedido.getNumero_original()));
					continue;
				}
			}

			conectorGeneral = new ConectorControlDeuda(configuracion, empresa,SincronizadorActivity.this);
			conectorGeneral.correr(SincronizadorActivity.this);

			conectorGeneral = new ConectorVendedor(configuracion, empresa,vendedor,funcion.getVersionAplicacion(),funcion.getDeviceName());
			conectorGeneral.correr(SincronizadorActivity.this);
			
			ControladorInforme busLog = new ControladorInforme(SincronizadorActivity.this);
			List<Log> lista_log = busLog.buscarLogs();
			for (Log log : lista_log) {
				conectorGeneral = new ConectorLog(configuracion,SincronizadorActivity.this,empresa,log);
			    conectorGeneral.correr(SincronizadorActivity.this);							
			}

			ControladorInforme busAnalityc = new ControladorInforme(SincronizadorActivity.this);
			List<Informe> list_analityc = busAnalityc.buscarInformes();
			for (Informe informe : list_analityc) {
				conectorGeneral = new ConectorInforme(configuracion,SincronizadorActivity.this,empresa,informe);
			    conectorGeneral.correr(SincronizadorActivity.this);							
			}
			
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}
	
	/*private boolean recibir(){
		boolean recibir=false;
		Connection conectionManager = null;
		Statement st = null;
		ResultSet rs = null;
		ContentValues cont;
		cont=new ContentValues();
		BusquedaActualizacion busquedaActualizacion;
		busquedaActualizacion=new BusquedaActualizacion(this);
		try {
			String usuario = "widus";
			String password = "qwerty12345/*";
			String url = "jdbc:mysql://gvs-mdz.dyndns.org:16101/mobileIdus";
			
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conectionManager = DriverManager.getConnection(url,usuario, password);
			if (!conectionManager.isClosed()) {
				st = conectionManager.createStatement();
				//vendedor
				rs = st.executeQuery("SELECT V.CODIGO,V.NICK,V.CLAVE,V.COBERTURAREGULAR,V.COBERTURABUENA,"
						+ "V.COBERTURAMUYBUENA FROM VENDEDOR AS V INNER JOIN EMPRESA AS E "
						+ "ON V.EMPRESA_ID=E.ID WHERE E.CODIGO="+empresa+" AND V.CODIGO="+vendedor);
				while (rs.next()) {
					cont.clear();		
					cont.put("CODIGOEMPRESA", empresa);
					cont.put("CODIGO", rs.getInt("CODIGO"));
					cont.put("NOMBRE", rs.getString("NICK"));
					cont.put("PASS",rs.getString("CLAVE"));
					cont.put("COBREGULAR",rs.getInt("COBERTURAREGULAR"));
					cont.put("COBBUENA",rs.getInt("COBERTURABUENA"));
					cont.put("COBMUYBUENA", rs.getInt("COBERTURAMUYBUENA"));
					busquedaActualizacion.guardarVendedor(cont);
				}
				//lista de precio
				rs = st.executeQuery("SELECT L.CODIGO,L.CODIGORUBRO,L.CODIGOSUBRUBRO,L.CODIGOARTICULO,"
						+ "L.PORCENTAJE,L.HABILITADO FROM LISTAPRECIO AS L INNER JOIN EMPRESA AS E "
						+ "ON L.EMPRESA_ID=E.ID WHERE L.HABILITADO=TRUE AND E.CODIGO="+empresa);
				while (rs.next()) {
					cont.clear();
					cont.put("CODIGO",rs.getInt("CODIGO"));
					cont.put("CODIGORUBRO", rs.getInt("CODIGORUBRO"));
					cont.put("CODIGOSUBRUBRO",rs.getInt("CODIGOSUBRUBRO"));
					cont.put("CODIGOARTICULO",rs.getString("CODIGOARTICULO"));
					cont.put("PORCENTAJE",rs.getDouble("PORCENTAJE"));
					cont.put("HAB", funcion.devolverBoolean(rs.getBoolean("HABILITADO")));	
					busquedaActualizacion.guardarListaPrecio(cont);
				}
				//clientes
				rs = st.executeQuery("SELECT C.CODIGO,C.NOMBRE,C.DOMICILIO,C.TELEFONO,P.NOMBRE AS PROVINCIA,LO.NOMBRE AS LOCALIDAD,"
						+ "C.OBSERVACION,L.CODIGO AS LISTAPRECIO,C.SALDOCUENTACORRIENTE,C.HABILITADO,C.CANAL,C.RESPONSABILIDAD,C.LATITUD,C.LONGITUD "
						+ "FROM CLIENTE AS C "
						+ "INNER JOIN VENDEDOR AS V ON C.VENDEDOR_ID=V.ID "
						+ "INNER JOIN EMPRESA AS E ON C.EMPRESA_ID=E.ID "
						+ "INNER JOIN LISTAPRECIO AS L ON C.LISTAPRECIO_ID=L.ID "
						+ "INNER JOIN PROVINCIA AS P ON C.PROVINCIA_ID=P.ID "
						+ "INNER JOIN LOCALIDAD AS LO ON C.LOCALIDAD_ID=LO.ID "
						+ "WHERE C.HABILITADO=TRUE AND E.CODIGO="+empresa+" AND V.CODIGO="+vendedor+" ORDER BY C.CODIGO");
				while (rs.next()) {
					cont.clear();
					cont.put("CODIGO", rs.getInt("CODIGO"));
					cont.put("NOMBRE",rs.getString("NOMBRE"));
					cont.put("DOMICILIO",rs.getString("DOMICILIO"));
					cont.put("TELEFONO",rs.getString("TELEFONO"));
					cont.put("PROVINCIA",rs.getString("PROVINCIA"));
					cont.put("LOCALIDAD", rs.getString("LOCALIDAD"));
					cont.put("OBSERVACIONES",rs.getString("OBSERVACION"));
					cont.put("CODIGOLISTAPRECIO", rs.getInt("LISTAPRECIO"));
					cont.put("SALDO", rs.getDouble("SALDOCUENTACORRIENTE"));
					cont.put("CODIGOVENDEDOR", vendedor);
					cont.put("HAB",funcion.devolverBoolean(rs.getBoolean("HABILITADO")));
					cont.put("CANAL", rs.getString("CANAL"));	//VER ESTO				
					cont.put("RESPONSABILIDAD", rs.getString("RESPONSABILIDAD"));//VER ESTO
					cont.put("LATITUD", rs.getString("LATITUD"));
					cont.put("LONGITUD",rs.getString("LONGITUD"));
					cont.put("HABILITARGPS", 1);//VER ESTO
					cont.put("EDITADO",0);
					busquedaActualizacion.guardarCliente(cont);
				}
				//visita
				rs = st.executeQuery("SELECT V.DIASASIGNADOS AS DIAS,V.HORAVISITA AS HORA,V.ORDENDEVISITAPORDIA AS "
						+ "ORDEN,C.CODIGO AS CLIENTE FROM VISITA AS V "
						+ "INNER JOIN CLIENTE AS C ON V.CLIENTE_ID=C.ID "
						+ "INNER JOIN VENDEDOR AS VE ON C.VENDEDOR_ID=VE.ID "
						+ "INNER JOIN EMPRESA E ON C.EMPRESA_ID=E.ID WHERE E.CODIGO="+empresa+" AND VE.CODIGO="+vendedor+" ORDER BY C.CODIGO");
				while (rs.next()) {
					cont.clear();
					cont.put("DIAS",rs.getString("DIAS"));
					cont.put("HORA",rs.getString("HORA"));
					cont.put("ORDEN",rs.getInt("ORDEN"));
					cont.put("CODIGOCLIENTE",rs.getInt("CLIENTE"));
					busquedaActualizacion.guardarVisita(cont);
				}
				//rubro
				rs = st.executeQuery("SELECT R.CODIGO,R.NOMBRE,R.HABILITADO FROM RUBRO AS R "
						+ "INNER JOIN EMPRESA AS E  ON R.EMPRESA_ID=E.ID WHERE E.CODIGO="+empresa);
				while (rs.next()) {
					cont.clear();
					cont.put("CODIGO",rs.getInt("CODIGO"));
					cont.put("NOMBRE",rs.getString("NOMBRE"));
					cont.put("HAB", funcion.devolverBoolean(rs.getBoolean("HABILITADO")));
					busquedaActualizacion.guardarRubro(cont);
				}
				//subrubro
				rs = st.executeQuery("SELECT R.CODIGO AS RUBRO,S.CODIGO,S.NOMBRE,S.CONTROLASTOCK,"
						+ "S.HABILITADO FROM SUBRUBRO AS S INNER JOIN RUBRO AS R  "
						+ "ON S.RUBRO_ID=R.ID INNER JOIN EMPRESA AS E ON R.EMPRESA_ID=E.ID "
						+ "WHERE E.CODIGO="+empresa);
				while (rs.next()) {
					cont.clear();
					cont.put("CODIGORUBRO", rs.getInt("RUBRO"));
					cont.put("CODIGO",rs.getInt("CODIGO"));
					cont.put("NOMBRE",rs.getString("NOMBRE"));
					cont.put("CONTROLASTOCK", funcion.devolverBoolean(Boolean.parseBoolean(rs.getString("CONTROLASTOCK"))));
					cont.put("HAB",funcion.devolverBoolean(rs.getBoolean("HABILITADO")));
					busquedaActualizacion.guardarSubrubro(cont);
				}
				//articulo
				rs = st.executeQuery("SELECT A.CODIGO,R.CODIGO AS RUBRO,S.CODIGO AS SUBRUBRO,A.NOMBRE,A.PRECIOVENTA,A.EXISTENCIA,"
						+ "A.HABILITADO,A.MULTIPLO,A.PRECIOOFERTA FROM ARTICULO AS A INNER JOIN RUBRO AS R ON A.RUBRO_ID=R.ID "
						+ "INNER JOIN SUBRUBRO AS S ON S.RUBRO_ID=R.ID "
						+ "INNER JOIN EMPRESA AS E ON A.EMPRESA_ID=E.ID "
						+ "WHERE A.HABILITADO=TRUE AND E.CODIGO="+empresa);
				while (rs.next()) {
					cont.clear();
					cont.put("CODIGO",rs.getString("CODIGO"));
					cont.put("CODIGORUBRO", rs.getInt("RUBRO"));
					cont.put("CODIGOSUBRUBRO",rs.getInt("SUBRUBRO"));
					cont.put("NOMBRE",rs.getString("NOMBRE"));
					cont.put("PRECIO",rs.getDouble("PRECIOVENTA"));
					cont.put("EXISTENCIA",rs.getInt("EXISTENCIA"));
					cont.put("HAB", funcion.devolverBoolean(rs.getBoolean("HABILITADO")));
					cont.put("MULTIPLO",rs.getInt("MULTIPLO"));
					cont.put("PRECIOOFERTA",rs.getDouble("PRECIOOFERTA"));
					busquedaActualizacion.guardarArticulo(cont);
				}
				//MOTIVOS
				rs = st.executeQuery("SELECT M.CODIGO,M.MOTIVO FROM MOTIVOS AS M "
						+ "INNER JOIN EMPRESA AS E ON M.EMPRESA_ID=E.ID WHERE E.CODIGO="+empresa);
				while (rs.next()) {
					cont.clear();
					cont.put("CODIGO",rs.getInt("CODIGO"));
					cont.put("NOMBRE",rs.getString("MOTIVO"));
					busquedaActualizacion.guardarMotivo(cont);
				}
				//SUGERIDOS
				rs = st.executeQuery("SELECT S.ORDEN,S.CODIGOSIDISGO,S.MENSAJE,S.FECHAVENCIMIENTOOFERTA,S.EXIGIBLE "
						+ "FROM SUGERIDOS AS S INNER JOIN EMPRESA AS E ON S.EMPRESA_ID=E.ID WHERE E.CODIGO="+empresa);
				while (rs.next()) {
					cont.clear();
					cont.put("ORDEN",rs.getInt("ORDEN"));
					cont.put("CODIGOARTICULO", rs.getString("CODIGOSIDISGO"));
					cont.put("MENSAJE",rs.getString("MENSAJE"));
					cont.put("VENCIMIENTO", rs.getDate("FECHAVENCIMIENTOOFERTA").getTime());
					cont.put("EXIGIBLE", funcion.devolverBoolean(rs.getBoolean("EXIGIBLE")));
					busquedaActualizacion.guardarSugerido(cont);
				}
				//configuracion
				rs = st.executeQuery("SELECT E.CANTIDADDEITEMS,V.DIA_COMPLETO,V.VER_SUGERIDOS,V.OTRO_DIA,V.DESCUENTO,V.GPS "
						+ "FROM EMPRESA AS E INNER JOIN VENDEDOR AS V ON V.EMPRESA_ID=E.ID WHERE E.CODIGO="+empresa+" AND V.CODIGO="+vendedor);
				while (rs.next()) {
					int cantidadItems = rs.getInt("CANTIDADDEITEMS");
					if (cantidadItems == 0) {cantidadItems = 25;}
					cont = new ContentValues();
					cont.put("PUERTO", 8080);
					cont.put("CANTIDADITEMS", cantidadItems);
					cont.put("DIACOMPLETO", rs.getInt("DIA_COMPLETO"));
					cont.put("MUESTRASUGERIDOS", rs.getInt("VER_SUGERIDOS"));
					cont.put("OTRODIA", rs.getInt("OTRO_DIA"));
					cont.put("DESCUENTO", rs.getInt("DESCUENTO"));
					cont.put("SERVICIOGEO", rs.getInt("GPS"));
					busquedaActualizacion.modificarConfiguracion(cont);
				}
				
				//PEDIDO
				rs = st.executeQuery("SELECT MAX(P.NUMEROORIGINAL) AS NUMEROPEDIDO FROM PEDIDOCABECERA AS P  "
						+ "INNER JOIN EMPRESA AS E ON P.EMPRESA_ID=E.ID "
						+ "INNER JOIN VENDEDOR AS V ON P.VENDEDOR_ID=V.ID "
						+ "WHERE E.CODIGO="+empresa+" AND V.CODIGO="+vendedor+" AND  P.NUMEROORIGINAL<990000000");
				while (rs.next()) {
					cont.clear();
					cont.put("NUMERO", rs.getLong("NUMEROPEDIDO"));
					cont.put("NUMEROFINAL", "0");
					cont.put("FECHA", "0");
					cont.put("AVANCE",0);
					cont.put("CODIGOCLIENTE", -1);
					cont.put("CODIGOVENDEDOR", 0);
					cont.put("CANTIDADITEMS", "0");
					cont.put("ESTADO", 0);
					cont.put("FECHAENVIADO",0);
					cont.put("LATITUD", "0");
					cont.put("LONGITUD","0");
					cont.put("PRECISION", "0");
					cont.put("PROVEE","");
					cont.put("INTERNET",funcion.estadoRedes());
					busquedaActualizacion.guardarPedido(cont);
				}
				//articulo impresindible
				rs = st.executeQuery("SELECT C.CODIGO AS CLIENTE,AR.CODIGO,A.CANTOBJETIVO,A.CANTVENTA "
						+ "FROM ARTICULOSIMPRESINDIBLES AS A "
						+ "INNER JOIN ARTICULO AS AR ON A.ARTICULO_ID=AR.ID "
						+ "INNER JOIN CLIENTE AS C ON A.CLIENTE_ID=C.ID "
						+ "INNER JOIN VENDEDOR AS V ON C.VENDEDOR_ID=V.ID "
						+ "INNER JOIN EMPRESA AS E ON C.EMPRESA_ID=E.ID "
						+ "WHERE E.CODIGO="+empresa+" AND V.CODIGO="+vendedor+" AND A.PERIODO='06/2016'");
				while (rs.next()) {
					if (rs.getDouble("CANTOBJETIVO")< rs.getDouble("CANTVENTA")) {
						cont.clear();
						cont.put("CODIGOCLIENTE",rs.getInt("CLIENTE"));
						cont.put("CODIGOARTICULO", rs.getString("CODIGO"));
						cont.put("CANTIDADOBJ",rs.getDouble("CANTOBJETIVO"));
						cont.put("CANTIDADVTA",rs.getDouble("CANTVENTA"));
						cont.put("ACTUALIZACION", new Date().getTime());
						busquedaActualizacion.guardarImpresindible(cont);
					}	
				}
				//articulos lanzamientos
				rs = st.executeQuery("SELECT C.CODIGO AS CLIENTE,AR.CODIGO,A.CANTOBJETIVO,A.CANTVENTA "
						+ "FROM ARTICULOSLANZAMIENTOS AS A "
						+ "INNER JOIN ARTICULO AS AR ON A.ARTICULO_ID=AR.ID "
						+ "INNER JOIN CLIENTE AS C ON A.CLIENTE_ID=C.ID "
						+ "INNER JOIN VENDEDOR AS V ON C.VENDEDOR_ID=V.ID "
						+ "INNER JOIN EMPRESA AS E ON C.EMPRESA_ID=E.ID "
						+ "WHERE E.CODIGO="+empresa+" AND V.CODIGO="+vendedor+" AND A.PERIODO='06/2016'");
				while (rs.next()) {
					if (rs.getDouble("CANTOBJETIVO")< rs.getDouble("CANTVENTA")) {
						cont.clear();
						cont.put("CODIGOCLIENTE",rs.getInt("CLIENTE"));
						cont.put("CODIGOARTICULO", rs.getString("CODIGO"));
						cont.put("CANTIDADOBJ",rs.getDouble("CANTOBJETIVO"));
						cont.put("CANTIDADVTA",rs.getDouble("CANTVENTA"));
						cont.put("ACTUALIZACION", new Date().getTime());
					    busquedaActualizacion.guardarLanzamiento(cont);
					}
				}
				//OBJETIVO  X VISITA
				rs = st.executeQuery("SELECT C.CODIGO AS CLIENTE,B.PERIODO,B.VENTAPANTERIOR,B.OBJETIVOPACTUAL,B.CANTIDADVISITAS, "
						+ "B.VENTAACTUAL,B.ACTUALIZACION,B.PORCENTAJE FROM OBJXVISITA AS B "
						+ "INNER JOIN CLIENTE AS C ON B.CLIENTE_ID=C.ID "
						+ "INNER JOIN EMPRESA AS E ON C.EMPRESA_ID=E.ID "
						+ "INNER JOIN VENDEDOR AS V ON C.VENDEDOR_ID=V.ID "
						+ "WHERE E.CODIGO="+empresa+" AND V.CODIGO="+vendedor);
				while (rs.next()) {
					cont.clear();
					cont.put("CODIGOCLIENTE",rs.getInt("CLIENTE"));
					cont.put("PERIODO",rs.getString("PERIODO"));
					cont.put("VTA_ANT",rs.getDouble("VENTAPANTERIOR"));
					cont.put("OBJ_ACT",rs.getDouble("OBJETIVOPACTUAL"));
					cont.put("CAN_VIS", rs.getDouble("CANTIDADVISITAS"));
					cont.put("VTA_ACT", rs.getDouble("VENTAACTUAL"));
					cont.put("FEC_ACT",rs.getDate("ACTUALIZACION").getTime());
					cont.put("PORC", rs.getDouble("PORCENTAJE"));
					busquedaActualizacion.guardarObjetivoVisita(cont);
				}
				//ventas x dia
				rs = st.executeQuery("SELECT VE.PERIODO,V.DIA_COMPLETO,VE.IMPORTE,VE.ACTUALIZACION FROM VENTAXPDIAS AS VE  "
						+ "INNER JOIN VENDEDOR AS V ON VE.VENDEDOR_ID=V.ID "
						+ "INNER JOIN EMPRESA AS E ON V.EMPRESA_ID=E.ID "
						+ "WHERE E.CODIGO="+empresa+" AND V.CODIGO="+vendedor+" AND VE.PERIODO='06/2016'");
				while (rs.next()) {
					cont.clear();
					cont.put("PERIODO", rs.getString("PERIODO"));
					cont.put("DIA", rs.getInt("DIA_COMPLETO"));
					cont.put("IMPORTE", rs.getDouble("IMPORTE"));
					cont.put("ACTUALIZACION",rs.getDate("ACTUALIZACION").getTime());
					busquedaActualizacion.guardarVentas(cont);
				}
				//encuesta
				rs = st.executeQuery("SELECT EN.NUMERO,EN.NOMBRE,EN.FECHAINICIO,EN.FECHAFINAL,EN.EXIGIBLE "
						+ "FROM ENCUESTA AS EN INNER JOIN EMPRESA AS E "
						+ "ON EN.EMPRESA_ID=E.ID WHERE E.CODIGO="+empresa+" "
						+ "AND EN.FECHAINICIO<=CURDATE() AND EN.FECHAFINAL>=CURDATE()");
				while (rs.next()) {
					cont.clear();
					cont.put("NUMERO",rs.getInt("NUMERO"));
					cont.put("NOMBRE", rs.getString("NOMBRE"));
					cont.put("FECHAINI",rs.getDate("FECHAINICIO").getTime());
					cont.put("FECHAFIN",rs.getDate("FECHAFINAL").getTime());
					cont.put("EXIGIBLE",funcion.devolverBoolean(rs.getBoolean("EXIGIBLE")));
					busquedaActualizacion.guardarEncuesta(cont);
				}
				// item encuesta
				rs = st.executeQuery("SELECT EN.NUMERO,P.ITEM,P.PREGUNTA,P.AYUDA FROM PREGUNTASENCUESTA AS P "
						+ "INNER JOIN ENCUESTA AS EN  ON P.ENCUESTA_ID=EN.ID "
						+ "INNER JOIN EMPRESA AS E ON EN.EMPRESA_ID=E.ID WHERE E.CODIGO="+empresa+" "
						+ "AND EN.FECHAINICIO<=CURDATE() AND EN.FECHAFINAL>=CURDATE()");
				while (rs.next()) {
					cont.clear();
					cont.put("NUMERO", rs.getInt("NUMERO"));
					cont.put("ITEM",rs.getInt("ITEM"));
					cont.put("PREGUNTA",rs.getString("PREGUNTA"));
					cont.put("AYUDA",rs.getString("AYUDA"));
					busquedaActualizacion.guardarItemEncuesta(cont);
				}
				//comprobantes
				rs = st.executeQuery("SELECT TIPO,CLASE,SUCURSAL,NUMERO,FECHA,CL.CODIGO AS CLIENTE,SALDO FROM COMPROBANTEVENTA AS C  "
						+ "INNER JOIN EMPRESA AS E ON C.EMPRESA_ID=E.ID "
						+ "INNER JOIN VENDEDOR AS V ON C.VENDEDOR_ID=V.ID "
						+ "INNER JOIN CLIENTE AS CL ON C.CLIENTE_ID=CL.ID "
						+ "WHERE FECHA>= '2014/06/01'  "
						+ "AND CLASE IN ('FT','CI','ND','NI','NC')  "
						+ "AND MARCA = 0 AND FORMADEPAGO = 2 AND ANULADO = 0 "
						+ "AND V.CODIGO = "+vendedor+" AND E.CODIGO="+empresa+" "
						+ "ORDER BY FECHA,CLASE,TIPO,SUCURSAL,NUMERO");
				while (rs.next()) {
					cont.clear();
					cont.put("TIPO",rs.getString("TIPO"));
					cont.put("CLASE",rs.getString("CLASE"));
					cont.put("SUCURSAL",rs.getInt("SUCURSAL"));
					cont.put("NUMERO", rs.getInt("NUMERO"));
					cont.put("FECHA",rs.getDate("FECHA").getTime());
					cont.put("CODIGOCLIENTE",rs.getInt("CLIENTE"));
					cont.put("SALDO", rs.getDouble("SALDO"));
					busquedaActualizacion.guardarComprobante(cont);
				}
				//comprobantes
				rs = st.executeQuery("SELECT TIPO,CLASE,SUCURSAL,NUMERO,FECHA,CL.CODIGO AS CLIENTE,SALDO FROM COMPROBANTEVENTA AS C  "
						+ "INNER JOIN EMPRESA AS E ON C.EMPRESA_ID=E.ID "
						+ "INNER JOIN VENDEDOR AS V ON C.VENDEDOR_ID=V.ID "
						+ "INNER JOIN CLIENTE AS CL ON C.CLIENTE_ID=CL.ID "
						+ "WHERE FECHA>= '2014/06/01'  "
						+ "AND CLASE IN ('AC')  "
						+ "AND FORMADEPAGO = 2 AND MARCA = 1 AND SUCURSAL<9001 AND ANULADO = 0 "
						+ "AND V.CODIGO = "+vendedor+" AND E.CODIGO="+empresa+" "
						+ "ORDER BY FECHA,CLASE,TIPO,SUCURSAL,NUMERO");
				while (rs.next()) {
					cont.clear();
					cont.put("TIPO",rs.getString("TIPO"));
					cont.put("CLASE",rs.getString("CLASE"));
					cont.put("SUCURSAL",rs.getInt("SUCURSAL"));
					cont.put("NUMERO", rs.getInt("NUMERO"));
					cont.put("FECHA",rs.getDate("FECHA").getTime());
					cont.put("CODIGOCLIENTE",rs.getInt("CLIENTE"));
					cont.put("SALDO", rs.getDouble("SALDO"));	
					busquedaActualizacion.guardarComprobante(cont);
				}
				//combo
				rs = st.executeQuery("SELECT C.CODIGO_COMBO,C.CANTIDAD_ITEMS FROM COMBO AS C INNER JOIN EMPRESA AS E "
						+ "ON C.EMPRESA_ID=E.ID WHERE E.CODIGO="+empresa);
				while (rs.next()) {
					cont.clear();
					cont.put("CODIGO",rs.getInt("CODIGO_COMBO"));
					cont.put("CANTIDAD", rs.getInt("CANTIDAD_ITEMS"));
					busquedaActualizacion.guardarCombo(cont);
				}
				//control deuda
				rs = st.executeQuery("SELECT AIDUS,VERIFICA_DEUDA,DIAS_CONTROL FROM EMPRESA WHERE CODIGO="+empresa);
				while (rs.next()) {
					Configuracion configuracion=new  Configuracion();
					configuracion.setAiuds(rs.getInt("AIDUS"));
					configuracion.setControl_deuda(rs.getInt("VERIFICA_DEUDA"));
					configuracion.setDia_contol(rs.getInt("DIAS_CONTROL"));
					BusquedaConfiguracion busquedaConfiguracion=new BusquedaConfiguracion(this);
					busquedaConfiguracion.modificarConfiguracionCenector(configuracion);
				}

				Date fechaInicio = new Date();
				cont.clear();
				cont.put("CODIGOEMPRESA", empresa);
				cont.put("CODIGOVENDEDOR",this.vendedor);
				cont.put("FECHAINICIO", fechaInicio.getTime());
				cont.put("FECHA", fechaInicio.getTime());
				cont.put("MODO", "T");
				cont.put("INTERNET",funcion.estadoRedes());
				cont.put("ESTADO", 0);
				busquedaActualizacion.guardarSincronizacionTotal(cont);					
				recibir=true;

			}
		} catch (SQLException e) {
			recibir=false;
			e.getMessage();
		} catch (Exception ex) {
			recibir=false;
			ex.getMessage();
		} finally {
			try {
				conectionManager.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return recibir;
	}
	

	private void enviar(){		
		Connection conectionManager = null;
		Statement st = null;
		try {
			String usuario = "widus";
			String password = "qwerty12345/*";
			String url = "jdbc:mysql://gvs-mdz.dyndns.org:16101/mobileIdus";	
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			conectionManager = DriverManager.getConnection(url,usuario, password);
			if (!conectionManager.isClosed()) {
				st = conectionManager.createStatement();				
				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				BusquedaNoAtendidos controladorNoAtendido = new BusquedaNoAtendidos(SyncActivity.this);
				controladorNoAtendido.marcarParaEnviar();
				Collection<NoAtendido> noAtendidos = controladorNoAtendido.buscarNoAtendidosParaEnviar();
				for (NoAtendido noAtendido : noAtendidos) {
					st.executeUpdate("INSERT INTO NOATENDIDOS "
							+ "SELECT '"+UUID.randomUUID()+"' ,'"+format.format(new Date(noAtendido.getFecha()))+"',M.ID AS MOTIVO_ID,C.ID AS CLIENTE_ID,V.ID AS VENDEDOR_ID,E.ID AS EMPRESA_ID,0,'"+noAtendido.getLatitud()+"','"+noAtendido.getLongitud()+"','"+noAtendido.getPresicion()+"','"+noAtendido.getProveedor()+"' "
							+ "FROM EMPRESA AS E INNER JOIN VENDEDOR AS V ON V.EMPRESA_ID=E.ID "
							+ "INNER JOIN CLIENTE AS C ON C.EMPRESA_ID=E.ID "
							+ "INNER JOIN MOTIVOS AS M ON M.EMPRESA_ID=E.ID "
							+ "WHERE C.CODIGO="+noAtendido.getCodigoCliente()+" AND "
							+ "V.CODIGO="+noAtendido.getCodigoVendedor()+" AND M.CODIGO="+noAtendido.getCodigoMotivo()+" AND E.CODIGO="+empresa);
					controladorNoAtendido=new BusquedaNoAtendidos(this);
					controladorNoAtendido.modificarNoAtendidoConector(noAtendido);
				}
				
				BusquedasCierreDeDia controladorCierreDia = new BusquedasCierreDeDia(SyncActivity.this, vendedor);
				controladorCierreDia.marcarParaEnviar();
				Collection<CierresDeDia> cierres = controladorCierreDia.buscarNoAtendidosParaEnviar();	
				for (CierresDeDia cierre : cierres) {
					st.executeUpdate("INSERT INTO FINDEDIA "
							+ "SELECT '"+UUID.randomUUID()+"',"+cierre.getPedidosSinEnviar()+","+cierre.getMotivosSinEnviar()+","
							+ "'"+format.format(new Date(cierre.getFecha()))+"',"+cierre.getCobGeneral()+","+cierre.getClientesAtendidos()+","
							+ ""+cierre.getCobEfectiva()+",0,"+cierre.getClientesNoAtendidos()+","+cierre.getClientesDelDia()+","
							+ "V.ID AS VENDEDOR_ID,E.ID AS EMPRESA_ID "
							+ "FROM EMPRESA AS E INNER JOIN VENDEDOR AS V ON V.EMPRESA_ID=E.ID "
							+ "WHERE V.CODIGO="+vendedor+" AND E.CODIGO="+empresa);
					
					controladorCierreDia=new BusquedasCierreDeDia(this);
					controladorCierreDia.modificarCierreDiaConector(cierre);
				}
				
				BusquedaSincronizacion trbSyncro = new BusquedaSincronizacion(SyncActivity.this);
				trbSyncro.marcarParaEnviar();
				Collection<Sincronizador> syncros = trbSyncro.buscarSincronizacionesParaEnviar();
				for (Sincronizador syncro : syncros) {
					String sql="INSERT INTO SYNCRONIZACIONES "
							+ "SELECT '"+UUID.randomUUID()+"', '"+format.format(new Date(syncro.getFecha()))+"' ,'"+syncro.getModo()+"','"+format.format(new Date(syncro.getFechaIncio()))+"',"
							+ " '"+syncro.getInternet()+"', V.ID AS VENDEDOR_ID FROM VENDEDOR AS V  "
							+ "INNER JOIN EMPRESA AS E ON V.EMPRESA_ID=E.ID "
							+ "WHERE V.CODIGO="+vendedor+" AND E.CODIGO="+empresa;
					st.executeUpdate(sql);
					
					trbSyncro=new BusquedaSincronizacion(this);
					trbSyncro.modificarSincronizacionConector(syncro);
				}
				
				BusquedaEncuestas controladorEncuesta = new BusquedaEncuestas(SyncActivity.this);
				controladorEncuesta.marcarParaEnviar();
				Collection<RespuestaEncuestas> respuestas = controladorEncuesta.buscarRespuestasParaEnviar();
				for (RespuestaEncuestas resp : respuestas) {
					UUID id_respuesta_encuesta=UUID.randomUUID();
					st.executeUpdate("INSERT INTO RESPUESTASENCUESTAS "
							+ "SELECT '"+id_respuesta_encuesta+"',0,'"+format.format(new Date(resp.getFecha()))+"','"+resp.getLatitud()+"','"+resp.getLongitud()+"','"+resp.getObservacion()+"','"+resp.getPrecis()+"','"+resp.getProvee()+"', "
							+ "EN.ID AS ENCUESTA_ID,C.ID AS CLIENTE_ID,V.ID AS VENDEDOR_ID "
							+ "FROM ENCUESTA AS EN "
							+ "INNER JOIN EMPRESA AS E ON EN.EMPRESA_ID=E.ID "
							+ "INNER JOIN VENDEDOR AS V ON V.EMPRESA_ID=E.ID "
							+ "INNER JOIN CLIENTE AS C ON C.VENDEDOR_ID=V.ID "
							+ "WHERE E.CODIGO="+empresa+" AND V.CODIGO="+vendedor+" AND C.CODIGO="+resp.getCodigoCliente()+" AND EN.NUMERO="+resp.getNumero());
					String[] respuesta=resp.getRespuestas().split("~");
					for(String r:respuesta){
						String[] values=r.split(";");
						String pre=values[0];
						String res=values[1];
						String sql="INSERT INTO RESPUESTASPREGUNTASENCUESTAS "
								+ "SELECT '"+UUID.randomUUID()+"',0,'',"+res+",'"+id_respuesta_encuesta+"' AS RESPUESTASENCUESTAS_ID, P.ID AS PREGUNTASENCUESTA_ID FROM PREGUNTASENCUESTA AS P "
								+ "INNER JOIN ENCUESTA AS E ON P.ENCUESTA_ID=E.ID "
								+ "INNER JOIN EMPRESA AS EM ON E.EMPRESA_ID=EM.ID "
								+ "WHERE E.NUMERO="+resp.getNumero()+" AND EM.CODIGO="+empresa+" AND P.ITEM="+Integer.parseInt(pre);
						st.executeUpdate(sql);	
					}
					
					controladorEncuesta=new BusquedaEncuestas(this);
					controladorEncuesta.modificarRespuestaConector(resp);		
				}
				
				BusquedaCobranza controladorCobranza = new BusquedaCobranza(SyncActivity.this);
				List<Gain> list_gain = controladorCobranza.buscarCobranzas();
				for (Gain gain : list_gain) {
					st.executeUpdate("INSERT INTO COBRANZA "
							+ "SELECT '"+UUID.randomUUID()+"','"+gain.getClase()+"',0,'"+format.format(new Date(gain.getFecha()))+"',NULL,'"+format.format(new Date().getTime())+"',"+gain.getImporte_pagado()+","
							+ ""+gain.getNumero_comprobante()+","+gain.getNumero_recibo()+","+gain.getSaldo()+","
							+ ""+gain.getSucursal()+",'"+gain.getTipo()+"',C.ID AS CLIENTE_ID,E.ID AS EMPRESA_ID,V.ID AS VENDEDOR_ID FROM CLIENTE AS C  "
							+ "INNER JOIN VENDEDOR AS V ON C.VENDEDOR_ID=V.ID "
							+ "INNER JOIN EMPRESA AS E ON C.EMPRESA_ID=E.ID "
							+ "WHERE E.CODIGO="+empresa+" AND V.CODIGO="+gain.getCodigo_vendedor()+" AND C.CODIGO="+gain.getCodigo_cliente());
					
					controladorCobranza=new BusquedaCobranza(this);
					controladorCobranza.modificarCobranzaConector(gain);
				}
				
				BusquedaClientes controladorCliente = new BusquedaClientes(SyncActivity.this);
				List<Client> list_client = controladorCliente.buscarClientesActualizados();
				for (Client client : list_client) {
					st.executeUpdate("SET SQL_SAFE_UPDATES = 0;");
					st.executeUpdate("UPDATE CLIENTE AS C "
							+ "INNER JOIN VENDEDOR AS V ON C.VENDEDOR_ID=V.ID "
							+ "INNER JOIN EMPRESA AS E ON C.EMPRESA_ID=E.ID "
							+ "SET C.LATITUD='"+client.getLatitud()+"',C.LONGITUD='"+client.getLongitud()+"' "
							+ "WHERE E.CODIGO="+empresa+" AND V.CODIGO="+client.getCodigo_vendedor()+" AND C.CODIGO="+client.getCodigo());
				}	
				
				BusquedaPedidos trbPedidos = new BusquedaPedidos(SyncActivity.this);
				trbPedidos.marcarParaEnviarPedidos();
				List<Order> pedidos = trbPedidos.buscarPedidosParaEnviar();
				int Min=1700000000;
				int Max=2000000000;
				for (Order pedido : pedidos) {
					int numero_pedido=((int)(Math.random()*(Max-Min))+Min);
					List<OrderDetail> items = trbPedidos.buscarItemsPorPedido(Integer.parseInt(pedido.getNumber()));
					
					st.executeUpdate("INSERT INTO PEDIDOCABECERA "
							+ "SELECT '"+UUID.randomUUID()+"',1,0,"+numero_pedido+","+pedido.getItem()+","+pedido.getAvance()+",'"+format.format(new Date(Long.parseLong(pedido.getDate())))+"', "
							+ "V.ID AS VENDEDOR_ID,C.ID AS CLIENTE_ID,E.ID AS EMPRESA_ID, "
							+ " '"+pedido.getInternet()+"','"+pedido.getLatitud()+"','"+pedido.getLongitud()+"','"+pedido.getPrecision()+"','"+pedido.getProvee()+"',"
							+ ""+pedido.getNumber()+",'"+format.format(new Date().getTime())+"' ,NULL, "
							+ "'"+ pedido.getObservacion()+"','"+format.format(new Date(pedido.getFechaEntrega()))+"','"+format.format(new Date(pedido.getFechainicio()))+"','"+format.format(new Date(pedido.getFechafin()))+"' FROM CLIENTE AS C "
							+ "INNER JOIN VENDEDOR V ON C.VENDEDOR_ID=V.ID "
							+ "INNER JOIN EMPRESA AS E ON C.EMPRESA_ID=E.ID "
							+ "WHERE E.CODIGO="+empresa+" AND C.CODIGO="+pedido.getCodigoCliente()+" AND V.CODIGO="+pedido.getCodigoVendedor());
					
					for(OrderDetail item:items){
						String sql="INSERT INTO PEDIDOCUERPO SELECT '"+UUID.randomUUID()+"',"+Double.parseDouble(item.getCantidad().toString())+",0,"+item.getItem()+","+numero_pedido+","
								+ "A.ID AS ARTIUCULO_ID,E.ID AS EMPRESA_ID,"+item.getNumero()+","+Double.parseDouble(item.getPrecio().toString())+","+Double.parseDouble(item.getDescuento().toString())+" "
								+ "FROM ARTICULO AS A "
								+ "INNER JOIN EMPRESA AS E ON A.EMPRESA_ID=E.ID "
								+ "WHERE E.CODIGO="+empresa+" AND A.CODIGO="+item.getCodigo();
						//String sql="INSERT INTO PEDIDOCUERPO SELECT 'bbbb',1.0,0,1,1793523772,A.ID AS ARTIUCULO_ID,E.ID AS EMPRESA_ID,71,6.48,0.0 FROM ARTICULO AS A INNER JOIN EMPRESA AS E ON A.EMPRESA_ID=E.ID WHERE E.CODIGO=289 AND A.CODIGO=1";
						st.executeUpdate(sql);
					}
					
					pedido.setNumber_final(numero_pedido+"");
					//trbPedidos.modificarPedidoConector(pedido);
					numero_pedido=0;		
				}		

				st.executeUpdate("SET SQL_SAFE_UPDATES = 0;");
			    st.executeUpdate("UPDATE VENDEDOR AS V "
						+ "INNER JOIN EMPRESA AS E ON V.EMPRESA_ID=E.ID "
						+ "SET VERSION='"+funcion.getVersionAplicacion()+"' ,MODELO='"+funcion.getDeviceName()+"' "
						+ "WHERE V.CODIGO="+vendedor+" AND E.CODIGO="+empresa);

				BusquedaInforme busLog = new BusquedaInforme(SyncActivity.this);
				List<Log> lista_log = busLog.buscarLogs();
				for (Log log : lista_log) {
					st.executeUpdate("INSERT INTO ACTIVIDADTELEFONO "
							+ "SELECT '"+UUID.randomUUID()+"','"+log.getDescripcion()+"','"+format.format(new Date(log.getFecha()))+"',"+log.getTipo()+",E.ID AS EMPRESA_ID,V.ID AS VENDEDOR_ID FROM VENDEDOR AS V "
							+ "INNER JOIN EMPRESA AS E ON V.EMPRESA_ID=E.ID "
							+ "WHERE E.CODIGO="+empresa+" AND V.CODIGO="+log.getVendedor());
					
					busLog=new BusquedaInforme(this);
					busLog.modificarLog(log);
				}
				
				BusquedaInforme busAnalityc = new BusquedaInforme(SyncActivity.this);
				List<Informe> list_analityc = busAnalityc.buscarInformes();
				for (Informe informe : list_analityc) {
					st.executeUpdate("INSERT INTO INFORME "
							+ "SELECT '"+UUID.randomUUID()+"','"+informe.getAccion()+"',0,'"+format.format(new Date(informe.getFecha()))+"','"+informe.getMejora()+"','"+informe.getRecomendacion()+"',C.ID AS CLIENTE_ID, "
							+ "E.ID AS EMPRESA_ID, V.ID AS VENDEDOR_ID FROM CLIENTE AS C "
							+ "INNER JOIN EMPRESA AS E ON C.EMPRESA_ID=E.ID "
							+ "INNER JOIN VENDEDOR AS V ON C.VENDEDOR_ID=V.ID "
							+ "WHERE E.CODIGO="+empresa+" AND V.CODIGO="+informe.getVendedor()+" AND C.CODIGO="+informe.getCliente());
				
					busAnalityc=new BusquedaInforme(this);
				    busAnalityc.modificarInforme(informe);
				}
				
			}
		} catch (SQLException ex) {
			ex.getMessage();
		} catch (Exception ex) {
			ex.getMessage();
		} finally {
			try {
				conectionManager.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}*/
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			 Toast toast = Toast.makeText(getApplicationContext(),"Espere que la sincronización termine", Toast.LENGTH_SHORT);
			 toast.show();
			 return false;
		 }else{
			 return super.onKeyDown(keyCode, event);
		 }
	}

}
