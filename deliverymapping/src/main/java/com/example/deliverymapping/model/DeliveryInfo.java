package com.example.deliverymapping.model;

public class DeliveryInfo implements Comparable<DeliveryInfo> {

    String name;
    Double weight;

    public DeliveryInfo(String name, Double weight) {
        super();
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return this.name;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int compareTo(DeliveryInfo otherDeliveryInfo) {
        return name.compareTo(otherDeliveryInfo.getName());
    }

}
