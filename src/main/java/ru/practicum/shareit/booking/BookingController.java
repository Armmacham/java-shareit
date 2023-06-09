package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@AllArgsConstructor
@Validated
class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDTO create(@RequestBody BookingInputDTO bookingDTO, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.addBooking(userId, bookingDTO);
    }

    @PatchMapping("/{bookingId}")
    public BookingDTO updateStatus(@RequestParam(value = "approved") boolean approved, @PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.approveOrRejectBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDTO getBooking(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingInformation(bookingId, userId);
    }

    @GetMapping
    public List<BookingDTO> getAllUserBookings(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                               @RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return bookingService.getAllBookingsOfCurrentUser(State.convert(state), userId, PageRequest.of(from / size, size).withSort(Sort.by("start").descending()));
    }

    @GetMapping("/owner")
    public List<BookingDTO> getAllUserItemsBookings(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                    @RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(20) Integer size) {
        return bookingService.getAllBookingsOfOwner(State.convert(state), userId, PageRequest.of(from / size, size).withSort(Sort.by("start").descending()));
    }

}
