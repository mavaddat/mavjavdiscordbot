package com.discord4j.discord4j;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import reactor.core.publisher.Mono;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        final DiscordClient client = DiscordClientBuilder.create("SUPER-SECRET-AUTH-KEY").build();
        //Attempting to follow https://github.com/Discord4J/Discord4J/wiki/Migrating-from-v3.0-to-v3.1
        GatewayDiscordClient gateway = client.login().block();
        gateway.on(ReadyEvent.class).subscribe(ready -> System.out.println("Logged in as " +ready.getSelf().getUsername()));
        gateway.on(MessageCreateEvent.class, event -> Mono.just(event.getMessage()).filter(message -> message.getContent().map("!ping"::equals).orElse(false)).flatMap(Message::getChannel).flatMap(channel -> channel.createMessage("Pong!"))).subscribe();
        gateway.onDisconnect().block();

    }   
}
