package ru.mrbrikster.chatty.commands;

import com.google.gson.JsonPrimitive;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mrbrikster.baseplugin.commands.BukkitCommand;
import ru.mrbrikster.chatty.Chatty;
import ru.mrbrikster.chatty.chat.JsonStorage;

public class ToggleJoinsCommand extends BukkitCommand {

  private final JsonStorage jsonStorage;
  
  public ToggleJoinsCommand(JsonStorage jsonStorage) {
    super("togglejoins", "joins");
    this.jsonStorage = jsonStorage;
  }

  @Override
  public void handle(CommandSender sender, String label, String[] args) {
    if (sender instanceof Player) {
      if (!sender.hasPermission("chatty.command.togglejoins")) {
        sender.sendMessage(Chatty.instance().messages().get("no-permission"));
        return;
      }
      if (jsonStorage.getProperty((Player) sender, "joins").orElse(new JsonPrimitive(true)).getAsBoolean()) {
        jsonStorage.setProperty((Player) sender, "joins", new JsonPrimitive(false));
        sender.sendMessage(Chatty.instance().messages().get("joins-off"));
      } else {
        jsonStorage.setProperty((Player) sender, "joins", new JsonPrimitive(true));
        sender.sendMessage(Chatty.instance().messages().get("joins-on"));
      }
    } else {
      sender.sendMessage(Chatty.instance().messages().get("only-for-players"));
    }
  }
}
