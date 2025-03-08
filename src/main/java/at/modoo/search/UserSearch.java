package at.modoo.search;

import at.modoo.core.page.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

/**
 * 계정 검색 조건
 */
@Getter
@Setter
@Schema(description = "계정 검색 조건")
public class UserSearch extends PageVO {

    private String username;

    private String name;

    private String email;

    private String contactNumber;

    @Override
    protected UriComponentsBuilder getUriComponentsBuilder() {
        return super.getUriComponentsBuilder()
                .queryParamIfPresent("username", Optional.ofNullable(username))
                .queryParamIfPresent("name", Optional.ofNullable(name))
                .queryParamIfPresent("email", Optional.ofNullable(email))
                .queryParamIfPresent("contactNumber", Optional.ofNullable(contactNumber));
    }

}