package com.bookspot.batch.step.reader.file.excel.library;

import com.bookspot.batch.data.Library;
import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.support.rowset.RowSet;

public class LibraryExcelRowMapper implements RowMapper<Library> {
    @Override
    public Library mapRow(RowSet rs) throws Exception {
        String[] row = rs.getCurrentRow();
        return new Library(
                getString(row, LibraryExcelSpec.LIBRARY_CODE),
                getString(row, LibraryExcelSpec.NAME),
                getAddress(row),
                getString(row, LibraryExcelSpec.TEL),
                Double.parseDouble(getString(row, LibraryExcelSpec.LATITUDE)),
                Double.parseDouble(getString(row, LibraryExcelSpec.LONGITUDE)),
                getString(row, LibraryExcelSpec.HOMEPAGE),
                getString(row, LibraryExcelSpec.CLOSED),
                getString(row, LibraryExcelSpec.OPERATING_INFO)
        );
    }

    private static String getAddress(String[] row) {
        return getString(row, LibraryExcelSpec.ADDRESS)
                .replace("&middot;", "·") // "경상남도 창원시 마산회원구 3&middot;15대로 558"
                .replaceAll("\\s{2,}", " "); // "경상북도  포항시..." - 공백 2개 이상
    }

    private static String getString(String[] row, LibraryExcelSpec spec) {
        return row[spec.index].trim();
    }
}
