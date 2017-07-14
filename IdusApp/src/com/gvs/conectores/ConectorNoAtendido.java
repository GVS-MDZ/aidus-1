package com.gvs.conectores;

import java.io.DataInputStream;
import android.content.Context;
import com.gvs.controladores.ControladorNoAtendido;
import com.gvs.modelos.Configuracion;
import com.gvs.modelos.NoAtendido;


public class ConectorNoAtendido extends ConectorGeneral{

	private int empresa;
	private Context context;
	private NoAtendido noAtendido = new NoAtendido();
	private ControladorNoAtendido controladorNoAtendidos;
	
	public ConectorNoAtendido(Configuracion conf,Context contexto,int empresa, NoAtendido NoAten){
		super(conf);
		this.context=contexto;
		this.empresa=empresa;
		this.noAtendido=NoAten;
		controladorNoAtendidos=new ControladorNoAtendido();
		controladorNoAtendidos.setContexto(context);

	}
	
	@Override
	protected String CompletarURL() throws Exception {
		String strUrl="altaNoAtendido_AndroidV5?empresa=" + empresa + "&fecha=" + noAtendido.getFecha() +
				"&vendedor=" + noAtendido.getCodigoVendedor() + "&cliente=" + noAtendido.getCodigoCliente() +
				"&motivo=" + noAtendido.getCodigoMotivo()+"&latitud="+noAtendido.getLatitud()+"&longitud="+noAtendido.getLongitud()+"&precis="+noAtendido.getPresicion()+"&provee="+noAtendido.getProveedor();
		return strUrl;
	}

	@Override
	protected void procesarRespuesta(DataInputStream in)throws Exception {
		try {
			controladorNoAtendidos.modificarNoAtendido(noAtendido);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}finally{
			in.close();
		}
	}

}
