package com.example.usuario.practicalistviewjuegos;

/**
 * Created by USUARIO on 11/10/2014.
 */
public class Juego implements Comparable<Juego> {

    private String titulo, genero, plataforma;

    public Juego() {
        titulo="";
        genero="";
        plataforma="";
    }

    public Juego(String titulo, String genero, String plataforma) {
        this.titulo = titulo;
        this.genero = genero;
        this.plataforma = plataforma;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }


    public int compareTo(Juego juego) {
        String a = getTitulo().toLowerCase();
        String b = juego.getTitulo().toLowerCase();
        return a.compareTo(b);

    }
}
