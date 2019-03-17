package com.gmail.gremorydev14.gremoryskywars.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Data {

	v1_8("g", "c", "d", "a", "h", "i", "b"), v1_9("h", "c", "d", "a", "i", "j", "b"), v1_10("h", "c", "d", "a", "i", "j", "b"), v1_11("h", "c", "d", "a", "i", "j", "b"), v1_12("h", "c", "d", "a", "i", "j", "b");

	private String members;
	private String prefix;
	private String suffix;
	private String teamName;
	private String paramInt;
	private String packOption;
	private String displayName;
}