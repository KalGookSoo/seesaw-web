package at.modoo.entity;

import at.modoo.core.hierarchy.Hierarchical;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)

@Entity
@Table(name = "tb_site")
@DynamicInsert
@DynamicUpdate
@Comment("사이트")
public class Site extends BaseEntity implements Hierarchical<Site, String> {

    @Comment("이름")
    private String name;

    @Comment("URL")
    private String url;

    @Comment("설명")
    private String description;

    @Enumerated(EnumType.STRING)
    @Comment("분류")
    private SiteDistribution distribution;

    @Enumerated(EnumType.STRING)
    @Comment("타입")
    private SiteType type;

    @Comment("검색엔진 노출여부")
    private boolean searchEngineExposed;

    @Comment("이미지 노출여부")
    private boolean imageExposed;

    @Comment("태그")
    private String tags;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "zipcode", column = @Column(name = "address_zipcode")),
            @AttributeOverride(name = "detail", column = @Column(name = "address_detail"))
    })
    @Comment("주소")
    private Address address;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "first", column = @Column(name = "first_contact_number")),
            @AttributeOverride(name = "middle", column = @Column(name = "middle_contact_number")),
            @AttributeOverride(name = "last", column = @Column(name = "last_contact_number"))
    })
    @Comment("연락처")
    private ContactNumber contactNumber;

    @Comment("공개여부")
    private boolean isPublic;

    @Comment("소개")
    private String introduction;

    @Comment("부모 식별자")
    @Column(length = 36)
    private String parentId;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
    private Site parent;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private List<Site> children = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    @OneToMany(mappedBy = "site", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Category> categories = new ArrayList<>();

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "profile_image_id", referencedColumnName = "id")
    @Comment("프로필이미지")
    private Attachment profileImage;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "background_image_id", referencedColumnName = "id")
    @Comment("배경이미지")
    private Attachment backgroundImage;

    @Override
    public void addChild(Site child) {
        children.add(child);
        child.parentId = getId();
        child.parent = this;
    }

}
