package kr.me.seesaw.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Date;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode

@Entity
@Table(name = "tb_remember_me_token")
@DynamicInsert
@DynamicUpdate
public class RememberMeToken {

    @Id
    private String series;

    @Column(nullable = false, length = 64)
    private String username;

    @Column(nullable = false, length = 64)
    private String token;

    @Column(nullable = false)
    private Date lastUsed;

    public void update(String token, Date lastUsed) {
        this.token = token;
        this.lastUsed = lastUsed;
    }

}
