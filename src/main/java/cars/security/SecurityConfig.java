package cars.security;

import cars.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.HttpSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * configuration class for spring security
 */
@Configuration
/*
we are enabling the security here
 */
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  //@Autowired
  private UserDetailsService userDetailsService;

    public SecurityConfig( @Lazy  UserDetailsService userDetailsService){
        this.userDetailsService=userDetailsService;
    }

    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        /*
        from any ip, we can ebable to reach endpoints
         */
        httpSecurity.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().authorizeRequests().antMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                .and().authorizeRequests().antMatchers("/register","/login","/files/download/**","/files/display/**",
                        "/contactmessage/visitors","/car/visitors/**",
                        "/actuator/health").permitAll()
                .anyRequest().authenticated();
        /*

         */
        httpSecurity.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
    @Bean
    public AuthTokenFilter authTokenFilter(){
        return new AuthTokenFilter();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**").allowedOrigins("*").allowedHeaders("*")
                        .allowedMethods("*");

            }
        };
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authProvider()).build();
    }
    //test line 1

    @Bean
    public DaoAuthenticationProvider authProvider(){
        //we are initializing an object here
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);


    }
    private static final String [] AUTH_WHITE_LIST={
            "/v3/api-docs/**",
            "swagger-ui.html",
            "swagger-ui//**",
            "/",
            "index.html",
            "/images/**",
            "/css/**",
            "/js/**"
    };
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring().antMatchers(AUTH_WHITE_LIST);

    }

}
