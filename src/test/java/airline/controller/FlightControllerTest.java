package airline.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import airline.config.SecurityConfig;
import airline.dto.CreateFlightRequest;
import airline.dto.UpdateFlightRequest;
import airline.entity.Flight;
import airline.entity.User;
import airline.entity.enums.UserRole;
import airline.error.ResourceNotFoundException;
import airline.security.CustomUserDetails;
import airline.service.FlightService;

@WebMvcTest(FlightController.class)
@Import(SecurityConfig.class) 
class FlightControllerTest {

  @Autowired MockMvc mockMvc;

  @MockitoBean FlightService flightService;

  private static final String VALID_FLIGHT_JSON = """
      {
        "flightNumber": "KL123",
        "origin": "Amsterdam",
        "destination": "London",
        "departureTime": "2030-01-01T10:00:00",
        "arrivalTime": "2030-01-01T11:00:00",
        "price": 150.0,
        "totalSeats": 60
      }
      """;

  private static final String UPDATE_FLIGHT_JSON = """
      {
        "origin": "Amsterdam",
        "destination": "Paris",
        "departureTime": "2030-01-01T10:00:00",
        "arrivalTime": "2030-01-01T11:15:00",
        "price": 120.0
      }
      """;

  // ---------- GET: public ----------

  @Test
  void getAllFlights_guest_returns200() throws Exception {
    when(flightService.getAllFlights()).thenReturn(List.of(flight("KL123")));

    mockMvc.perform(get("/api/flights"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].flightNumber").value("KL123"));
  }

  @Test
  void getFlight_unknownFlightNumber_returns404() throws Exception {
    // Relies on your exception handler mapping ResourceNotFoundException to 404
    when(flightService.getFlightByFlightNumber("XX999"))
        .thenThrow(new ResourceNotFoundException("Flight not found: XX999"));

    mockMvc.perform(get("/api/flights/XX999"))
        .andExpect(status().isNotFound());
  }

  // ---------- POST /api/flights: admin only ----------

  @Test
  void createFlight_guest_returns401() throws Exception {
    mockMvc.perform(post("/api/flights")
            .contentType(MediaType.APPLICATION_JSON)
            .content(VALID_FLIGHT_JSON))
        .andExpect(status().isUnauthorized());

    verify(flightService, never()).createFlight(any());
  }

  @Test
  void createFlight_client_returns403() throws Exception {
    mockMvc.perform(post("/api/flights")
            .with(user(principal(UserRole.CLIENT)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(VALID_FLIGHT_JSON))
        .andExpect(status().isForbidden());

    verify(flightService, never()).createFlight(any());
  }

  @Test
  void createFlight_admin_returns201WithLocation() throws Exception {
    when(flightService.createFlight(any(CreateFlightRequest.class))).thenReturn(flight("KL123"));

    mockMvc.perform(post("/api/flights")
            .with(user(principal(UserRole.ADMIN)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(VALID_FLIGHT_JSON))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "http://localhost/api/flights/KL123"))
        .andExpect(jsonPath("$.flightNumber").value("KL123"));
  }

  @Test
  void createFlight_adminEmptyBody_returns400() throws Exception {
    mockMvc.perform(post("/api/flights")
            .with(user(principal(UserRole.ADMIN)))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
        .andExpect(status().isBadRequest());

    verify(flightService, never()).createFlight(any());
  }

  @Test
  void createFlight_adminMalformedJson_returns400() throws Exception {
    mockMvc.perform(post("/api/flights")
            .with(user(principal(UserRole.ADMIN)))
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ this is not json"))
        .andExpect(status().isBadRequest());

    verify(flightService, never()).createFlight(any());
  }

  // ---------- PUT /api/flights/{flightNumber}: admin only ----------

  @Test
  void updateFlight_client_returns403() throws Exception {
    mockMvc.perform(put("/api/flights/KL123")
            .with(user(principal(UserRole.CLIENT)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(UPDATE_FLIGHT_JSON))
        .andExpect(status().isForbidden());

    verify(flightService, never()).updateFlight(any(), any());
  }

  @Test
  void updateFlight_admin_returns200() throws Exception {
    when(flightService.updateFlight(eq("KL123"), any(UpdateFlightRequest.class)))
        .thenReturn(flight("KL123"));

    mockMvc.perform(put("/api/flights/KL123")
            .with(user(principal(UserRole.ADMIN)))
            .contentType(MediaType.APPLICATION_JSON)
            .content(UPDATE_FLIGHT_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.flightNumber").value("KL123"));
  }

  // ---------- DELETE /api/flights/{flightNumber}: admin only ----------

  @Test
  void deleteFlight_guest_returns401() throws Exception {
    mockMvc.perform(delete("/api/flights/KL123"))
        .andExpect(status().isUnauthorized());

    verify(flightService, never()).deleteFlight(any());
  }

  @Test
  void deleteFlight_client_returns403() throws Exception {
    mockMvc.perform(delete("/api/flights/KL123")
            .with(user(principal(UserRole.CLIENT))))
        .andExpect(status().isForbidden());

    verify(flightService, never()).deleteFlight(any());
  }

  @Test
  void deleteFlight_admin_returns200() throws Exception {
    mockMvc.perform(delete("/api/flights/KL123")
            .with(user(principal(UserRole.ADMIN))))
        .andExpect(status().isOk());

    verify(flightService).deleteFlight("KL123");
  }

  // ---------- helpers ----------

  private static CustomUserDetails principal(UserRole role) {
    User u = new User();
    u.setUsername(role.name().toLowerCase());
    u.setPassword("irrelevant");
    u.setRole(role);
    return new CustomUserDetails(u);
  }

  private static Flight flight(String flightNumber) {
    Flight f = new Flight();
    f.setFlightNumber(flightNumber);
    return f;
  }
}