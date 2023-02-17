package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepositoryJpa extends JpaRepository<Booking, Long> {
    /**
     * Метод возвращает список бронирований, созданных пользователем, отсортированных по времени.
     * @param user     пользователь, создавший бронирования.
     * @param pageable параметр постраничного отображения результатов запроса.
     * @return список бронирований.
     */
    //@Query("select b from Booking b where b.booker = ?1 order by b.startTime DESC")
    List<Booking> findAllByBookerOrderByStartTimeDesc(User user, Pageable pageable);     //Или так.
    //List<Booking> findAllBookingsByBooker(User user, Pageable pageable);


    /**
     * Метод возвращает все бронирования пользователя, которые действуют на промежутке времени.
     * <p>Если dateTime1 = dateTime2 = now(), то вернётся список вещей, которые сейчас в аренде.</p>
     * @param user      пользователь, брони которого надо найти.
     * @param dateTime1 Момент времени, до которого должна была начаться аренда.
     * @param dateTime2 Момент времени, после которого должна закончиться аренда.
     * @param pageable  параметр постраничного отображения результатов запроса.
     * @return список бронирований.
     */
    @Query("select b from Booking b " +
            "where b.booker = ?1 and b.startTime < ?2 and b.endTime > ?3 " +
            "order by b.startTime DESC")
//    List<Booking> findAllByBookerAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(   //Или так
    List<Booking> findAllBookingsForBookerWithStartAndEndTime(
            User user, LocalDateTime dateTime1, LocalDateTime dateTime2, Pageable pageable);

    /**
     * Метод возвращает все бронирования вещей пользователя, которые действуют на промежутке времени.
     * <p>Если dateTime1 = dateTime2 = now(), то вернётся список вещей, которые сейчас в аренде.</p>
     * @param user      пользователь, брони вещей которого надо найти.
     * @param dateTime1 Момент времени, до которого должна была начаться аренда.
     * @param dateTime2 Момент времени, после которого должна закончиться аренда.
     * @param pageable  параметр постраничного отображения результатов запроса.
     * @return список бронирований.
     */
    @Query("select b from Booking b " +
            "where b.item.owner = ?1 and b.startTime < ?2 and b.endTime > ?3 " +
            "order by b.startTime DESC")
//    List<Booking> findAllByItem_OwnerAndStartTimeIsBeforeAndEndTimeIsAfterOrderByStartTimeDesc(
    List<Booking> findAllBookingsItemByForOwnerWithStartAndEndTime(
            User user, LocalDateTime dateTime1, LocalDateTime dateTime2, Pageable pageable);

    /**
     * Метод возвращает все брони пользователя завершённые до даты.
     * @param user          пользователь, брони которого надо найти.
     * @param localDateTime момент времени, до которого должна закончиться аренда.
     * @return список бронирований.
     */
    List<Booking> findAllByBookerAndEndTimeIsBeforeOrderByStartTimeDesc(
            User user, LocalDateTime localDateTime, Pageable pageable);

    /**
     * Возвращает все брони пользователя, начало которых после даты.
     * @param user          пользователь, брони которого надо найти.
     * @param localDateTime момент времени, после которого должна начаться аренда.
     * @param pageable      параметр постраничного отображения результатов запроса.
     * @return список бронирований.
     */
    List<Booking> findAllByBookerAndStartTimeIsAfterOrderByStartTimeDesc(
            User user, LocalDateTime localDateTime, Pageable pageable);

    /**
     * Возвращает все брони пользователя, статус которых такой-то.
     * @param user          пользователь, брони которого надо найти.
     * @param bookingStatus статус.
     * @param pageable      параметр постраничного отображения результатов запроса.
     * @return список бронирований.
     */

    List<Booking> findAllByBookerAndBookingStatusEqualsOrderByStartTimeDesc(
            User user, BookingStatus bookingStatus, Pageable pageable);


    //////////////////////////////////

    /**
     * Метод возвращает список бронирований вещей пользователя, отсортированных по времени.
     * @param userId   пользователь, хозяин вещей.
     * @param pageable параметр постраничного отображения результатов запроса.
     * @return список бронирований.
     */
    List<Booking> findAllByItem_OwnerOrderByStartTimeDesc(User userId, Pageable pageable);


    /**
     * Метод возвращает все бронирования вещей пользователя, которые действуют на промежутке времени.
     * <p>Если dateTime1 = dateTime2 = now(), то вернётся список вещей, которые сейчас в аренде.</p>
     * @param user      пользователь, брони которого надо найти.
     * @param dateTime1 Момент времени, до которого должна была начаться аренда.
     * @param dateTime2 Момент времени, после которого должна закончиться аренда.
     * @return список бронирований.
     */
    List<Booking> findAllByItem_OwnerAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(
            User user, LocalDateTime dateTime1, LocalDateTime dateTime2);

    /**
     * Метод возвращает все брони вещей пользователя завершённые до даты.
     * @param user          пользователь, брони которого надо найти.
     * @param localDateTime момент времени, до которого должна закончиться аренда.
     * @param pageable      параметр постраничного отображения результатов запроса.
     * @return список бронирований.
     **/
    @Query("select b from Booking b where b.item.owner = ?1 and b.endTime < ?2 order by b.startTime DESC")
    List<Booking> findAllByItem_OwnerAndEndTimeIsBeforeOrderByStartTimeDesc(
            User user, LocalDateTime localDateTime, Pageable pageable);

    /**
     * Возвращает все брони пользователя, начало которых после даты.
     * @param user          пользователь, брони которого надо найти.
     * @param localDateTime момент времени, после которого должна начаться аренда.
     * @param pageable      параметр постраничного отображения результатов запроса.
     * @return список бронирований.
     **/
    List<Booking> findAllByItem_OwnerAndStartTimeIsAfterOrderByStartTimeDesc(
            User user, LocalDateTime localDateTime, Pageable pageable);

    /**
     * Возвращает все брони пользователя, статус которых такой-то.
     * @param user          пользователь, брони которого надо найти.
     * @param bookingStatus статус.
     * @param pageable      параметр постраничного отображения результатов запроса.
     * @return список бронирований.
     **/

    List<Booking> findAllByItem_OwnerAndBookingStatusEqualsOrderByStartTimeDesc(
            User user, BookingStatus bookingStatus, Pageable pageable);

}
