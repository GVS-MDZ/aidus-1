package com.gvs.idusapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.gvs.controladores.ControladorConfiguracion;
import com.gvs.controladores.ControladorInforme;
import com.gvs.modelos.Configuracion;
import com.gvs.modelos.Dia;
import com.gvs.modelos.Log;
import com.gvs.utilidades.Funciones;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MenuDiaActivity extends Activity {

	private ListView listView;
	private List<Dia> lista;
	private int vendedor;
	private Funciones funcion;
	private Configuracion configuracion;
	private Bundle bun;
	private ControladorConfiguracion controladorConfiguracion;
	private ControladorInforme controlador_log;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_dia);
		listView = (ListView) findViewById(R.id.lista_dias);
		bun = getIntent().getExtras();
		vendedor = bun.getInt("VENDEDOR");
		funcion = new Funciones();
		controladorConfiguracion=new ControladorConfiguracion(this); 
		configuracion = controladorConfiguracion.buscarConfiguracion();
	    controlador_log=new ControladorInforme(MenuDiaActivity.this);
	    
		lista=new ArrayList<Dia>();
		llenarLista();
		mostrarLista();
		realizarOperacion();
	}
	
	private void llenarLista() {
		lista.add(new Dia("Lu","LUNES"));
		lista.add(new Dia("Ma","MARTES"));
		lista.add(new Dia("Mi","MIERCOLES"));
		lista.add(new Dia("Ju","JUEVES"));
		lista.add(new Dia("Vi","VIERNES"));
		lista.add(new Dia("Sa","SABADO"));
		lista.add(new Dia("Sr","SIN RUTA"));
	}
	
	private void mostrarLista() {
		ArrayAdapter<Dia> adapter = new MyListAdapter();
		listView.setAdapter(adapter);
	}
	
	private void realizarOperacion(){
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					try{
						Dia dia=lista.get(position);
						switch (position) {
							case 0:			
								bun.putInt("DIA", 1);
								bun.putInt("VENDEDOR",vendedor);
								break;
							case 1:	
								bun.putInt("DIA", 2);
								bun.putInt("VENDEDOR",vendedor);
								break;
							case 2:	
								bun.putInt("DIA", 3);
								bun.putInt("VENDEDOR",vendedor);
								break;
							case 3:	
								bun.putInt("DIA", 4);
								bun.putInt("VENDEDOR",vendedor);
								break;
							case 4:	
								bun.putInt("DIA", 5);
								bun.putInt("VENDEDOR",vendedor);
								break;
							case 5:	
								bun.putInt("DIA", 6);
								bun.putInt("VENDEDOR",vendedor);
								break;
							case 6:	
								bun.putInt("DIA", 0);
								bun.putInt("VENDEDOR",vendedor);
								break;
							default:
								break;
						}
						Log log =new Log();
						log.setDescripcion("VISITA - "+ dia.getDia());
						log.setTipo(3);
						log.setFecha(new Date().getTime());
						log.setEstado(0);
						log.setVendedor(vendedor);
						controlador_log.guardarLog(log);
						boolean pasa=false;
						if (configuracion.getOtroDia()==1){
							pasa=true;
						} else {
							if (funcion.optenerNumeroDiaActual()==bun.getInt("DIA")){
								pasa=true;
							} 
						}
						
						if (pasa) {
							Intent intent = new Intent(MenuDiaActivity.this, TabGeneralActivity.class);
							intent.putExtras(bun);
							startActivity(intent);
						}		
				}catch(Exception ex){
					Toast toast =Toast.makeText(getApplicationContext(),"Bug en el dia seleccionado", Toast.LENGTH_SHORT);
					toast.show();
				}	
			}
		});
	}
	
	private class MyListAdapter extends ArrayAdapter<Dia> {

		public MyListAdapter() {
			super(MenuDiaActivity.this,R.layout.activity_menu_dia, lista);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.item_menu_dia, parent, false);
			}
			Dia dia = lista.get(position);
			TextView dia_corto = (TextView) view.findViewById(R.id.lbl_dia);
			dia_corto.setText(dia.getDia());

			TextView dia_largo = (TextView) view.findViewById(R.id.lbl_item_day);
			dia_largo.setText(dia.getDia_completo());
			return view;
		}

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		try{	
	        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	        		AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
	        		alertbox.setTitle("Advertencia");
	        		alertbox.setMessage("¿Desea salir?");
	        		alertbox.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface arg0, int arg1) {
	        				onBackPressed();
	        			}
	        		});
	        		alertbox.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	        			public void onClick(DialogInterface arg0, int arg1) {
	        			
	        			}
	        		});
	        		alertbox.show();    	        	
	            return true;
	        }
		}catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug no puede validar el regreso", Toast.LENGTH_SHORT);
			toast.show();
		}
        return super.onKeyDown(keyCode, event);
    }
    
	@Override
	public void onBackPressed() {
    	super.onBackPressed();
  
	}
	
	@Override
 	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_dia, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.ventas_periodo) {	
			try{
				Log log =new Log();
				log.setDescripcion("MENU VENTAS PERIODO");
				log.setTipo(4);
				log.setFecha(new Date().getTime());
				log.setEstado(0);
				log.setVendedor(vendedor);
				controlador_log.guardarLog(log);
				Intent intent = new Intent(MenuDiaActivity.this, VentasMensualesActivity.class);
				intent.putExtras(bun);
				startActivity(intent);
			}catch(Exception ex){
				
			}
			return true;
		}else if(id == R.id.cerrar_sesion){
			startActivity(new Intent(getBaseContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
			finish();
		}else if(id == R.id.sincronizar){
			Intent intent= new Intent(getApplicationContext(), SincronizadorActivity.class);
			bun.putString("SINCRONIZACION","GENERAL");
			intent.putExtras(bun);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		if(controladorConfiguracion.estaAbierto() || controlador_log.estaAbierto())
		{
		android.util.Log.e("MenuDiaActivity", "onPause");	
		}
		super.onPause();
	}

}
