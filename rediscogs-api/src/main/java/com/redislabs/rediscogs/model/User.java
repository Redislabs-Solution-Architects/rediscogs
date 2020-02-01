package com.redislabs.rediscogs.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class User implements Serializable {

	private static final long serialVersionUID = -8200810671621141323L;
	private String name;

}
