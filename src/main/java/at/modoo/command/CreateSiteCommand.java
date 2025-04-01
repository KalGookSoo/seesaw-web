package at.modoo.command;

import at.modoo.model.vo.Address;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CreateSiteCommand {

    private String name;
    private String url;
    private String description;
    private String distributionCode;
    private boolean searchEngineExposed;
    private boolean imageExposed;
    private String tags;
    private Address address;
    private String contactNumber;
    private MultipartFile profileImage;

}
