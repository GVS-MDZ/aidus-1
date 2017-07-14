package com.gvs.conectores;

import java.io.DataInputStream;
import android.content.Context;
import com.gvs.controladores.ControladorConfiguracion;
import com.gvs.modelos.Configuracion;

public class ConectorControlDeuda extends ConectorGeneral {

	private int empresa;
	private Context context;
	private Configuracion configuracion;
	private ControladorConfiguracion controladorConfiguracion;

	public ConectorControlDeuda(Configuracion conf,int empresa,Context context) {
		super(conf);
		this.empresa=empresa;
		this.context = context;

	}

	@Override
	protected String CompletarURL() {
		return "buscarControles?empresa="+empresa;
	}

	@Override
	protected void procesarRespuesta(DataInputStream in)throws Exception {
		try {
			configuracion=new  Configuracion();
			configuracion.setAiuds(in.readInt());
			configuracion.setControl_deuda(in.readInt());
			configuracion.setDia_contol((in.readInt()));
			controladorConfiguracion=new ControladorConfiguracion(context);
			controladorConfiguracion.modificarConfiguracionCenector(configuracion);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {	
			in.close();	
		}
	}

}