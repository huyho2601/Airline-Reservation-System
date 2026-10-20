package airline.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import airline.dto.CreateUserRequest;
import airline.dto.PasswordChangeRequest;
import airline.entity.User;
import airline.entity.enums.UserRole;
import airline.error.DeletionException;
import airline.error.DuplicateResourceException;
import airline.error.InvalidCredentialsException;
import airline.repository.BookingRepository;
import airline.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock UserRepository userRepository;
  @Mock PasswordEncoder passwordEncoder;
  @Mock BookingRepository bookingRepository;

  @InjectMocks UserServiceImpl userService;

  private User existingUser;

  @BeforeEach
  void setUp() {
    existingUser = new User();
    existingUser.setUsername("huy");
    existingUser.setPassword("stored-hash");
    existingUser.setRole(UserRole.CLIENT);
  }

  // ---------- createUser ----------

  @Test
  void createUser_valid_storesHashedPasswordAndClientRole() {
    CreateUserRequest request = mock(CreateUserRequest.class);
    when(request.getName()).thenReturn("Huy");
    when(request.getUsername()).thenReturn("huy");
    when(request.getEmail()).thenReturn("huy@example.com");
    when(request.getPassword()).thenReturn("plain-password");
    when(userRepository.existsByUsername("huy")).thenReturn(false);
    when(userRepository.existsByUserEmail("huy@example.com")).thenReturn(false);
    when(passwordEncoder.encode("plain-password")).thenReturn("hashed-password");
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    User created = userService.createUser(request);

    assertThat(created.getPassword()).isEqualTo("hashed-password");
    assertThat(created.getPassword()).isNotEqualTo("plain-password");
    assertThat(created.getRole()).isEqualTo(UserRole.CLIENT);
    assertThat(created.getUsername()).isEqualTo("huy");
  }

  @Test
  void createUser_duplicateUsername_throwsDuplicate() {
    CreateUserRequest request = mock(CreateUserRequest.class);
    when(request.getUsername()).thenReturn("huy");
    when(userRepository.existsByUsername("huy")).thenReturn(true);

    assertThatThrownBy(() -> userService.createUser(request))
        .isInstanceOf(DuplicateResourceException.class);

    verify(userRepository, never()).save(any());
    verify(passwordEncoder, never()).encode(any());
  }

  @Test
  void createUser_duplicateEmail_throwsDuplicate() {
    CreateUserRequest request = mock(CreateUserRequest.class);
    when(request.getUsername()).thenReturn("huy");
    when(request.getEmail()).thenReturn("taken@example.com");
    when(userRepository.existsByUsername("huy")).thenReturn(false);
    when(userRepository.existsByUserEmail("taken@example.com")).thenReturn(true);

    assertThatThrownBy(() -> userService.createUser(request))
        .isInstanceOf(DuplicateResourceException.class);

    verify(userRepository, never()).save(any());
  }

  // ---------- updateUserPassword ----------

  @Test
  void updateUserPassword_wrongCurrentPassword_throwsInvalidCredentials() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
    when(passwordEncoder.matches("wrong", "stored-hash")).thenReturn(false);

    assertThatThrownBy(() -> userService.updateUserPassword(1L, new PasswordChangeRequest("wrong", "new-pass")))
        .isInstanceOf(InvalidCredentialsException.class);

    verify(passwordEncoder, never()).encode(any());
    verify(userRepository, never()).save(any());
  }

  @Test
  void updateUserPassword_correctCurrentPassword_storesNewHash() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
    when(passwordEncoder.matches("old-pass", "stored-hash")).thenReturn(true);
    when(passwordEncoder.encode("new-pass")).thenReturn("new-hash");
    when(userRepository.save(existingUser)).thenReturn(existingUser);

    User updated = userService.updateUserPassword(1L, new PasswordChangeRequest("old-pass", "new-pass"));

    assertThat(updated.getPassword()).isEqualTo("new-hash");
  }

  // ---------- deleteUser ----------

  @Test
  void deleteUser_withBookings_throwsDeletion() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
    when(bookingRepository.existsByUser(existingUser)).thenReturn(true);

    assertThatThrownBy(() -> userService.deleteUser(1L))
        .isInstanceOf(DeletionException.class);

    verify(userRepository, never()).delete(any());
  }

  @Test
  void deleteUser_noBookings_deletesUser() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
    when(bookingRepository.existsByUser(existingUser)).thenReturn(false);

    userService.deleteUser(1L);

    verify(userRepository).delete(existingUser);
  }
}