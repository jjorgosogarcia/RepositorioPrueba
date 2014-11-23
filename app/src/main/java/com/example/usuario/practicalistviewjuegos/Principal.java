package com.example.usuario.practicalistviewjuegos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Xml;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Principal extends Activity {
    private ArrayList<Juego> juegos = new ArrayList<Juego>();
    private ListView lv;
    private AdaptadorArray ad;
    private String s = "";
    private TextView tvPre;
    Juego game;
    private static final int CREAR=0;
    private static final int MODIFICAR=1;


    /***************************************************************/
    /*                                                             */
    /*                         METODOS ON                          */
    /*                                                             */
    /***************************************************************/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==Activity.RESULT_OK){
            String titulo = data.getStringExtra(getString(R.string.titulo));
            String genero = data.getStringExtra(getString(R.string.genero));
            String plataforma = data.getStringExtra(getString(R.string.plataforma));
            int index = data.getIntExtra(getString(R.string.index),-1);
            Juego j = new Juego(titulo,genero,plataforma,"");
            switch (requestCode){
                case CREAR:
                    //Añado el juego ordenado siempre que no esté repetido en la lista
                    if(j.getTitulo().length()==0) {
                        tostada(getString(R.string.error));
                    }else
                    if(!juegos.contains(j)){
                        juegos.add(j);
                        guardar();
                        Collections.sort(juegos);
                        ad.notifyDataSetChanged();
                    }else{
                        tostada(getString(R.string.Yatiene));
                    }
                    break;
                case MODIFICAR:
                    //Modifico el juego ordenado siempre que no esté repetido en la lista
                    if(j.getTitulo().length()==0) {
                        tostada(getString(R.string.error));
                    }else
                    if(!juegos.contains(j)){
                        juegos.set(index, j);
                        guardar();
                        Collections.sort(juegos);
                        ad.notifyDataSetChanged();
                    }else{
                        tostada(getString(R.string.Yatiene));
                    }
                    break;
            }
        }else {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actividad_principal);
        initComponents();
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
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
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        if (id == R.id.action_eliminar) {
            eliminar(index);
        } else if (id == R.id.action_editar) {
             editar(index);
        } else if (id == R.id.action_prestado) {
            prestar(index);
        } else if (id == R.id.action_devolver) {
            devolver(index);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_anadir) {
            Intent i = new Intent(this,gestionJuego.class);
            startActivityForResult(i, CREAR);
        } else if (id == R.id.action_borrarTodo) {
            borrarTodo();
        }else if (id == R.id.action_ordenarPlataforma) {
         Collections.sort(juegos, new ordenarPlataforma());
            ad.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle savingInstanceState) {
        super.onSaveInstanceState(savingInstanceState);
        savingInstanceState.putSerializable(getString(R.string.objeto), juegos);
    }

    /***************************************************************/
    /*                                                             */
    /*                       METODOS CLICK                         */
    /*                                                             */
    /***************************************************************/

    public void borrarTodo() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.borrarTodos);
        alert.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        juegos.clear();
                        guardar();
                        ad.notifyDataSetChanged();
                    }
                });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
    }

    public void devolver(final int index) {
        if (juegos.get(index).getPrestado() == "") {
            tostada(getString(R.string.error2));
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.juegoDevuelto);
            alert.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            juegos.get(index).setPrestado("");
                            guardar();
                            ad.notifyDataSetChanged();
                        }
                    });
            alert.setNegativeButton(android.R.string.no, null);
            alert.show();
        }
    }

    public void editar(final int index){
        Intent i = new Intent(this,gestionJuego.class);
        Bundle b = new Bundle();
        b.putSerializable(getString(R.string.juegos), juegos.get(index));
        b.putInt(getString(R.string.index), index);
        i.putExtras(b);
        startActivityForResult(i, MODIFICAR);
    }

    public void eliminar(final int index) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.eliminarUno);
        alert.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        juegos.remove(index);
                        guardar();
                        ad.notifyDataSetChanged();
                    }
                });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
    }

    public void prestar(final int index) {
        if (juegos.get(index).getPrestado() != "") {
            tostada(getString(R.string.yaPrestado));
        } else {
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
                            tvPre = (TextView) findViewById(R.id.tvPrestado);
                            juegos.get(index).setPrestado(etPres.getText().toString());
                            juegos.get(index).setPrestado(getString(R.string.prestado) + " " + juegos.get(index).getPrestado());
                            guardar();
                            ad.notifyDataSetChanged();
                        }
                    });
            alert.setNegativeButton(android.R.string.no, null);
            alert.show();
        }
    }

    /***************************************************************/
    /*                                                             */
    /*                         AUXILIARES                          */
    /*                                                             */
    /***************************************************************/

    //Guarda nuestra lista de juegos en un archivo xml
    private void guardar() {
        //Preparamos el archivo
        FileOutputStream fosxml = null;
        try {
            fosxml = new FileOutputStream(new File(getExternalFilesDir(null), getString(R.string.archivo)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Preparamos el documento XML
        XmlSerializer docxml = Xml.newSerializer();
        try {
            docxml.setOutput(fosxml, getString(R.string.codificacion));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            docxml.startDocument(null, Boolean.valueOf(true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        docxml.setFeature(getString(R.string.feature), true);

        //Creo las etiquetas
        try {
            docxml.startTag(null, getString(R.string.raizJuegos)); //Etiqueta Raiz

            for (int i = 0; i < juegos.size(); i++) {
                docxml.startTag(null, getString(R.string.tvJuego));
                docxml.startTag(null, getString(R.string.Titulo));
                docxml.text(juegos.get(i).getTitulo());
                docxml.endTag(null, getString(R.string.Titulo));
                docxml.startTag(null, getString(R.string.Genero));
                docxml.text(juegos.get(i).getGenero());
                docxml.endTag(null,  getString(R.string.Genero));
                docxml.startTag(null,  getString(R.string.Plataforma));
                docxml.text(juegos.get(i).getPlataforma());
                docxml.endTag(null,  getString(R.string.Plataforma));
                docxml.startTag(null, getString(R.string.tvPrestado));
                docxml.text(juegos.get(i).getPrestado());
                docxml.endTag(null, getString(R.string.tvPrestado));
                docxml.endTag(null, getString(R.string.tvJuego));
            }
            // Cierro el documento
            docxml.endDocument();
            docxml.flush();
            fosxml.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Iniciamos las variables
    private void initComponents() {
        ad = new AdaptadorArray(this, R.layout.lista_datos, juegos);
        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(ad);
        registerForContextMenu(lv);
        leer();
        Collections.sort(juegos);
    }

    //Lee los juegos de un archivo xml
    private void leer()  {
        try {
            XmlPullParser lectorxml = Xml.newPullParser();
            lectorxml.setInput(new FileInputStream(new File(getExternalFilesDir(null),  getString(R.string.archivo))),  getString(R.string.codificacion));
            int evento = lectorxml.getEventType();
            Juego j = new Juego();

            while (evento != XmlPullParser.END_DOCUMENT) {
                if (evento == XmlPullParser.START_TAG) {
                    String etiqueta = lectorxml.getName();
                    if (etiqueta.compareTo( getString(R.string.Titulo)) == 0) {
                        j.setTitulo(lectorxml.nextText());
                    } else if (etiqueta.compareTo( getString(R.string.Genero)) == 0) {
                        j.setGenero(lectorxml.nextText());
                    } else if (etiqueta.compareTo( getString(R.string.tvPrestado)) == 0) {
                        j.setPrestado(lectorxml.nextText());
                    } else if (etiqueta.compareTo( getString(R.string.Plataforma)) == 0) {
                        j.setPlataforma(lectorxml.nextText());
                    }
                }
                if (evento == XmlPullParser.END_TAG) {
                    String etiqueta = lectorxml.getName();
                    if (etiqueta.compareTo( getString(R.string.tvJuego)) == 0) {
                        juegos.add(j);
                        j = new Juego();
                    }
                }
                evento = lectorxml.next();
            }
        }catch (XmlPullParserException e){}
        catch (IOException e){}
    }

    //Método que compara juegos y los oredena por plataforma
    private class ordenarPlataforma implements Comparator<Juego> {
        public int compare(Juego game1, Juego game2) {
            int compara = game1.getPlataforma().compareTo(game2.getPlataforma());
            if(compara == 0){
                return game1.compareTo(game2);
            }
            return compara;
        }
    }

    //Sacamos mensajes de información
    private void tostada(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

}
