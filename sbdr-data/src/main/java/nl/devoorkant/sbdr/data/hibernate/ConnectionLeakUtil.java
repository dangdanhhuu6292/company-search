package nl.devoorkant.sbdr.data.hibernate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.hibernate.dialect.Dialect;

public class ConnectionLeakUtil {
	 
    private JdbcProperties jdbcProperties = JdbcProperties.INSTANCE;
 
    private List<IdleConnectionCounter> idleConnectionCounters = 
        Arrays.asList(
            //H2IdleConnectionCounter.INSTANCE,
            //OracleIdleConnectionCounter.INSTANCE,
            //PostgreSQLIdleConnectionCounter.INSTANCE,
            MySQLIdleConnectionCounter.INSTANCE
    );
 
    private IdleConnectionCounter connectionCounter;
 
    private int connectionLeakCount;
 
    public ConnectionLeakUtil() {
//        for ( IdleConnectionCounter connectionCounter : 
//            idleConnectionCounters ) {
//            if ( connectionCounter.appliesTo( 
//                Dialect.getDialect().getClass() ) ) {
//                this.connectionCounter = connectionCounter;
//                break;
//            }
//        }
    	
    	this.connectionCounter = idleConnectionCounters.get(0);
    	
        if ( connectionCounter != null ) {
            connectionLeakCount = countConnectionLeaks();
        }
    }
 
    public void assertNoLeaks() throws ConnectionLeakException {
        if ( connectionCounter != null ) {
            int currentConnectionLeakCount = countConnectionLeaks();
            int diff = currentConnectionLeakCount - connectionLeakCount;
            if ( diff > 0 ) {
                throw new ConnectionLeakException( 
                    String.format(
                        "%d connection(s) have been leaked! Previous leak count: %d, Current leak count: %d",
                        diff,
                        connectionLeakCount,
                        currentConnectionLeakCount
                    ) 
                );
            }
        }
    }
 
    private int countConnectionLeaks() {
    	Connection connection = null;
    	
    	// throws IllegalStateException
        connection = newConnection();
        int count = connectionCounter.count( connection );
        
    	try {
    		if (!connection.isClosed())
    			connection.close();
    	} catch ( SQLException e) {
    		throw new IllegalStateException( e );
    	}
    	
    	return count;
    }
 
    private Connection newConnection() {
        try {
        	return DriverManager.getConnection(
                jdbcProperties.getUrl(),
                jdbcProperties.getUser(),
                jdbcProperties.getPassword()
            );
        }
        catch ( SQLException e ) {
            throw new IllegalStateException( e );
        }
    }
}
