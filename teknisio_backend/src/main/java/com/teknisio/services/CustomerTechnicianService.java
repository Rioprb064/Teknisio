package com.teknisio.services;

import com.teknisio.common.exception.BadRequestException;
import com.teknisio.common.exception.ResourceNotFoundException;
import com.teknisio.dto.responses.DeviceCategoryResponse;
import com.teknisio.dto.responses.TechnicianDetailResponse;
import com.teknisio.dto.responses.TechnicianSummaryResponse;
import com.teknisio.model.entities.KategoriLayanan;
import com.teknisio.model.entities.TeknisiKategoriLayanan;
import com.teknisio.model.entities.TeknisiProfile;
import com.teknisio.model.entities.User;
import com.teknisio.model.enums.TeknisiStatus;
import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;
import com.teknisio.repositories.KategoriLayananRepository;
import com.teknisio.repositories.TeknisiKategoriLayananRepository;
import com.teknisio.repositories.TeknisiProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerTechnicianService {

  private final KategoriLayananRepository kategoriLayananRepository;
  private final TeknisiKategoriLayananRepository teknisiKategoriLayananRepository;
  private final TeknisiProfileRepository teknisiProfileRepository;

  @Transactional(readOnly = true)
  public List<TechnicianSummaryResponse> getTechniciansByDeviceCategory(
    UUID deviceCategoryId,
    String availabilityStatus,
    String sort
  ) {
    kategoriLayananRepository
      .findByIdKategoriAndAktifTrueAndDeletedAtIsNull(deviceCategoryId)
      .orElseThrow(() -> new ResourceNotFoundException("Device category not found"));

    TeknisiStatus statusFilter = parseAvailabilityStatus(availabilityStatus);
    Comparator<TeknisiProfile> comparator = getTechnicianComparator(sort);

    return teknisiKategoriLayananRepository
      .findByKategori_IdKategoriAndAktifTrue(deviceCategoryId)
      .stream()
      .map(TeknisiKategoriLayanan::getTeknisiProfile)
      .filter(this::isActiveTechnicianProfile)
      .filter(technician -> matchesAvailabilityStatus(technician, statusFilter))
      .distinct()
      .sorted(comparator)
      .map(this::toTechnicianSummaryResponse)
      .toList();
  }

  @Transactional(readOnly = true)
  public TechnicianDetailResponse getTechnicianDetail(UUID technicianProfileId) {
    TeknisiProfile technicianProfile = teknisiProfileRepository.findById(technicianProfileId)
      .filter(this::isActiveTechnicianProfile)
      .orElseThrow(() -> new ResourceNotFoundException("Technician not found"));

    return toTechnicianDetailResponse(technicianProfile);
  }

  private TeknisiStatus parseAvailabilityStatus(String availabilityStatus) {
    if (availabilityStatus == null || availabilityStatus.isBlank()) {
      return null;
    }

    try {
      return TeknisiStatus.valueOf(
        availabilityStatus.trim().toUpperCase(Locale.ROOT)
      );
    } catch (IllegalArgumentException exception) {
      throw new BadRequestException(
        "Invalid availability status. Allowed values: ONLINE, OFFLINE, BUSY, ON_LEAVE"
      );
    }
  }

  private boolean matchesAvailabilityStatus(
    TeknisiProfile technicianProfile,
    TeknisiStatus statusFilter
  ) {
    return statusFilter == null
      || technicianProfile.getStatusKetersediaan() == statusFilter;
  }

  private Comparator<TeknisiProfile> getTechnicianComparator(String sort) {
    String normalizedSort = sort == null || sort.isBlank()
      ? "name"
      : sort.trim().toLowerCase(Locale.ROOT);

    Comparator<TeknisiProfile> byName = Comparator.comparing(
      technician -> technician.getUser().getNama(),
      String.CASE_INSENSITIVE_ORDER
    );

    return switch (normalizedSort) {
      case "name" -> byName;

      case "rating" -> Comparator
        .comparing(
          TeknisiProfile::getRatingAvg,
          Comparator.nullsLast(BigDecimal::compareTo)
        )
        .reversed()
        .thenComparing(
          Comparator.comparing(
            TeknisiProfile::getRatingCount,
            Comparator.nullsLast(Integer::compareTo)
          ).reversed()
        )
        .thenComparing(byName);

      case "totaljobs" -> Comparator
        .comparing(
          TeknisiProfile::getTotalPekerjaan,
          Comparator.nullsLast(Integer::compareTo)
        )
        .reversed()
        .thenComparing(byName);

      default -> throw new BadRequestException(
        "Invalid sort option. Allowed values: name, rating, totalJobs"
      );
    };
  }

  private boolean isActiveTechnicianProfile(TeknisiProfile technicianProfile) {
    User user = technicianProfile.getUser();

    return user != null
      && user.getRole() == UserRole.TECHNICIAN
      && user.getStatusAkun() == UserStatus.ACTIVE
      && user.getDeletedAt() == null;
  }

  private TechnicianSummaryResponse toTechnicianSummaryResponse(
    TeknisiProfile technicianProfile
  ) {
    User user = technicianProfile.getUser();

    return new TechnicianSummaryResponse(
      technicianProfile.getIdTeknisiProfile(),
      user.getNama(),
      user.getFotoProfil(),
      technicianProfile.getStatusKetersediaan().name(),
      technicianProfile.getRatingAvg(),
      technicianProfile.getRatingCount(),
      technicianProfile.getTotalPekerjaan(),
      getSupportedDeviceCategories(technicianProfile.getIdTeknisiProfile())
    );
  }

  private TechnicianDetailResponse toTechnicianDetailResponse(
    TeknisiProfile technicianProfile
  ) {
    User user = technicianProfile.getUser();

    return new TechnicianDetailResponse(
      technicianProfile.getIdTeknisiProfile(),
      user.getNama(),
      user.getFotoProfil(),
      technicianProfile.getStatusKetersediaan().name(),
      technicianProfile.getRatingAvg(),
      technicianProfile.getRatingCount(),
      technicianProfile.getTotalPekerjaan(),
      technicianProfile.getDeskripsi(),
      getSupportedDeviceCategories(technicianProfile.getIdTeknisiProfile())
    );
  }

  private List<DeviceCategoryResponse> getSupportedDeviceCategories(
    UUID technicianProfileId
  ) {
    return teknisiKategoriLayananRepository
      .findByTeknisiProfile_IdTeknisiProfileAndAktifTrue(technicianProfileId)
      .stream()
      .map(TeknisiKategoriLayanan::getKategori)
      .filter(this::isActiveDeviceCategory)
      .sorted(Comparator.comparing(
        KategoriLayanan::getNamaKategori,
        String.CASE_INSENSITIVE_ORDER
      ))
      .map(this::toDeviceCategoryResponse)
      .toList();
  }

  private boolean isActiveDeviceCategory(KategoriLayanan category) {
    return category != null
      && Boolean.TRUE.equals(category.getAktif())
      && category.getDeletedAt() == null;
  }

  private DeviceCategoryResponse toDeviceCategoryResponse(KategoriLayanan category) {
    return new DeviceCategoryResponse(
      category.getIdKategori(),
      category.getNamaKategori(),
      category.getIcon()
    );
  }
}
