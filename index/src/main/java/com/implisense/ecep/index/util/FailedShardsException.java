package com.implisense.ecep.index.util;

import com.google.common.base.Joiner;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.ShardSearchFailure;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

public class FailedShardsException extends ElasticsearchException {
    public FailedShardsException(String msg) {
        super(msg);
    }

    public FailedShardsException(ShardSearchFailure[] shardFailures) {
        super(buildMessage(shardFailures));
    }

    private static String buildMessage(ShardSearchFailure[] shardFailures) {
        List<String> errors = Arrays.stream(shardFailures)
                .filter(Objects::nonNull)
                .map(f -> f.index() + ":" + f.shardId() + " (" + f.reason() + ")")
                .collect(toList());

        if (errors.isEmpty()) {
            return "Request failed for unknown reasons";
        }
        if (errors.size() == 1) {
            return "Request failed on shard " + errors.get(0);
        }
        return "Request failed on shards " + Joiner.on(", ").join(errors);
    }
}
