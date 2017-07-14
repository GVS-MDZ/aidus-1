package com.gvs.idusapp;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.gvs.controladores.ControladorArticulo;
import com.gvs.controladores.ControladorInforme;
import com.gvs.modelos.Log;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LanzamientoActivity extends Activity {
	private ListView lista;
	private Bundle bun;
	private ControladorArticulo controladorArticulo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lanzamiento);
		try{
			lista=(ListView) findViewById(R.id.list_lanzamientos);
			bun = getIntent().getExtras();
			controladorArticulo = new ControladorArticulo(this);
			mostrarLista(controladorArticulo.buscarLanzamientos(bun.getInt("CODIGOCLIENTE")));
			realizarOperacion();
		} catch (Exception e){
			Toast toast =Toast.makeText(this,"Bug al listar articulos lanzamientos", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private void mostrarLista(List<Map<String, String>> list){
		if (list!=null){
			try{
				SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
						new String[] {"DATO10","DATO11"},new int[] {android.R.id.text1, android.R.id.text2});
				lista.setAdapter(adapter);
			} catch (Exception e){
				Toast toast =Toast.makeText(this,"Bug al listar articulos", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}
	
	private void realizarOperacion(){
		lista.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int position, long id){
				try{
					@SuppressWarnings("unchecked")
					HashMap<String, String> map = (HashMap<String, String>) lista.getItemAtPosition(position);
					String[] dato01 = map.get("DATO10").toString().split("-");
					String codigoArticulo = dato01[0].toString().trim();
				
					ControladorInforme controlador_log=new ControladorInforme(LanzamientoActivity.this);
					Log log =new Log();
					log.setDescripcion("ARTICULO LANZAMIENTO - "+codigoArticulo);
					log.setTipo(23);
					log.setFecha(new Date().getTime());
					log.setEstado(0);
					log.setVendedor(bun.getInt("VENDEDOR"));
					controlador_log.guardarLog(log);
					
					Intent intent = new Intent();
					intent.setData(Uri.parse(codigoArticulo));
					setResult(RESULT_OK,intent);
					finish();
				} catch (Exception e){
					Toast toast =Toast.makeText(getApplicationContext(),"Bug en articulo seleccionado", Toast.LENGTH_SHORT);
					toast.show();
				}
			}			
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.articulo, menu);
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
