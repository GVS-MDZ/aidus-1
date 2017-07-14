package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class ObjetivoVisita implements Parcelable {
	
	private int codigo_cliente;
	private String nombre;
	private String actual;
	private String visita;
	private String objetivo;
	private String porcentaje;
	private double cantidad_visitas;
	private double objetivo_actual;
	private String periodo;
	private double venta_anterior;
	private double venta_actual;
	private long fecha_actualizacion;
	private double porcentajes;

	public int getCodigo_cliente() {
		return codigo_cliente;
	}

	public void setCodigo_cliente(int codigo_cliente) {
		this.codigo_cliente = codigo_cliente;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getActual() {
		return actual;
	}

	public void setActual(String actual) {
		this.actual = actual;
	}

	public String getVisita() {
		return visita;
	}

	public void setVisita(String visita) {
		this.visita = visita;
	}

	public String getObjetivo() {
		return objetivo;
	}

	public void setObjetivo(String objetivo) {
		this.objetivo = objetivo;
	}

	public String getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(String porcentaje) {
		this.porcentaje = porcentaje;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public double getVenta_anterior() {
		return venta_anterior;
	}

	public void setVenta_anterior(double venta_anterior) {
		this.venta_anterior = venta_anterior;
	}

	public double getVenta_actual() {
		return venta_actual;
	}

	public void setVenta_actual(double venta_actual) {
		this.venta_actual = venta_actual;
	}

	public long getFecha_actualizacion() {
		return fecha_actualizacion;
	}

	public void setFecha_actualizacion(long fecha_actualizacion) {
		this.fecha_actualizacion = fecha_actualizacion;
	}

	public double getObjetivo_actual() {
		return objetivo_actual;
	}

	public void setObjetivo_actual(double objetivo_actual) {
		this.objetivo_actual = objetivo_actual;
	}

	public double getCantidad_visitas() {
		return cantidad_visitas;
	}

	public void setCantidad_visitas(double cantidad_visitas) {
		this.cantidad_visitas = cantidad_visitas;
	}

	public double getPorcentajes() {
		return porcentajes;
	}

	public void setPorcentajes(double porcentajes) {
		this.porcentajes = porcentajes;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

	}

}