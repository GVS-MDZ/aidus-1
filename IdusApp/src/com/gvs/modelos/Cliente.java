package com.gvs.modelos;

import android.os.Parcel;
import android.os.Parcelable;

public class Cliente implements Parcelable {

	private int codigo;
	private String nombre;
	private String direccion;
	private String responsabilidad;
	private String canal;
	private String localidad;
	private String provincia;
	private String telefono;
	private String observaciones;
	private int codigo_lista;
	private double saldo;
	private int codigo_vendedor;
	private int icono;
	private double latitud;
	private double longitud;
	private int habilitargps;
	private int editado;


	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getCodigo_lista() {
		return codigo_lista;
	}

	public void setCodigo_lista(int codigo_lista) {
		this.codigo_lista = codigo_lista;
	}

	public int getCodigo_vendedor() {
		return codigo_vendedor;
	}

	public void setCodigo_vendedor(int codigo_vendedor) {
		this.codigo_vendedor = codigo_vendedor;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getResponsabilidad() {
		return responsabilidad;
	}

	public void setResponsabilidad(String responsabilidad) {
		this.responsabilidad = responsabilidad;
	}

	public String getCanal() {
		return canal;
	}

	public void setCanal(String canal) {
		this.canal = canal;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public int getIcono() {
		return icono;
	}

	public void setIcono(int icono) {
		this.icono = icono;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
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

	public int getHabilitargps() {
		return habilitargps;
	}

	public void setHabilitargps(int habilitargps) {
		this.habilitargps = habilitargps;
	}

	public int getEditado() {
		return editado;
	}

	public void setEditado(int editado) {
		this.editado = editado;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {


	}

}
