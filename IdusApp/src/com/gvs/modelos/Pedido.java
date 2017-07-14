package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Pedido implements Parcelable {
	
	private String numero_original;
	private String numero_final;	
	private String nombre_cliente;
	private int codigoCliente;
	private int codigoVendedor;
	private String internet;
	private String latitud;
	private String longitud;
	private String precision;
	private String proveedor;
	private String Observacion;
	private String item;
	private int icono;
	private double total;
	private int avance;
	private int estado;
	private String fecha;
	private long fechaEntrega;
	private long fechaEnviado;
	private long fechainicio;
	private long fechafin;

	

	public String getNumero_original() {
		return numero_original;
	}

	public void setNumero_original(String numero_original) {
		this.numero_original = numero_original;
	}

	public long getFechaEnviado() {
		return fechaEnviado;
	}

	public void setFechaEnviado(long fechaEnviado) {
		this.fechaEnviado = fechaEnviado;
	}

	public String getNumero_final() {
		return numero_final;
	}

	public void setNumero_final(String numero_final) {
		this.numero_final = numero_final;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int getIcono() {
		return icono;
	}

	public void setIcono(int icono) {
		this.icono = icono;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getNombre_cliente() {
		return nombre_cliente;
	}

	public void setNombre_cliente(String nombre_cliente) {
		this.nombre_cliente = nombre_cliente;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public int getAvance() {
		return avance;
	}

	public void setAvance(int avance) {
		this.avance = avance;
	}

	public int getCodigoCliente() {
		return codigoCliente;
	}

	public void setCodigoCliente(int codigoCliente) {
		this.codigoCliente = codigoCliente;
	}

	public int getCodigoVendedor() {
		return codigoVendedor;
	}

	public void setCodigoVendedor(int codigoVendedor) {
		this.codigoVendedor = codigoVendedor;
	}

	public String getInternet() {
		return internet;
	}

	public void setInternet(String internet) {
		this.internet = internet;
	}

	public String getLatitud() {
		return latitud;
	}

	public void setLatitud(String latitud) {
		this.latitud = latitud;
	}

	public String getLongitud() {
		return longitud;
	}

	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public String getProveedor() {
		return proveedor;
	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	public String getObservacion() {
		return Observacion;
	}

	public void setObservacion(String observacion) {
		Observacion = observacion;
	}

	public long getFechaEntrega() {
		return fechaEntrega;
	}

	public void setFechaEntrega(long fechaEntrega) {
		this.fechaEntrega = fechaEntrega;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public long getFechainicio() {
		return fechainicio;
	}

	public void setFechainicio(long fechainicio) {
		this.fechainicio = fechainicio;
	}

	public long getFechafin() {
		return fechafin;
	}

	public void setFechafin(long fechafin) {
		this.fechafin = fechafin;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {


	}

	/**/

}
