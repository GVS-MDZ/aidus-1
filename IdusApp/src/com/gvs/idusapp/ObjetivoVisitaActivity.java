package com.gvs.idusapp;

import java.util.Date;
import java.util.List;
import com.gvs.controladores.ControladorCliente;
import com.gvs.controladores.ControladorInforme;
import com.gvs.modelos.Log;
import com.gvs.modelos.ObjetivoVisita;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ObjetivoVisitaActivity extends Fragment {
	
	private ListView list_view;
	private Bundle bun;
	private int vendedor, dia;
	private List<ObjetivoVisita> lista;
	private ControladorCliente controladorCliente;
	private ObjetivoVisita objetive;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View V = inflater.inflate(R.layout.activity_objetivo_visita, container, false);
		list_view = (ListView)V.findViewById(R.id.lista_objetivos);
		return V;
	}
	
	@Override
	public void onResume() {
		try{
			controladorCliente=new ControladorCliente(getActivity());
			bun = getActivity().getIntent().getExtras();
			dia = bun.getInt("DIA");
			vendedor = bun.getInt("VENDEDOR");
			buscarObjetivos();
			mostrarLista();
			realizarOperacion();
			}catch(Exception ex){
				Toast toast =Toast.makeText(getActivity(),"Bug en los objetivos por visita", Toast.LENGTH_SHORT);
				toast.show();
			}
		super.onResume();
	}
	
	private void buscarObjetivos(){
		try{
			lista=controladorCliente.buscarObjetivosPorVisita(dia, vendedor);
		}catch(Exception ex){
			Toast toast =Toast.makeText(getActivity(),"Bug en la busqueda de objetivos", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private void mostrarLista() {
		if(lista!=null){
			ArrayAdapter<ObjetivoVisita> adapter = new MyListAdapter();
			list_view.setAdapter(adapter);
		}
	}
	
	private void realizarOperacion() {
		
		list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				try{
					
					ObjetivoVisita objetive = lista.get(position);
					ControladorInforme controlador_log=new ControladorInforme(getActivity());
					Log log =new Log();
					log.setDescripcion("OBJETIVO CLIENTE - "+objetive.getCodigo_cliente());
					log.setTipo(6);
					log.setFecha(new Date().getTime());
					log.setEstado(0);
					log.setVendedor(vendedor);
					controlador_log.guardarLog(log);
					
					bun.putString("NOMBRECLIENTE", objetive.getNombre());
					bun.putInt("CODIGOCLIENTE",objetive.getCodigo_cliente());
					Intent intent = new Intent(getActivity(),MenuClienteActivity.class);
					intent.putExtras(bun);
					startActivity(intent);
				}catch(Exception ex){
					Toast toast =Toast.makeText(getActivity(),"Bug en el motivo seleccionado", Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
	}
	
	private class MyListAdapter extends ArrayAdapter<ObjetivoVisita> {

		public MyListAdapter() {
			super(getActivity(), R.layout.activity_objetivo_visita, lista);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			try{
				ViewHolder holder;
				if (view == null) {
					view = getActivity().getLayoutInflater().inflate(R.layout.item_objetivo, parent,false);
					holder = new ViewHolder();
					holder.lbl_nombre=(TextView) view.findViewById(R.id.lbl_nombre_cliente);
					holder.lbl_actual= (TextView) view.findViewById(R.id.lbl_actual);
					holder.lbl_visita=(TextView) view.findViewById(R.id.lbl_cantidad_visita);
					holder.lbl_objetivo= (TextView) view.findViewById(R.id.lbl_objetivo);
					holder.lbl_porcentaje=(TextView) view.findViewById(R.id.lbl_porcentaje);
					view.setTag(holder);
				}else{
					holder = (ViewHolder) view.getTag();
				}
				objetive = lista.get(position);
				holder.lbl_nombre.setText(objetive.getCodigo_cliente()+" - "+objetive.getNombre());
				holder.lbl_actual.setText(objetive.getActual());
				holder.lbl_visita.setText(objetive.getVisita());
				holder.lbl_objetivo.setText(objetive.getObjetivo());
				holder.lbl_porcentaje.setText(objetive.getPorcentaje());	
			}catch(Exception ex){
				Toast toast =Toast.makeText(getActivity(),"Bug en los objetivos por visita", Toast.LENGTH_SHORT);
				toast.show();
			}
			return view;
		}

	}
	
	static class ViewHolder {
		TextView lbl_nombre;
		TextView lbl_actual;
		TextView lbl_visita;
		TextView lbl_objetivo;
		TextView lbl_porcentaje ;
	}

}
