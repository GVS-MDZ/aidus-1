package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Imprescindible implements Parcelable {
	
	private int codigo_cliente;
	private String codigo_articulo;
	private double cantidad_objetivo;
	private double cantidad_venta;

	public int getCodigo_cliente() {
		return codigo_cliente;
	}

	public void setCodigo_cliente(int codigo_cliente) {
		this.codigo_cliente = codigo_cliente;
	}

	public String getCodigo_articulo() {
		return codigo_articulo;
	}

	public void setCodigo_articulo(String codigo_articulo) {
		this.codigo_articulo = codigo_articulo;
	}

	public double getCantidad_objetivo() {
		return cantidad_objetivo;
	}

	public void setCantidad_objetivo(double cantidad_objetivo) {
		this.cantidad_objetivo = cantidad_objetivo;
	}

	public double getCantidad_venta() {
		return cantidad_venta;
	}

	public void setCantidad_venta(double cantidad_venta) {
		this.cantidad_venta = cantidad_venta;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {


	}

}
