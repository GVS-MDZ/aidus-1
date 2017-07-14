package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class ListaPrecio implements Parcelable {
	
	private int codigo;
	private int codigo_rubro;
	private int codigo_subrubro;
	private String codigo_articulo;
	private double porcentaje;
	private boolean hab;

	public boolean getHab() {
		return hab;
	}

	public void setHab(boolean hab) {
		this.hab = hab;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public int getCodigo_rubro() {
		return codigo_rubro;
	}

	public void setCodigo_rubro(int codigo_rubro) {
		this.codigo_rubro = codigo_rubro;
	}

	public int getCodigo_subrubro() {
		return codigo_subrubro;
	}

	public void setCodigo_subrubro(int codigo_subrubro) {
		this.codigo_subrubro = codigo_subrubro;
	}

	public String getCodigo_articulo() {
		return codigo_articulo;
	}

	public void setCodigo_articulo(String codigo_articulo) {
		this.codigo_articulo = codigo_articulo;
	}

	public double getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(double porcentaje) {
		this.porcentaje = porcentaje;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {


	}

}