package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    //@Query("select i from ItemRequest i where i.requester.id = ?1 order by i.created DESC")
    List<ItemRequest> getAllByRequester_IdOrderByCreatedDesc(Long requesterId);

    /**
     * Получить список заявок пользователя с ID.
     * @param userId ID пользователя.
     * @param pageable пагинация.
     * @return список заявок на вещи.
     */
    //@Query("select i from ItemRequest i where i.requester.id <> ?1 order by i.created")
    List<ItemRequest> getItemRequestByRequesterIdIsNotOrderByCreated(Long userId, Pageable pageable);

    /**
     * Получить заявки пользователя.
     * @param userId ID пользователя.
     * @param pageable пагинация страницы.
     * @return
     */
    List<ItemRequest> getAllByRequester_Id(Long userId, Pageable pageable);
}
