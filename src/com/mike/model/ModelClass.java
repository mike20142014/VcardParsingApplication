package com.mike.model;

public class ModelClass {

	private String destination_number, contact_list_phonenumber,
			contact_list_name, contact_list_address, edited_name,
			edited_number, edited_address;

	public ModelClass(String destination_number) {
		super();
		this.destination_number = destination_number;
	}

	public ModelClass(String destination_number,
			String contact_list_phonenumber, String contact_list_name,
			String contact_list_address, String edited_name,
			String edited_number, String edited_address) {
		super();
		this.destination_number = destination_number;
		this.contact_list_phonenumber = contact_list_phonenumber;
		this.contact_list_name = contact_list_name;
		this.contact_list_address = contact_list_address;
		this.edited_name = edited_name;
		this.edited_number = edited_number;
		this.edited_address = edited_address;
	}

	public String getDestination_number() {
		return destination_number;
	}

	public void setDestination_number(String destination_number) {
		this.destination_number = destination_number;
	}

	public String getContact_list_phonenumber() {
		return contact_list_phonenumber;
	}

	public void setContact_list_phonenumber(String contact_list_phonenumber) {
		this.contact_list_phonenumber = contact_list_phonenumber;
	}

	public String getContact_list_name() {
		return contact_list_name;
	}

	public void setContact_list_name(String contact_list_name) {
		this.contact_list_name = contact_list_name;
	}

	public String getContact_list_address() {
		return contact_list_address;
	}

	public void setContact_list_address(String contact_list_address) {
		this.contact_list_address = contact_list_address;
	}

	public String getEdited_name() {
		return edited_name;
	}

	public void setEdited_name(String edited_name) {
		this.edited_name = edited_name;
	}

	public String getEdited_number() {
		return edited_number;
	}

	public void setEdited_number(String edited_number) {
		this.edited_number = edited_number;
	}

	public String getEdited_address() {
		return edited_address;
	}

	public void setEdited_address(String edited_address) {
		this.edited_address = edited_address;
	}

}
