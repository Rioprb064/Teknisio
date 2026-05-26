package com.teknisio.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class TeknisiLayananId implements Serializable {

    @Column(name = "id_teknisi_profile")
    private UUID idTeknisiProfile;

    @Column(name = "id_layanan")
    private UUID idLayanan;
}
