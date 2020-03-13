package com.glyde.mall.batch.job.sample.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PeopleDto implements Serializable {

	private static final long serialVersionUID = -7685905244214117531L;

	private int id;
	private String firstName;
	private String lastName;
	private int _page;
	private int _pagesize;
    private int _skiprows;
}
