package pt.ist.socialsoftware.edition.reading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import pt.ist.socialsoftware.edition.core.config.Application;

@PropertySource({ "classpath:application.properties" })
@Import(Application.class)
@ComponentScan(basePackages = "pt.ist.socialsoftware.edition.reading")
@EnableAutoConfiguration(exclude = { SocialWebAutoConfiguration.class, DataSourceAutoConfiguration.class })
@SpringBootApplication
public class ReadingApplication extends SpringBootServletInitializer{
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ReadingApplication.class);
    }
    
    public static void main(String[] args){
        SpringApplication.run(ReadingApplication.class, args);
    }
}
