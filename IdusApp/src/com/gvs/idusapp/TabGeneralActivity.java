package com.gvs.idusapp;

import java.util.Date;
import com.gvs.controladores.ControladorInforme;
import com.gvs.modelos.Log;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;

public class TabGeneralActivity extends FragmentActivity {
	 private FragmentTabHost tabHost;
	 private Bundle bun; 
	 private String dia_semana;
	  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bun=getIntent().getExtras();
        int dia = bun.getInt("DIA");
        switch (dia) {
		case 1:
			dia_semana="Lunes";
			break;
		case 2:
			dia_semana="Martes";
			break;
		case 3:
			dia_semana="Miércoles";
			break;
		case 4:
			dia_semana="Jueves";
			break;
		case 5:
			dia_semana="Viernes";
			break;
		case 6:
			dia_semana="Sábado";
			break;
		default:
			dia_semana="Sin Ruta";
			break;
		}
        setTitle(dia_semana);
        setContentView(R.layout.activity_tab_general);  
        ResumenActivity resume=new ResumenActivity();
        resume.setArguments(bun);
        ObjetivoVisitaActivity objetive=new ObjetivoVisitaActivity();
        objetive.setArguments(bun);
        
        tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);    
        tabHost.addTab(tabHost.newTabSpec("resume").setIndicator("Resumen"),ResumenActivity.class, null);
        tabHost.addTab(tabHost.newTabSpec("objetive").setIndicator("Objetivos"),ObjetivoVisitaActivity.class, null);
        tabHost.addTab(tabHost.newTabSpec("order").setIndicator("Pedidos"),PedidosDiariosActivity.class, null);
        
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tab_general, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id=item.getItemId();
		if (id == R.id.cierre_dia) {
			try{
				ControladorInforme controlador_log=new ControladorInforme(TabGeneralActivity.this);
				Log log =new Log();
				log.setDescripcion("MENU CIERRE DE DIA - "+dia_semana);
				log.setTipo(9);
				log.setFecha(new Date().getTime());
				log.setEstado(0);
				log.setVendedor(bun.getInt("VENDEDOR"));
				controlador_log.guardarLog(log);
				Intent i = new Intent(this, TabCierreDiaActivity.class);
				i.putExtras(bun);
				startActivity(i);
			}catch(Exception ex){
				
			}
			return true;			
		}else if(id == R.id.cerrar_sesion){
			startActivity(new Intent(getBaseContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
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