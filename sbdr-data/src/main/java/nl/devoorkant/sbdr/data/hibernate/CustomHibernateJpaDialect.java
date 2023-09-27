package nl.devoorkant.sbdr.data.hibernate;

import java.sql.Connection;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;

public class CustomHibernateJpaDialect extends HibernateJpaDialect {
	private static final Logger ioLogger = LoggerFactory.getLogger("CustomHibernateJpaDialect");
	private static final long serialVersionUID = 1L;

	private static ConnectionLeakUtil connectionLeakUtil = null;
	private static boolean enableConnectionLeakDetection = true;

	//@BeforeClass
	private static void initConnectionLeakUtility() {
		if (enableConnectionLeakDetection && connectionLeakUtil == null) {
			connectionLeakUtil = new ConnectionLeakUtil();
		}
	}

	//@AfterClass
	private static void assertNoLeaks() {
		if (enableConnectionLeakDetection) {
			try {
				connectionLeakUtil.assertNoLeaks();
			} catch (ConnectionLeakException e) {
				ioLogger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public Object beginTransaction(final EntityManager entityManager, final TransactionDefinition definition)
			throws PersistenceException, SQLException, TransactionException {

		// CONNECTION LEAK UTIL
		// initConnectionLeakUtility();
		
		Session session = (Session) entityManager.getDelegate();
		if (definition.getTimeout() != TransactionDefinition.TIMEOUT_DEFAULT) {
			getSession(entityManager).getTransaction().setTimeout(definition.getTimeout());
		}

		final TransactionData data = new TransactionData();

		session.doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				Integer previousIsolationLevel = DataSourceUtils.prepareConnectionForTransaction(connection,
						definition);
				data.setPreviousIsolationLevel(previousIsolationLevel);
				data.setConnection(connection);
			}
		});

		entityManager.getTransaction().begin();

		Object springTransactionData = prepareTransaction(entityManager, definition.isReadOnly(), definition.getName());

		data.setSpringTransactionData(springTransactionData);

		return data;
	}

	@Override
	public void cleanupTransaction(Object transactionData) {
		super.cleanupTransaction(((TransactionData) transactionData).getSpringTransactionData());
		((TransactionData) transactionData).resetIsolationLevel();
		
		// CONNECTION LEAK UTIL
		// assertNoLeaks();
	}

	private static class TransactionData {

		private Object springTransactionData;
		private Integer previousIsolationLevel;
		private Connection connection;

		public TransactionData() {
		}

		public void resetIsolationLevel() {
			if (this.previousIsolationLevel != null) {
				DataSourceUtils.resetConnectionAfterTransaction(connection, previousIsolationLevel);
			}
		}

		public Object getSpringTransactionData() {
			return this.springTransactionData;
		}

		public void setSpringTransactionData(Object springTransactionData) {
			this.springTransactionData = springTransactionData;
		}

		public void setPreviousIsolationLevel(Integer previousIsolationLevel) {
			this.previousIsolationLevel = previousIsolationLevel;
		}

		public void setConnection(Connection connection) {
			this.connection = connection;
		}

	}

}
