package core.cred;

import org.aeonbits.owner.Config;


public interface TestInitValues extends Config {

    ////////////////      URLs      ////////////////////
    @Key("base.api.url")
    String baseApiUrl();

    @Key("base.api.token")
    String baseApiToken();

}
