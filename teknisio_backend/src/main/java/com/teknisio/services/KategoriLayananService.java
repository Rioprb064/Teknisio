package com.teknisio.services;

import com.teknisio.model.entities.KategoriLayanan;
import com.teknisio.repositories.KategoriLayananRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KategoriLayananService {

    private final KategoriLayananRepository kategoriLayananRepository;

    public KategoriLayananService(KategoriLayananRepository kategoriLayananRepository) {
        this.kategoriLayananRepository = kategoriLayananRepository;
    }

    public List<KategoriLayanan> getAllKategoriAktif() {
        return kategoriLayananRepository.findAll().stream()
                .filter(KategoriLayanan::getAktif)
                .toList();
    }
}
