package org.eustrosoft.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class JdbcBlobProcessor {
    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;

//    public Consumer<InputStream> pusher(Long id) {
//        return is -> {
//            String statement = "update fjd set file = :content where id = :id";
//            NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(this.jdbcTemplate);
//            MapSqlParameterSource parameters = new MapSqlParameterSource();
//            parameters.addValue("id", id);
//            parameters.addValue("content", new SqlLobValue(is, guessStreamSize(is)), Types.BLOB);
//            jdbcTemplate.update(statement, parameters);
//        };
//    }

    public Consumer<OutputStream> puller(Long id) {
        return os -> {
            RowMapper<Void> rowMapper = (rs, rowNum) -> withBlob(rs, is -> copy(is, os));
            String statement = "select file_data from file where id = :id";
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("id", id);
            NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(this.jdbcTemplate);
            jdbcTemplate.queryForObject(statement, parameters, rowMapper);
        };
    }

    @SneakyThrows
    protected Void withBlob(ResultSet rs, Consumer<InputStream> consumer) {
        LobHandler lobHandler = new DefaultLobHandler();
        try (InputStream is = lobHandler.getBlobAsBinaryStream(rs, "file_data")) {
            consumer.accept(is);
            return null;
        }
    }

    @SneakyThrows
    protected long copy(InputStream in, OutputStream out) {
        return IOUtils.copy(in, out);
    }

//    protected int guessStreamSize(InputStream is) {
//        Dialect dialect = getDialect();
//        if (dialect instanceof Oracle8iDialect) {
//            return Integer.MAX_VALUE;
//        }
//        return -1;
//    }

//    protected Dialect getDialect() {
//        return entityManager.unwrap(SharedSessionContractImplementor.class)
//                .getJdbcServices().getDialect();
//    }
}
