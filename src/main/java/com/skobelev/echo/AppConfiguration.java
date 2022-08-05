package com.skobelev.echo;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.sql.DataSource;

@Configuration
public class AppConfiguration {

    @Bean
    public PomodoroBot pomodoroBot(TimerDao timerDao) {
        return new PomodoroBot(timerDao);
    }

    @Bean
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/postgres");
        hikariConfig.setUsername("postgres");
        hikariConfig.setPassword("test");
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public TimerDao timerDao(DataSource dataSource) {
        return new TimerDao(dataSource);
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(PomodoroBot pomodoroBot) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(pomodoroBot);
        new Thread(() -> {
            try {
                pomodoroBot.checkTimer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).run();
        return telegramBotsApi;
    }
}
