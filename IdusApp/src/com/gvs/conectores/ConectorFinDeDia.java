package com.gvs.conectores;

import java.io.DataInputStream;
import android.content.Context;
import com.gvs.controladores.ControladorCierreDeDia;
import com.gvs.modelos.CierreDeDia;
import com.gvs.modelos.Configuracion;

public class ConectorFinDeDia extends ConectorGeneral{
	
	private int empresa;
	private Context context;
	private CierreDeDia cierreDia;
	private ControladorCierreDeDia controladorFinDeDia;
	
	public ConectorFinDeDia(Configuracion conf, Context context, int empresa, CierreDeDia cierre){
		super(conf);
		this.context=context;
		this.empresa=empresa;
		this.cierreDia=cierre;
	}

	@Override
	protected String CompletarURL() throws Exception {
		String strUrl = "altaFinDeDia?empresa=" + empresa + "&vendedor=" + cierreDia.getCodigoVendedor() +
				"&fecha=" + cierreDia.getFecha() + "&deldia=" + cierreDia.getClientesDelDia() + 
				"&atendidos=" + cierreDia.getClientesAtendidos() + "&noatendidos=" + cierreDia.getClientesNoAtendidos() +
				"&pedidos=" + cierreDia.getPedidosSinEnviar() + "&motivos=0&general=" + cierreDia.getCoberturaGeneral() +
				"&efectiva=" + cierreDia.getCoberturaEfectiva() + "&estado=" + cierreDia.getEstado();
		return strUrl;
	}

	@Override
	protected void procesarRespuesta(DataInputStream in)throws Exception {
		try {
			controladorFinDeDia=new ControladorCierreDeDia(context,cierreDia.getCodigoVendedor());
			controladorFinDeDia.modificarCierreDiaConector(cierreDia);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally{
			in.close();
		}
	}
}
	