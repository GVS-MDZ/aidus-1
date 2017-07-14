package com.gvs.idusapp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.gvs.controladores.ControladorInforme;
import com.gvs.controladores.ControladorPedido;
import com.gvs.modelos.Log;
import com.gvs.modelos.OperacionCliente;
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

public class MenuClienteActivity extends Activity {

	private List<OperacionCliente> lista;
	private ListView listView;
	private Bundle bun;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_cliente);
		try {
			listView = (ListView) findViewById(R.id.lista_operaciones_clientes);
			lista = new ArrayList<OperacionCliente>();
			bun = getIntent().getExtras();
			setTitle(bun.getString("NOMBRECLIENTE"));
			llenarLista();
			mostrarLista();
			realizarOperacion();
		} catch (Exception ex) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Bug al mostrar las operaciones", Toast.LENGTH_SHORT);
			toast.show();
			finish();
		}
	}

	private void llenarLista() {
		lista.add(new OperacionCliente("Realizar Pedidos", R.drawable.pedir));
		lista.add(new OperacionCliente("Datos del Cliente",R.drawable.info_cliente));
		lista.add(new OperacionCliente("Historico de Pedidos",R.drawable.historico));
		lista.add(new OperacionCliente("Motivos de no Venta",R.drawable.motivos));
		lista.add(new OperacionCliente("Encuestas al Cliente",R.drawable.encuesta2));
		lista.add(new OperacionCliente("Deuda y Cobranza", R.drawable.cobranza));
		lista.add(new OperacionCliente("Localizacion GPS", R.drawable.gps));
		lista.add(new OperacionCliente("Fotos", R.drawable.camara));
	}

	private void mostrarLista() {
		ArrayAdapter<OperacionCliente> adapter = new MyListAdapter();
		listView.setAdapter(adapter);
	}

	private void realizarOperacion() {
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			Intent intent;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					OperacionCliente operacion = lista.get(position);
					ControladorInforme controlador_log = new ControladorInforme(
							MenuClienteActivity.this);
					Log log = new Log();
					log.setDescripcion("OPERACION - " + operacion.getNombre());
					log.setTipo(12);
					log.setFecha(new Date().getTime());
					log.setEstado(0);
					log.setVendedor(bun.getInt("VENDEDOR"));
					controlador_log.guardarLog(log);
					switch (position) {
					case 0: {
					//	ControladorPedido pedidos = new ControladorPedido();
						ControladorPedido pedidos = new ControladorPedido(MenuClienteActivity.this);
					//	pedidos.setContexto(MenuClienteActivity.this);
						intent = new Intent(MenuClienteActivity.this,
								RealizarPedidoActivity.class);
						bun.putLong("NUMERO", pedidos.numeroPedido());
						intent.putExtras(bun);
						startActivity(intent);
						break;
					}
					case 1: {
						intent = new Intent(MenuClienteActivity.this,
								InformacionClienteActivity.class);
						intent.putExtras(bun);
						startActivity(intent);
						break;
					}
					case 2: {
						intent = new Intent(MenuClienteActivity.this,
								PedidoClienteActivity.class);
						intent.putExtras(bun);
						startActivity(intent);
						break;
					}
					case 3: {
						intent = new Intent(MenuClienteActivity.this,
								MotivoNoVentaActivity.class);
						intent.putExtras(bun);
						startActivity(intent);
						break;
					}
					case 4: {
						intent = new Intent(MenuClienteActivity.this,
								EncuestaActivity.class);
						intent.putExtras(bun);
						startActivity(intent);
						break;
					}
					case 5: {
						intent = new Intent(MenuClienteActivity.this,
								ComprobanteActivity.class);
						intent.putExtras(bun);
						startActivity(intent);
						break;
					}
					case 6: {
						intent = new Intent(MenuClienteActivity.this,
								GpsClienteActivity.class);
						intent.putExtras(bun);
						startActivity(intent);
						break;
					}
					case 7: {
						intent = new Intent(MenuClienteActivity.this,
								CamaraActivity.class);
						intent.putExtras(bun);
						startActivity(intent);
						break;
					}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					Toast toast = Toast.makeText(getApplicationContext(),
							"Bug en el item seleccionado", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
	}

	private class MyListAdapter extends ArrayAdapter<OperacionCliente> {

		public MyListAdapter() {
			super(MenuClienteActivity.this,
					R.layout.activity_menu_cliente, lista);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = getLayoutInflater().inflate(
						R.layout.item_menu_cliente, parent, false);
			}
			OperacionCliente operaciones = lista.get(position);
			ImageView image_operacion = (ImageView) view
					.findViewById(R.id.img_operation);
			image_operacion.setImageResource(operaciones.getIcono());

			TextView lbl_operacion = (TextView) view
					.findViewById(R.id.lbl_operation);
			lbl_operacion.setText(operaciones.getNombre());
			return view;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_cliente, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.cerrar_sesion) {
			startActivity(new Intent(getBaseContext(), MainActivity.class)
					.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
							| Intent.FLAG_ACTIVITY_SINGLE_TOP));
			finish();
		} else if (id == R.id.sincronizar) {
			Intent intent = new Intent(getApplicationContext(), SincronizadorActivity.class);
			bun.putString("SINCRONIZACION", "GENERAL");
			intent.putExtras(bun);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
}
