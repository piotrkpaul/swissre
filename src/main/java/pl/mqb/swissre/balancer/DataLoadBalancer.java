package pl.mqb.swissre.balancer;

import pl.mqb.swissre.provider.Provider;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public record DataLoadBalancer(List<Provider> providerList, AtomicInteger latestProvider) implements LoadBalancer {

    public DataLoadBalancer() {
        this(new LinkedList<>(), new AtomicInteger(0));
    }

    public boolean registerProvider(Provider provider) {
        return providerList.add(provider);
    }

    public boolean removeProvider(Provider provider) {
        return providerList.remove(provider);
    }

    private Provider getProvider() {
        if (latestProvider.get() >= providerList.size()) {
            latestProvider.set(0);
        }
        Provider roundRobinProvider = providerList.get(latestProvider.get());
        latestProvider.incrementAndGet();
        return roundRobinProvider;
    }

    public String get() {
        return getProvider().get();
    }
}
