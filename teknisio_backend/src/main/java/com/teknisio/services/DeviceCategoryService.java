package com.teknisio.services;

import com.teknisio.common.exception.ResourceNotFoundException;
import com.teknisio.dto.responses.DeviceCategoryResponse;
import com.teknisio.model.entities.KategoriLayanan;
import com.teknisio.repositories.KategoriLayananRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceCategoryService {
  private final KategoriLayananRepository kategoriLayananRepository;

  public List<DeviceCategoryResponse> getActiveDeviceCategories() {
    return kategoriLayananRepository.findByAktifTrueAndDeletedAtIsNull()
      .stream()
      .sorted(Comparator.comparing(
        KategoriLayanan::getNamaKategori,
        String.CASE_INSENSITIVE_ORDER
      ))
      .map(this::toResponse)
      .toList();
  }

  public DeviceCategoryResponse getDeviceCategoryById(UUID deviceCategoryId) {
    KategoriLayanan category = kategoriLayananRepository
      .findByIdKategoriAndAktifTrueAndDeletedAtIsNull(deviceCategoryId)
      .orElseThrow(() -> new ResourceNotFoundException("Device category not found"))
    ;

    return toResponse(category);
  }

  private DeviceCategoryResponse toResponse(KategoriLayanan category) {
    return new DeviceCategoryResponse(
      category.getIdKategori(),
      category.getNamaKategori(),
      category.getIcon()
    );
  }
}
