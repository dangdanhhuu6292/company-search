package nl.devoorkant.sbdr.spring;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource("classpath:sbdr.properties")
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "sbdrEntityManagerFactory", 
        transactionManagerRef = "sbdrTransactionManager",
        basePackages = { "nl.devoorkant.sbdr.data.repository" })
public class SbdrDbConfig {
	  
	  @Primary
	  @Bean(name = "dataSourceSbdr")
	  @ConfigurationProperties(prefix = "sbdr.datasource")
	  public DataSource dataSource() {
	    DataSource x = DataSourceBuilder.create().build();
	    return x;
	  }
	  
	  @Primary
	  @Bean(name = "sbdrEntityManagerFactory")
	  public LocalContainerEntityManagerFactoryBean 
	  entityManagerFactory(
	    EntityManagerFactoryBuilder builder,
	    @Qualifier("dataSourceSbdr") DataSource dataSource
	  ) {
		  LocalContainerEntityManagerFactoryBean x = builder
	      .dataSource(dataSource)
	      .packages("nl.devoorkant.sbdr.data.model")
	      .persistenceUnit("sbdr")
	      .build();
		  
		  return x;
	  }
	    
	  @Primary
	  @Bean(name = "sbdrTransactionManager")
	  public PlatformTransactionManager transactionManager(
	    @Qualifier("sbdrEntityManagerFactory") EntityManagerFactory 
	    entityManagerFactory
	  ) {
	    return new JpaTransactionManager(entityManagerFactory);
	  }
	}