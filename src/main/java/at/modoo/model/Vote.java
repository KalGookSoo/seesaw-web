package at.modoo.model;

import at.modoo.model.vo.VoteType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.LinkedHashSet;
import java.util.Set;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_vote")
@Comment("투표")
@DynamicInsert
@DynamicUpdate
public class Vote extends BaseEntity {

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "votes")
    private Set<Article> articles = new LinkedHashSet<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "votes")
    private Set<Reply> replies = new LinkedHashSet<>();

    @Enumerated(EnumType.STRING)
    @Comment("타입")
    private VoteType type;

}
