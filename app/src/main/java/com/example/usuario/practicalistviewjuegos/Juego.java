package com.example.usuario.practicalistviewjuegos;

/**
 * Created by USUARIO on 11/10/2014.
 */
public class Juego implements Comparable<Juego> {

    private String titulo, genero, plataforma, prestado;

    public Juego() {
        titulo="";
        genero="";
        plataforma="";
        prestado="";
    }

    public Juego(String titulo, String genero, String plataforma, String prestado) {
        this.titulo = titulo;
        this.genero = genero;
        this.plataforma = plataforma;
        this.prestado = prestado;
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

    public String getPrestado() {
        return prestado;
    }

    public void setPrestado(String prestado) {
        this.prestado = prestado;
    }



    public int compareTo(Juego juego) {
        String a = getTitulo().toLowerCase();
        String b = juego.getTitulo().toLowerCase();
        return a.compareTo(b);

    }
}
