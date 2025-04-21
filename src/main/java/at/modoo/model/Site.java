package at.modoo.model;

import at.modoo.core.hierarchy.Hierarchical;
import at.modoo.model.vo.Address;
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
@Comment("사이트")
@DynamicInsert
@DynamicUpdate
public class Site extends AbstractHierarchical<Site> implements Hierarchical<Site, String> {

    @Comment("이름")
    private String name;

    @Comment("도메인이름")
    private String domainName;

    @Comment("설명")
    private String description;

    @Comment("분류코드")
    private String distributionCode;

    @Comment("검색엔진 노출여부")
    private boolean searchEngineExposed;

    @Comment("이미지 노출여부")
    private boolean imageExposed;

    @Comment("태그")
    private String tags;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "zipcode", column = @Column(name = "zipcode")),
            @AttributeOverride(name = "value", column = @Column(name = "address"))
    })
    @Comment("주소")
    private Address address;

    @Comment("연락처")
    private String contactNumber;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private List<Category> categories = new ArrayList<>();

    /**
     * @deprecated 해당 필드는 다음과 같이 대체될 것. THUMBNAIL type in {@link at.modoo.model.Attachment.Type}.
     */
    @Deprecated
    @Comment("프로필이미지 식별자")
    @Column(length = 36)
    private String profileImageId;

    @Transient
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonManagedReference
    private Attachment profileImage;

    @Override
    public void addChild(Site child) {
        children.add(child);
        child.setParentId(getId());
        child.setParent(this);
    }

    public Address getAddress() {
        return address == null ? Address.empty() : address;
    }

}
