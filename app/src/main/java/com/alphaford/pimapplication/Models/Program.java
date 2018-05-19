package com.alphaford.pimapplication.Models;

/**
 * Created by asus on 21/04/2018.
 */

public class Program {
    private int id_program;
    private String nom_program;
    private int nb_telesp;
    private String nom_chaine;
    private long nb_minute;

    public long getNb_minute() {
        return nb_minute;
    }

    public Program(String nom_program, long nb_minute) {
        this.nom_program = nom_program;
        this.nb_minute = nb_minute;
    }

    public void setNb_minute(long nb_minute) {
        this.nb_minute = nb_minute;
    }

    public String getNom_chaine() {
        return nom_chaine;
    }

    public void setNom_chaine(String nom_chaine) {
        this.nom_chaine = nom_chaine;
    }

    public int getId_program() {
        return id_program;
    }

    public void setId_program(int id_program) {
        this.id_program = id_program;
    }

    public String getNom_program() {
        return nom_program;
    }

    public void setNom_program(String nom_program) {
        this.nom_program = nom_program;
    }

    public int getNb_telesp() {
        return nb_telesp;
    }

    public void setNb_telesp(int nb_telesp) {
        this.nb_telesp = nb_telesp;
    }

    public Program(String nom_program, int nb_telesp) {
        this.nom_program = nom_program;
        this.nb_telesp = nb_telesp;
    }

    @Override
    public String toString() {
        return "Program{" +
                "nom_program='" + nom_program + '\'' +
                ", nb_telesp=" + nb_telesp +
                '}';
    }
}
