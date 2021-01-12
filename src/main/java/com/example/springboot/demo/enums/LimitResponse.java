package com.example.springboot.demo.enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LimitResponse {

    private final boolean allowed;

    private final long tokensRemaining;

    private final List<String> keys;

    private final Map<String, String> headers;

    public LimitResponse(boolean allowed,List<String> key){
        this(allowed,-1,key,new HashMap<>());
    }

    public LimitResponse(boolean allowed, long tokensRemaining,List<String> key,Map<String, String> headers) {
        this.allowed = allowed;
        this.tokensRemaining = tokensRemaining;
        this.keys = key;
        this.headers = headers;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public long getTokensRemaining() {
        return tokensRemaining;
    }

    public List<String> getKeys(){
        return keys;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    @Override
    public String toString() {
        return "LimitResponse{" + "allowed=" + allowed +
                ", headers=" + headers +
                ", tokensRemaining=" + tokensRemaining +
                ", key=" + (keys == null || keys.size() == 0 ? "null" : keys.get(0)) +
                '}';
    }
}
