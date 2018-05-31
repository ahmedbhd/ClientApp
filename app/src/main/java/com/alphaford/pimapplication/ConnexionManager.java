package com.alphaford.pimapplication;

public class    ConnexionManager {
    String adress="http://192.168.1.6";
    String port;

    public ConnexionManager(String prot) {
        this.port = prot;
    }
    public String getPath(String s){
        return adress+s;
    }

    public ConnexionManager() {
    }
}
