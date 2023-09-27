package nl.devoorkant.sbdr.spring;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
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
        entityManagerFactoryRef = "cirEntityManagerFactory", 
        transactionManagerRef = "cirTransactionManager",
        basePackages = { "nl.devoorkant.sbdr.cir.data.repository" })
public class CirDbConfig {
	  
	  @Bean(name = "dataSourceCir")
	  @ConfigurationProperties(prefix = "cir.datasource")
	  public DataSource dataSource() {
	    return DataSourceBuilder.create().build();
	  }
	  
	  @Bean(name = "cirEntityManagerFactory")
	  public LocalContainerEntityManagerFactoryBean 
	  entityManagerFactory(
	    final EntityManagerFactoryBuilder builder,
	    final @Qualifier("dataSourceCir") DataSource dataSource
	  ) {
	    return builder
	      .dataSource(dataSource)
	      .packages("nl.devoorkant.sbdr.cir.data.model")
	      .persistenceUnit("cir")
	      .build();
	  }
	    
	  @Bean(name = "cirTransactionManager")
	  public PlatformTransactionManager transactionManager(
	    @Qualifier("cirEntityManagerFactory") EntityManagerFactory 
	    entityManagerFactory
	  ) {
	    return new JpaTransactionManager(entityManagerFactory);
	  }
	}