package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class CierreDeDia implements Parcelable {
	
	private int codigoVendedor;	
	private int clientesDelDia;
	private int clientesAtendidos;
	private int clientesNoAtendidos;
	private int pedidosSinEnviar;
	private int motivosSinEnviar;
	private double coberturaGeneral;
	private double coberturaEfectiva;
	private long fecha;
	private int estado;

	public int getCodigoVendedor() {
		return codigoVendedor;
	}

	public void setCodigoVendedor(int codigoVendedor) {
		this.codigoVendedor = codigoVendedor;
	}

	public long getFecha() {
		return fecha;
	}

	public void setFecha(long fecha) {
		this.fecha = fecha;
	}

	public int getClientesDelDia() {
		return clientesDelDia;
	}

	public void setClientesDelDia(int clientesDelDia) {
		this.clientesDelDia = clientesDelDia;
	}

	public int getClientesAtendidos() {
		return clientesAtendidos;
	}

	public void setClientesAtendidos(int clientesAtendidos) {
		this.clientesAtendidos = clientesAtendidos;
	}

	public int getClientesNoAtendidos() {
		return clientesNoAtendidos;
	}

	public void setClientesNoAtendidos(int clientesNoAtendidos) {
		this.clientesNoAtendidos = clientesNoAtendidos;
	}

	public int getPedidosSinEnviar() {
		return pedidosSinEnviar;
	}

	public void setPedidosSinEnviar(int pedidosSinEnviar) {
		this.pedidosSinEnviar = pedidosSinEnviar;
	}

	public int getMotivosSinEnviar() {
		return motivosSinEnviar;
	}

	public void setMotivosSinEnviar(int motivosSinEnviar) {
		this.motivosSinEnviar = motivosSinEnviar;
	}

	public double getCoberturaGeneral() {
		return coberturaGeneral;
	}

	public void setCoberturaGeneral(double coberturaGeneral) {
		this.coberturaGeneral = coberturaGeneral;
	}

	public double getCoberturaEfectiva() {
		return coberturaEfectiva;
	}

	public void setCoberturaEfectiva(double coberturaEfectiva) {
		this.coberturaEfectiva = coberturaEfectiva;
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
