package ru.mrbrikster.chatty.commands;

import com.google.gson.JsonPrimitive;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mrbrikster.baseplugin.commands.BukkitCommand;
import ru.mrbrikster.baseplugin.config.Configuration;
import ru.mrbrikster.chatty.Chatty;
import ru.mrbrikster.chatty.chat.JsonStorage;
import ru.mrbrikster.chatty.dependencies.DependencyManager;
import ru.mrbrikster.chatty.util.TextUtil;

public class PrefixCommand extends BukkitCommand {

  private final Configuration configuration;
  private final DependencyManager dependencyManager;
  private final JsonStorage jsonStorage;

  PrefixCommand(Configuration configuration,
      DependencyManager dependencyManager,
      JsonStorage jsonStorage) {
    super("prefix", "setprefix");

    this.configuration = configuration;
    this.dependencyManager = dependencyManager;
    this.jsonStorage = jsonStorage;
  }

  @Override
  public void handle(CommandSender sender, String label, String[] args) {
    if (args.length >= 2) {
      if (!sender.hasPermission("chatty.command.prefix")) {
        sender.sendMessage(Chatty.instance().messages().get("no-permission"));
        return;
      }

      Player player = Bukkit.getPlayer(args[0]);

      if (player == null) {
        sender.sendMessage(Chatty.instance().messages().get("prefix-command.player-not-found"));
        return;
      }

      if (!player.equals(sender) && !sender.hasPermission("chatty.command.prefix.others")) {
        sender.sendMessage(Chatty.instance().messages().get("prefix-command.no-permission-others"));
        return;
      }

      if (args[1].equalsIgnoreCase("clear")) {
        jsonStorage.setProperty(player, "prefix", null);

        if (configuration.getNode("miscellaneous.commands.prefix.auto-nte").getAsBoolean(false)) {
          if (dependencyManager.getNametagEdit() != null) {
            dependencyManager.getNametagEdit().setPrefix(player, null);
          }
        }

        sender.sendMessage(Chatty.instance().messages().get("prefix-command.prefix-clear")
            .replace("{player}", player.getName()));
      } else {
        String prefix = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String formattedPrefix = prefix + configuration.getNode("miscellaneous.commands.prefix.after-prefix").getAsString("");

        int minLimit = configuration.getNode("miscellaneous.commands.prefix.length-limit.min").getAsInt(3);
        int maxLimit = configuration.getNode("miscellaneous.commands.prefix.length-limit.max").getAsInt(16);
        if (formattedPrefix.length() > maxLimit) {
          sender.sendMessage(Chatty.instance().messages().get("prefix-command.length-limit-max")
              .replace("{limit}", String.valueOf(maxLimit - formattedPrefix.length() + prefix.length())));
          return;
        }

        if (formattedPrefix.length() < minLimit) {
          sender.sendMessage(Chatty.instance().messages().get("prefix-command.length-limit-min")
              .replace("{limit}", String.valueOf(minLimit - formattedPrefix.length() + prefix.length())));
          return;
        }

        jsonStorage.setProperty(player, "prefix", new JsonPrimitive(formattedPrefix));

        if (configuration.getNode("miscellaneous.commands.prefix.auto-nte").getAsBoolean(false)) {
          if (dependencyManager.getNametagEdit() != null) {
            dependencyManager.getNametagEdit().setPrefix(player, formattedPrefix);
          }
        }

        sender.sendMessage(Chatty.instance().messages().get("prefix-command.prefix-set")
            .replace("{player}", player.getName())
            .replace("{prefix}", TextUtil.stylish(prefix)));
      }
    } else {
      sender.sendMessage(Chatty.instance().messages().get("prefix-command.usage")
          .replace("{label}", label));
    }
  }

}
