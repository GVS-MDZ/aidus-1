package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class OperacionCliente implements Parcelable {
	
	private String nombre;
	private int icono;

	public OperacionCliente(String nombre, int icono) {
		super();
		this.nombre = nombre;
		this.icono = icono;
	}

	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public int getIcono() {
		return icono;
	}


	public void setIcono(int icono) {
		this.icono = icono;
	}


	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {


	}

}
