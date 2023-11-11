package com.aptmgt.auth.enums;

public enum UserTypes {

	ADMIN,
	CUSTOMER,
	EMPLOYEE;

	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
