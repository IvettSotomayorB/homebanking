package com.mindhub.homebanking1.configurations;

import com.mindhub.homebanking1.models.Client;
import com.mindhub.homebanking1.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebAuthentication extends GlobalAuthenticationConfigurerAdapter {
    //GlolbalAuth... Es el obj q utiliza el Spring Security para saber como buscar los detalles del user
    @Autowired
    ClientRepository clientRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        //Se busca un usuario por el nombre en el repositorio
        auth.userDetailsService(inputName-> {
            Client client = clientRepository.findByEmail(inputName);
            if (client != null) {
                return new User(client.getEmail(), client.getPassword(),
                        AuthorityUtils.createAuthorityList("CLIENT"));
            } else {
                throw new UsernameNotFoundException("Unknown user: " + inputName);
            }
        });

    }
}
