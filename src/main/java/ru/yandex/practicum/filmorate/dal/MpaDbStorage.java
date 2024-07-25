package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;
import java.util.Optional;

/**
 * Класс для взаимодействия Рейтингов(Mpa) с БД
 */

@Slf4j
@Repository
@Primary
public class MpaDbStorage extends BaseRepository<Mpa> implements MpaStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM films_rating";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films_rating WHERE id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Mpa getMpaById(int id) {
        Optional<Mpa> mpaOptional = findOne(FIND_BY_ID_QUERY, id);
        return mpaOptional.orElseThrow(() -> new NotFoundException("Рейтинг(MPA) с ID=" + id + " не найден"));
    }

    @Override
    public List<Mpa> getAllMpa() {
        return findMany(FIND_ALL_QUERY);
    }
}
