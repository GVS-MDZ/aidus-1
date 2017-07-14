package com.gvs.conectores;

import java.io.DataInputStream;
import java.util.List;
import android.content.Context;
import com.gvs.controladores.ControladorPedido;
import com.gvs.modelos.Configuracion;
import com.gvs.modelos.Pedido;
import com.gvs.modelos.DetallePedido;

public class ConectorPedido extends ConectorGeneral{

	private Pedido pedido;
	private Context context;
	private int empresa;
	private ControladorPedido controladorPedido;
	
	public ConectorPedido(Configuracion conf, Context contexto, Pedido pedido,int empresa) {
		super(conf);
		this.context=contexto;
		this.pedido=pedido;
		this.empresa=empresa;
		/*controladorPedido = new ControladorPedido();
		controladorPedido.setContexto(context);*/
		controladorPedido = new ControladorPedido(this.context);
	}

	@Override
	protected String CompletarURL() throws Exception {
		String cuerpo="";
		
		try {
			List<DetallePedido> items = controladorPedido.buscarItemsPorPedido(Integer.parseInt(pedido.getNumero_original()));
			for (DetallePedido item: items){
				if (cuerpo.equals("")){
					cuerpo= item.getNumero() +";"+ item.getItem() + ";" + item.getCodigo() + ";" + item.getCantidad() + ";" + item.getPrecio() + ";" + item.getDescuento();
				} else {
					cuerpo = cuerpo + "|" + item.getNumero() +";"+ item.getItem() + ";" + item.getCodigo() + ";" + item.getCantidad() + ";" + item.getPrecio() + ";" + item.getDescuento();
				}
			}
			String observacion="";
			if(pedido.getObservacion().equals(" ")){
				observacion= pedido.getObservacion().replace("", "%20");
			}else{
				 observacion=pedido.getObservacion();
			}
			String strURL ="insertarPedidosAndroidNuevo2?codigoEmpresa=" + empresa + "&numeroOriginal=" + pedido.getNumero_original() + "&" +
			"fecha=" + pedido.getFecha() + "&avance=" + pedido.getAvance() + "&codigoVendedor=" + pedido.getCodigoVendedor() + 
			"&codigoCliente="+ pedido.getCodigoCliente() + "&cantidadItem=" + pedido.getItem() + 
			"&lugar=1&estado=0&internet=" + pedido.getInternet() + "&latitud=" + pedido.getLatitud() +
			"&longitud=" + pedido.getLongitud() + "&precision=" + pedido.getPrecision() + "&proveedor=" + pedido.getProveedor() +
			"&obs=" + observacion + "&fechaEntrega="+ pedido.getFechaEntrega()+"&fechaInicio="+pedido.getFechainicio()+
			"&fechaFin="+pedido.getFechafin() + "&itemsCuerpo=" + cuerpo;
			return strURL;
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}

	@Override
	protected void procesarRespuesta(DataInputStream in)throws Exception {
		try {
		    int tipoDeDato=0;		
		   
			while (true){
				tipoDeDato = in.readInt();
				switch (tipoDeDato) {
				case 1099:	
					break;
				case 1:	
					Pedido pedido=new Pedido();
					pedido.setNumero_original(String.valueOf(in.readInt()));
					pedido.setNumero_final(String.valueOf(in.readInt()));
					pedido.setFecha(String.valueOf(in.readLong()));
					controladorPedido.modificarPedido(pedido);
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		finally{
			in.close();
		}
	}
	}