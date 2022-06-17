package pl.mqb.swissre.balancer;

import pl.mqb.swissre.provider.Provider;

public interface LoadBalancer {
    boolean registerProvider(Provider provider);

    void registerProviders(Provider... provider);

    boolean removeProvider(Provider provider);

    String get();

    String getFromRandomProvider();
}
