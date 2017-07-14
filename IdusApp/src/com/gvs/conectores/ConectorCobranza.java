package com.gvs.conectores;

import java.io.DataInputStream;
import android.content.Context;
import com.gvs.controladores.ControladorCobranza;
import com.gvs.modelos.Configuracion;
import com.gvs.modelos.Cobranza;

public class ConectorCobranza extends ConectorGeneral{

	private Context context;
	private int empresa;
	private Cobranza gain;
	private ControladorCobranza controladorCobranza;
	
	public ConectorCobranza(Configuracion conf, Context context,int empresa,Cobranza gain) {
		super(conf);
		this.context = context;
		this.empresa=empresa;
		this.gain=gain;
	}

	@Override
	protected String CompletarURL() throws Exception {
		String strUrl="abmCobranzas?empresa=" + empresa +"&vendedor="+gain.getCodigo_vendedor()+"&cliente="+gain.getCodigo_cliente()+"&fechaCelular="+gain.getFecha()+"" +
				"&clase="+gain.getClase()+"&tipo="+gain.getTipo()+"&sucursal="+gain.getSucursal()+"&numeroComprobante="+gain.getNumero_comprobante()+"" +
				"&numeroRecibo="+gain.getNumero_recibo()+"&saldo="+gain.getSaldo()+"&importe_pagado="+gain.getImporte_pagado()+"&estado="+gain.getEstado()+"";
		return strUrl;
	}

	@Override
	protected void procesarRespuesta(DataInputStream in)throws Exception {
		try {
			controladorCobranza=new ControladorCobranza(context);
			controladorCobranza.modificarCobranza(gain);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally{
			in.close();
		}
	}
}
