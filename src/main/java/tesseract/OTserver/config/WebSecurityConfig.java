package tesseract.OTserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.core.env.Environment;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        try {
            if (env.getProperty("spring.profiles.active").equals("prod")) {
                System.out.println("Prod security configurations activating...");
                http
                    .requiresChannel(channel ->
                            channel.anyRequest().requiresSecure())
                    .authorizeRequests(authorize ->
                            authorize.anyRequest().permitAll())
                    .csrf().disable();
            } else {
                System.out.println("Dev security configurations activating...");
                http.
                    authorizeRequests()
                    .antMatchers("/**").permitAll()
                    .anyRequest().authenticated();
            }
        } catch (NullPointerException e) {
            //throw new IllegalStateException("Environment was not set. Use --spring.profiles.active");
        }
    }
}
