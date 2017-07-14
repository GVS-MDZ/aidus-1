package com.gvs.idusapp;

import com.gvs.controladores.ControladorCliente;
import com.gvs.modelos.Cliente;
import com.gvs.modelos.ObjetivoVisita;
import com.gvs.utilidades.Funciones;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class InformacionClienteActivity extends Activity {

	private TextView lbl_nombre,lbl_domicilio,lbl_responsabilidad,lbl_canal,
	lbl_provincia,lbl_localidad,lbl_telefono,lbl_observaciones,lbl_saldo,
	lbl_periodo,lbl_periodo_anterior,lbl_periodo_actual,lbl_visita_semana,lbl_vanta_periodo_actual,
	lbl_fecha_actualizacion,lbl_porcentaje_alcanzado,lbl_objetivo_visita;
	private Funciones funcion;
	private int codigoCliente;
	private Bundle bun;
	private ControladorCliente controladorCliente;
	private Cliente cliente;
	private ObjetivoVisita objetivo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_informacion_cliente);	
		try{

			lbl_nombre=(TextView)findViewById(R.id.lbl_value_nombre);
			lbl_domicilio=(TextView)findViewById(R.id.lbl_value_domicilio);
			lbl_responsabilidad=(TextView)findViewById(R.id.lbl_value_responsabilidad);
			lbl_canal=(TextView)findViewById(R.id.lbl_value_canal);
			lbl_provincia=(TextView)findViewById(R.id.lbl_value_provincia);
			lbl_localidad=(TextView)findViewById(R.id.lbl_value_localidad);
			lbl_telefono=(TextView)findViewById(R.id.lbl_value_telefono);
			lbl_saldo=(TextView)findViewById(R.id.lbl_value_saldo);
			lbl_observaciones=(TextView)findViewById(R.id.lbl_value_observaciones);
			
			lbl_periodo=(TextView)findViewById(R.id.lbl_value_periodo);
			lbl_periodo_anterior=(TextView)findViewById(R.id.lbl_value_periodo_anterior);
			lbl_periodo_actual=(TextView)findViewById(R.id.lbl_value_periodo_actual);
			lbl_visita_semana=(TextView)findViewById(R.id.lbl_value_visita_semana);
			lbl_vanta_periodo_actual=(TextView)findViewById(R.id.lbl_value_venta_periodo_actual);
			lbl_fecha_actualizacion=(TextView)findViewById(R.id.lbl_value_fecha_actualizacion);
			lbl_porcentaje_alcanzado=(TextView)findViewById(R.id.lbl_value_porcentaje_alcanzado);
			lbl_objetivo_visita=(TextView)findViewById(R.id.lbl_value_objtivo_visita);
			
			funcion=new Funciones();
		    bun = getIntent().getExtras();
		    controladorCliente=new ControladorCliente(this);
			codigoCliente =bun.getInt("CODIGOCLIENTE");
			
			cliente=controladorCliente.buscarCliente(codigoCliente);
			if (cliente!=null){
				lbl_nombre.setText("NOMBRE: " +cliente.getCodigo()+ " - " + cliente.getNombre());
				lbl_domicilio.setText("DOMICILIO: "+cliente.getDireccion());
				lbl_responsabilidad.setText("RESPONSABILIDAD: "+cliente.getResponsabilidad());
				lbl_canal.setText("CANAL: "+cliente.getCanal());
				lbl_provincia.setText("PROVINCIA: "+cliente.getProvincia());
				lbl_localidad.setText("LOCALIDAD: "+cliente.getLocalidad());
				lbl_telefono.setText("TELEFONO: "+cliente.getTelefono());
				lbl_saldo.setText("SALDO: "+String.valueOf(funcion.formatDecimal(cliente.getSaldo(),2)));
				lbl_observaciones.setText("OBSERVACIONES: "+cliente.getObservaciones());
			}
			
			objetivo=controladorCliente.buscarObjetivoPorVisita(codigoCliente);
			if (objetivo!=null){
				double objetivoActual=objetivo.getObjetivo_actual();
				double cantidadVisitas=objetivo.getCantidad_visitas();	
				lbl_periodo.setText(objetivo.getPeriodo());
				lbl_periodo_anterior.setText(funcion.formatDecimal(objetivo.getVenta_anterior(), 2));
				lbl_periodo_actual.setText(funcion.formatDecimal(objetivoActual, 2));
				lbl_visita_semana.setText(funcion.formatDecimal(cantidadVisitas, 2));
				lbl_vanta_periodo_actual.setText(funcion.formatDecimal(objetivo.getVenta_actual(), 2));
				lbl_fecha_actualizacion.setText(funcion.dateToString_ddmmyyyy_hhmm(objetivo.getFecha_actualizacion()));
				lbl_porcentaje_alcanzado.setText(funcion.formatDecimal(objetivo.getPorcentajes(), 2));
				if (objetivoActual>0 && cantidadVisitas>0){
					double objetivoXVisita = objetivoActual/cantidadVisitas;
					lbl_objetivo_visita.setText(funcion.formatDecimal(objetivoXVisita, 2));
				}
			}
		}catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug al mostrar informacion del cliente", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.informacion_cliente, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.cerrar_sesion){
		startActivity(new Intent(getBaseContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
		finish();
	}else if(id == R.id.sincronizar){
		Intent intent = new Intent(getApplicationContext(), SincronizadorActivity.class);
		bun.putString("SINCRONIZACION","GENERAL");
		intent.putExtras(bun);
		startActivity(intent);
	}
		return super.onOptionsItemSelected(item);
	}

}
