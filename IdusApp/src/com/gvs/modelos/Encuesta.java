package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Encuesta implements Parcelable {
	
	private int numero;
	private String descripcion;
	private String fecha_inicio;
	private String fecha_fin;
	private int exigible;
	private int icono;

	
	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getFecha_inicio() {
		return fecha_inicio;
	}

	public void setFecha_inicio(String fecha_inicio) {
		this.fecha_inicio = fecha_inicio;
	}

	public String getFecha_fin() {
		return fecha_fin;
	}

	public void setFecha_fin(String fecha_fin) {
		this.fecha_fin = fecha_fin;
	}

	public int getIcono() {
		return icono;
	}

	public void setIcono(int icono) {
		this.icono = icono;
	}

	public int getExigible() {
		return exigible;
	}

	public void setExigible(int exigible) {
		this.exigible = exigible;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {


	}

}
