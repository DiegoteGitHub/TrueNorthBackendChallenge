package com.dlalo.truenorth.springboot.backendchallenge.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Reserve implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column
	private String email;
	
	@Column
	private String fullName;
	
	@Column
	private Long arrivalDate;
	
	@Column
	private Long departureDate;
	
	// N reserves => 1 campsite
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "campsite_id")
	@JsonIgnore
	private Campsite campsite;
	
	public Reserve() {
	}
	
	public Reserve(String email, String fullName, Long arrivalDate, Long departureDate) {
		this.email = email; 
		this.fullName = fullName;
		this.arrivalDate = arrivalDate;
		this.departureDate = departureDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Long getArrivalDate() {
		return arrivalDate;
	}

	public void setArrivalDate(Long arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public Long getDepartureDate() {
		return departureDate;
	}

	public void setDepartureDate(Long departureDate) {
		this.departureDate = departureDate;
	}

	public Campsite getCampsite() {
		return campsite;
	}

	public void setCampsite(Campsite campsite) {
		this.campsite = campsite;
	}
}
