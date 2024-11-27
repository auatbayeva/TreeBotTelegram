package com.pandev.telbot;

import com.pandev.telbot.telegrambot.CategoryBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
public class TelbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelbotApplication.class, args);

	}

	@Bean
	public TelegramBotsApi telegramBotsApi(CategoryBot categoryBot) {
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(categoryBot); // Spring сам передаст экземпляр CategoryBot
			return botsApi;
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize bot", e);
		}
	}

}
