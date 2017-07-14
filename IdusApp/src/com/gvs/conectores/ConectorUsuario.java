package com.gvs.conectores;

import java.io.DataInputStream;
import android.content.Context;
import com.gvs.controladores.ControladorUsuario;
import com.gvs.modelos.Configuracion;
import com.gvs.modelos.Usuario;

public class ConectorUsuario extends ConectorGeneral {

	private String usuario,contrasena;
	private Context context;
	private ControladorUsuario controladorUsuario;

	public ConectorUsuario(Configuracion conf, String usuario, String contrasena,Context context) {
		super(conf);
		this.usuario = usuario;
		this.contrasena = contrasena;
		this.context = context;
	}

	@Override
	protected String CompletarURL() {
		return "devolverUsuario2?usuario=" + usuario + "&contrasena="+ contrasena+"";
	}

	@Override
	protected void procesarRespuesta(DataInputStream in) throws Exception {	
		try {
			Usuario usu = new Usuario();
			usu.setId(in.readInt());
			usu.setTipo(in.readUTF());
			usu.setEmpresa(in.readInt());
			usu.setCodigo(in.readInt());
			usu.setNombre(in.readUTF());
			usu.setContrasena(in.readUTF());
			controladorUsuario=new ControladorUsuario(context);
			controladorUsuario.modificarUsuario(usu);
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		} finally {
			in.close();
		}
	}

}
