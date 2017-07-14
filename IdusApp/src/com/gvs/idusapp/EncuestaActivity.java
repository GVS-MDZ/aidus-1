package com.gvs.idusapp;

import java.util.Date;
import java.util.List;
import com.gvs.controladores.ControladorEncuesta;
import com.gvs.controladores.ControladorInforme;
import com.gvs.modelos.Log;
import com.gvs.modelos.Encuesta;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EncuestaActivity extends Activity {
	
	private ListView list_view;
	private TextView lbl_cliente;
	private List<Encuesta> lista;
	private Bundle bun;
	private ControladorEncuesta controladorEncuesta;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_encuesta);
		try{
			controladorEncuesta = new ControladorEncuesta(EncuestaActivity.this);
			list_view = (ListView) findViewById(R.id.lista_encuesta);
			lbl_cliente=(TextView)findViewById(R.id.lbl_cliente);		
			bun = getIntent().getExtras();
			lbl_cliente.setText(bun.getString("NOMBRECLIENTE"));
			buscarEncuesta();
			mostrarLista();
			realizarOperacion();
		}catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug al listar encuestas", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	@Override
	protected void onResume() {
		try{
			buscarEncuesta();
			mostrarLista();
			realizarOperacion();
		}catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug al listar encuestas", Toast.LENGTH_SHORT);
			toast.show();
		}
		super.onResume();
	}
	
	private void buscarEncuesta(){
		try{
			lista=controladorEncuesta.buscarEncuestas(bun.getInt("CODIGOCLIENTE"), bun.getInt("VENDEDOR"));
		}catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug al buscar encuestas", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private void mostrarLista() {
		if(lista!=null){
			ArrayAdapter<Encuesta> adapter = new MyListAdapter();
			list_view.setAdapter(adapter);
		}
	}
	
	private void realizarOperacion() {
		list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				try{
					Encuesta question = lista.get(position);
					ControladorInforme controlador_log=new ControladorInforme(EncuestaActivity.this);
					Log log =new Log();
					log.setDescripcion("ENCUESTA - "+question.getNumero());
					log.setTipo(26);
					log.setFecha(new Date().getTime());
					log.setEstado(0);
					log.setVendedor(bun.getInt("VENDEDOR"));
					controlador_log.guardarLog(log);
					bun.putInt("NUMEROENCUESTA",question.getNumero());
					bun.putString("NOMBREENCUESTA", question.getDescripcion());
					Intent intent = new Intent(EncuestaActivity.this,PreguntaEncuestaActivity.class);
					intent.putExtras(bun);
					startActivity(intent);
				}catch(Exception ex){
					Toast toast =Toast.makeText(getApplicationContext(),"Bug en la encuesta seleccionada", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
	}
	
	private class MyListAdapter extends ArrayAdapter<Encuesta> {

		public MyListAdapter() {
			super(EncuestaActivity.this, R.layout.activity_encuesta, lista);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder holder;
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.item_encuesta, parent,false);
				holder = new ViewHolder();
				holder.image_operation=(ImageView) view.findViewById(R.id.img_estado_encuesta);
				holder.lbl_name= (TextView) view.findViewById(R.id.lbl_nombre_escuesta);
				holder.lbl_date= (TextView) view.findViewById(R.id.lbl_fecha_encuesta);
				view.setTag(holder);
			}else{
				holder = (ViewHolder) view.getTag();
			}
			Encuesta encuesta = lista.get(position);
			holder.image_operation.setImageResource(encuesta.getIcono());
			holder.lbl_name.setText(encuesta.getNumero()+" - "+encuesta.getDescripcion());
			holder.lbl_date.setText(encuesta.getFecha_inicio() +" - "+encuesta.getFecha_fin());

			return view;
		}

	}

	static class ViewHolder {
		ImageView image_operation;
		TextView lbl_name ;
		TextView lbl_date;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.encuesta, menu);
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
