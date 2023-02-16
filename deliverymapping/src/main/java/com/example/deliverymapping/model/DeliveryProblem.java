package com.example.deliverymapping.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Delivery_Problem")
public class DeliveryProblem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String problemId;
    
    private Integer isRouteProcessed;
    
    private Integer isResultParsed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProblemId() {
        return problemId;
    }

    public void setProblemId(String problemId) {
        this.problemId = problemId;
    }

    public Integer getIsRouteProcessed() {
        return isRouteProcessed;
    }

    public void setIsRouteProcessed(Integer isRouteProcessed) {
        this.isRouteProcessed = isRouteProcessed;
    }

    public Integer getIsResultParsed() {
        return isResultParsed;
    }

    public void setIsResultParsed(Integer isResultParsed) {
        this.isResultParsed = isResultParsed;
    }

    
}