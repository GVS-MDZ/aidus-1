package com.gvs.conectores;

import java.io.DataInputStream;
import android.database.SQLException;
import com.gvs.modelos.Configuracion;

public class ConectorVendedor extends ConectorGeneral {

	private int empresa,vendedor;
	private String version,modelo;

	public ConectorVendedor(Configuracion conf,int empresa, int vendedor,String version,String modelo) {
		super(conf);
		this.empresa=empresa;
		this.vendedor=vendedor;
		this.version=version;
		this.modelo=modelo;
	}

	@Override
	protected String CompletarURL() throws Exception {
		String strUrl = "datosVendedor?empresa=" + empresa + "&vendedor="
				+ vendedor + "&version=" + version + "&modelo=" + modelo;
		return strUrl;
	}

	@Override
	protected void procesarRespuesta(DataInputStream in) throws Exception {
		try {

		} catch (SQLException e) {
			throw new Exception(e.getMessage());
		}
	}

}