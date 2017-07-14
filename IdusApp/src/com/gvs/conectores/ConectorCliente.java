package com.gvs.conectores;

import java.io.DataInputStream;
import android.database.SQLException;
import com.gvs.modelos.Configuracion;

public class ConectorCliente extends ConectorGeneral{

	private int empresa,cliente;
	private String latitud,longitud;
	
	public ConectorCliente(Configuracion conf,int empresa,int cliente,double latitud,double longitud){
		super(conf);
		this.empresa=empresa;
		this.cliente=cliente;
		this.latitud=String.valueOf(latitud);
		this.longitud=String.valueOf(longitud);
	}
	
	@Override
	protected String CompletarURL() throws Exception {
		String strUrl="datosGpsCliente?empresa=" + empresa + "&cliente=" + cliente +
				"&latitud=" + latitud + "&longitud=" + longitud +"";
		return strUrl;
	}

	@Override
	protected void procesarRespuesta(DataInputStream in)throws Exception {
		try {
		
		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
	}

}