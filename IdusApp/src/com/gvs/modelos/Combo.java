package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Combo implements Parcelable {
	
	private int codigo;
	private int cantidad;

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public int getCantidad() {
		return cantidad;
	}

	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {


	}

}
