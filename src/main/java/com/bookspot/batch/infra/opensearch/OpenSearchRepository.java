package com.bookspot.batch.infra.opensearch;

import com.bookspot.batch.data.document.BookCommonFields;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.opensearch.client.opensearch.indices.update_aliases.Action;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchRepository {
    private final OpenSearchClient openSearchClient;
    private final RestClient openSearchRestClient;

    public void save(String indexName, List<? extends BookCommonFields> list) {
        BulkRequest.Builder br = new BulkRequest.Builder();
        for (BookCommonFields bookDocument : list) {
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
            log.error("Bulk Insert 실패 ", e);
            handleRetryableError(e);
            throw new RuntimeException("Bulk Insert 실패: " + indexName, e);
        }
    }

    // TODO: 별로지만 일단 504만 처리함. catch문 개선 필요
    private void handleRetryableError(IOException exception) {
        if(exception instanceof SocketTimeoutException)
            throw new OpenSearchRetryableException(exception);

        if(!(exception instanceof ResponseException re))
            return;

        boolean is504Error = re.getResponse().getStatusLine().getStatusCode()
                        == HttpStatus.SC_GATEWAY_TIMEOUT;
        boolean is429Error = re.getResponse().getStatusLine().getStatusCode()
                == HttpStatus.SC_TOO_MANY_REQUESTS;

        if(is504Error || is429Error)
            throw new OpenSearchRetryableException(re);
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
            log.error("Alias 추가 실패 ", e);
            handleRetryableError(e);
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
            log.error("Alias 제거 실패", e);
            handleRetryableError(e);
            throw new RuntimeException("Alias 제거 실패: " + alias, e);
        }
    }

    public boolean moveIndexAlias(String fromIndex, String toIndex, String alias) {
        try {
            UpdateAliasesResponse response = openSearchClient.indices().updateAliases(
                    u -> u.actions(
                            List.of(
                                    new Action.Builder()
                                            .remove(r -> r.index(fromIndex).alias(alias))
                                            .build(),
                                    new Action.Builder()
                                            .add(r -> r.index(toIndex).alias(alias))
                                            .build()
                            )
                    )
            );
            return response.acknowledged();
        } catch (IOException e) {
            log.error("Alias 제거 실패", e);
            handleRetryableError(e);
            throw new RuntimeException("Alias 제거 실패: " + alias, e);
        }
    }

    public boolean delete(String indexName) {
        try {
            DeleteIndexResponse response = openSearchClient.indices().delete(d -> d.index(indexName));
            return response.acknowledged();
        } catch (IOException e) {
            log.error("인덱스 삭제 실패", e);
            handleRetryableError(e);
            throw new RuntimeException("인덱스 삭제 실패: " + indexName, e);
        }
    }

    public void createIndex(String indexName, String schema) {
        try {
            Request req = new Request("PUT", "/" + indexName);
            req.setEntity(new NStringEntity(schema, ContentType.APPLICATION_JSON));
            Response resp = openSearchRestClient.performRequest(req);
            int status = resp.getStatusLine().getStatusCode();
            if (status < 200 || 300 <= status) {
                log.error("인덱스 생성 실패 response code : {}", status);
                throw new RuntimeException("인덱스 생성 실패: " + resp);
            }
        } catch (IOException e) {
            log.error("인덱스 생성 실패", e);
            handleRetryableError(e);
            throw new RuntimeException("인덱스 생성 실패: " + indexName, e);
        }
    }
}
