package com.scott.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class FiDAO {

	public void save(Connection conn, String sql) {
		try {
			PreparedStatement pstmt = null;			
			System.out.println("\t≤Â»Î”Ôæ‰\t" + sql);
			
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();

			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
