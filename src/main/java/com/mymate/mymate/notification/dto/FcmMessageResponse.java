package com.mymate.mymate.notification.dto;

public class FcmMessageResponse {
    private boolean success;
    private int successCount;
    private int failureCount;
    private String messageId;
    private String errorMessage;

    public static FcmMessageResponse success(String messageId) {
        FcmMessageResponse r = new FcmMessageResponse();
        r.success = true;
        r.messageId = messageId;
        return r;
    }

    public static FcmMessageResponse success(int successCount, int failureCount) {
        FcmMessageResponse r = new FcmMessageResponse();
        r.success = true;
        r.successCount = successCount;
        r.failureCount = failureCount;
        return r;
    }

    public static FcmMessageResponse failure(String error) {
        FcmMessageResponse r = new FcmMessageResponse();
        r.success = false;
        r.errorMessage = error;
        return r;
    }

    public boolean isSuccess() { return success; }
    public int getSuccessCount() { return successCount; }
    public int getFailureCount() { return failureCount; }
    public String getMessageId() { return messageId; }
    public String getErrorMessage() { return errorMessage; }
}


