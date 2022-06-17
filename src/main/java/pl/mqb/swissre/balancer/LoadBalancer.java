package pl.mqb.swissre.balancer;

import pl.mqb.swissre.provider.Provider;

public interface LoadBalancer {
    boolean registerProvider(Provider provider);

    boolean removeProvider(Provider provider);

    String get();
}
