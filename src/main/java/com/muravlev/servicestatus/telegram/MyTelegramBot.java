package com.muravlev.servicestatus.telegram;

import com.muravlev.servicestatus.ServiceStatus;
import com.muravlev.servicestatus.ServiceStatusEvent;
import com.muravlev.servicestatus.ServiceStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(MyTelegramBot.class);

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.chatId}")
    private String chatId;

    private final ServiceStatusService serviceStatusService;

    // Конструкторное внедрение зависимости
    public MyTelegramBot(ServiceStatusService serviceStatusService) {
        this.serviceStatusService = serviceStatusService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            logger.info("Received message: {} from chatId: {}", messageText, chatId);

            if (messageText.equals("/status")) {
                List<ServiceStatus> services = serviceStatusService.getAllServices();
                StringBuilder response = new StringBuilder("Current services status:\n");
                for (ServiceStatus service : services) {
                    response.append(service.getServiceName())
                            .append(" - ")
                            .append(service.isAlive() ? "Active" : "Inactive")
                            .append("\n");
                }
                sendTextMessage(chatId, response.toString());
            } else {
                sendTextMessage(chatId, "Unknown command.");
            }
        }
    }

    public void sendTextMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
            logger.info("Sent message to chatId {}: {}", chatId, text);
        } catch (TelegramApiException e) {
            logger.error("Failed to send message to chatId {}: {}", chatId, e.getMessage(), e);
        }
    }

    // Обработчик событий
    @EventListener
    public void handleServiceStatusEvent(ServiceStatusEvent event) {
        logger.info("Handling service status event: {}", event.getMessage());
        sendNotification(event.getMessage());
    }

    public void sendNotification(String message) {
        sendTextMessage(Long.parseLong(chatId), message);
    }
}
