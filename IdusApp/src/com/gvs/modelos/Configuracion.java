package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Configuracion implements Parcelable {

	private int diaCompleto;
	private int muestraSugeridos;
	private int servicioGeo;
	private int otroDia;
	private int cantidadItems;
	private int descuento;
	private int aiuds;
	private int control_deuda;
	private int dia_contol;

	public int getDiaCompleto() {
		return diaCompleto;
	}

	public void setDiaCompleto(int diaCompleto) {
		this.diaCompleto = diaCompleto;
	}

	public int getMuestraSugeridos() {
		return muestraSugeridos;
	}

	public void setMuestraSugeridos(int muestraSugeridos) {
		this.muestraSugeridos = muestraSugeridos;
	}

	public int getServicioGeo() {
		return servicioGeo;
	}

	public void setServicioGeo(int servicioGeo) {
		this.servicioGeo = servicioGeo;
	}

	public int getOtroDia() {
		return otroDia;
	}

	public void setOtroDia(int otroDia) {
		this.otroDia = otroDia;
	}

	public int getCantidadItems() {
		return cantidadItems;
	}

	
	public void setCantidadItems(int cantidadItems) {
		this.cantidadItems = cantidadItems;
	}

	public int getDescuento() {
		return descuento;
	}

	public void setDescuento(int descuento) {
		this.descuento = descuento;
	}

	public int getAiuds() {
		return aiuds;
	}

	public void setAiuds(int aiuds) {
		this.aiuds = aiuds;
	}

	public int getControl_deuda() {
		return control_deuda;
	}

	public void setControl_deuda(int control_deuda) {
		this.control_deuda = control_deuda;
	}

	public int getDia_contol() {
		return dia_contol;
	}

	public void setDia_contol(int dia_contol) {
		this.dia_contol = dia_contol;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {


	}

}