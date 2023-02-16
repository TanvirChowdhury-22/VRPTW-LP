package com.example.deliverymapping.model;


public class ProblemResultParsed {
    
    private String vehicleName;

    private String route;

    public ProblemResultParsed(String vehicleName, String route) {
        super();
        this.vehicleName = vehicleName;
        this.route = route;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}
