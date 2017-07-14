package com.gvs.conectores;

import java.io.DataInputStream;
import android.content.Context;
import com.gvs.controladores.ControladorInforme;
import com.gvs.modelos.Configuracion;
import com.gvs.modelos.Informe;

public class ConectorInforme extends ConectorGeneral{
	
	private int empresa;
	private Context conte;
	private Informe informe;
	private ControladorInforme controladorInforme;
	
	public ConectorInforme(Configuracion conf, Context cont, int empresa, Informe informe){
		super(conf);
		this.conte=cont;
		this.empresa=empresa;
		this.informe=informe;
	}

	@Override
	protected String CompletarURL() throws Exception {
		String strUrl = "abmInforme?empresa=" + empresa + "&vendedor=" +informe.getVendedor()+"&fecha="+informe.getFecha()+"&cliente="+informe.getCliente()+"&accion="+informe.getAccion()+"&mejora="+informe.getMejora()+"&recomendacion="+informe.getRecomendacion();
		return strUrl;
	}

	@Override
	protected void procesarRespuesta(DataInputStream in)throws Exception {
		try {
			controladorInforme=new ControladorInforme(conte);
			controladorInforme.modificarInforme(informe);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally{
			in.close();
		}
	}
}