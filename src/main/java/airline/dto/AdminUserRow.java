package airline.dto;

import airline.entity.User;

public record AdminUserRow(
    User user,
    int totalBookings) {

}
