package com.bookspot.batch.step.service;

import com.bookspot.batch.global.file.spec.AggregatedBooksCsvSpec;
import com.bookspot.batch.step.service.memory.Isbn13Convertor;
import com.bookspot.batch.step.service.memory.loan.LoanCountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class AggregatedBooksCsvWriter {
    private final String filePath;
    private final LoanCountService loanCountService;

    public void saveToCsv() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            // 헤더?
            /*writer.write("id,title,author");
            writer.newLine();*/
            loanCountService.processAll(
                    (k, v) -> {
                        try {
                            String line = AggregatedBooksCsvSpec.createLine(Isbn13Convertor.convert(k), v);
                            writer.write(line);
                            writer.newLine();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

            );

        } catch (IOException e) {
            log.warn("유니크 도서 파일 생성 오류", e);
            throw e;
        }
    }
}
