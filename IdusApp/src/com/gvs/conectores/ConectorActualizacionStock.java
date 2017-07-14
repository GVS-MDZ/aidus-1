package com.gvs.conectores;

import java.io.DataInputStream;
import android.content.Context;
import com.gvs.controladores.ControladorArticulo;
import com.gvs.modelos.Articulo;
import com.gvs.modelos.Configuracion;
import com.gvs.utilidades.Funciones;

public class ConectorActualizacionStock extends ConectorGeneral {

	private int empresa;
	private int lugar;
	private Context context;
	private ControladorArticulo controladorArticulo;
	
	public ConectorActualizacionStock(Configuracion conf,int empresa, int lugar, Context context) {
		super(conf);
		this.empresa=empresa;
		this.lugar=lugar;
		this.context=context;
	}

	@Override
	protected String CompletarURL() throws Exception {
		return "ActualizarStockAndroid?empresa=" + empresa + "&lugar=" + lugar;
	}

	@Override
	protected void procesarRespuesta(DataInputStream in)throws Exception {
		int tipoActualizacion=0;
		Funciones funcion = new Funciones();
		try {
			while (true){
				tipoActualizacion=in.readInt();
				if (tipoActualizacion==1099){
					break;
				}else if(tipoActualizacion==1007){
					Articulo articulo=new Articulo();
					articulo.setCodigo(in.readUTF());
					articulo.setPrecio( in.readDouble());
					articulo.setExistencia(in.readDouble());
					articulo.setHab(funcion.devolverBoolean(in.readBoolean()));
					controladorArticulo=new ControladorArticulo(context);
					controladorArticulo.modificarArticulo(articulo);
					break;
				}else{
					break;
				}
			}
		}catch (Exception e){
			throw new Exception(e.getMessage());
		}finally{
			in.close();
		}
	}

}
