package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class SubRubro implements Parcelable {
	
	private int codigo_rubro;
	private int codigo;
	private String nombre;
	private int controla_stock;

	public int getCodigo_rubro() {
		return codigo_rubro;
	}

	public void setCodigo_rubro(int codigo_rubro) {
		this.codigo_rubro = codigo_rubro;
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

	public int getControla_stock() {
		return controla_stock;
	}

	public void setControla_stock(int controla_stock) {
		this.controla_stock = controla_stock;
	}
	
	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {


	}

}
