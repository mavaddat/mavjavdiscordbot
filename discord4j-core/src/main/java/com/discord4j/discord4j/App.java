package com.discord4j.discord4j;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;
import org.identityconnectors.common.security.GuardedString;
import com.discord4j.discord4j.AuthToken;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        //Using guide from https://www.java2depth.com/2018/05/secure-sensitive-data-while-storing-and.html
        GuardedString authToken = AuthToken.getAuthToken(); //getAuthToken() is a static method of our custom AuthToken class that returns the auth token as a GuardedString object
        final StringBuilder clearAuthToken = new StringBuilder();
        authToken.access(new GuardedString.Accessor() {
            @Override
            public void access(final char[] clearChars) {
                clearAuthToken.append(clearChars);
            }
        });
        final DiscordClient client = DiscordClientBuilder.create(clearAuthToken.toString()).build();
        authToken.dispose();
        // Attempting to follow
        // https://github.com/Discord4J/Discord4J/wiki/Migrating-from-v3.0-to-v3.1
        GatewayDiscordClient gateway = client.login().block();
        gateway.on(ReadyEvent.class)
                .subscribe(ready -> System.out.println("Logged in as " + ready.getSelf().getUsername()));
        gateway.on(MessageCreateEvent.class,
                event -> Mono.just(event.getMessage())
                        .filter(message -> message.getContent().map("!ping"::equals).orElse(false))
                        .flatMap(Message::getChannel).flatMap(channel -> channel.createMessage("Pong!")))
                .subscribe();
        gateway.onDisconnect().block();

    }
}
