package core;

import org.aeonbits.owner.Config;

@Config.Sources("classpath:testing.properties")
public interface TestProperties extends Config {

    @Key("staging")
    String staging();
}

