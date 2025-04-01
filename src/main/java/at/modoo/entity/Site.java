package at.modoo.entity;

import at.modoo.command.CreateSiteCommand;
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

    @Column(length = 36)
    @Comment("프로필 이미지 식별자")
    private String profileImageId;

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

    @Override
    public void addChild(Site child) {
        children.add(child);
        child.parentId = getId();
        child.parent = this;
    }

    public static Site create(CreateSiteCommand command) {
        Site site = new Site();
        site.name = command.getName();
        site.url = command.getUrl();
        site.description = command.getDescription();
        site.distributionCode = command.getDistributionCode();
        site.searchEngineExposed = command.isSearchEngineExposed();
        site.imageExposed = command.isImageExposed();
        site.tags = command.getTags();
        site.address = command.getAddress();
        site.profileImageId = command.getProfileImageId();
        return site;
    }

}
