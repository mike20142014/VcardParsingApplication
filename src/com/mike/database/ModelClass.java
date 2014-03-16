package com.mike.database;

public class ModelClass {

	// private variables
	int _id;
	String _name;
	String _message;
	String _phone_number;

	// Empty constructor
	public ModelClass() {

	}

	// constructor
	public ModelClass(int id, String name, String _message, String _phone_number) {
		this._id = id;
		this._name = name;
		this._message = _message;
		this._phone_number = _phone_number;
	}

	// constructor
	public ModelClass(String name, String _phone_number, String _message) {
		this._name = name;
		this._phone_number = _phone_number;
		this._message = _message;
	}

	// constructor
	public ModelClass(String message, String _phone_number) {
		this._name = message;
		this._phone_number = _phone_number;
	}

	// constructor
	public ModelClass(int _id, String _phon_number, String _message) {

		this._id = _id;
		this._phone_number = _phon_number;
		this._message = _message;
	}

	public ModelClass(int _id){
		
		this._id = _id;
		
	}
	// getting ID
	public int getID() {
		return this._id;
	}

	// setting id
	public void setID(int id) {
		this._id = id;
	}

	// getting name
	public String getName() {
		return this._name;
	}

	// setting name
	public void setName(String name) {
		this._name = name;
	}

	// getting _message
	public String getMessage() {
		return this._message;
	}

	// Setting _message
	public void setMessage(String message) {
		this._message = message;
	}

	// getting phone number
	public String getPhoneNumber() {
		return this._phone_number;
	}

	// setting phone number
	public void setPhoneNumber(String phone_number) {
		this._phone_number = phone_number;
	}

}
