package com.alphaford.pimapplication.Models;

import org.joda.time.DateTime;

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
    DateTime date;
    int Duree;

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

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public String getId() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public History(String channel, String program, String recepteur, DateTime date, String id) {
        this.channel = channel;
        this.program = program;
        this.recepteur = recepteur;
        this.date = date;
        this._id = id;
    }

    public History(String _id, String recepteur, String bouquet, String channel, String program, DateTime date) {
        this._id = _id;
        this.recepteur = recepteur;
        this.bouquet = bouquet;
        this.channel = channel;
        this.program = program;
        this.date = date;
    }

    public History(String recepteur, String bouquet, String channel, String program, DateTime date) {
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

    public History(String recepteur, String bouquet, String channel, String program, int duree) {
        this.recepteur = recepteur;
        this.bouquet = bouquet;
        this.channel = channel;
        this.program = program;
        Duree = duree;
    }

    @Override
    public String toString() {
        return "History{" +
                "_id='" + _id + '\'' +
                ", recepteur='" + recepteur + '\'' +
                ", bouquet='" + bouquet + '\'' +
                ", channel='" + channel + '\'' +
                ", program='" + program + '\'' +
                ", date=" + date +
                ", Duree=" + Duree +
                '}';
    }
}
