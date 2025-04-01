package at.modoo.model;

import at.modoo.command.CreateSiteCommand;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class SiteTest {

    @Test
    void init() {
        CreateSiteCommand command = new CreateSiteCommand();
        command.setName("대전포스트잇 ♣대전독서모임");
        command.setUrl("daejeonstickybook");
        command.setDescription("대전독서모임 홈페이지");
        command.setDistributionCode("95017abd-2b98-4466-9511-c399352f3742");
        command.setSearchEngineExposed(true);
        command.setImageExposed(true);
        command.setTags("#대전포스트잇 #대전독서모임 #대전독서 #독서모임 #대전모임연합");
        command.setAddress(null);
        command.setContactNumber(null);

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file",
                "dummy.png",
                "image/png",
                new byte[]{1, 2, 3, 4, 5} // Example byte array data
        );
        command.setProfileImage(mockMultipartFile);

    }

}