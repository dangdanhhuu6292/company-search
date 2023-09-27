package nl.devoorkant.sbdr.data.hibernate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.hibernate.dialect.Dialect;

import org.hibernate.dialect.MySQL5Dialect;

public class MySQLIdleConnectionCounter implements IdleConnectionCounter {
	 
    public static final IdleConnectionCounter INSTANCE = 
        new MySQLIdleConnectionCounter();
 
    @Override
    public boolean appliesTo(Class<? extends Dialect> dialect) {
        return MySQL5Dialect.class.isAssignableFrom( dialect );
    }
 
    @Override
    public int count(Connection connection) {
    	Statement statement = null;
        try {
        	statement = connection.createStatement();
        	
        	ResultSet resultSet = statement.executeQuery( "SHOW PROCESSLIST" );
        	          
            int count = 0;
            while ( resultSet.next() ) {
                String state = resultSet.getString( "command" );
                if ( "sleep".equalsIgnoreCase( state ) ) {
                    count++;
                }
            }
            
            resultSet.close();
            
            return count;
        }
        catch ( SQLException e ) {
            throw new IllegalStateException( e );
        } finally {
        	try {
        		if (!statement.isClosed())
        			statement.close();
        	} catch ( SQLException e ) {
        		throw new IllegalStateException( e );
        	}
        }
    }
}
