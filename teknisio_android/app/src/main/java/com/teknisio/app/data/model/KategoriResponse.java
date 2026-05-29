package com.teknisio.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class KategoriResponse {
    @SerializedName("idKategori")
    private UUID idKategori;

    @SerializedName("namaKategori")
    private String namaKategori;

    @SerializedName("icon")
    private String icon;

    public UUID getIdKategori() { return idKategori; }
    public void setIdKategori(UUID idKategori) { this.idKategori = idKategori; }

    public String getNamaKategori() { return namaKategori; }
    public void setNamaKategori(String namaKategori) { this.namaKategori = namaKategori; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}
