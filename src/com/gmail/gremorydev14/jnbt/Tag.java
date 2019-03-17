package com.gmail.gremorydev14.jnbt;

public abstract class Tag {

	private final String name;

	public Tag(String name) {
		this.name = name;
	}

	public final String getName() {
		return name;
	}

	public abstract Object getValue();
}
