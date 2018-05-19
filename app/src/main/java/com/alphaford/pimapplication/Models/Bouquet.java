package com.alphaford.pimapplication.Models;

/**
 * Created by slim on 25/02/2018.
 */

public class Bouquet {
    private String nom_bouquet;
    private int nbr_telesp_j, nbr_telesp_m,nbr_telesp_an,nombre_chaine;

    public Bouquet() {
    }

    public Bouquet(String nom_bouquet, int nbr_telesp_j, int nbr_telesp_m, int nbr_telesp_an, int nombre_chaine) {
        this.nom_bouquet = nom_bouquet;
        this.nbr_telesp_j = nbr_telesp_j;
        this.nbr_telesp_m = nbr_telesp_m;
        this.nbr_telesp_an = nbr_telesp_an;
        this.nombre_chaine = nombre_chaine;
    }

    public String getNom_bouquet() {
        return nom_bouquet;
    }

    public void setNom_bouquet(String nom_bouquet) {
        this.nom_bouquet = nom_bouquet;
    }

    public int getNbr_telesp_j() {
        return nbr_telesp_j;
    }

    public void setNbr_telesp_j(int nbr_telesp_j) {
        this.nbr_telesp_j = nbr_telesp_j;
    }

    public int getNbr_telesp_m() {
        return nbr_telesp_m;
    }

    public void setNbr_telesp_m(int nbr_telesp_m) {
        this.nbr_telesp_m = nbr_telesp_m;
    }

    public int getNbr_telesp_an() {
        return nbr_telesp_an;
    }

    public void setNbr_telesp_an(int nbr_telesp_an) {
        this.nbr_telesp_an = nbr_telesp_an;
    }

    public int getNombre_chaine() {
        return nombre_chaine;
    }

    public void setNombre_chaine(int nombre_chaine) {
        this.nombre_chaine = nombre_chaine;
    }
}

