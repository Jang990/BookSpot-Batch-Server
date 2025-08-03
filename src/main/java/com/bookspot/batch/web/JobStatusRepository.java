package com.bookspot.batch.web;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JobStatusRepository {
    private final JdbcTemplate jdbc;

    private static final String RUNNING_JOB_NAMES_SQL = """
        SELECT DISTINCT i.JOB_NAME
        FROM BATCH_JOB_INSTANCE i
        JOIN BATCH_JOB_EXECUTION e ON i.JOB_INSTANCE_ID = e.JOB_INSTANCE_ID
        WHERE e.STATUS IN ('STARTING','STARTED','STOPPING')
        """;

    public Set<String> getRunningJobNames() {
        return new HashSet<>(
                jdbc.query(
                        RUNNING_JOB_NAMES_SQL,
                        (rs, rowNum) -> rs.getString("JOB_NAME")
                )
        );
    }
}
