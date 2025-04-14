package at.modoo.event.listener;

import at.modoo.annotation.CreatedIp;
import at.modoo.annotation.LastModifiedIp;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public class IpAddressAuditingEntityListener {

    @PrePersist
    public void setCreatedIp(Object target) {
        setIpField(target, CreatedIp.class, getCurrentIp());
    }

    @PreUpdate
    public void setLastModifiedIp(Object target) {
        setIpField(target, LastModifiedIp.class, getCurrentIp());
    }

    private void setIpField(Object target, Class<? extends Annotation> annotationClass, String value) {
        Class<?> clazz = target.getClass();
        while (clazz != null) {
            Arrays.stream(clazz.getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(annotationClass))
                    .peek(field -> field.setAccessible(true))
                    .forEach(field -> {
                        try {
                            field.set(target, value);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("IP 할당 예외");
                        }
                    });
            clazz = clazz.getSuperclass();
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
