package pl.mqb.swissre.provider;

import java.util.UUID;

public class DataProvider implements Provider {

    private final String uuid;

    public DataProvider(String uuid) {
        this.uuid = uuid;
    }

    public DataProvider() {
        this(UUID.randomUUID().toString());
    }

    @Override
    public String get() {
        return uuid;
    }
}
