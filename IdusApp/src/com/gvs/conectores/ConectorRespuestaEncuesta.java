package com.gvs.conectores;

import java.io.DataInputStream;
import android.content.Context;
import com.gvs.controladores.ControladorEncuesta;
import com.gvs.modelos.Configuracion;
import com.gvs.modelos.RespuestaEncuesta;

public class ConectorRespuestaEncuesta extends ConectorGeneral{

	private Context context;
	private int empresa,vendedor;
	private RespuestaEncuesta respuesta;
	private ControladorEncuesta controladorEncuestas;
	
	public ConectorRespuestaEncuesta(Configuracion conf,Context context, int empresa, int vendedor,RespuestaEncuesta respuesta) {
		super(conf);
		this.context=context;
		this.empresa=empresa;
		this.vendedor=vendedor;
		this.respuesta=respuesta;		
	}

	@Override
	protected String CompletarURL() throws Exception {
		String strURL ="recibirRespuestasEncuestas?idaidus=" + respuesta.getId() + "&empresa=" + empresa +
				"&numero=" + respuesta.getNumero() + "&vendedor=" + vendedor + "&cliente=" + respuesta.getCodigoCliente() +
				"&fecha=" + respuesta.getFecha() + "&observacion=" + respuesta.getObservacion() +
				"&latitud=" + respuesta.getLatitud() + "&longitud=" + respuesta.getLongitud() +
				"&precis=" + respuesta.getPrecision() + "&provee" + respuesta.getProveedor() +"&respuestas=" + respuesta.getRespuestas();
		return strURL;
	}

	@Override
	protected void procesarRespuesta(DataInputStream in)throws Exception {
		try {
			controladorEncuestas=new ControladorEncuesta(context);
			controladorEncuestas.modificarRespuesta(respuesta);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally{
			in.close();
		}
	}
	
}