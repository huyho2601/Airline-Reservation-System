package airline.service;

import airline.dto.CreateBookingRequest;
import airline.entity.Booking;
import airline.entity.User;

import java.util.List;

public interface BookingService {
  List<Booking> getAllBookings(long adminId);

  // get booking by id and userName
  public Booking getBooking(long id, User user);

  public Booking createBooking(CreateBookingRequest request, User user);

  public Booking updateSeatBooking(long bookingId, long seatId, User currentUser);
  
  public void deleteBooking(long id, User currentUser);
}
