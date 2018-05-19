package com.alphaford.pimapplication.Models;

/**
 * Created by asus on 06/03/2018.
 */

public class historiqueChaine {
    private String nom_chaine;
    private int nbr_teles;
    private String nom_bouquet;
    private Long nb_minute;

    public historiqueChaine(String nom_chaine, long nb_minute) {
        this.nom_chaine = nom_chaine;
        this.nb_minute = nb_minute;
    }

    public Long getNb_minute() {
        return nb_minute;
    }

    public void setNb_minute(Long nb_minute) {
        this.nb_minute = nb_minute;
    }

    public historiqueChaine(String nom_chaine, int nbr_teles) {
        this.nom_chaine = nom_chaine;
        this.nbr_teles = nbr_teles;
    }

    public historiqueChaine(String nom_chaine, int nbr_teles, String nom_bouquet) {
        this.nom_chaine = nom_chaine;
        this.nbr_teles = nbr_teles;
        this.nom_bouquet = nom_bouquet;
    }

    public String getNom_bouquet() {
        return nom_bouquet;
    }

    public void setNom_bouquet(String nom_bouquet) {
        this.nom_bouquet = nom_bouquet;
    }

    public historiqueChaine() {
    }

    public String getNom_chaine() {
        return nom_chaine;
    }

    public void setNom_chaine(String nom_chaine) {
        this.nom_chaine = nom_chaine;
    }

    public int getNbr_teles() {
        return nbr_teles;
    }

    public void setNbr_teles(int nbr_teles) {
        this.nbr_teles = nbr_teles;
    }

    @Override
    public String toString() {
        return "historiqueChaine{" +
                "nom_chaine='" + nom_chaine + '\'' +
                ", nbr_teles=" + nbr_teles +
                '}';
    }
}
