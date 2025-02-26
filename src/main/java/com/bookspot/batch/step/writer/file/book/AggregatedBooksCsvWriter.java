package com.bookspot.batch.step.writer.file.book;

import com.bookspot.batch.global.file.spec.AggregatedBooksCsvSpec;
import com.bookspot.batch.step.service.memory.Isbn13Convertor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregatedBooksCsvWriter {
    public void saveToCsv(Map<Long, Integer> map) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(AggregatedBooksCsvSpec.FILE_PATH, false))) {
            // 헤더?
            /*writer.write("id,title,author");
            writer.newLine();*/

            for (Map.Entry<Long, Integer> data : map.entrySet()) {
                String line = AggregatedBooksCsvSpec.createLine(
                        Isbn13Convertor.convert(data.getKey()),
                        data.getValue()
                );
                writer.write(line);
                writer.newLine();
            }

        } catch (IOException e) {
            log.warn("유니크 도서 파일 생성 오류", e);
        }
    }
}
