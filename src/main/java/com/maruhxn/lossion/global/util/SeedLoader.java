package com.maruhxn.lossion.global.util;

import com.maruhxn.lossion.global.common.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("dev")
public class SeedLoader {
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final int TOTAL_SIZE = 10;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        long beforeTime = System.currentTimeMillis();
        log.info("더미 데이터 생성 시작");
        batchInsertMember();
        log.info("Member 생성 완료");
        batchInsertCategory();
        log.info("Category 생성 완료");
        batchInsertTopic();
        log.info("Topic 생성 완료");
        long afterTime = System.currentTimeMillis();
        long diffTime = afterTime - beforeTime;
        log.info("실행 시간(ms) = {}", diffTime);
    }

    private void batchInsertMember() {
        String admin_sql = "INSERT INTO member" +
                "(account_id, username, email, password, profile_image, created_at, updated_at, role)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(admin_sql, ps -> {
            ps.setString(1, "admin");
            ps.setString(2, "admin");
            ps.setString(3, "admin@test.com");
            ps.setString(4, passwordEncoder.encode("admin"));
            ps.setString(5, Constants.BASIC_PROFILE_IMAGE_NAME);
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, "ROLE_ADMIN");
        });

        String member_sql = "INSERT INTO member" +
                "(account_id, username, email, password, profile_image, created_at, updated_at)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(member_sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String password = "test";
                String accountIdFormat = "tester%d";
                String usernameFormat = "tester%d";
                String emailFormat = "test%d@test.com";
                ps.setString(1, String.format(accountIdFormat, i + 1));
                ps.setString(2, String.format(usernameFormat, i + 1));
                ps.setString(3, String.format(emailFormat, i + 1));
                ps.setString(4, passwordEncoder.encode(password));
                ps.setString(5, Constants.BASIC_PROFILE_IMAGE_NAME);
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            }

            @Override
            public int getBatchSize() {
                return TOTAL_SIZE;
            }
        });
    }

    private void batchInsertCategory() {
        String sql = "INSERT INTO category" +
                " (name, created_at, updated_at)" +
                " VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, String.format("test%d", i + 1));
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            }

            @Override
            public int getBatchSize() {
                return TOTAL_SIZE;
            }
        });
    }

    private void batchInsertTopic() {
        String sql = "INSERT INTO topic" +
                " (title, description, first_choice, second_choice, closed_at, author_id, category_id, created_at, updated_at)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, String.format("test%d", i + 1));
                ps.setString(2, "test입니다.");
                ps.setString(3, "first choice");
                ps.setString(4, "second choice");
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                ps.setLong(6, (long) (Math.random() * TOTAL_SIZE + 1));
                ps.setLong(7, (long) (Math.random() * TOTAL_SIZE + 1));
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            }

            @Override
            public int getBatchSize() {
                return TOTAL_SIZE;
            }
        });
    }
}
