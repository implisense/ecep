package com.implisense.ecep.index.util;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticsearchRequestExecutor {

    private static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchRequestExecutor.class);
    public static int DEFAULT_NUM_RETRIES = 5;
    public static boolean DEFAULT_EXCEPTION_HANDLING_QUIET = false;
    public static TimeValue DEFAULT_TIMEOUT = null;

    public static <T extends ActionResponse> T execute(ActionRequestBuilder<?, T, ?> request) {
        return execute(request, DEFAULT_NUM_RETRIES, DEFAULT_EXCEPTION_HANDLING_QUIET);
    }

    public static <T extends ActionResponse> T execute(ActionRequestBuilder<?, T, ?> request, boolean quietly) {
        return execute(request, DEFAULT_NUM_RETRIES, quietly);
    }

    public static <T extends ActionResponse> T execute(ActionRequestBuilder<?, T, ?> request, TimeValue timeout) {
        return execute(request, DEFAULT_NUM_RETRIES, DEFAULT_EXCEPTION_HANDLING_QUIET, timeout);
    }

    public static <T extends ActionResponse> T execute(ActionRequestBuilder<?, T, ?> request, int numRetries) {
        return execute(request, numRetries, DEFAULT_EXCEPTION_HANDLING_QUIET);
    }

    public static <T extends ActionResponse> T execute(ActionRequestBuilder<?, T, ?> request, int numRetries,
                                                       boolean quietly) {
        return execute(request, numRetries, quietly, DEFAULT_TIMEOUT);
    }

    public static <T extends ActionResponse> T execute(ActionRequestBuilder<?, T, ?> request, int numRetries,
                                                       boolean quietly, TimeValue timeout) {
        return execute(request, numRetries, quietly, timeout, false);
    }

    public static <T extends ActionResponse> T execute(ActionRequestBuilder<?, T, ?> request, int numRetries,
                                                       boolean quietly, TimeValue timeout, Boolean ignoreErrors) {
        T response = null;
        int n = 0;
        while (response == null) {
            try {
                if (timeout != null) {
                    response = (T) request.get(timeout);
                } else {
                    response = (T) request.get();
                }
                if (!ignoreErrors) {
                    if (response != null && response instanceof BulkResponse && ((BulkResponse) response).hasFailures()) {
                        throw new ElasticsearchException("BulkResponse has failures:" + ((BulkResponse) response).buildFailureMessage());
                    }
                }
            } catch (ElasticsearchException e) {
                LOGGER.error("Exception in ES request execution!", e);
                if (n < numRetries) {
                    n++;
                    delay(n);
                } else {
                    if (quietly) {
                        break;
                    } else {
                        throw e;
                    }
                }
            }
        }
        if (response != null && response instanceof SearchResponse) {
            int failedShards = ((SearchResponse) response).getFailedShards();
            if (failedShards > 0) {
                throw new FailedShardsException(((SearchResponse) response).getShardFailures());
            }
        }
        return response;
    }

    private static void delay(int retry) {
        try {
            Thread.sleep(Math.round(Math.pow(2.0, (double) retry)) * 1000L);
        } catch (InterruptedException interruptedException) {
            LOGGER.error("Exception while waiting for retry!", interruptedException);
        }
    }
}
