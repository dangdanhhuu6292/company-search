package nl.devoorkant.sbdr.data.hibernate;

import java.sql.Connection;

import org.hibernate.dialect.Dialect;

public interface IdleConnectionCounter {
	 
    /**
     * Specifies which Dialect the counter applies to.
     *
     * @param dialect dialect
     *
     * @return applicability.
     */
    boolean appliesTo(Class<? extends Dialect> dialect);
 
    /**
     * Count the number of idle connections.
     *
     * @param connection current JDBC connection to be used for querying the number of idle connections.
     *
     * @return idle connection count.
     */
    int count(Connection connection);
}
