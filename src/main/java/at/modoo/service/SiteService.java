package at.modoo.service;

import at.modoo.domain.Site;

public interface SiteService {

    Site getSite(String domainName);

    Site getSiteContext(String domainName);

}
