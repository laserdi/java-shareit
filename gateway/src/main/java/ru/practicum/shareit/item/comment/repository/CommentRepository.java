package ru.practicum.shareit.item.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    /**
     * Поиск комментариев к вещи.
     * @param itemId ID вещи.
     * @return список комментариев.
     */
    List<Comment> findAllByItem_Id(Long itemId);

    List<Comment> findAllByItemOrderById(Item item);
}
