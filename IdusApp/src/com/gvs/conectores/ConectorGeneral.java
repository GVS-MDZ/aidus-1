package com.gvs.conectores;

import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.content.Context;
import com.gvs.modelos.Configuracion;
import com.gvs.utilidades.Funciones;

public abstract class ConectorGeneral {

	protected Configuracion configuracion;
	private Funciones funcion;
	
	protected ConectorGeneral(Configuracion configuracion){
		this.configuracion=configuracion;
	}
	
	public boolean correr(Context context) throws Exception {
		URL url=null;
		HttpURLConnection urlConection=null;
		try{	
			funcion=new Funciones();
			String urlCompleto= "http://gvs-mdz.dyndns.org:8080/serverSiDisGo-war/";
			urlCompleto = urlCompleto + CompletarURL();
			urlCompleto = funcion.replace(urlCompleto, " ", "%20");
			urlCompleto = funcion.replace(urlCompleto, "{", "(");
			urlCompleto = funcion.replace(urlCompleto, "}", ")");
			urlCompleto = funcion.replace(urlCompleto, ">", " ");
			urlCompleto = funcion.replace(urlCompleto, "<", " ");
			urlCompleto = funcion.replace(urlCompleto, "^", " ");
			urlCompleto = funcion.replace(urlCompleto, "°", "g");
			urlCompleto = funcion.replace(urlCompleto, "#", "N");
			urlCompleto = funcion.replace(urlCompleto, "!", "");
			
			url = new URL(urlCompleto);
			urlConection = (HttpURLConnection) url.openConnection();			 
			urlConection.setConnectTimeout(20000);
			if (urlConection.getResponseCode()==200){
				procesarRespuesta(new DataInputStream(urlConection.getInputStream()));
				return true;
			} else {
				throw new Exception("Respuesta 500 del servidor");
			}
	
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			urlConection.disconnect();
		}
	}
	
	protected abstract String CompletarURL() throws Exception;
	
	protected abstract void procesarRespuesta(DataInputStream in) throws Exception;
}
