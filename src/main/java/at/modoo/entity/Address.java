package at.modoo.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static lombok.AccessLevel.PROTECTED;

/**
 * 주소
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = PROTECTED)
public class Address implements Serializable {

    private String zipcode;

    private String detail;

    @Override
    public String toString() {
        return detail + " [" + zipcode + "]";
    }

}
