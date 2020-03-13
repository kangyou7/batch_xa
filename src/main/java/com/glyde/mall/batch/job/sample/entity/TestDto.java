package com.glyde.mall.batch.job.sample.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class TestDto implements Serializable {

    private static final long serialVersionUID = -7685905244214117531L;

    private String id;
    private String name;

}
