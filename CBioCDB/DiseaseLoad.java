import java.sql.*;

public class DiseaseLoad {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//		 Register MySQL driver
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();

		   // Connection info for MySQL
		   String cs =
		      "jdbc:mysql://149.169.177.153/mysql?user=bllogsdon&password=Azstate1";

		   // The SQL statement to execute
		   String sql = "SELECT host,user FROM user";

		   // Open a connection to the database
		   Connection conn = DriverManager.getConnection(cs);
		   Statement stat = conn.createStatement();

		   // Execute the query
		   ResultSet rs = stat.executeQuery(sql);

		   // Display results
		   System.out.println("HOST\tUSER");
		   System.out.println("--\t----");
		   while (rs.next())
		   {
		      System.out.println(rs.getString("host")+"\t"+rs.getString("user"));
		   }

		   // Close everyting
		   rs.close();
		   stat.close();
		   conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}

	}

}
