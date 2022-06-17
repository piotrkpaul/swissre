package pl.mqb.swissre.balancer;

import pl.mqb.swissre.provider.Provider;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public record DataLoadBalancer(List<Provider> providerList, AtomicInteger latestProvider) implements LoadBalancer {

    private static final int SIZE_LIMIT = 10;

    public DataLoadBalancer() {
        this(new LinkedList<>(), new AtomicInteger(0));
    }

    public synchronized boolean registerProvider(Provider provider) {
        if (providerList.size() >= SIZE_LIMIT) {
            throw new IllegalStateException("Maximum number of " + SIZE_LIMIT + " registered Providers has been reached");
        }
        return providerList.add(provider);
    }

    @Override
    public void registerProviders(Provider... provider) {
        Arrays.stream(provider).forEach(this::registerProvider);
    }

    public boolean removeProvider(Provider provider) {
        return providerList.remove(provider);
    }

    private synchronized Provider getProvider() {
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
