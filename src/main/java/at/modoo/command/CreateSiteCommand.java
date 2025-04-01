package at.modoo.command;

import at.modoo.entity.Address;
import lombok.Data;

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
    private String profileImageId;

}
