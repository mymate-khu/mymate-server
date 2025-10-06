package com.mymate.mymate.notification.dto;

import java.util.List;
import java.util.Map;

public class FcmMessageRequest {
    private List<Long> recipientIds;
    private String title;
    private String content;
    private Map<String, String> variables;

    public List<Long> getRecipientIds() { return recipientIds; }
    public void setRecipientIds(List<Long> recipientIds) { this.recipientIds = recipientIds; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Map<String, String> getVariables() { return variables; }
    public void setVariables(Map<String, String> variables) { this.variables = variables; }
}


