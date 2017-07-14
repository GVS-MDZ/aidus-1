package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class NoAtendido implements Parcelable {
	
	private long fecha;
	private int codigoCliente;
	private int codigoMotivo;
	private int codigoVendedor;
	private double latitud;
	private double longitud;
	private double presicion;
	private String proveedor;

	public long getFecha() {
		return fecha;
	}

	public void setFecha(long fecha) {
		this.fecha = fecha;
	}

	public int getCodigoCliente() {
		return codigoCliente;
	}

	public void setCodigoCliente(int codigoCliente) {
		this.codigoCliente = codigoCliente;
	}

	public int getCodigoMotivo() {
		return codigoMotivo;
	}

	public void setCodigoMotivo(int codigoMotivo) {
		this.codigoMotivo = codigoMotivo;
	}

	public int getCodigoVendedor() {
		return codigoVendedor;
	}

	public void setCodigoVendedor(int codigoVendedor) {
		this.codigoVendedor = codigoVendedor;
	}

	public double getLatitud() {
		return latitud;
	}

	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	public double getPresicion() {
		return presicion;
	}

	public void setPresicion(double presicion) {
		this.presicion = presicion;
	}

	public String getProveedor() {
		return proveedor;
	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {


	}

}
