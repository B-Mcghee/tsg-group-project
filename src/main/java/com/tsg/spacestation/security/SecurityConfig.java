package com.tsg.spacestation.security;


        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
        import org.springframework.security.config.annotation.web.builders.HttpSecurity;
        import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
        import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    public void configureInMemory(AuthenticationManagerBuilder auth) throws Exception{

        auth
                .inMemoryAuthentication()
                .withUser("admin").password("{noop}password").roles("ADMIN")
                .and()
                .withUser("diplomat").password("{noop}password").roles("DIPLOMAT")
                .and()
                .withUser("user").password("{noop}password").roles("USER")
                .and()
                .withUser("all").password("{noop}password").roles("ADMIN","DIPLOMAT","USER")
                .and()
                .withUser("neither").password("{noop}password").roles("NOOB");


    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .authorizeRequests()
                    .antMatchers("/spaceblog/blog/review","/").hasRole("ADMIN")

                    .antMatchers("/spaceblog/post/new","/spaceblog/post/new/createPost").hasAnyRole("ADMIN", "DIPLOMAT")



                    .antMatchers( "/"," /img/*","/js/*","/css/*","/spaceblog/", "/spaceblog/home").permitAll()
                    .anyRequest().fullyAuthenticated()
                .and()
                    .formLogin()
                        .loginPage("/spaceblog/login").defaultSuccessUrl("/spaceblog/home", true)
                    .failureUrl("/spaceblog/login?login_error=1")
                    .permitAll()
                .and()
                    .logout()
                    .logoutSuccessUrl("/spaceblog/login")
                    .permitAll();


    }
}
