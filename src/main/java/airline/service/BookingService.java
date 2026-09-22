package airline.service;

import airline.entity.Booking;
import java.util.List;

public interface BookingService {
  List<Booking> getAllBookings();

  // get booking by id and userName
  Booking getBooking(long id, String userName);

  Booking createBooking(Booking booking);

  Booking updateBooking(Booking booking);
  
  void deleteBooking(long id);
}
