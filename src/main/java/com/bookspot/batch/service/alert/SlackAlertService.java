package com.bookspot.batch.service.alert;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import com.slack.api.webhook.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SlackAlertService {
    @Value("${notification.slack.webhook.url.info}")
    private String infoWebhookUrl;

    @Value("${notification.slack.webhook.url.error}")
    private String errorWebhookUrl;

    public void info(AlertMessage alertMessage) {
        Payload message = createMessage(alertMessage);
        sendTo(infoWebhookUrl, message);
    }

    public void error(AlertMessage alertMessage) {
        Payload message = createMessage(alertMessage);
        sendTo(errorWebhookUrl, message);
    }

    private Payload createMessage(AlertMessage alertMessage) {
        Payload.PayloadBuilder messageBuilder = Payload.builder();
        setTitle(messageBuilder, alertMessage.title());
        setBody(messageBuilder, toBodyContent(alertMessage.bodies()));
        return messageBuilder.build();
    }

    private void sendTo(String webhookUrl,Payload message) {
        try {
            Slack slack = Slack.getInstance();
            slack.send(webhookUrl, message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setBody(Payload.PayloadBuilder builder, Attachment bodyContent) {
        builder.attachments(List.of(bodyContent));
    }

    private Attachment toBodyContent(List<AlertBody> bodies) {
        List<Field> fields = new LinkedList<>();
        for (AlertBody body : bodies) {
            fields.add(
                    Field.builder()
                            .title(body.header())
                            .value(body.content())
                            .build()
            );
        }

        return Attachment.builder()
                .fields(fields)
                .build();
    }

    private void setTitle(Payload.PayloadBuilder builder, String title) {
        builder.text(title);
    }
}
