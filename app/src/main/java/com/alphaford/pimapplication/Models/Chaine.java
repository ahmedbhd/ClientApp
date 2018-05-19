package com.alphaford.pimapplication.Models;

/**
 * Created by slim on 20/02/2018.
 */

public class Chaine {
    private String nom_chaine;
    private int nb_telesp;
    private int nb_telesp_jour;
    private int nb_telesp_mois;

    public Chaine(String nom_chaine, int nb_telesp) {
        this.nom_chaine = nom_chaine;
        this.nb_telesp = nb_telesp;
    }

    public Chaine(String nom_chaine) {
        this.nom_chaine = nom_chaine;
    }

    public Chaine(String nom_chaine, int nb_telesp, int nb_telesp_jour, int nb_telesp_mois) {
        this.nom_chaine = nom_chaine;
        this.nb_telesp = nb_telesp;
        this.nb_telesp_jour = nb_telesp_jour;
        this.nb_telesp_mois = nb_telesp_mois;
    }

    public Chaine() {
    }

    public String getNom_chaine() {
        return nom_chaine;
    }

    public void setNom_chaine(String nom_chaine) {
        this.nom_chaine = nom_chaine;
    }

    public int getNb_telesp() {
        return nb_telesp;
    }

    public void setNb_telesp(int nb_telesp) {
        this.nb_telesp = nb_telesp;
    }

    public int getNb_telesp_jour() {
        return nb_telesp_jour;
    }

    public void setNb_telesp_jour(int nb_telesp_jour) {
        this.nb_telesp_jour = nb_telesp_jour;
    }

    public int getNb_telesp_mois() {
        return nb_telesp_mois;
    }

    public void setNb_telesp_mois(int nb_telesp_mois) {
        this.nb_telesp_mois = nb_telesp_mois;
    }
}
