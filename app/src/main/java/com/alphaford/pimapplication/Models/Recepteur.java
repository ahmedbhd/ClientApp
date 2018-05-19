package com.alphaford.pimapplication.Models;

public class Recepteur {
    String id_rec;
    String client;
    String fam_region;
    String fam_size;
    String fam_age;


    public Recepteur(String id_rec, String client, String fam_region, String fam_size, String fam_age) {
        this.id_rec = id_rec;
        this.client = client;
        this.fam_region = fam_region;
        this.fam_size = fam_size;
        this.fam_age = fam_age;
    }

    public String getId_rec() {
        return id_rec;
    }

    public void setId_rec(String id_rec) {
        this.id_rec = id_rec;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getFam_region() {
        return fam_region;
    }

    public void setFam_region(String fam_region) {
        this.fam_region = fam_region;
    }

    public String getFam_size() {
        return fam_size;
    }

    public void setFam_size(String fam_size) {
        this.fam_size = fam_size;
    }

    public String getFam_age() {
        return fam_age;
    }

    public void setFam_age(String fam_age) {
        this.fam_age = fam_age;
    }

    public Recepteur() {
    }

    @Override
    public String toString() {
        return "Recepteur{" +
                "id_rec='" + id_rec + '\'' +
                ", client='" + client + '\'' +
                ", fam_region='" + fam_region + '\'' +
                ", fam_size='" + fam_size + '\'' +
                ", fam_age='" + fam_age + '\'' +
                '}';
    }
}
