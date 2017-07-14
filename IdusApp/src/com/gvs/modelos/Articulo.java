package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Articulo implements Parcelable {
	
	private String codigo;
	private String detalle;
	private double stock;
	private double precio;
	private int rubro;
	private int subRubro;
	private int multiplo;
	private double oferta;
	private double existencia;
	private int hab;


	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	public double getStock() {
		return stock;
	}

	public void setStock(double stock) {
		this.stock = stock;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public int getRubro() {
		return rubro;
	}

	public void setRubro(int rubro) {
		this.rubro = rubro;
	}

	public int getSubRubro() {
		return subRubro;
	}

	public void setSubRubro(int subRubro) {
		this.subRubro = subRubro;
	}

	public int getMultiplo() {
		return multiplo;
	}

	public void setMultiplo(int multiplo) {
		this.multiplo = multiplo;
	}

	public double getOferta() {
		return oferta;
	}

	public void setOferta(double oferta) {
		this.oferta = oferta;
	}

	public double getExistencia() {
		return existencia;
	}

	public void setExistencia(double existencia) {
		this.existencia = existencia;
	}

	public int getHab() {
		return hab;
	}

	public void setHab(int hab) {
		this.hab = hab;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

	}

}
