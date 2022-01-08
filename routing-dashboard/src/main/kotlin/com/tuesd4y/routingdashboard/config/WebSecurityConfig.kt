package com.tuesd4y.routingdashboard.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.cors()
            .and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers( "/servers/processingFinished", "/", "/login").permitAll()
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin(withDefaults())
    }

    @Bean
    override fun userDetailsService(): UserDetailsService? {
        val user: UserDetails = User.withDefaultPasswordEncoder()
            .username("user")
            .password("password")
            .roles("USER")
            .build()
        return InMemoryUserDetailsManager(user)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = UrlBasedCorsConfigurationSource()
        val conf = CorsConfiguration()
        conf.applyPermitDefaultValues()
        conf.addAllowedMethod(HttpMethod.PUT)
        conf.addAllowedMethod(HttpMethod.DELETE)
        conf.addAllowedMethod(HttpMethod.PATCH)
        conf.addAllowedOrigin("*")
        conf.addAllowedHeader("Location")
        conf.addExposedHeader("Location")
        config.registerCorsConfiguration("/**", conf)
        return config
    }
}