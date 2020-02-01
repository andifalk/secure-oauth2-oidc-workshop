package com.example.library.server.business;

import com.example.library.server.dataaccess.User;
import com.example.library.server.dataaccess.UserRepository;
import com.example.library.server.security.PreAuthorizeNotRequired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.IdGenerator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;
  private final IdGenerator idGenerator;

  @Autowired
  public UserService(UserRepository userRepository, IdGenerator idGenerator) {
    this.userRepository = userRepository;
    this.idGenerator = idGenerator;
  }

  // This has to be unsecured as this is used to
  // look up the user during authentication
  @PreAuthorizeNotRequired
  public Optional<User> findOneByEmail(String email) {
    return userRepository.findOneByEmail(email);
  }

  @PreAuthorize("hasRole('LIBRARY_ADMIN')")
  @Transactional
  public UUID create(User user) {
    if (user.getIdentifier() == null) {
      user.setIdentifier(idGenerator.generateId());
    }
    return userRepository.save(user).getIdentifier();
  }

  @PreAuthorize("hasRole('LIBRARY_ADMIN')")
  @Transactional
  public User update(User user) {
    return userRepository.save(user);
  }

  @PreAuthorize("hasRole('LIBRARY_ADMIN')")
  public Optional<User> findByIdentifier(UUID userIdentifier) {
    return userRepository.findOneByIdentifier(userIdentifier);
  }

  @PreAuthorize("hasRole('LIBRARY_ADMIN')")
  public List<User> findAll() {
    return userRepository.findAll();
  }

  @PreAuthorize("hasRole('LIBRARY_ADMIN')")
  @Transactional
  public void deleteByIdentifier(UUID userIdentifier) {
    userRepository.deleteUserByIdentifier(userIdentifier);
  }
}
