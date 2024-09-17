package com.muravlev.servicestatus.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebhookController {

    @Autowired
    private MyTelegramBot myTelegramBot;

    @PostMapping("/webhook")
    public void onUpdateReceived(@RequestBody Update update) {
        myTelegramBot.onUpdateReceived(update);
    }
}

