package org.configuration;


import org.domain.usecase.GameSequenceProcessor;
import org.domain.usecase.TennisScoreQuery;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WiringConfigurations {

    @Bean
    TennisScoreQuery tennisScoreQuery() {
        return new TennisScoreQuery(new GameSequenceProcessor());
    }

}
