package com.teknisio.app.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class PermintaanResponse {
    @SerializedName("idPermintaan")
    private UUID idPermintaan;

    @SerializedName("kodePermintaan")
    private String kodePermintaan;

    @SerializedName("status")
    private String status;

    public UUID getIdPermintaan() { return idPermintaan; }
    public void setIdPermintaan(UUID idPermintaan) { this.idPermintaan = idPermintaan; }

    public String getKodePermintaan() { return kodePermintaan; }
    public void setKodePermintaan(String kodePermintaan) { this.kodePermintaan = kodePermintaan; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
