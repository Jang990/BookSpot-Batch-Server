package com.bookspot.batch.stock.writer.file;

import com.bookspot.batch.stock.data.StockFileData;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StockFileWriterConfig {
    private final StockFileDownloader stockFileDownloader;

    @Bean
    public ItemWriter<StockFileData> stockFileDownloaderWriter() {
        ItemWriterAdapter<StockFileData> adapter = new ItemWriterAdapter<>();
        adapter.setTargetObject(stockFileDownloader);
        adapter.setTargetMethod("download");
        return adapter;
    }
}
