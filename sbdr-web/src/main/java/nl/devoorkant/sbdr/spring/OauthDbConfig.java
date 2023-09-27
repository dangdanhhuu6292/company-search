package nl.devoorkant.sbdr.spring;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource("classpath:sbdr.properties")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "oauthEntityManagerFactory", 
        transactionManagerRef = "oauthTransactionManager")
public class OauthDbConfig {
	  
	  @Autowired
	  private Environment env;
	  
	  @Bean(name = "dataSourceOauth")
	  @ConfigurationProperties(prefix = "oauth.datasource")
	  public DataSource dataSource() {
	    return DataSourceBuilder.create().build();
	  }
	  
//	  @Bean(name = "oauthEntityManagerFactory")
//	  public LocalContainerEntityManagerFactoryBean 
//	  entityManagerFactory(
//	    final EntityManagerFactoryBuilder builder,
//	    final @Qualifier("dataSourceOauth") DataSource dataSource
//	  ) {
//	    return builder
//	      .dataSource(dataSource)
//	      .persistenceUnit("oauth")
//	      .build();
//	  }
	  
	  @Bean(name = "oauthEntityManagerFactory")
	  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

		  HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		  vendorAdapter.setDatabase(Database.MYSQL);
		  vendorAdapter.setGenerateDdl(true);

		  LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		  em.setDataSource(dataSource());
		  em.setPackagesToScan("com.thomasvitale.jpa.demo.model");
		  em.setJpaVendorAdapter(vendorAdapter);
		  em.setJpaProperties(additionalProperties());
	
		  return em;
	  }
	  
	    
	  @Bean(name = "oauthTransactionManager")
	  public PlatformTransactionManager transactionManager(
	    @Qualifier("oauthEntityManagerFactory") EntityManagerFactory 
	    entityManagerFactory
	  ) {
	    return new JpaTransactionManager(entityManagerFactory);
	  }
	  
	 private Properties additionalProperties() {
		  Properties properties = new Properties();
		  properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("oauth.spring.jpa.hibernate.hbm2ddl.auto"));
		  properties.setProperty("hibernate.dialect", env.getProperty("oauth.spring.jpa.hibernate.dialect"));
		  properties.setProperty("hibernate.current_session_context_class", env.getProperty("oauth.spring.jpa.hibernate.current_session_context_class"));
		  properties.setProperty("hibernate.jdbc.lob.non_contextual_creation", env.getProperty("oauth.spring.jpa.hibernate.jdbc.lob.non_contextual_creation"));
		  properties.setProperty("hibernate.show_sql", env.getProperty("oauth.spring.jpa.hibernate.show_sql"));
		  properties.setProperty("hibernate.format_sql", env.getProperty("oauth.spring.jpa.hibernate.format_sql"));
		  return properties;
		  }	  
	  
	  
	}