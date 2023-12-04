package com.example.freesoul.ui;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Biodata {

    private String nama;

    private String alamat;
    private String nomorHP;



    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public Biodata (String nama, String alamat, String nomorHP){
        this.nama = nama;
        this.alamat = alamat;
        this.nomorHP= nomorHP;

    }
    public Biodata(){

    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNomorHP() {
        return nomorHP;
    }

    public void setNomorHP(String nomorHP) {
        this.nomorHP = nomorHP;
    }
}
