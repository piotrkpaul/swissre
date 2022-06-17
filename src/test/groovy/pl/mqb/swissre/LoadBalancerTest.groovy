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
}