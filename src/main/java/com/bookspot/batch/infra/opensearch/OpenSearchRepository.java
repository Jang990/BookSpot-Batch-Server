package com.bookspot.batch.infra.opensearch;

import com.bookspot.batch.data.BookDocument;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.opensearch.client.Request;
import org.opensearch.client.Response;
import org.opensearch.client.ResponseException;
import org.opensearch.client.RestClient;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.opensearch.client.opensearch.indices.UpdateAliasesResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenSearchRepository {
    private final OpenSearchClient openSearchClient;
    private final RestClient openSearchRestClient;

    public void save(String indexName, List<? extends BookDocument> list) {
        BulkRequest.Builder br = new BulkRequest.Builder();
        for (BookDocument bookDocument : list) {
            br.operations(
                    op -> op.index(
                            idx -> idx.index(indexName)
                                    .id(bookDocument.getBookId())
                                    .document(bookDocument)
                    )
            );
        }

        try {
            openSearchClient.bulk(br.build());
        } catch (IOException e) {
            handleIf504Error(e);
            throw new RuntimeException("Bulk Insert 실패: " + indexName, e);
        }
    }

    // TODO: 별로지만 일단 504만 처리함. catch문 개선 필요
    private void handleIf504Error(IOException exception) {
        if(!(exception instanceof ResponseException re))
            return;

        boolean is504Error = re.getResponse().getStatusLine().getStatusCode()
                        == HttpStatus.SC_GATEWAY_TIMEOUT;

        if(is504Error)
            throw new OpenSearch504Exception(re);
    }

    public boolean addAlias(String indexName, String alias) {
        try {
            UpdateAliasesResponse response = openSearchClient.indices().updateAliases(u -> u
                    .actions(a -> a
                            .add(add -> add
                                    .index(indexName)
                                    .alias(alias)
                            )
                    )
            );
            return response.acknowledged();
        } catch (IOException e) {
            handleIf504Error(e);
            throw new RuntimeException("Alias 추가 실패: " + alias, e);
        }
    }

    public boolean removeAlias(String indexName, String alias) {
        try {
            UpdateAliasesResponse response = openSearchClient.indices().updateAliases(u -> u
                    .actions(a -> a
                            .remove(rem -> rem
                                    .index(indexName)
                                    .alias(alias)
                            )
                    )
            );
            return response.acknowledged();
        } catch (IOException e) {
            handleIf504Error(e);
            throw new RuntimeException("Alias 제거 실패: " + alias, e);
        }
    }

    public boolean delete(String indexName) {
        try {
            DeleteIndexResponse response = openSearchClient.indices().delete(d -> d.index(indexName));
            return response.acknowledged();
        } catch (IOException e) {
            handleIf504Error(e);
            throw new RuntimeException("인덱스 삭제 실패: " + indexName, e);
        }
    }

    public void createIndex(String indexName, String schema) {
        try {
            Request req = new Request("PUT", "/" + indexName);
            req.setEntity(new NStringEntity(schema, ContentType.APPLICATION_JSON));
            Response resp = openSearchRestClient.performRequest(req);
            int status = resp.getStatusLine().getStatusCode();
            if(status < 200 || 300 <= status)
                throw new RuntimeException("인덱스 생성 실패: " + resp);
        } catch (IOException e) {
            handleIf504Error(e);
            throw new RuntimeException("인덱스 생성 실패: " + indexName, e);
        }
    }
}
