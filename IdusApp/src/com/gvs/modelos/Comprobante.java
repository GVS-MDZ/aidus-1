package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Comprobante implements Parcelable {

	private String clase;
	private String tipo;
	private int sucursal;
	private int numero;
	private String comprobante;
	private String fecha;
	private String saldo;
	private int codigo_cliente;
	private int icono;
	private int estado;

	public int getCodigo_cliente() {
		return codigo_cliente;
	}

	public void setCodigo_cliente(int codigo_cliente) {
		this.codigo_cliente = codigo_cliente;
	}

	public String getClase() {
		return clase;
	}

	public void setClase(String clase) {
		this.clase = clase;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getSucursal() {
		return sucursal;
	}

	public void setSucursal(int sucursal) {
		this.sucursal = sucursal;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public String getComprobante() {
		return comprobante;
	}

	public void setComprobante(String comprobante) {
		this.comprobante = comprobante;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getSaldo() {
		return saldo;
	}

	public void setSaldo(String saldo) {
		this.saldo = saldo;
	}

	public int getIcono() {
		return icono;
	}

	public void setIcono(int icono) {
		this.icono = icono;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

	}

}
