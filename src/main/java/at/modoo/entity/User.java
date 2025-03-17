package at.modoo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

/**
 * 계정
 */
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_user")
@Comment("계정")
@DynamicInsert
@DynamicUpdate
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    @Comment("계정명")
    private String username;

    @JsonIgnore
    @Comment("패스워드")
    private String password;

    @Comment("이름")
    private String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "email_id")),
            @AttributeOverride(name = "domain", column = @Column(name = "email_domain"))
    })
    @Comment("이메일")
    private Email email;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "first", column = @Column(name = "first_contact_number")),
            @AttributeOverride(name = "middle", column = @Column(name = "middle_contact_number")),
            @AttributeOverride(name = "last", column = @Column(name = "last_contact_number"))
    })
    @Comment("연락처")
    private ContactNumber contactNumber;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "tb_user_authority",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private final Set<Authority> authorities = new LinkedHashSet<>();

    @CreatedDate
    @Comment("생성 일시")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Comment("수정 일시")
    private LocalDateTime lastModifiedDate;

    @Comment("만료 일시")
    private LocalDateTime expiredDate;

    @Comment("잠금 일시")
    private LocalDateTime lockedDate;

    @Comment("패스워드 만료 일시")
    private LocalDateTime credentialsExpiredDate;

    /**
     * 계정을 생성합니다.
     * @param username      계정명
     * @param password      패스워드
     * @param name          이름
     * @param email         이메일
     * @param contactNumber 연락처
     * @return 계정
     */
    public static User create(String username, String password, String name, Email email, ContactNumber contactNumber) {
        User user = new User();
        user.username = username;
        user.password = password;
        user.name = name;
        user.email = email;
        user.contactNumber = contactNumber;
        user.initializeAccountPolicy();
        return user;
    }

    public static User create(String username, Email email) {
        User user = new User();
        user.username = username;
        user.email = email;
        user.initializeAccountPolicy();
        return user;
    }

    /**
     * 계정을 수정합니다.
     * @param name          이름
     * @param email         이메일
     * @param contactNumber 연락처
     */
    public void update(String name, Email email, ContactNumber contactNumber) {
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
    }

    /**
     * 패스워드를 변경합니다.
     * @param password 패스워드
     */
    public void changePassword(String password) {
        Assert.notNull(password, "패스워드는 NULL이 될 수 없습니다.");
        this.password = password;
        this.credentialsExpiredDate = LocalDate.now().atTime(LocalTime.MIDNIGHT).plusDays(180L);
    }

    /**
     * 만료 일시는 금일(00:00)로부터 2년 후 까지로 설정합니다.
     * 패스워드 만료 일시는 생성일(00:00)로부터 180일 후 까지로 설정합니다.
     */
    private void initializeAccountPolicy() {
        expiredDate = LocalDate.now().atTime(LocalTime.MIDNIGHT).plusYears(2L);
        credentialsExpiredDate = LocalDate.now().atTime(LocalTime.MIDNIGHT).plusDays(180L);
    }

    /**
     * 계정이 만료되지 않았는지 여부를 반환합니다.
     * @return 계정이 만료되지 않았는지 여부
     */
    public boolean isAccountNonExpired() {
        return expiredDate == null || expiredDate.isAfter(LocalDateTime.now());
    }

    /**
     * 계정이 잠겨있지 않은지 여부를 반환합니다.
     * @return 계정이 잠겨있지 않은지 여부
     */
    public boolean isAccountNonLocked() {
        return lockedDate == null || lockedDate.isBefore(LocalDateTime.now());
    }

    /**
     * 계정의 패스워드가 만료되지 않았는지 여부를 반환합니다.
     * @return 계정의 패스워드가 만료되지 않았는지 여부
     */
    public boolean isCredentialsNonExpired() {
        return credentialsExpiredDate == null || credentialsExpiredDate.isAfter(LocalDateTime.now());
    }

    /**
     * 권한을 추가합니다.
     * @param authority 권한
     */
    public void addAuthority(Authority authority) {
        authorities.add(authority);
        authority.getUsers().add(this);
    }

    /**
     * 권한을 제거합니다.
     * @param authority 권한
     */
    public void removeAuthority(Authority authority) {
        authorities.remove(authority);
        authority.getUsers().remove(this);
    }

    /**
     * 권한을 모두 제거합니다.
     */
    public void removeAuthorities() {
        authorities.clear();
    }

    /**
     * 이메일 접근자
     * @return 이메일
     */
    public Email getEmail() {
        return Optional.ofNullable(email).orElseGet(Email::new);
    }

    /**
     * 연락처 접근자
     * @return 연락처
     */
    public ContactNumber getContactNumber() {
        return Optional.ofNullable(contactNumber).orElseGet(ContactNumber::new);
    }

}
