package com.gvs.conectores;

import java.io.DataInputStream;
import android.content.Context;
import com.gvs.controladores.ControladorSincronizacion;
import com.gvs.modelos.Configuracion;
import com.gvs.modelos.Sincronizacion;

public class ConectorSincronizador extends ConectorGeneral {

	private Context context;
	private Sincronizacion syncronizacion;
	private ControladorSincronizacion controladorSincronizacion;
	
	public ConectorSincronizador(Configuracion conf, Context context,int empresa, Sincronizacion syncronizacion){
		super(conf);
		this.context=context;
		this.syncronizacion=syncronizacion;
	}

	@Override
	protected String CompletarURL() throws Exception {
		String strURL = "insertarSyncronizacion?empresa=" + syncronizacion.getCodigoEmpresa() + 
				"&vendedor=" + syncronizacion.getCodigoVendedor() + "&fechaInicio=" + syncronizacion.getFechaIncio() +
				"&fecha=" + syncronizacion.getFecha() + "&modo=" + syncronizacion.getModo() + "&internet=" + syncronizacion.getInternet();
		return strURL;
	}

	@Override
	protected void procesarRespuesta(DataInputStream in)throws Exception {
		try {
			controladorSincronizacion=new ControladorSincronizacion(context);
			controladorSincronizacion.modificarSincronizacion(syncronizacion);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally{
			in.close();
		}
	}
}
	
	