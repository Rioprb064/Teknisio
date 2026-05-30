package com.teknisio.app.data.model;

import java.util.List;

public class TeknisiModel {
    private String nama;
    private String fotoUrl;
    private float rating;
    private String jarak;
    private String hargaMin;
    private String hargaMax;
    private String deskripsi;
    private List<String> spesialisasi; // nama kategori

    public TeknisiModel(String nama, String fotoUrl, float rating, String jarak,
                        String hargaMin, String hargaMax, String deskripsi, List<String> spesialisasi) {
        this.nama = nama;
        this.fotoUrl = fotoUrl;
        this.rating = rating;
        this.jarak = jarak;
        this.hargaMin = hargaMin;
        this.hargaMax = hargaMax;
        this.deskripsi = deskripsi;
        this.spesialisasi = spesialisasi;
    }

    public String getNama() { return nama; }
    public String getFotoUrl() { return fotoUrl; }
    public float getRating() { return rating; }
    public String getJarak() { return jarak; }
    public String getHargaMin() { return hargaMin; }
    public String getHargaMax() { return hargaMax; }
    public String getDeskripsi() { return deskripsi; }
    public List<String> getSpesialisasi() { return spesialisasi; }

    /** Returns formatted price range string */
    public String getHargaRange() {
        return "Rp" + hargaMin + " - Rp" + hargaMax;
    }
}
