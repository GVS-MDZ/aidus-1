package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Venta implements Parcelable {

	private String periodo;
	private int dia;
	private double importe;
	private long actualizacion;

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public int getDia() {
		return dia;
	}

	public void setDia(int dia) {
		this.dia = dia;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public long getActualizacion() {
		return actualizacion;
	}

	public void setActualizacion(long actualizacion) {
		this.actualizacion = actualizacion;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {


	}

}
