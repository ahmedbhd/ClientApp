package com.alphaford.pimapplication.Models;

import java.util.Date;

/**
 * Created by USER on 07/03/2018.
 */

public class History {
    String _id;
    String recepteur;
    String bouquet;
    String channel;
    String program;
    Date date;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getRecepteur() {
        return recepteur;
    }

    public void setRecepteur(String recepteur) {
        this.recepteur = recepteur;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public History(String channel, String program, String recepteur, Date date, String id) {
        this.channel = channel;
        this.program = program;
        this.recepteur = recepteur;
        this.date = date;
        this._id = id;
    }

    public History(String _id, String recepteur, String bouquet, String channel, String program, Date date) {
        this._id = _id;
        this.recepteur = recepteur;
        this.bouquet = bouquet;
        this.channel = channel;
        this.program = program;
        this.date = date;
    }

    public History(String recepteur, String bouquet, String channel, String program, Date date) {
        this.recepteur = recepteur;
        this.bouquet = bouquet;
        this.channel = channel;
        this.program = program;
        this.date = date;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getBouquet() {
        return bouquet;
    }

    public void setBouquet(String bouquet) {
        this.bouquet = bouquet;
    }

    public History(String _id, String recepteur, String bouquet, String channel, String program) {
        this._id = _id;
        this.recepteur = recepteur;
        this.bouquet = bouquet;
        this.channel = channel;
        this.program = program;
    }

    public History(String recepteur, String bouquet, String channel, String program) {
        this.recepteur = recepteur;
        this.bouquet = bouquet;
        this.channel = channel;
        this.program = program;
    }

    public History() {
    }

    @Override
    public String toString() {
        return "History{" +
                "recepteur='" + recepteur + '\'' +
                ", bouquet='" + bouquet + '\'' +
                ", channel='" + channel + '\'' +
                ", program='" + program + '\'' +
                '}';
    }
}
