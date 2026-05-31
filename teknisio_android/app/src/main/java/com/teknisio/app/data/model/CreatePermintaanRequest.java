package com.teknisio.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class CreatePermintaanRequest {
    @SerializedName("idLayanan")
    private UUID idLayanan;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    @SerializedName("alamat")
    private String alamat;

    @SerializedName("detailAlamat")
    private String detailAlamat;

    @SerializedName("deskripsiMasalah")
    private String deskripsiMasalah;

    // Getter dan Setter...
    public UUID getIdLayanan() { return idLayanan; }
    public void setIdLayanan(UUID idLayanan) { this.idLayanan = idLayanan; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getDetailAlamat() { return detailAlamat; }
    public void setDetailAlamat(String detailAlamat) { this.detailAlamat = detailAlamat; }

    public String getDeskripsiMasalah() { return deskripsiMasalah; }
    public void setDeskripsiMasalah(String deskripsiMasalah) { this.deskripsiMasalah = deskripsiMasalah; }
}
