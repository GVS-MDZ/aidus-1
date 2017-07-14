package com.gvs.idusapp;

import java.util.Date;
import com.gvs.controladores.ControladorCliente;
import com.gvs.controladores.ControladorInforme;
import com.gvs.controladores.ControladorCierreDeDia;
import com.gvs.modelos.Log;
import com.gvs.utilidades.Funciones;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CierreDiaActivity extends Fragment {

	private TextView txt_cantidad, txt_venta, txt_no_venta, txt_no_enviado,
			txt_efectividad_visita, txt_efectividad_ruta;
	private Button btn_grabar;
	private int vendedor, cantidad, ventas, noVentas, noEnviados,clientes_sin_motivos,dia;
	private Funciones funcion = new Funciones();
	private double cobGral, cobEfe;
	private ControladorCierreDeDia controladorCierreDia;
	private ControladorCliente controladorCliente;
	private Bundle bun;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		View V = inflater.inflate(R.layout.activity_cierre_dia, container, false);
		this.getActivity().setTitle("Cierre del Día");
		try {
			txt_cantidad = (TextView) V.findViewById(R.id.txt_cantidad_visita);
			txt_venta = (TextView) V.findViewById(R.id.txt_cierre_venta);
			txt_no_venta = (TextView) V.findViewById(R.id.txt_cierre_no_venta);
			txt_no_enviado = (TextView) V.findViewById(R.id.txt_cierre_no_enviados);
			txt_efectividad_visita = (TextView) V.findViewById(R.id.txt_efectividad_visita);
			txt_efectividad_ruta = (TextView) V.findViewById(R.id.txt_efectividad_ruta);
			btn_grabar = (Button) V.findViewById(R.id.btn_grabar_cierre);
			bun = getActivity().getIntent().getExtras();
		    dia = bun.getInt("DIA");
		    vendedor = bun.getInt("VENDEDOR");
		    controladorCliente=new ControladorCliente(getActivity());
			controladorCierreDia = new ControladorCierreDeDia(getActivity(), vendedor);
			clientes_sin_motivos=controladorCliente.buscarClienteSinMotivosNoVenta(dia,vendedor);
			cantidad = controladorCierreDia.devolverCantidadClientesDelDia(dia);
			ventas = controladorCierreDia.devolverCantidadVendidos(dia);
			noVentas = controladorCierreDia.devolverCantidadNoVendidos(dia);
			noEnviados = controladorCierreDia.devolverCantidadNoEnviados(dia);
			txt_cantidad.setText(String.valueOf(cantidad));
			txt_venta.setText(String.valueOf(ventas));
			txt_no_venta.setText(String.valueOf(noVentas));
			txt_no_enviado.setText(String.valueOf(noEnviados));
			if (ventas == 0 && noVentas == 0) {
				cobGral = 0;
			} else {
				cobGral = (double) (ventas * 100) / (ventas + noVentas);
			}
			cobEfe = (double) (ventas * 100) / cantidad;
			txt_efectividad_visita.setText(funcion.formatDecimal(cobGral, 2)+ " %");
			txt_efectividad_ruta.setText(funcion.formatDecimal(cobEfe, 2)+ " %");
		
			btn_grabar.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					if (noEnviados > 0) {
						mensajeError("NO ENVIADOS","No puede realizar el cierre de dia porque hay pedidos pendientes sin enviar",1);
					}else if(clientes_sin_motivos>0){
						mensajeError("CLIENTES","Existen "+ clientes_sin_motivos +" clientes que no tienen motivos de no venta",1);
					} 
					else {
						mensajeConfirmacion("CONFIRME","¿Está seguro de realizar el cierre del día?", 2);
					}
				}
			});
			
		} catch (Exception e) {
			Toast toast =Toast.makeText(getActivity(),"Bug en cierre de día", Toast.LENGTH_SHORT);
			toast.show();
		}
		return V;
	}

	private void mensajeConfirmacion(String titulo, String cadena, final int tipo) {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());
		alertbox.setTitle(titulo);
		alertbox.setMessage(cadena);
		alertbox.setPositiveButton("ACEPTAR",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						try{
							ControladorInforme controlador_log=new ControladorInforme(getActivity());
							Log log =new Log();
							log.setDescripcion("CIERRE DIA");
							log.setTipo(35);
							log.setFecha(new Date().getTime());
							log.setEstado(0);
							log.setVendedor(vendedor);
							controlador_log.guardarLog(log);
							controlador_log.EliminarLog();
							respuestaConfirmacion(true, tipo);
						}catch(Exception ex){
							
						}
					}
				});
		alertbox.setNegativeButton("CANCELAR",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						respuestaConfirmacion(false, tipo);
					}
				});
		alertbox.show();
	}

	private void mensajeError(String titulo, String cadena, final int tipo) {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());
		alertbox.setTitle(titulo);
		alertbox.setMessage(cadena);
		alertbox.setPositiveButton("ACEPTAR",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						respuestaError(true, tipo);
					}
				});
		alertbox.setNegativeButton("CANCELAR",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						respuestaError(false, tipo);
					}
				});
		alertbox.show();
	}

	private void respuestaConfirmacion(boolean respuesta, int tipo) {
		if (respuesta) {
			switch (tipo) {
			case 1:
				getActivity().finish();
				break;
			case 2:
				guardarFinDeDia();
				getActivity().finish();
				break;
			default:
				break;
			}
		} else {

		}
	}

	private void respuestaError(boolean respuesta, int tipo) {
		if (respuesta) {
			switch (tipo) {
			case 1:
				getActivity().finish();
				break;
			default:
				break;
			}
		} else {

		}
	}
	
	private void guardarFinDeDia() {
		try {
			controladorCierreDia.guardarFinDeDia(cantidad, ventas, noVentas,noEnviados, cobGral, cobEfe);
		} catch (Exception ex) {
			funcion.MostrarMensajeAceptar(getActivity(), "ERROR",ex.getMessage());
		}
	}

}
