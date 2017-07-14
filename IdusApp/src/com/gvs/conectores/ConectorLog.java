package com.gvs.conectores;

import java.io.DataInputStream;
import android.content.Context;
import com.gvs.controladores.ControladorInforme;
import com.gvs.modelos.Configuracion;
import com.gvs.modelos.Log;

public class ConectorLog extends ConectorGeneral{
	
	private int empresa;
	private Context context;
	private Log log;
	private ControladorInforme controladorInforme;
	
	public ConectorLog(Configuracion conf, Context context, int empresa, Log log){
		super(conf);
		this.context=context;
		this.empresa=empresa;
		this.log=log;
	}

	@Override
	protected String CompletarURL() throws Exception {
		String strUrl = "abmLog?empresa=" + empresa + "&vendedor=" +log.getVendedor()+"&tipo="+log.getTipo()+"&descripcion="+log.getDescripcion()+"&fecha="+log.getFecha();
		return strUrl;
	}

	@Override
	protected void procesarRespuesta(DataInputStream in)throws Exception {
		try {
			controladorInforme=new ControladorInforme(context);
			controladorInforme.modificarLog(log);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally{
			in.close();
		}
	}
}