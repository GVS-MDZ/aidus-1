package com.gvs.idusapp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.gvs.controladores.ControladorArticulo;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BusquedaArticuloActivity extends Activity {
	private ListView list_view;
	private int rubro,subrubro;
	private String detalle="";
	private Bundle bun;;
	private ControladorArticulo controladorArticulo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_busqueda_articulo);
		try{
			list_view = (ListView) findViewById(R.id.list_detalle);
			bun = getIntent().getExtras();
			rubro=bun.getInt("RUBRO");
			subrubro=bun.getInt("SUBRUBRO");
			detalle=bun.getString("DETALLEBUSQUEDA");
			controladorArticulo = new ControladorArticulo(BusquedaArticuloActivity.this);	
		    if(rubro==0 || subrubro==0){
		    	mostrarLista(controladorArticulo.buscarPorDetalle(bun.getString("DETALLE")));
		    }else{
		    	mostrarLista(controladorArticulo.buscarPorRubroSubro(rubro, subrubro,detalle));
		    }	
		    list_view.setOnItemClickListener(new OnItemClickListener() {
				
				public void onItemClick(AdapterView<?> arg0, View v, int position, long id){
					try{
						@SuppressWarnings("unchecked")
						HashMap<String, String> map = (HashMap<String, String>)list_view.getItemAtPosition(position);
						String[] dato01 = map.get("DATO06").toString().split("-");
						String codigoArticulo = dato01[0].toString().trim();
						Intent intent = new Intent();
						intent.setData(Uri.parse(codigoArticulo));
						setResult(RESULT_OK,intent);
						finish();
					}catch(Exception ex){
						Toast toast =Toast.makeText(getApplicationContext(),"Bug en articulos seleccionado", Toast.LENGTH_SHORT);
						toast.show();
					}
				}			
				
			});
		}catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug al listar articulos", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private void mostrarLista(List<Map<String, String>> lista){
		if (lista!=null){
			try{
				SimpleAdapter adapter = new SimpleAdapter(BusquedaArticuloActivity.this, lista, android.R.layout.simple_list_item_2,
						new String[] {"DATO05","DATO06"},
						new int[] {android.R.id.text1,android.R.id.text2});
				list_view.setAdapter(adapter);
			}catch(Exception ex){
				Toast toast =Toast.makeText(getApplicationContext(),"Bug al listar articulos", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	
}
