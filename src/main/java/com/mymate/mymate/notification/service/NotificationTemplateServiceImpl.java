package com.mymate.mymate.notification.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.mymate.mymate.common.exception.notification.NotificationHandler;
import com.mymate.mymate.common.exception.notification.status.NotificationErrorStatus;
import com.mymate.mymate.notification.entity.NotificationTemplate;
import com.mymate.mymate.notification.enums.NotificationType;
import com.mymate.mymate.notification.repository.NotificationTemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private final NotificationTemplateRepository templateRepository;

    @Override
    public TemplateResult render(NotificationType type, Map<String, Object> variables) {
        NotificationTemplate template = templateRepository.findByTypeAndIsActiveTrue(type)
                .orElseThrow(() -> new NotificationHandler(NotificationErrorStatus.TEMPLATE_NOT_FOUND));

        String title = applyVariables(template.getTitleTemplate(), variables);
        String content = applyVariables(template.getContentTemplate(), variables);
        String navigationUrl = template.getNavigationUrlTemplate() != null
                ? applyVariables(template.getNavigationUrlTemplate(), variables)
                : null;
        return new TemplateResult(title, content, navigationUrl);
    }

    private String applyVariables(String template, Map<String, Object> variables) {
        if (template == null || variables == null || variables.isEmpty()) return template;
        String result = template;
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            result = result.replace("{" + key + "}", value == null ? "" : String.valueOf(value));
        }
        return result;
    }
}


