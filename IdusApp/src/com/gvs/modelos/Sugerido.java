package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Sugerido implements Parcelable {
	
	private int orden;
	private String codigo_articulo;
	private String mensaje;
	private long vencimiento;


	public int getOrden() {
		return orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}

	public String getCodigo_articulo() {
		return codigo_articulo;
	}

	public void setCodigo_articulo(String codigo_articulo) {
		this.codigo_articulo = codigo_articulo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public long getVencimiento() {
		return vencimiento;
	}

	public void setVencimiento(long vencimiento) {
		this.vencimiento = vencimiento;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

	}

}
