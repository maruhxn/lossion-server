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
    private int TOTAL_SIZE = 10;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        long beforeTime = System.currentTimeMillis();
        log.info("더미 데이터 생성 시작");
        batchInsertMember();
        log.info("Member 생성 완료");
        long afterTime = System.currentTimeMillis();
        long diffTime = afterTime - beforeTime;
        log.info("실행 시간(ms) = {}", diffTime);
    }

    private void batchInsertMember() {
        String admin_sql = "INSERT INTO member" +
                "(account_id, username, email, tel_number, password, profile_image, created_at, updated_at, role)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(admin_sql, ps -> {
            ps.setString(1, "admin");
            ps.setString(2, "admin");
            ps.setString(3, "admin@test.com");
            ps.setString(4, "01023686397");
            ps.setString(5, passwordEncoder.encode("admin"));
            ps.setString(6, Constants.BASIC_PROFILE_IMAGE_NAME);
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(9, "ROLE_ADMIN");
        });

        String member_sql = "INSERT INTO member" +
                "(account_id, username, email, tel_number, password, profile_image, created_at, updated_at)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(member_sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String password = "test";
                String accountIdFormat = "tester%d";
                String usernameFormat = "tester%d";
                String emailFormat = "test%d@test.com";
                String telNumberFormat = "%011d";
                ps.setString(1, String.format(accountIdFormat, i + 1));
                ps.setString(2, String.format(usernameFormat, i + 1));
                ps.setString(3, String.format(emailFormat, i + 1));
                ps.setString(4, String.format(telNumberFormat, i + 1));
                ps.setString(5, passwordEncoder.encode(password));
                ps.setString(6, Constants.BASIC_PROFILE_IMAGE_NAME);
                ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            }

            @Override
            public int getBatchSize() {
                return TOTAL_SIZE;
            }
        });
    }
}