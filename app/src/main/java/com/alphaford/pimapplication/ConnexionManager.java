package com.alphaford.pimapplication;

public class    ConnexionManager {
    String adress="http://196.224.13.135";
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
