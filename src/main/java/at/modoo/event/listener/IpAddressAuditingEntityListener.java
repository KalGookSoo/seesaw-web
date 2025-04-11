package at.modoo.event.listener;

import at.modoo.annotation.CreatedIp;
import at.modoo.annotation.LastModifiedIp;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.lang.reflect.Field;

public class IpAddressAuditingEntityListener {

    @PrePersist
    public void setCreatedIp(Object target) {
        setIpField(target, CreatedIp.class, getCurrentIp());
    }

    @PreUpdate
    public void setLastModifiedIp(Object target) {
        setIpField(target, LastModifiedIp.class, getCurrentIp());
    }

    private void setIpField(Object target, Class annotationClass, String value) {
        try {
            for (Field field : target.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(annotationClass)) {
                    field.setAccessible(true);
                    field.set(target, value);
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("IP 할당 예외");
        }
    }

    private String getCurrentIp() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof WebAuthenticationDetails) {
            return ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        }
        return "UNKNOWN";
    }

}
