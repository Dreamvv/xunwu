package com.ly.xunwu.config;

import com.ly.xunwu.security.AuthProvider;
import com.ly.xunwu.security.LoginAuthFailHandler;
import com.ly.xunwu.security.LoginUrlEntryPoint;
import com.ly.xunwu.security.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @author liyong
 * @date 2019/9/4
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * HTTP权限控制
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {


        // 资源访问权限
        http.authorizeRequests()
                // 管理员登录入口
                .antMatchers("/admin/login").permitAll()
                // 静态资源
                .antMatchers("/static/**").permitAll()
                // 用户登录入口
                .antMatchers("/user/login").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/user/**").hasAnyRole("ADMIN", "USER")
                .antMatchers("/api/user/**").hasAnyRole("ADMIN",
                "USER")
                .and()
                .formLogin()
                // 配置角色登录处理入口
                .loginProcessingUrl("/login")
                .failureHandler(authFailHandler())
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/logout/page")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(urlEntryPoint())
                .accessDeniedPage("/403");



        http.csrf().disable();
        http.headers().frameOptions().sameOrigin();
    }

    /**
     * 自定义认证策略
     */
    @Autowired
    public void configGlobal(AuthenticationManagerBuilder auth)  throws Exception{
      auth.authenticationProvider(authProvider()).eraseCredentials(true);
        System.out.println("自定义认证策略-----------------");

       //auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN").and();
    }


   @Bean
    public AuthProvider authProvider() {
        return new AuthProvider();
    }
    @Bean
    public LoginUrlEntryPoint urlEntryPoint() {
        return new LoginUrlEntryPoint("/user/login");
    }

    @Bean
    public LoginAuthFailHandler authFailHandler() {
        return new LoginAuthFailHandler(urlEntryPoint());
    }
}
