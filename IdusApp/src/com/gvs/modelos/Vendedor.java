package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Vendedor implements Parcelable {

	private int codigo;
	private String nombre;
	private int codigoEmpesa;

	public int getCodigoEmpesa() {
		return codigoEmpesa;
	}

	public void setCodigoEmpesa(int codigoEmpesa) {
		this.codigoEmpesa = codigoEmpesa;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {


	}

}
