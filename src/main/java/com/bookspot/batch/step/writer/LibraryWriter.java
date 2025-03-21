package com.bookspot.batch.step.writer;

import com.bookspot.batch.data.Library;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import javax.sql.DataSource;

public class LibraryWriter extends JdbcBatchItemWriter<Library> {

    public LibraryWriter(DataSource dataSource) {
        setDataSource(dataSource);
        setSql("""
                    INSERT INTO library (name, library_code, location, address, updated_at, operating_info, closed_info, home_page) VALUES
                    (?, ?, ST_GeomFromText(CONCAT('POINT(', ?, ' ', ?, ')'), 4326), ?, NOW(), ?, ?, ?)
                    ON DUPLICATE KEY UPDATE
                        name = VALUES(name), location = VALUES(location), address = VALUES(address), updated_at = NOW(),
                        operating_info = VALUES(operating_info), closed_info = VALUES(closed_info), home_page = VALUES(home_page);
                """);

        setItemPreparedStatementSetter(
                (library, ps) -> {
                    ps.setString(1, library.getName());
                    ps.setString(2, library.getLibraryCode());
                    ps.setDouble(3, library.getLongitude());
                    ps.setDouble(4, library.getLatitude());
                    ps.setString(5, library.getAddress());
                    ps.setString(6, library.getOperatingInfo());
                    ps.setString(7, library.getClosedInfo());
                    ps.setString(8, library.getHomePage());
                }
        );
    }
}
