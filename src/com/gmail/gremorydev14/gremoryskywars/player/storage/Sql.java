package com.gmail.gremorydev14.gremoryskywars.player.storage;

import java.sql.Connection;

public interface Sql {

	public Connection getConnection();

	public void execute(String query, boolean flag, Object... objects);
}
