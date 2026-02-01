package io.github.taichi0373.benefit_map.dto;

import java.util.List;

public class StoreResponse {
    private boolean success;
    private String message;
    private List<StoreInfo> stores;

    public StoreResponse() {}

    public StoreResponse(boolean success, String message, List<StoreInfo> stores) {
        this.success = success;
        this.message = message;
        this.stores = stores;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<StoreInfo> getStores() {
        return stores;
    }

    public void setStores(List<StoreInfo> stores) {
        this.stores = stores;
    }
}
