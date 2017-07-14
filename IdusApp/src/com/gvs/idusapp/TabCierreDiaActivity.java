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

public class TabCierreDiaActivity extends FragmentActivity {
	  private FragmentTabHost tabHost;
	  private Bundle bun;
		
	  public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        bun=getIntent().getExtras();
	        setContentView(R.layout.activity_tab_cierre_dia);  
	        tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
	        tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
	        tabHost.addTab(tabHost.newTabSpec("close").setIndicator("Cierre"),CierreDiaActivity.class, null);
	        tabHost.addTab(tabHost.newTabSpec("order").setIndicator("Pedidos"),PedidoNoEnviadoActivity.class, null);       
	  }
	  
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.tab_cierre_dia, menu);
			return true;
	  }

	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
			int id=item.getItemId();
			try{
				if (id == R.id.mail) {
					ControladorInforme controlador_log=new ControladorInforme(TabCierreDiaActivity.this);
					Log log =new Log();
					log.setDescripcion("MENU MAIL");
					log.setTipo(38);
					log.setFecha(new Date().getTime());
					log.setEstado(0);
					log.setVendedor(bun.getInt("VENDEDOR"));
					controlador_log.guardarLog(log);
					Intent i = new Intent(this, PedidoMailActivity.class);
					i.putExtras(bun);
					startActivity(i);
					return true;			
				}else if (id == R.id.cerrar_sesion) {
					startActivity(new Intent(getBaseContext(), MainActivity.class)
		            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
					finish();
				}else if(id == R.id.sincronizar){
					Intent i = new Intent(getApplicationContext(), SincronizadorActivity.class);
					bun.putString("SINCRONIZACION","GENERAL");
					i.putExtras(bun);
					startActivity(i);
				}
			}catch(Exception ex){
				
			}
			return super.onOptionsItemSelected(item);
		}
}
