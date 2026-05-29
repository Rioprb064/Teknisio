package com.teknisio.services;

import com.teknisio.common.exception.BadRequestException;
import com.teknisio.common.exception.ForbiddenException;
import com.teknisio.common.exception.ResourceNotFoundException;
import com.teknisio.common.exception.UnauthorizedException;
import com.teknisio.dto.requests.UpdateTechnicianAvailabilityStatusRequest;
import com.teknisio.dto.responses.TechnicianAvailabilityStatusResponse;
import com.teknisio.model.entities.TeknisiProfile;
import com.teknisio.model.entities.User;
import com.teknisio.model.enums.TeknisiStatus;
import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;
import com.teknisio.repositories.TeknisiProfileRepository;
import com.teknisio.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TechnicianMeService {
  private final UserRepository userRepository;
  private final TeknisiProfileRepository teknisiProfileRepository;

  @Transactional
  public TechnicianAvailabilityStatusResponse updateAvailabilityStatus(User authenticatedUser,UpdateTechnicianAvailabilityStatusRequest request) {
    if (authenticatedUser == null) {
      throw new UnauthorizedException("Unauthorized");
    }

    User user = userRepository
      .findByIdUserAndDeletedAtIsNull(authenticatedUser.getIdUser())
      .orElseThrow(() -> new UnauthorizedException("Unauthorized"));

    if (user.getStatusAkun() != UserStatus.ACTIVE) {
      throw new ForbiddenException("Forbidden");
    }

    if (user.getRole() != UserRole.TECHNICIAN) {
      throw new ForbiddenException("Forbidden");
    }

    TeknisiStatus availabilityStatus = parseAvailabilityStatus(
      request.availabilityStatus()
    );

    TeknisiProfile technicianProfile = teknisiProfileRepository
      .findByUser_IdUser(user.getIdUser())
      .orElseThrow(() -> new ResourceNotFoundException("Technician profile not found"));

    technicianProfile.setStatusKetersediaan(availabilityStatus);

    return new TechnicianAvailabilityStatusResponse(
      technicianProfile.getIdTeknisiProfile(),
      technicianProfile.getStatusKetersediaan().name()
    );
  }

  private TeknisiStatus parseAvailabilityStatus(String availabilityStatus) {
    if (availabilityStatus == null || availabilityStatus.isBlank()) {
      throw new BadRequestException("Availability status is required");
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
}
