package ru.practicum.shareit.booking.repository;

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
     * @param user пользователь, создавший бронирования.
     * @return список бронирований.
     */
    List<Booking> findAllByBookerOrderByStartTimeDesc(User user);


    /**
     * Метод возвращает все бронирования пользователя, которые действуют на промежутке времени.
     * <p>Если dateTime1 = dateTime2 = now(), то вернётся список вещей, которые сейчас в аренде.</p>
     * @param user      пользователь, брони которого надо найти.
     * @param dateTime1 Момент времени, до которого должна была начаться аренда.
     * @param dateTime2 Момент времени, после которого должна закончиться аренда.
     * @return список бронирований.
     */
    List<Booking> findAllByBookerAndStartTimeBeforeAndEndTimeAfterOrderByStartTimeDesc(
            User user, LocalDateTime dateTime1, LocalDateTime dateTime2);

    /**
     * Метод возвращает все бронирования вещей пользователя, которые действуют на промежутке времени.
     * <p>Если dateTime1 = dateTime2 = now(), то вернётся список вещей, которые сейчас в аренде.</p>
     * @param user      пользователь, брони вещей которого надо найти.
     * @param dateTime1 Момент времени, до которого должна была начаться аренда.
     * @param dateTime2 Момент времени, после которого должна закончиться аренда.
     * @return список бронирований.
     */
    List<Booking> findAllByItem_OwnerAndStartTimeIsBeforeAndEndTimeIsAfterOrderByStartTimeDesc(
            User user, LocalDateTime dateTime1, LocalDateTime dateTime2);

    /**
     * Метод возвращает все брони пользователя завершённые до даты.
     * @param user          пользователь, брони которого надо найти.
     * @param localDateTime момент времени, до которого должна закончиться аренда.
     * @return список бронирований.
     */
    List<Booking> findAllByBookerAndEndTimeIsBeforeOrderByStartTimeDesc(
            User user, LocalDateTime localDateTime);

    /**
     * Возвращает все брони пользователя, начало которых после даты.
     * @param user          пользователь, брони которого надо найти.
     * @param localDateTime момент времени, после которого должна начаться аренда.
     * @return список бронирований.
     */
    List<Booking> findAllByBookerAndStartTimeIsAfterOrderByStartTimeDesc(
            User user, LocalDateTime localDateTime);

    /**
     * Возвращает все брони пользователя, статус которых такой-то.
     * @param user          пользователь, брони которого надо найти.
     * @param bookingStatus статус.
     * @return список бронирований.
     */

    List<Booking> findAllByBookerAndBookingStatusEqualsOrderByStartTimeDesc(
            User user, BookingStatus bookingStatus);


    //////////////////////////////////

    /**
     * Метод возвращает список бронирований вещей пользователя, отсортированных по времени.
     * @param userId пользователь, хозяин вещей.
     * @return список бронирований.
     */
    List<Booking> findAllByItem_OwnerOrderByStartTimeDesc(User userId);


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
     * @return список бронирований.
     **/
    @Query("select b from Booking b where b.item.owner = ?1 and b.endTime < ?2 order by b.startTime DESC")
    List<Booking> findAllByItem_OwnerAndEndTimeIsBeforeOrderByStartTimeDesc(
            User user, LocalDateTime localDateTime);

    /**
     * Возвращает все брони пользователя, начало которых после даты.
     * @param user          пользователь, брони которого надо найти.
     * @param localDateTime момент времени, после которого должна начаться аренда.
     * @return список бронирований.
     **/
    List<Booking> findAllByItem_OwnerAndStartTimeIsAfterOrderByStartTimeDesc(
            User user, LocalDateTime localDateTime);

    /**
     * Возвращает все брони пользователя, статус которых такой-то.
     * @param user          пользователь, брони которого надо найти.
     * @param bookingStatus статус.
     * @return список бронирований.
     **/

    List<Booking> findAllByItem_OwnerAndBookingStatusEqualsOrderByStartTimeDesc(
            User user, BookingStatus bookingStatus);

}
