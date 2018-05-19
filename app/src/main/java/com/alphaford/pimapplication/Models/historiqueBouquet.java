package com.alphaford.pimapplication.Models;

/**
 * Created by asus on 06/03/2018.
 */

public class historiqueBouquet {
    private String nom_bouquet;
    private int nbr_teles;

    public historiqueBouquet(String nom_bouquet, int nbr_teles) {
        this.nom_bouquet = nom_bouquet;
        this.nbr_teles = nbr_teles;
    }


    public historiqueBouquet() {
    }

    public String getNom_bouquet() {
        return nom_bouquet;
    }

    public void setNom_bouquet(String nom_bouquet) {
        this.nom_bouquet = nom_bouquet;
    }

    public int getNbr_teles() {
        return nbr_teles;
    }

    public void setNbr_teles(int nbr_teles) {
        this.nbr_teles = nbr_teles;
    }

    @Override
    public String toString() {
        return "historiqueBouquet{" +
                "nom_bouquet='" + nom_bouquet + '\'' +
                ", nbr_teles=" + nbr_teles +
                '}';
    }
}
