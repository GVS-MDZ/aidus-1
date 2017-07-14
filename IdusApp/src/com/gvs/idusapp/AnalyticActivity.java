package com.gvs.idusapp;

import com.gvs.controladores.ControladorCliente;
import com.gvs.modelos.Cliente;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AnalyticActivity extends Activity {
	
	private TextView lbl_cliente;
	private EditText txt_accion,txt_mejora,txt_recomendacion;
	private Button btn_guardar;
	private int codigoCliente;
	private Bundle bun;
	private ControladorCliente controladorCliente;
	private Cliente cliente;
	private boolean wasModify;
	private Intent returnIntent = new Intent();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_analytic);
		lbl_cliente=(TextView)findViewById(R.id.lbl_cliente);
		txt_accion=(EditText)findViewById(R.id.txt_accion);
		bun = getIntent().getExtras();
		txt_accion.setText(bun.getString("ACCION"));
		
		txt_mejora=(EditText)findViewById(R.id.txt_mejora);
		
		//if(bun.getString("MEJORA")!=null)
			txt_mejora.setText(bun.getString("MEJORA"));
		
		txt_recomendacion=(EditText)findViewById(R.id.txt_recomendacion);
		
		//if(bun.getString("RECOMENDACION")!=null)
		txt_recomendacion.setText(bun.getString("RECOMENDACION"));
		
		btn_guardar=(Button)findViewById(R.id.btn_guardar);
		
		controladorCliente=new ControladorCliente(this);
		codigoCliente =bun.getInt("CODIGOCLIENTE");
	    cliente=controladorCliente.buscarCliente(codigoCliente);
	    lbl_cliente.setText(cliente.getNombre());
	    
	    btn_guardar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					
					wasModify = false;
					
					String accion=txt_accion.getText().toString();
					if(accion.length()!=0){
						accion=accion.replace("#"," numeral ");
						accion=accion.replace("%"," porciento ");
						wasModify = true;
					}
					
					String mejora=txt_mejora.getText().toString();
					
					if(mejora.length()!=0){
						mejora=mejora.replace("#"," numeral ");
						mejora=mejora.replace("%"," porciento ");
						wasModify = true;
						
					}
					
					String recomendacion=txt_recomendacion.getText().toString();
					if(recomendacion.length()!=0){
						recomendacion=recomendacion.replace("#"," numeral ");
						recomendacion=recomendacion.replace("%"," porciento ");
						wasModify = true;
						
					}
					
					if (!wasModify) {
						AlertDialog.Builder builder = new AlertDialog.Builder(AnalyticActivity.this);
						builder.setMessage("No ha completado los campos correspondientes. Desea seguir con el pedido?");
						// Add the buttons
						builder.setPositiveButton("Si.", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								setResult(Activity.RESULT_CANCELED, returnIntent);
								AnalyticActivity.this.finish();
							}
						});
						builder.setNegativeButton("Cancelar.", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						});

						AlertDialog dialog = builder.create();
						dialog.show();
					} else {

						returnIntent.putExtra("ACCION",accion);
						returnIntent.putExtra("MEJORA",mejora);
						returnIntent.putExtra("RECOMENDACION",recomendacion);
						
						setResult(Activity.RESULT_OK, returnIntent);
						AnalyticActivity.this.finish();
						
					}

					
				}catch(Exception ex){
					Toast toast =Toast.makeText(getApplicationContext(),"Bug al guardar", Toast.LENGTH_SHORT);
					toast.show();
				}	
			}
		});
	    
	}

		
	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Está seguro que desea salir? Sus datos se perderán.");
		// Add the buttons
		builder.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				setResult(Activity.RESULT_CANCELED, returnIntent);
				AnalyticActivity.super.onBackPressed();
			}
		});
		builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

		
}
