package com.gvs.idusapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.gvs.controladores.ControladorCliente;
import com.gvs.controladores.ControladorInforme;
import com.gvs.modelos.Cliente;
import com.gvs.modelos.Log;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ClienteActivity extends Activity {
	
	private ListView list_view;
	private EditText txt_filtro;
	private RadioGroup radio_group;
	private RadioButton radio_nombre,radio_direccion,radio_visita;
	private int vendedor,dia;
	private Bundle bun;
	private List<Cliente> lista;
	private ControladorCliente controladorCliente;
	private ArrayAdapter<Cliente> adapter ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cliente);
		try{
			list_view = (ListView) findViewById(R.id.lista_clientes);
			txt_filtro=(EditText)findViewById(R.id.txt_filtro);
			radio_group = (RadioGroup) findViewById(R.id.radio_tipo_texto);
			radio_nombre = (RadioButton) findViewById(R.id.radio_tipo_numerico);
			radio_direccion = (RadioButton) findViewById(R.id.radio_tipo_alfa);
			radio_visita = (RadioButton) findViewById(R.id.radio_detalle);
			
			bun = getIntent().getExtras();
			controladorCliente=new ControladorCliente(this);
			dia = bun.getInt("DIA");
			vendedor = bun.getInt("VENDEDOR");
			txt_filtro.getBackground().setColorFilter(Color.parseColor("#502b70"), PorterDuff.Mode.SRC_ATOP);
			
			BuscarClientes(dia, 0);
			mostrarLista();
			realizarOperacion();
			
			radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(RadioGroup group, int checkedId) {
							if (radio_nombre.isChecked() == true) {
								BuscarClientes(dia, 1);
								mostrarLista();
							} else if (radio_direccion.isChecked() == true) {
								BuscarClientes(dia, 2);
								mostrarLista();
							} else if (radio_visita.isChecked() == true) {
								BuscarClientes(dia, 0);
								mostrarLista();
							}
						}
			});
			
			txt_filtro.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before,int count) {
					adapter.getFilter().filter(s.toString());
					
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					
				}

				@Override
				public void afterTextChanged(Editable s) {
				

				}
			});
			
		}catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug en la busqueda de clientes", Toast.LENGTH_SHORT);
			toast.show();
		}
		
	}
	
	@Override
	protected void onRestart() {
		try{
			radio_visita.setChecked(true);
			BuscarClientes(dia, 0);
			mostrarLista();
		}catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug en la busqueda de clientes", Toast.LENGTH_SHORT);
			toast.show();
		}
		super.onRestart();
	}

	private void BuscarClientes(int dia, int sOrden) {
		try{
			lista=controladorCliente.buscarClientes(dia, sOrden, vendedor);
		}catch(Exception ex){
			Toast toast =Toast.makeText(this,"Bug al listar clientes", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	private void mostrarLista() {
		if(lista!=null){
			adapter = new MyListAdapter(lista);
			list_view.setAdapter(adapter);
		}
	}

	private void realizarOperacion() {
		
		list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {		
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				try{
					Cliente cliente = lista.get(position);
					ControladorInforme controlador_log=new ControladorInforme(ClienteActivity.this);
					Log log =new Log();
					log.setDescripcion("CLIENTE - "+cliente.getCodigo());
					log.setTipo(10);
					log.setFecha(new Date().getTime());
					log.setEstado(0);
					log.setVendedor(vendedor);
					controlador_log.guardarLog(log);
					bun.putString("NOMBRECLIENTE", cliente.getNombre());
					bun.putInt("CODIGOCLIENTE", cliente.getCodigo());
					Intent intent = new Intent(ClienteActivity.this,MenuClienteActivity.class);
					intent.putExtras(bun);
					startActivity(intent);
				}catch(Exception ex){
					Toast toast =Toast.makeText(getApplicationContext(),"Bug en el cliente seleccionado", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
	}

	private class MyListAdapter extends ArrayAdapter<Cliente> {
		private FiltroCliente filter;
		private List<Cliente> lista_original;
		private ArrayList<Cliente> lista_filtro;
		public MyListAdapter(List<Cliente> lista_clientes) {
			super(ClienteActivity.this, R.layout.activity_cliente, lista);
			this.lista_original = new ArrayList<Cliente>();
			this.lista_original.addAll(lista_clientes);
			this.lista_filtro = new ArrayList<Cliente>();
			this.lista_filtro.addAll(lista_clientes);
		}
		
		@Override
		public Filter getFilter() {
			if (filter == null) {
				filter = new FiltroCliente();
			}
			return filter;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder holder;
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.item_cliente, parent,false);
				holder = new ViewHolder();
				holder.image_operation= (ImageView) view.findViewById(R.id.img_estado_cliente);
				holder.lbl_name= (TextView) view.findViewById(R.id.lbl_nombre_cliente);
				holder.lbl_address=(TextView) view.findViewById(R.id.lbl_direccion_cliente);
				view.setTag(holder);
			}else{
				holder = (ViewHolder) view.getTag();
			}
			Cliente cliente = lista.get(position);
			holder.image_operation.setImageResource(cliente.getIcono());
			holder.lbl_name.setText(cliente.getCodigo()+" - "+cliente.getNombre());
			holder.lbl_address.setText(cliente.getDireccion());
			return view;	
		}
		
		@SuppressLint("DefaultLocale")
		private class FiltroCliente extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				constraint = constraint.toString().toLowerCase();
				FilterResults result = new FilterResults();
				if (constraint != null && constraint.toString().length() > 0) {
					ArrayList<Cliente> itemsFiltrados = new ArrayList<Cliente>();
					
					for (int i = 0, l = lista_original.size(); i < l; i++) {
						Cliente cliente = lista_original.get(i);
							if (cliente.getNombre().toString().toLowerCase().contains(constraint))
								itemsFiltrados.add(cliente);
					}
					result.count = itemsFiltrados.size();
					result.values = itemsFiltrados;
				} else {
					synchronized (this) {
						result.values = lista_original;
						result.count = lista_original.size();
					}
				}
				return result;
			}
			
			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,FilterResults results) {
				lista_filtro = (ArrayList<Cliente>) results.values;
				notifyDataSetChanged();
				clear();
				for (int i = 0, l = lista_filtro.size(); i < l; i++)
					add(lista_filtro.get(i));
					notifyDataSetInvalidated();
			}
		}
		
	}
	
	static class ViewHolder {
		ImageView image_operation;
		TextView lbl_name;
		TextView lbl_address;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.cliente, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();		
		if (id == R.id.clientes_mapa) {
			try{
				ControladorInforme controlador_log=new ControladorInforme(ClienteActivity.this);
				Log log =new Log();
				log.setDescripcion("MAPA CLIENTES");
				log.setTipo(11);
				log.setFecha(new Date().getTime());
				log.setEstado(0);
				log.setVendedor(vendedor);
				controlador_log.guardarLog(log);
				Intent intent = new Intent(ClienteActivity.this, MapaClientesActivity.class);
				intent.putExtras(bun);
				startActivity(intent);
			}catch(Exception ex){
				
			}
		}else if(id == R.id.cerrar_sesion){
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
