package me.khanh.libs.kcore.command.bukkit;


import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class KCommand implements CommandExecutor, TabCompleter {
    @Getter
    @Nullable
    private final KCommand parent;
    private final List<KCommand> subCommands;
    @Getter
    @NotNull
    private final String name;
    @Getter
    @NotNull
    private final List<String> alias;
    @Getter
    @NotNull
    private final String permission;
    @Getter
    @NotNull
    private final String description;
    @Getter
    @NotNull
    private final String usage;

    /**
     * Regular expression pattern for matching hexadecimal color codes'.
     */
    private static final Pattern HEX_PATTERN = Pattern.compile("&(#[a-f0-9]{6})", Pattern.CASE_INSENSITIVE);

    /**
     * Constructs a new KCommand.
     *
     * @param name        the name of the command.
     * @param parent      the parent command.
     * @param alias       the aliases of the command.
     * @param permission  the permission required to execute the command.
     * @param description the description of the command.
     * @param usage       the usage of the command.
     */
    public KCommand(@NotNull String name, @Nullable KCommand parent, @Nullable List<String> alias, @Nullable String permission, @Nullable String description, @Nullable String usage) {
        this.parent = parent;
        this.subCommands = new ArrayList<>();
        this.name = name;
        this.alias = alias != null ? alias : new ArrayList<>();
        this.permission = permission != null ? permission : "";
        this.description = description != null ? description : "";
        this.usage = usage != null ? usage : "";
    }

    /**
     * Constructs a new KCommand without a parent command.
     *
     * @param name        the name of the command.
     * @param alias       the aliases of the command.
     * @param permission  the permission required to execute the command.
     * @param description the description of the command.
     * @param usage       the usage of the command.
     */
    public KCommand(@NotNull String name, @Nullable List<String> alias, @Nullable String permission, @Nullable String description, @Nullable String usage) {
        this(name, null, alias, permission, description, usage);
    }

    /**
     * Constructs a new KCommand without a parent command and aliases.
     *
     * @param name        the name of the command.
     * @param permission  the permission required to execute the command.
     * @param description the description of the command.
     * @param usage       the usage of the command.
     */
    public KCommand(@NotNull String name, @Nullable String permission, @Nullable String description, @Nullable String usage) {
        this(name, null, null, permission, description, usage);
    }

    /**
     * Constructs a new KCommand with a parent command.
     *
     * @param name        the name of the command.
     * @param parent      the parent command.
     * @param permission  the permission required to execute the command.
     * @param description the description of the command.
     * @param usage       the usage of the command.
     */
    public KCommand(@NotNull String name, @Nullable KCommand parent, @Nullable String permission, @Nullable String description, @Nullable String usage) {
        this(name, parent, null, permission, description, usage);
    }

    /**
     * Constructs a new KCommand with a parent command and permission.
     *
     * @param name        the name of the command.
     * @param parent      the parent command.
     * @param permission  the permission required to execute the command.
     */
    public KCommand(@NotNull String name, @Nullable KCommand parent, @Nullable String permission) {
        this(name, parent, null, permission, null, null);
    }

    /**
     * Constructs a new KCommand with a parent command, description, and usage.
     *
     * @param name        the name of the command.
     * @param parent      the parent command.
     * @param description the description of the command.
     * @param usage       the usage of the command.
     */
    public KCommand(@NotNull String name, @Nullable KCommand parent, @Nullable String description, @Nullable String usage) {
        this(name, parent, null, null, description, usage);
    }

    /**
     * Constructs a new KCommand with a parent command, description, and usage.
     *
     * @param name        the name of the command.
     * @param parent      the parent command.
     */
    public KCommand(@NotNull String name, @Nullable KCommand parent){
        this(name, parent, null);
    }

    /**
     * Retrieves a list containing the name and all aliases of the command.
     *
     * @return a list of the command name and aliases.
     */
    public List<String> getNameAndAlias(){
        List<String> result = new ArrayList<>(alias);
        result.add(name);
        return result;
    }

    /**
     * Adds a subcommand to this command and returns the modified command.
     *
     * @param subCommand the subcommand to add.
     * @return the modified command.
     * @throws IllegalAccessException if a duplicate subcommand or alias is found.
     */
    public final KCommand addSubCommand(KCommand subCommand) throws IllegalAccessException {
        for (KCommand command : subCommands) {
            if (command.getName().equalsIgnoreCase(subCommand.getName())) {
                throw new IllegalAccessException("Duplicate subcommand: " + command.getName());
            }

            for (String alias : subCommand.getAlias()) {
                if (command.getAlias().contains(alias)) {
                    throw new IllegalAccessException("Duplicate subcommand alias: " + alias);
                }
            }
        }

        subCommands.add(subCommand);
        return this;
    }

    /**
     * Executes the command when called by a Console.
     *
     * @param sender the CommandSender executing the command.
     * @param args   the arguments provided with the command.
     */
    public void onCommand(CommandSender sender, List<String> args){};

    /**
     * Executes the command when called by a Player.
     *
     * @param player the Player executing the command.
     * @param args   the arguments provided with the command.
     */
    public void onCommand(Player player, List<String> args){
        player.sendMessage(ChatColor.RED + "Console only command.");
    };


    /**
     * Provides tab-completion for the command when called by a Console.
     *
     * @param sender the CommandSender tab-completing the command.
     * @param args   the arguments provided with the command.
     */
    public void onTabComplete(CommandSender sender, List<String> args){};

    /**
     * Provides tab-completion for the command when called by a Player.
     *
     * @param player the Player tab-completing the command.
     * @param args   the arguments provided with the command.
     */
    public void onTabComplete(Player player, List<String> args){};


    /**
     * Retrieves the message to be displayed when a command sender does not have permission to execute the command.
     *
     * @return the message to be displayed for a lack of permission.
     */
    public abstract String getNoPermissionMessage();

    /**
     * Retrieves the message to be displayed when an unknown command is executed.
     *
     * @return the unknown command message.
     */
    public abstract String getUnknownCommandMessage();

    /**
     * Colorizes a message by replacing color codes and hexadecimal color codes with the corresponding ChatColor.
     *
     * @param message the message to be colorized.
     * @return the colorized message.
     */
    protected static String colorize(@NotNull String message){
        if (message.isEmpty()) {
            return message;
        }
        Matcher m = HEX_PATTERN.matcher(message);
        try {
            ChatColor.class.getDeclaredMethod("of", String.class);
            while (m.find())
                message = message.replace(m.group(), ChatColor.of(m.group(1)).toString());
        } catch (Exception e) {
            while (m.find())
                message = message.replace(m.group(), "");
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Replaces placeholders in the given message with their corresponding values from the replacement map.
     *
     * @param message       the message to replace placeholders in.
     * @param replaceMap    a map containing placeholders as keys and their corresponding replacement values.
     * @return the modified message with replaced placeholders.
     */
    protected static String replacePlaceholders(@NotNull String message, @NotNull Map<String, String> replaceMap) {
        if (message.isEmpty()) {
            return message;
        }

        for (String key : replaceMap.keySet()) {
            message = message.replace(key, replaceMap.get(key));
        }

        return message;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!permission.isEmpty() && !sender.hasPermission(permission)){
            sender.sendMessage(colorize(getNoPermissionMessage()));
            return true;
        }

        if (args.length == 0){

            List<String> arguments = new ArrayList<>();

            execute(sender, () -> onCommand(sender, arguments));

            if (sender instanceof Player){
                execute(sender, () -> onCommand((Player) sender, arguments));
            } else {
                execute(sender, () -> onCommand(sender, arguments));
            }

        } else {

            String nexArgs = args[1];

            KCommand subCommand = getSubCommand(nexArgs, true);

            if (subCommand == null){

                List<String> arguments = Arrays.asList(args);

                if (sender instanceof Player){
                    execute(sender, () -> onCommand((Player) sender, arguments));
                } else {
                    execute(sender, () -> onCommand(sender, arguments));
                }

            } else {

                subCommand.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));

            }
        }

        return true;
    }

    private void execute(CommandSender sender, Runnable runnable){
        try {
            runnable.run();
        } catch (Throwable throwable){

            sender.sendMessage(ChatColor.RED + "An error occurred while execute command.");
            sender.sendMessage(ChatColor.RED + String.format("%s: %s", throwable.getClass().getName(), throwable.getMessage()));

            throwable.printStackTrace();
        }
    }

    private KCommand getSubCommand(String name, boolean checkAlias){

        for (KCommand subCommand: subCommands){

            if (subCommand.getName().equalsIgnoreCase(name)){
                return subCommand;
            }

            if (checkAlias){

                for (String s: subCommand.getAlias()){
                    if (name.equalsIgnoreCase(s)){
                        return subCommand;
                    }
                }

            }
        }

        return null;
    }

    /**
     * Retrieves a sublist of strings from an array starting from the specified index.
     *
     * @param startIndex the starting index of the sublist.
     * @param arr        the array from which to extract the sublist.
     * @return the sublist of strings.
     * @throws IllegalAccessException if the array is null, the start index is negative, or the start index is greater than or equal to the array length.
     */
    private List<String> getSubStringListFromArray(int startIndex, String[] arr) throws IllegalAccessException {
        if (arr == null) {
            throw new IllegalAccessException("Array is null.");
        }

        if (startIndex < 0) {
            throw new IllegalAccessException("The start index cannot be negative.");
        }

        if (startIndex >= arr.length) {
            throw new IllegalAccessException("The start index is greater than or equal to the length of the array.");
        }

        List<String> fullArray = Arrays.asList(arr);
        return fullArray.subList(startIndex, fullArray.size());
    }

}
