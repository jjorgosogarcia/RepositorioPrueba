package com.example.usuario.practicalistviewjuegos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;


public class Principal extends Activity {
    private ArrayList<Juego> juegos =new ArrayList<Juego>();
    private ListView lv;
    private AdaptadorArray ad;
    private String s="";
    private TextView tvPre;

    /***************************************************************/
    /*                      METODOS ON                             */
    /***************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        initComponents();
        cargarJuegos();
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.contextual, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    /* Hacer long click sobre un item del ListView */
    @Override
    public boolean onContextItemSelected(MenuItem item){
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        if(id == R.id.action_eliminar){
            eliminar(index);
        }
        else if(id ==R.id.action_editar){
            editar(index);
        }
        else if(id ==R.id.action_prestado){
            prestar(index);
        }
        else if(id ==R.id.action_devolver){
            devolver(index);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_anadir) {
            aniadir();
        }
        else if (id == R.id.action_borrarTodo) {
            borrarTodo();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onRadioButtonClicked(View view) {
        // Comprobar si el boton esta seleccionado
        boolean checked = ((RadioButton) view).isChecked();
        // Mirarmos que boton esta seleccionado
        switch (view.getId()) {
            case R.id.rbPc:
                if (checked)
                    s=getString(R.string.rbPc);
                break;
            case R.id.rbPs3:
                if (checked)
                    s=getString(R.string.rbPs3);
                break;
            case R.id.rbXbox:
                if (checked)
                    s=getString(R.string.rbXbox);
                break;
        }
    }

    /***************************************************************/
    /*                      METODOS CLICK                          */
    /***************************************************************/

    private boolean aniadir(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.action_anadir);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.alta, null);
        alert.setView(vista);
        alert.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText et1, et2;
                        et1 = (EditText) vista.findViewById(R.id.etTitulo);
                        et2 = (EditText) vista.findViewById(R.id.etGenero);
                        if(et1.getText().length()==0) {
                            tostada(getString(R.string.error));
                        }else {
                            juegos.add(new Juego(et1.getText().toString(), et2.getText().toString(), s, ""));
                            tostada(getString(R.string.add));
                            Collections.sort(juegos);
                            ad.notifyDataSetChanged();
                            //s=""; para que si se nos olvida poner una plataforma no nos coja la anterior
                            s = "";
                        }
                    }
                });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
        return true;
    }

    public void borrarTodo(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.borrarTodos);
        alert.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        juegos.clear();
                        ad.notifyDataSetChanged();
                    }
                });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
    }

    public void devolver(final int index){
        if (juegos.get(index).getPrestado()==""){
            tostada(getString(R.string.error2));
        }else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.juegoDevuelto);
            alert.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            juegos.get(index).setPrestado("");
                            ad.notifyDataSetChanged();
                        }
                    });
            alert.setNegativeButton(android.R.string.no, null);
            alert.show();
        }
    }

    private boolean editar(final int index){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.action_modify);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.alta, null);
        alert.setView(vista);
        final EditText et1, et2;
        et1 = (EditText) vista.findViewById(R.id.etTitulo);
        et2 = (EditText) vista.findViewById(R.id.etGenero);
        //Cargamos en los edittext los datos que teniamos, para modificarlos
        et1.setText(juegos.get(index).getTitulo());
        et2.setText(juegos.get(index).getGenero());
        alert.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(et1.getText().length()==0) {
                            tostada(getString(R.string.error));
                        }else {
                            //Modificamos los datos
                            juegos.get(index).setTitulo(et1.getText().toString());
                            juegos.get(index).setGenero(et2.getText().toString());
                            juegos.get(index).setPlataforma(s);
                            ad.notifyDataSetChanged();
                            tostada(getString(R.string.modify));
                            Collections.sort(juegos);
                        }
                    }
                });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
        return true;

    }

    public void eliminar(final int index){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.eliminarUno);
        alert.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        juegos.remove(index);
                        ad.notifyDataSetChanged();
                    }
                });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
    }

    public void prestar(final int index){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.prestar);
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.prestar, null);
        alert.setView(vista);
        final EditText etPres;
        etPres = (EditText) vista.findViewById(R.id.etPrestar);
        alert.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        tvPre = (TextView)findViewById(R.id.tvPrestado);
                        juegos.get(index).setPrestado(etPres.getText().toString());
                        juegos.get(index).setPrestado(getString(R.string.prestado) + " " + juegos.get(index).getPrestado());
                        ad.notifyDataSetChanged();
                    }
                });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
    }


    /***************************************************************/
    /*                        AUXILIARES                           */
    /***************************************************************/

    //Sacamos una lista de juegos predefinida y la ordenamos
    public void cargarJuegos(){
        juegos.add(new Juego("Final Fantasy","Rpg","Ps3",""));
        juegos.add(new Juego("Resident Evil","Terror","Xbox",""));
        juegos.add(new Juego("Counter Strike","Shooter","Pc",""));
        juegos.add(new Juego("Halo","Shooter","Xbox",""));
        juegos.add(new Juego("Kingdom hearts","Action-Rpg","Ps3",""));
        juegos.add(new Juego("Metal Gear Rising","Rpg","Pc",""));
        Collections.sort(juegos);
    }
    //Iniciamos las variables
    private void initComponents(){
        ad=new AdaptadorArray(this, R.layout.lista_datos, juegos);
        lv= (ListView)findViewById(R.id.listView);
        lv.setAdapter(ad);
        registerForContextMenu(lv);
    }

    //Sacamos mensajes de información
    private void tostada(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

}
