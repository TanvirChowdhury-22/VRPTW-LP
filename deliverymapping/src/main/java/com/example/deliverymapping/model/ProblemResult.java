package com.example.deliverymapping.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "problem_results")
public class ProblemResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String problemId;
    
    @Column(columnDefinition="TEXT")
    private String parsedResult;

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

    public String getParsedResult() {
        return parsedResult;
    }

    public void setParsedResult(String parsedResult) {
        this.parsedResult = parsedResult;
    }

}
