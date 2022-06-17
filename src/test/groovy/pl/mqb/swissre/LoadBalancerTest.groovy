package pl.mqb.swissre

import pl.mqb.swissre.balancer.DataLoadBalancer
import pl.mqb.swissre.balancer.LoadBalancer
import pl.mqb.swissre.provider.DataProvider
import pl.mqb.swissre.provider.Provider
import spock.lang.Specification

class LoadBalancerTest extends Specification {

    LoadBalancer loadBalancer

    def setup() {
        loadBalancer = new DataLoadBalancer()
    }

    def "load balancer with single provider return the same value on multiple calls"() {
        given:
        Provider singleProvider = new DataProvider()
        loadBalancer.registerProvider(singleProvider)

        when:
        def firstCall = loadBalancer.get()

        then:
        firstCall == singleProvider.get()

        then: "second call returns the same value as first one"
        def secondCall = loadBalancer.get()
        secondCall == firstCall
    }


    def "load balancer with two provider return the different value on multiple calls"() {
        given:
        Provider firstProvider = new DataProvider()
        Provider secondProvider = new DataProvider()

        loadBalancer.registerProvider(firstProvider)
        loadBalancer.registerProvider(secondProvider)

        when:
        def firstCall = loadBalancer.get()

        then:
        firstCall == firstProvider.get()

        then: "second call returns different value then first one"
        def secondCall = loadBalancer.get()
        secondCall == secondProvider.get()
        secondCall != firstCall
    }

    def "round robin load balancer goes back to first provider"() {
        given:
        Provider firstProvider = new DataProvider()
        Provider secondProvider = new DataProvider()

        loadBalancer.registerProvider(firstProvider)
        loadBalancer.registerProvider(secondProvider)

        when:
        def firstCall = loadBalancer.get()

        then:
        firstCall == firstProvider.get()

        then: "second call returns different value then first one"
        def secondCall = loadBalancer.get()
        secondCall == secondProvider.get()
        secondCall != firstCall

        then: "third call goes to first provider - return the same value as  first one"
        def thirdCall = loadBalancer.get()
        thirdCall == firstCall
    }

    def "load balancer can return value from random provider"() {
        given:
        Provider firstProvider = new DataProvider()
        Provider secondProvider = new DataProvider()

        loadBalancer.registerProvider(firstProvider)
        loadBalancer.registerProvider(secondProvider)

        when:
        def firstCall = loadBalancer.getFromRandomProvider()

        then:
        !firstCall.isBlank()
        firstCall == firstProvider.get() || firstCall == secondProvider.get()

        then:
        def secondCall = loadBalancer.get()
        !secondCall.isBlank()
        secondCall == firstProvider.get() || secondCall == secondProvider.get()
    }


    def "it's possible to register 10 provider"() {
        given:
        loadBalancer.registerProviders(
                new DataProvider(), //1
                new DataProvider(), //2
                new DataProvider(), //3
                new DataProvider(), //4
                new DataProvider(), //5
                new DataProvider(), //6
                new DataProvider(), //7
                new DataProvider(), //8
                new DataProvider(), //9
                new DataProvider()  //10
        )

        when:
        def firstCall = loadBalancer.get()

        then:
        firstCall != null
    }

    def "it's not possible to register over 10 provider - exception is thrown"() {
        when:
        loadBalancer.registerProviders(
                new DataProvider(), //1
                new DataProvider(), //2
                new DataProvider(), //3
                new DataProvider(), //4
                new DataProvider(), //5
                new DataProvider(), //6
                new DataProvider(), //7
                new DataProvider(), //8
                new DataProvider(), //9
                new DataProvider(), //10
                new DataProvider()  //11
        )

        then:
        def e = thrown(IllegalStateException)
        e.message == "Maximum number of 10 registered Providers has been reached"
    }

    def "it's possible to include and exclude"() {
        given:
        Provider firstProvider = new DataProvider()
        Provider secondProvider = new DataProvider()

        loadBalancer.registerProvider(firstProvider)
        loadBalancer.registerProvider(secondProvider)

        when:
        def firstCall = loadBalancer.get()

        then:
        firstCall == firstProvider.get()

        then:
        def secondCall = loadBalancer.get()
        secondCall == secondProvider.get()

        when:
        loadBalancer.removeProvider(firstProvider)

        then: " after removal of first provider, all returned values will come from second one"
        def firstCallAfterProviderRemoval = loadBalancer.get()
        firstCallAfterProviderRemoval == secondProvider.get()

        def secondCallAfterProviderRemoval = loadBalancer.get()
        secondCallAfterProviderRemoval == secondProvider.get()
    }

}