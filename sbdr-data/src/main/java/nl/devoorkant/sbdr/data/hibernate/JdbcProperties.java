package nl.devoorkant.sbdr.data.hibernate;

public class JdbcProperties {
	private String url;
	private String user;
	private String password;
	
    public static final JdbcProperties INSTANCE = 
            new JdbcProperties("jdbc:mysql://localhost:3306/sbdr", "sbdr", "sbdr");
	
    JdbcProperties(String url, String user, String password) {
    	this.url = url;
    	this.user = user;
    	this.password = password;
    }
    
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
