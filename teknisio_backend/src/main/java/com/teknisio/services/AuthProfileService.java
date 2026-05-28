package com.teknisio.services;

import com.teknisio.dto.responses.AuthProfileResponse;
import com.teknisio.dto.responses.CustomerAuthProfileResponse;
import com.teknisio.dto.responses.TeknisiAuthProfileResponse;
import com.teknisio.model.entities.TeknisiProfile;
import com.teknisio.model.entities.User;
import com.teknisio.model.enums.UserRole;
import com.teknisio.model.enums.UserStatus;
import com.teknisio.repositories.TeknisiProfileRepository;
import com.teknisio.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthProfileService {

  private final UserRepository userRepository;
  private final TeknisiProfileRepository teknisiProfileRepository;

  @Transactional(readOnly = true)
  public AuthProfileResponse profile(User authenticatedUser) {
    User user = userRepository.findByIdUserAndDeletedAtIsNull(authenticatedUser.getIdUser())
      .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Invalid user"
      ));

    if (user.getStatusAkun() != UserStatus.ACTIVE) {
      throw new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "Account is not active"
      );
    }

    if (user.getRole() == UserRole.TECHNICIAN) {
      TeknisiProfile profile = teknisiProfileRepository.findByUser_IdUser(user.getIdUser())
        .orElseThrow(() -> new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "Technician profile not found"
        ));

      return new TeknisiAuthProfileResponse(
        user.getIdUser(),
        profile.getIdTeknisiProfile(),
        user.getNama(),
        user.getEmail(),
        user.getNoTelepon(),
        user.getAlamat(),
        user.getRole(),
        user.getStatusAkun(),
        profile.getStatusKetersediaan(),
        profile.getRatingAvg(),
        profile.getRatingCount(),
        profile.getTotalPekerjaan(),
        profile.getDeskripsi(),
        user.getFotoProfil()
      );
    }

    return new CustomerAuthProfileResponse(
      user.getIdUser(),
      user.getNama(),
      user.getEmail(),
      user.getNoTelepon(),
      user.getAlamat(),
      user.getRole(),
      user.getStatusAkun(),
      user.getFotoProfil()
    );
  }
}
