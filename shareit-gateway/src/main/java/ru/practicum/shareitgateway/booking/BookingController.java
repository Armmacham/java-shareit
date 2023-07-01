package ru.practicum.shareitgateway.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareitgateway.booking.dto.BookingInputDTO;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid BookingInputDTO bookingDTO, @RequestHeader("X-Sharer-User-Id") long userId) {
        return client.addBooking(userId, bookingDTO);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@RequestParam(value = "approved") boolean approved, @PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return client.approveOrRejectBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return client.getBookingInformation(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                               @RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return client.getAllBookingsOfCurrentUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllUserItemsBookings(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                    @RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return client.getAllBookingsOfOwner(state, userId, from, size);
    }
}
