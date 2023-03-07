package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepositoryJpa extends JpaRepository<Item, Long> {
    /**
     * Поиск вещей пользователя.
     * @param owner пользователь, чьи вещи надо найти в БД.
     * @return список вещей.
     */
    List<Item> findAllByOwnerOrderById(User owner);


    @Query("select item from Item item "
            + "where lower(item.name) like lower(concat('%', ?1, '%')) "
            + "or lower(item.description) like lower(concat('%', ?1, '%')) ")
//@Query("select i from Item i " +
//        "where upper(i.name) like upper(concat('%', ?1, '%')) or upper(i.description) like upper(concat('%', ?2, '%')) " +
//        "order by i.id")
//List<Item> findAllByNameContainsIgnoreCaseOrDescriptionContainsIgnoreCaseOrderById(String text, String text, Pageable pageable);

    List<Item> searchItemsByText(String text, Pageable pageable);
}
