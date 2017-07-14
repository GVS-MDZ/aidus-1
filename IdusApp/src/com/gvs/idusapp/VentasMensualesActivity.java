package com.gvs.idusapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.gvs.utilidades.BaseDeDatos;
import com.gvs.utilidades.Funciones;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class VentasMensualesActivity extends Activity {

	private TextView lbl_detalle;
	private ListView grid;
	private Bundle bun;
	private Funciones funcion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ventas_mensuales);
		try{
			funcion = new Funciones();
			lbl_detalle=(TextView) findViewById(R.id.lbl_venta_periodo);
			grid=(ListView) findViewById(R.id.lista_venta_periodo);
			Date fecha = new Date();
			lbl_detalle.setText("Mes: " + funcion.dateToString_MMYYYY(fecha.getTime()));
			bun = getIntent().getExtras();
			mostrarLista(grid, funcion.dateToString_MMYYYY(fecha.getTime()));
		}catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug al listar ventas", Toast.LENGTH_SHORT);
			toast.show();
		}
		
	}
	
	private void mostrarLista(ListView grid, String periodo){
		try {
			List<Map<String, String>> list = new ArrayList <Map<String, String>>();	
			BaseDeDatos baseDeDatos = new BaseDeDatos(VentasMensualesActivity.this, funcion.BaseDeDatos(), null, funcion.Version());
			SQLiteDatabase db = baseDeDatos.getReadableDatabase();
			Cursor cur = null;
		
			String Sql = "SELECT DIA,IMPORTE,ACTUALIZACION FROM VENTAXPDIAS WHERE PERIODO='" + periodo + "'";
			cur = db.rawQuery(Sql, null);
			if (cur.getCount()>0){
				while (cur.moveToNext()){
					Map<String, String> datum = new HashMap<String, String>();
					int dia = cur.getInt(cur.getColumnIndex("DIA"));
					double Importe = cur.getDouble(cur.getColumnIndex("IMPORTE"));
					long actualizacion = cur.getLong(cur.getColumnIndex("ACTUALIZACION"));
					datum.put("DAT01","DIA: "+ funcion.format(dia, 2) +" - $"+ funcion.formatDecimal(Importe, 2) );
					datum.put("DAT02", funcion.dateToString_ddmmyyyy_hhmm(actualizacion));
					list.add(datum);
				}
				db.close();
				SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2,
						new String[] {"DAT01","DAT02"},new int[] {android.R.id.text1, android.R.id.text2});				
				grid.setAdapter(adapter);
			}			
		} catch (Exception e) {
			Toast toast =Toast.makeText(this,"Bug en la busqueda de ventas", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.ventas_mensuales, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == R.id.cerrar_sesion){
			startActivity(new Intent(getBaseContext(), MainActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
			finish();
		}else if(id == R.id.sincronizar){
			Intent i = new Intent(getApplicationContext(), SincronizadorActivity.class);
			bun.putString("SINCRONIZACION","GENERAL");
			i.putExtras(bun);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}
}
