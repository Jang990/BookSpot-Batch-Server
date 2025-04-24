package com.bookspot.batch;

import lombok.RequiredArgsConstructor;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.mapping.Property;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempService {
    private final OpenSearchClient openSearchClient;
    private static final String indexName = "my_book";

    //인덱스 생성
    public void createIndex() {
        try {
            CreateIndexRequest request = CreateIndexRequest.of(builder -> builder
                    .index("my_index")  // 인덱스 이름 지정
                    .settings(settings -> settings
                            .numberOfShards("1")
                            .numberOfReplicas("0")
                    )
            );
            openSearchClient.indices().create(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
