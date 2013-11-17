package net.citizensnpcs.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.citizensnpcs.Citizens;
import net.citizensnpcs.Settings.Setting;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.speech.SpeechContext;
import net.citizensnpcs.api.command.Command;
import net.citizensnpcs.api.command.CommandContext;
import net.citizensnpcs.api.command.CommandMessages;
import net.citizensnpcs.api.command.Requirements;
import net.citizensnpcs.api.command.exception.CommandException;
import net.citizensnpcs.api.command.exception.NoPermissionsException;
import net.citizensnpcs.api.command.exception.ServerCommandException;
import net.citizensnpcs.api.event.CommandSenderCreateNPCEvent;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.PlayerCreateNPCEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.trait.Spawned;
import net.citizensnpcs.api.trait.trait.Speech;
import net.citizensnpcs.api.util.Colorizer;
import net.citizensnpcs.api.util.Messaging;
import net.citizensnpcs.api.util.Paginator;
import net.citizensnpcs.npc.NPCSelector;
import net.citizensnpcs.npc.Template;
import net.citizensnpcs.trait.Anchors;
import net.citizensnpcs.trait.CurrentLocation;
import net.citizensnpcs.trait.Gravity;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.Poses;
import net.citizensnpcs.util.Anchor;
import net.citizensnpcs.util.Messages;
import net.citizensnpcs.util.NMS;
import net.citizensnpcs.util.StringHelper;
import net.citizensnpcs.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@Requirements(selected = true)
public class NPCCommands {
    private final NPCRegistry npcRegistry;
    private final NPCSelector selector;

    public NPCCommands(Citizens plugin) {
        npcRegistry = CitizensAPI.getNPCRegistry();
        selector = plugin.getNPCSelector();
    }

    @Command(
            aliases = { "npc" },
            usage = "anchor (--save [name]|--assume [name]|--remove [name]) (-a)(-c)",
            desc = "Changes/Saves/Lists NPC's location anchor(s)",
            flags = "ac",
            modifiers = { "anchor" },
            min = 1,
            max = 3,
            permission = "citizens.npc.anchor")
    public void anchor(CommandContext args, CommandSender sender, NPC npc) throws CommandException {
        Anchors trait = npc.getTrait(Anchors.class);
        if (args.hasValueFlag("save")) {
            if (args.getFlag("save").isEmpty())
                throw new CommandException(Messages.INVALID_ANCHOR_NAME);

            if (args.getSenderLocation() == null)
                throw new ServerCommandException();

            if (args.hasFlag('c')) {
                if (trait.addAnchor(args.getFlag("save"), args.getSenderTargetBlockLocation())) {
                    Messaging.sendTr(sender, Messages.ANCHOR_ADDED);
                } else
                    throw new CommandException(Messages.ANCHOR_ALREADY_EXISTS, args.getFlag("save"));
            } else {
                if (trait.addAnchor(args.getFlag("save"), args.getSenderLocation())) {
                    Messaging.sendTr(sender, Messages.ANCHOR_ADDED);
                } else
                    throw new CommandException(Messages.ANCHOR_ALREADY_EXISTS, args.getFlag("save"));
            }
        } else if (args.hasValueFlag("assume")) {
            if (args.getFlag("assume").isEmpty())
                throw new CommandException(Messages.INVALID_ANCHOR_NAME);

            Anchor anchor = trait.getAnchor(args.getFlag("assume"));
            if (anchor == null)
                throw new CommandException(Messages.ANCHOR_MISSING, args.getFlag("assume"));
            npc.teleport(anchor.getLocation(), TeleportCause.COMMAND);
        } else if (args.hasValueFlag("remove")) {
            if (args.getFlag("remove").isEmpty())
                throw new CommandException(Messages.INVALID_ANCHOR_NAME);
            if (trait.removeAnchor(trait.getAnchor(args.getFlag("remove"))))
                Messaging.sendTr(sender, Messages.ANCHOR_REMOVED);
            else
                throw new CommandException(Messages.ANCHOR_MISSING, args.getFlag("remove"));
        } else if (!args.hasFlag('a')) {
            Paginator paginator = new Paginator().header("Anchors");
            paginator.addLine("<e>Key: <a>ID  <b>Name  <c>World  <d>Location (X,Y,Z)");
            for (int i = 0; i < trait.getAnchors().size(); i++) {
                if (trait.getAnchors().get(i).isLoaded()) {
                    String line = "<a>" + i + "<b>  " + trait.getAnchors().get(i).getName() + "<c>  "
                            + trait.getAnchors().get(i).getLocation().getWorld().getName() + "<d>  "
                            + trait.getAnchors().get(i).getLocation().getBlockX() + ", "
                            + trait.getAnchors().get(i).getLocation().getBlockY() + ", "
                            + trait.getAnchors().get(i).getLocation().getBlockZ();
                    paginator.addLine(line);
                } else {
                    String[] parts = trait.getAnchors().get(i).getUnloadedValue();
                    String line = "<a>" + i + "<b>  " + trait.getAnchors().get(i).getName() + "<c>  "
                            + parts[0] + "<d>  " + parts[1] + ", " + parts[2] + ", " + parts[3] + " <f>(unloaded)";
                    paginator.addLine(line);
                }
            }

            int page = args.getInteger(1, 1);
            if (!paginator.sendPage(sender, page))
                throw new CommandException(Messages.COMMAND_PAGE_MISSING);
        }

        // Assume Player's position
        if (!args.hasFlag('a'))
            return;
        if (sender instanceof ConsoleCommandSender)
            throw new ServerCommandException();
        npc.teleport(args.getSenderLocation(), TeleportCause.COMMAND);
    }

    @Command(
            aliases = { "npc" },
            usage = "copy (--name newname)",
            desc = "Copies an NPC",
            modifiers = { "copy" },
            min = 1,
            max = 1,
            permission = "citizens.npc.copy")
    public void copy(CommandContext args, CommandSender sender, NPC npc) throws CommandException {
        String name = args.getFlag("name", npc.getFullName());
        NPC copy = npc.clone();
        if (!copy.getFullName().equals(name)) {
            copy.setName(name);
        }

        if (copy.isSpawned() && args.getSenderLocation() != null) {
            Location location = args.getSenderLocation();
            location.getChunk().load();
            copy.teleport(location, TeleportCause.COMMAND);
            copy.getTrait(CurrentLocation.class).setLocation(location);
        }

        CommandSenderCreateNPCEvent event = sender instanceof Player ? new PlayerCreateNPCEvent((Player) sender, copy)
                : new CommandSenderCreateNPCEvent(sender, copy);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            event.getNPC().destroy();
            String reason = "Couldn't create NPC.";
            if (!event.getCancelReason().isEmpty())
                reason += " Reason: " + event.getCancelReason();
            throw new CommandException(reason);
        }

        Messaging.sendTr(sender, Messages.NPC_COPIED, npc.getName());
        selector.select(sender, copy);
    }

    @Command(
            aliases = { "npc" },
            usage = "create [name] ((-b,u) --at (x:y:z:world) --b (behaviours))",
            desc = "Create a new NPC",
            flags = "bu",
            modifiers = { "create" },
            min = 2,
            permission = "citizens.npc.create")
    @Requirements
    public void create(CommandContext args, CommandSender sender, NPC npc) throws CommandException {
        String name = Colorizer.parseColors(args.getJoinedStrings(1));
        if (name.length() > 16) {
            Messaging.sendErrorTr(sender, Messages.NPC_NAME_TOO_LONG);
            name = name.substring(0, 16);
        }
        if (name.length() <= 0)
            throw new CommandException();

        if (!sender.hasPermission("citizens.npc.create"))
            throw new NoPermissionsException();

        npc = npcRegistry.createNPC(name);
        String msg = "You created [[" + npc.getName() + "]]";

        Location spawnLoc = null;
        if (sender instanceof Player) {
            spawnLoc = args.getSenderLocation();
        } else if (sender instanceof BlockCommandSender) {
            spawnLoc = args.getSenderLocation();
        }
        CommandSenderCreateNPCEvent event = sender instanceof Player ? new PlayerCreateNPCEvent((Player) sender, npc)
                : new CommandSenderCreateNPCEvent(sender, npc);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            npc.destroy();
            String reason = "Couldn't create NPC.";
            if (!event.getCancelReason().isEmpty())
                reason += " Reason: " + event.getCancelReason();
            throw new CommandException(reason);
        }

        if (args.hasValueFlag("at")) {
            spawnLoc = CommandContext.parseLocation(args.getSenderLocation(), args.getFlag("at"));
        }

        if (spawnLoc == null) {
            npc.destroy();
            throw new CommandException(Messages.INVALID_SPAWN_LOCATION);
        }

        if (!args.hasFlag('u')) {
            npc.spawn(spawnLoc);
        }

        if (args.hasValueFlag("trait")) {
            Iterable<String> parts = Splitter.on(',').trimResults().split(args.getFlag("trait"));
            StringBuilder builder = new StringBuilder();
            for (String tr : parts) {
                Trait trait = CitizensAPI.getTraitFactory().getTrait(tr);
                if (trait == null)
                    continue;
                npc.addTrait(trait);
                builder.append(StringHelper.wrap(tr) + ", ");
            }
            if (builder.length() > 0)
                builder.delete(builder.length() - 2, builder.length());
            msg += " with traits " + builder.toString();
        }

        if (args.hasValueFlag("template")) {
            Iterable<String> parts = Splitter.on(',').trimResults().split(args.getFlag("template"));
            StringBuilder builder = new StringBuilder();
            for (String part : parts) {
                Template template = Template.byName(part);
                if (template == null)
                    continue;
                template.apply(npc);
                builder.append(StringHelper.wrap(part) + ", ");
            }
            if (builder.length() > 0)
                builder.delete(builder.length() - 2, builder.length());
            msg += " with templates " + builder.toString();
        }

        selector.select(sender, npc);
        Messaging.send(sender, msg + '.');
    }

    @Command(
            aliases = { "npc" },
            usage = "despawn (id)",
            desc = "Despawn a NPC",
            modifiers = { "despawn" },
            min = 1,
            max = 2,
            permission = "citizens.npc.despawn")
    @Requirements
    public void despawn(CommandContext args, CommandSender sender, NPC npc) throws CommandException {
        if (npc == null || args.argsLength() == 2) {
            if (args.argsLength() < 2)
                throw new CommandException(Messages.COMMAND_MUST_HAVE_SELECTED);
            int id = args.getInteger(1);
            npc = CitizensAPI.getNPCRegistry().getById(id);
            if (npc == null)
                throw new CommandException(Messages.NO_NPC_WITH_ID_FOUND, id);
        }
        npc.getTrait(Spawned.class).setSpawned(false);
        npc.despawn(DespawnReason.REMOVAL);
        Messaging.sendTr(sender, Messages.NPC_DESPAWNED, npc.getName());
    }

    @Command(
            aliases = { "npc" },
            usage = "gamemode [gamemode]",
            desc = "Changes the gamemode",
            modifiers = { "gamemode" },
            min = 1,
            max = 2,
            permission = "citizens.npc.gamemode")
    @Requirements(selected = true)
    public void gamemode(CommandContext args, CommandSender sender, NPC npc) {
        Player player = (Player) npc.getBukkitEntity();
        if (args.argsLength() == 1) {
            Messaging.sendTr(sender, Messages.GAMEMODE_DESCRIBE, npc.getName(), player.getGameMode().name()
                    .toLowerCase());
            return;
        }
        GameMode mode = null;
        try {
            int value = args.getInteger(1);
            mode = GameMode.getByValue(value);
        } catch (NumberFormatException ex) {
            try {
                mode = GameMode.valueOf(args.getString(1));
            } catch (IllegalArgumentException e) {
            }
        }
        if (mode == null) {
            Messaging.sendErrorTr(sender, Messages.GAMEMODE_INVALID, args.getString(1));
            return;
        }
        player.setGameMode(mode);
        Messaging.sendTr(sender, Messages.GAMEMODE_SET, mode.name().toLowerCase());
    }

    @Command(
            aliases = { "npc" },
            usage = "gravity",
            desc = "Toggles gravity",
            modifiers = { "gravity" },
            min = 1,
            max = 1,
            permission = "citizens.npc.gravity")
    public void gravity(CommandContext args, CommandSender sender, NPC npc) {
        boolean enabled = npc.getTrait(Gravity.class).toggle();
        String key = enabled ? Messages.GRAVITY_ENABLED : Messages.GRAVITY_DISABLED;
        Messaging.sendTr(sender, key, npc.getName());
    }

    @Command(
            aliases = { "npc" },
            usage = "id",
            desc = "Sends the selected NPC's ID to the sender",
            modifiers = { "id" },
            min = 1,
            max = 1,
            permission = "citizens.npc.id")
    public void id(CommandContext args, CommandSender sender, NPC npc) {
        Messaging.send(sender, npc.getId());
    }

    @Command(
            aliases = { "npc" },
            usage = "list (page) ((-a) --char (char))",
            desc = "List NPCs",
            flags = "a",
            modifiers = { "list" },
            min = 1,
            max = 2,
            permission = "citizens.npc.list")
    @Requirements
    public void list(CommandContext args, CommandSender sender, NPC npc) throws CommandException {
        List<NPC> npcs = new ArrayList<NPC>();

        if (args.hasFlag('a')) {
            for (NPC add : npcRegistry)
                npcs.add(add);
        }

        Paginator paginator = new Paginator().header("NPCs");
        paginator.addLine("<e>Key: <a>ID  <b>Name");
        for (int i = 0; i < npcs.size(); i += 2) {
            String line = "<a>" + npcs.get(i).getId() + "<b>  " + npcs.get(i).getName();
            if (npcs.size() >= i + 2)
                line += "      " + "<a>" + npcs.get(i + 1).getId() + "<b>  " + npcs.get(i + 1).getName();
            paginator.addLine(line);
        }

        int page = args.getInteger(1, 1);
        if (!paginator.sendPage(sender, page))
            throw new CommandException(Messages.COMMAND_PAGE_MISSING);
    }

    @Command(
            aliases = { "npc" },
            usage = "lookclose",
            desc = "Toggle whether a NPC will look when a player is near",
            modifiers = { "lookclose", "look", "rotate" },
            min = 1,
            max = 1,
            permission = "citizens.npc.lookclose")
    public void lookClose(CommandContext args, CommandSender sender, NPC npc) {
        Messaging.sendTr(sender, npc.getTrait(LookClose.class).toggle() ? Messages.LOOKCLOSE_SET
                : Messages.LOOKCLOSE_STOPPED, npc.getName());
    }

    @Command(
            aliases = { "npc" },
            usage = "moveto x:y:z:world | x y z world",
            desc = "Teleports a NPC to a given location",
            modifiers = "moveto",
            min = 1,
            permission = "citizens.npc.moveto")
    public void moveto(CommandContext args, CommandSender sender, NPC npc) throws CommandException {
        // Spawn the NPC if it isn't spawned to prevent NPEs
        if (!npc.isSpawned())
            npc.spawn(npc.getTrait(CurrentLocation.class).getLocation());
        if (npc.getBukkitEntity() == null) {
            throw new CommandException("NPC could not be spawned.");
        }
        Location current = npc.getBukkitEntity().getLocation();
        Location to;
        if (args.argsLength() > 1) {
            String[] parts = Iterables.toArray(Splitter.on(':').split(args.getJoinedStrings(1, ':')), String.class);
            if (parts.length != 4 && parts.length != 3)
                throw new CommandException(Messages.MOVETO_FORMAT);
            double x = Double.parseDouble(parts[0]);
            double y = Double.parseDouble(parts[1]);
            double z = Double.parseDouble(parts[2]);
            World world = parts.length == 4 ? Bukkit.getWorld(parts[3]) : current.getWorld();
            if (world == null)
                throw new CommandException(Messages.WORLD_NOT_FOUND);
            to = new Location(world, x, y, z, current.getYaw(), current.getPitch());
        } else {
            to = current.clone();
            if (args.hasValueFlag("x"))
                to.setX(args.getFlagDouble("x"));
            if (args.hasValueFlag("y"))
                to.setY(args.getFlagDouble("y"));
            if (args.hasValueFlag("z"))
                to.setZ(args.getFlagDouble("z"));
            if (args.hasValueFlag("yaw"))
                to.setYaw((float) args.getFlagDouble("yaw"));
            if (args.hasValueFlag("pitch"))
                to.setPitch((float) args.getFlagDouble("pitch"));
            if (args.hasValueFlag("world")) {
                World world = Bukkit.getWorld(args.getFlag("world"));
                if (world == null)
                    throw new CommandException(Messages.WORLD_NOT_FOUND);
                to.setWorld(world);
            }
        }

        npc.teleport(to, TeleportCause.COMMAND);
        Messaging.sendTr(sender, Messages.MOVETO_TELEPORTED, npc.getName(), to);
    }

    @Command(
            aliases = { "npc" },
            modifiers = { "name" },
            usage = "name",
            desc = "Toggle nameplate visibility",
            min = 1,
            max = 1,
            permission = "citizens.npc.name")
    @Requirements(selected = true)
    public void name(CommandContext args, CommandSender sender, NPC npc) {
        npc.getBukkitEntity().setCustomNameVisible(!npc.getBukkitEntity().isCustomNameVisible());
        Messaging.sendTr(sender, Messages.NAMEPLATE_VISIBILITY_TOGGLED);
    }

    @Command(aliases = { "npc" }, desc = "Show basic NPC information", max = 0, permission = "citizens.npc.info")
    public void npc(CommandContext args, CommandSender sender, NPC npc) {
        Messaging.send(sender, StringHelper.wrapHeader(npc.getName()));
        Messaging.send(sender, "    <a>ID: <e>" + npc.getId());
        if (npc.isSpawned()) {
            Location loc = npc.getBukkitEntity().getLocation();
            String format = "    <a>Spawned at <e>%d, %d, %d <a>in world<e> %s";
            Messaging.send(sender,
                    String.format(format, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName()));
        }
        Messaging.send(sender, "    <a>Traits<e>");
        for (Trait trait : npc.getTraits()) {
            if (CitizensAPI.getTraitFactory().isInternalTrait(trait))
                continue;
            String message = "     <e>- <a>" + trait.getName();
            Messaging.send(sender, message);
        }
    }

    @Command(aliases = { "npc" }, usage = "pathrange [range]", desc = "Sets an NPC's pathfinding range", modifiers = {
            "pathrange", "pathfindingrange", "prange" }, min = 2, max = 2, permission = "citizens.npc.pathfindingrange")
    public void pathfindingRange(CommandContext args, CommandSender sender, NPC npc) {
        double range = Math.max(1, args.getDouble(1));
        npc.getNavigator().getDefaultParameters().range((float) range);
        Messaging.sendTr(sender, Messages.PATHFINDING_RANGE_SET, range);
    }

    @Command(
            aliases = { "npc" },
            usage = "playerlist (-a,r)",
            desc = "Sets whether the NPC is put in the playerlist",
            modifiers = { "playerlist" },
            min = 1,
            max = 1,
            flags = "ar",
            permission = "citizens.npc.playerlist")
    @Requirements(selected = true)
    public void playerlist(CommandContext args, CommandSender sender, NPC npc) {
        boolean remove = !npc.data().get("removefromplayerlist", Setting.REMOVE_PLAYERS_FROM_PLAYER_LIST.asBoolean());
        if (args.hasFlag('a')) {
            remove = false;
        } else if (args.hasFlag('r'))
            remove = true;
        npc.data().setPersistent("removefromplayerlist", remove);
        if (npc.isSpawned())
            NMS.addOrRemoveFromPlayerList(npc.getBukkitEntity(), remove);
        Messaging.sendTr(sender, remove ? Messages.REMOVED_FROM_PLAYERLIST : Messages.ADDED_TO_PLAYERLIST,
                npc.getName());
    }

    @Command(
            aliases = { "npc" },
            usage = "pose (--save [name]|--assume [name]|--remove [name]) (-a)",
            desc = "Changes/Saves/Lists NPC's head pose(s)",
            flags = "a",
            modifiers = { "pose" },
            min = 1,
            max = 2,
            permission = "citizens.npc.pose")
    public void pose(CommandContext args, CommandSender sender, NPC npc) throws CommandException {
        Poses trait = npc.getTrait(Poses.class);
        if (args.hasValueFlag("save")) {
            if (args.getFlag("save").isEmpty())
                throw new CommandException(Messages.INVALID_POSE_NAME);

            if (args.getSenderLocation() == null)
                throw new ServerCommandException();

            if (trait.addPose(args.getFlag("save"), args.getSenderLocation())) {
                Messaging.sendTr(sender, Messages.POSE_ADDED);
            } else
                throw new CommandException(Messages.POSE_ALREADY_EXISTS, args.getFlag("save"));
        } else if (args.hasValueFlag("assume")) {
            String pose = args.getFlag("assume");
            if (pose.isEmpty())
                throw new CommandException(Messages.INVALID_POSE_NAME);

            if (!trait.hasPose(pose))
                throw new CommandException(Messages.POSE_MISSING, pose);
            trait.assumePose(pose);
        } else if (args.hasValueFlag("remove")) {
            if (args.getFlag("remove").isEmpty())
                throw new CommandException(Messages.INVALID_POSE_NAME);
            if (trait.removePose(args.getFlag("remove"))) {
                Messaging.sendTr(sender, Messages.POSE_REMOVED);
            } else
                throw new CommandException(Messages.POSE_MISSING, args.getFlag("remove"));
        } else if (!args.hasFlag('a')) {
            trait.describe(sender, args.getInteger(1, 1));
        }

        // Assume Player's pose
        if (!args.hasFlag('a'))
            return;
        if (args.getSenderLocation() == null)
            throw new ServerCommandException();
        Location location = args.getSenderLocation();
        trait.assumePose(location);
    }

    @Command(
            aliases = { "npc" },
            usage = "remove|rem (all)",
            desc = "Remove a NPC",
            modifiers = { "remove", "rem" },
            min = 1,
            max = 2)
    @Requirements
    public void remove(CommandContext args, CommandSender sender, NPC npc) throws CommandException {
        if (args.argsLength() == 2) {
            if (!args.getString(1).equalsIgnoreCase("all"))
                throw new CommandException(Messages.REMOVE_INCORRECT_SYNTAX);
            if (!sender.hasPermission("citizens.admin.remove.all") && !sender.hasPermission("citizens.admin"))
                throw new NoPermissionsException();
            npcRegistry.deregisterAll();
            Messaging.sendTr(sender, Messages.REMOVED_ALL_NPCS);
            return;
        }
        if (npc == null)
            throw new CommandException(Messages.COMMAND_MUST_HAVE_SELECTED);
        if (!sender.hasPermission("citizens.npc.remove") && !sender.hasPermission("citizens.admin"))
            throw new NoPermissionsException();
        npc.destroy();
        Messaging.sendTr(sender, Messages.NPC_REMOVED, npc.getName());
    }

    @Command(
            aliases = { "npc" },
            usage = "rename [name]",
            desc = "Rename a NPC",
            modifiers = { "rename" },
            min = 2,
            permission = "citizens.npc.rename")
    public void rename(CommandContext args, CommandSender sender, NPC npc) {
        String oldName = npc.getName();
        String newName = args.getJoinedStrings(1);
        if (newName.length() > 16) {
            Messaging.sendErrorTr(sender, Messages.NPC_NAME_TOO_LONG);
            newName = newName.substring(0, 15);
        }
        Location prev = npc.isSpawned() ? npc.getBukkitEntity().getLocation() : null;
        npc.despawn(DespawnReason.PENDING_RESPAWN);
        npc.setName(newName);
        if (prev != null)
            npc.spawn(prev);

        Messaging.sendTr(sender, Messages.NPC_RENAMED, oldName, newName);
    }

    @Command(
            aliases = { "npc" },
            usage = "respawn [delay in ticks]",
            desc = "Sets an NPC's respawn delay in ticks",
            modifiers = { "respawn" },
            min = 1,
            max = 2,
            permission = "citizens.npc.respawn")
    public void respawn(CommandContext args, CommandSender sender, NPC npc) {
        if (args.argsLength() > 1) {
            int delay = args.getInteger(1);
            npc.data().setPersistent(NPC.RESPAWN_DELAY_METADATA, delay);
            Messaging.sendTr(sender, Messages.RESPAWN_DELAY_SET, delay);
        } else {
            Messaging.sendTr(sender, Messages.RESPAWN_DELAY_DESCRIBE, npc.data().get(NPC.RESPAWN_DELAY_METADATA, -1));
        }

    }

    @Command(
            aliases = { "npc" },
            usage = "select|sel [id|name] (--r range)",
            desc = "Select a NPC with the given ID or name",
            modifiers = { "select", "sel" },
            min = 1,
            max = 2,
            permission = "citizens.npc.select")
    @Requirements
    public void select(CommandContext args, CommandSender sender, NPC npc) throws CommandException {
        NPC toSelect = null;
        if (args.argsLength() <= 1) {
            if (!(sender instanceof Player))
                throw new ServerCommandException();
            double range = Math.abs(args.getFlagDouble("r", 10));
            Entity player = (Player) sender;
            final Location location = args.getSenderLocation();
            List<Entity> search = player.getNearbyEntities(range, range, range);
            Collections.sort(search, new Comparator<Entity>() {
                @Override
                public int compare(Entity o1, Entity o2) {
                    double d = o1.getLocation().distanceSquared(location) - o2.getLocation().distanceSquared(location);
                    return d > 0 ? 1 : d < 0 ? -1 : 0;
                }
            });
            for (Entity possibleNPC : search) {
                NPC test = npcRegistry.getNPC(possibleNPC);
                if (test == null)
                    continue;
                toSelect = test;
                break;
            }
        } else {
            try {
                int id = args.getInteger(1);
                toSelect = npcRegistry.getById(id);
            } catch (NumberFormatException ex) {
                String name = args.getString(1);
                List<NPC> possible = Lists.newArrayList();
                double range = -1;
                if (args.hasValueFlag("r")) {
                    range = Math.abs(args.getFlagDouble("r"));
                }
                for (NPC test : npcRegistry) {
                    if (test.getName().equalsIgnoreCase(name)) {
                        if (range > 0
                                && test.isSpawned()
                                && !Util.locationWithinRange(args.getSenderLocation(), test.getBukkitEntity()
                                .getLocation(), range))
                            continue;
                        possible.add(test);
                    }
                }
                if (possible.size() == 1) {
                    toSelect = possible.get(0);
                } else if (possible.size() > 1) {
                    SelectionPrompt.start(selector, (Conversable) sender, possible);
                    return;
                }
            }
        }
        if (toSelect == null || !toSelect.getTrait(Spawned.class).shouldSpawn())
            throw new CommandException(Messages.NPC_NOT_FOUND);
        if (npc != null && toSelect.getId() == npc.getId())
            throw new CommandException(Messages.NPC_ALREADY_SELECTED);
        selector.select(sender, toSelect);
        Messaging.sendWithNPC(sender, Setting.SELECTION_MESSAGE.asString(), toSelect);
    }

    @Command(
            aliases = { "npc" },
            usage = "spawn (id)",
            desc = "Spawn an existing NPC",
            modifiers = { "spawn" },
            min = 1,
            max = 2,
            permission = "citizens.npc.spawn")
    @Requirements()
    public void spawn(CommandContext args, CommandSender sender, NPC npc) throws CommandException {
        NPC respawn = args.argsLength() > 1 ? npcRegistry.getById(args.getInteger(1)) : npc;
        if (respawn == null) {
            if (args.argsLength() > 1) {
                throw new CommandException(Messages.NO_NPC_WITH_ID_FOUND, args.getInteger(1));
            } else {
                throw new CommandException(CommandMessages.MUST_HAVE_SELECTED);
            }
        }
        if (respawn.isSpawned())
            throw new CommandException(Messages.NPC_ALREADY_SPAWNED, respawn.getName());
        Location location = respawn.getTrait(CurrentLocation.class).getLocation();
        if (location == null || args.hasValueFlag("location")) {
            if (args.getSenderLocation() == null)
                throw new CommandException(Messages.NO_STORED_SPAWN_LOCATION);

            location = args.getSenderLocation();
        }
        if (respawn.spawn(location)) {
            selector.select(sender, respawn);
            Messaging.sendTr(sender, Messages.NPC_SPAWNED, respawn.getName());
        }
    }

    @Command(
            aliases = { "npc" },
            usage = "speak message to speak --target npcid|player_name --type vocal_type",
            desc = "Uses the NPCs SpeechController to talk",
            modifiers = { "speak" },
            min = 2,
            permission = "citizens.npc.speak")
    public void speak(CommandContext args, CommandSender sender, NPC npc) throws CommandException {
        String type = npc.getTrait(Speech.class).getDefaultVocalChord();
        String message = Colorizer.parseColors(args.getJoinedStrings(1));

        if (message.length() <= 0) {
            Messaging.send(sender, "Default Vocal Chord for " + npc.getName() + ": "
                    + npc.getTrait(Speech.class).getDefaultVocalChord());
            return;
        }

        SpeechContext context = new SpeechContext(message);

        if (args.hasValueFlag("target")) {
            if (args.getFlag("target").matches("\\d+")) {
                NPC target = CitizensAPI.getNPCRegistry().getById(Integer.valueOf(args.getFlag("target")));
                if (target != null)
                    context.addRecipient(target.getBukkitEntity());
            } else {
                Player player = Bukkit.getPlayer(args.getFlag("target"));
                if (player != null)
                    context.addRecipient(player);
            }
        }

        if (args.hasValueFlag("type")) {
            if (CitizensAPI.getSpeechFactory().isRegistered(args.getFlag("type")))
                type = args.getFlag("type");
        }

        npc.getDefaultSpeechController().speak(context, type);
    }

    @Command(
            aliases = { "npc" },
            usage = "speed [speed]",
            desc = "Sets the movement speed of an NPC as a percentage",
            modifiers = { "speed" },
            min = 2,
            max = 2,
            permission = "citizens.npc.speed")
    public void speed(CommandContext args, CommandSender sender, NPC npc) throws CommandException {
        float newSpeed = (float) Math.abs(args.getDouble(1));
        if (newSpeed >= Setting.MAX_SPEED.asDouble())
            throw new CommandException(Messages.SPEED_MODIFIER_ABOVE_LIMIT);
        npc.getNavigator().getDefaultParameters().speedModifier(newSpeed);

        Messaging.sendTr(sender, Messages.SPEED_MODIFIER_SET, newSpeed);
    }

    @Command(
            aliases = { "npc" },
            usage = "targetable",
            desc = "Toggles an NPC's targetability",
            modifiers = { "targetable" },
            min = 1,
            max = 1,
            permission = "citizens.npc.targetable")
    public void targetable(CommandContext args, CommandSender sender, NPC npc) {
        boolean targetable = !npc.data().get(NPC.TARGETABLE_METADATA,
                npc.data().get(NPC.DEFAULT_PROTECTED_METADATA, true));
        if (args.hasFlag('t')) {
            npc.data().set(NPC.TARGETABLE_METADATA, targetable);
        } else {
            npc.data().setPersistent(NPC.TARGETABLE_METADATA, targetable);
        }
        Messaging.sendTr(sender, targetable ? Messages.TARGETABLE_SET : Messages.TARGETABLE_UNSET, npc.getName());
    }

    @Command(
            aliases = { "npc" },
            usage = "tp",
            desc = "Teleport to a NPC",
            modifiers = { "tp", "teleport" },
            min = 1,
            max = 1,
            permission = "citizens.npc.tp")
    public void tp(CommandContext args, Player player, NPC npc) {
        Location to = npc.getTrait(CurrentLocation.class).getLocation();
        if (to == null) {
            Messaging.sendError(player, Messages.TELEPORT_NPC_LOCATION_NOT_FOUND);
            return;
        }
        player.teleport(to, TeleportCause.COMMAND);
        Messaging.sendTr(player, Messages.TELEPORTED_TO_NPC, npc.getName());
    }

    @Command(aliases = { "npc" }, usage = "tphere", desc = "Teleport a NPC to your location", modifiers = { "tphere",
            "tph", "move" }, min = 1, max = 1, permission = "citizens.npc.tphere")
    public void tphere(CommandContext args, CommandSender sender, NPC npc) throws CommandException {
        if (args.getSenderLocation() == null)
            throw new ServerCommandException();
        // Spawn the NPC if it isn't spawned to prevent NPEs
        if (!npc.isSpawned()) {
            npc.spawn(args.getSenderLocation());
            if (!sender.hasPermission("citizens.npc.tphere.multiworld")
                    && npc.getBukkitEntity().getLocation().getWorld() != args.getSenderLocation().getWorld()) {
                npc.despawn(DespawnReason.REMOVAL);
                throw new CommandException(Messages.CANNOT_TELEPORT_ACROSS_WORLDS);
            }
        } else {
            if (!sender.hasPermission("citizens.npc.tphere.multiworld")
                    && npc.getBukkitEntity().getLocation().getWorld() != args.getSenderLocation().getWorld()) {
                npc.despawn(DespawnReason.REMOVAL);
                throw new CommandException(Messages.CANNOT_TELEPORT_ACROSS_WORLDS);
            }
            npc.teleport(args.getSenderLocation(), TeleportCause.COMMAND);
        }
        Messaging.sendTr(sender, Messages.NPC_TELEPORTED, npc.getName());
    }

    @Command(
            aliases = { "npc" },
            usage = "tpto [player name|npc id] [player name|npc id]",
            desc = "Teleport an NPC or player to another NPC or player",
            modifiers = { "tpto" },
            min = 3,
            max = 3,
            permission = "citizens.npc.tpto")
    @Requirements
    public void tpto(CommandContext args, CommandSender sender, NPC npc) throws CommandException {
        Entity from = null, to = null;
        if (npc != null)
            from = npc.getBukkitEntity();
        boolean firstWasPlayer = false;
        try {
            int id = args.getInteger(1);
            NPC fromNPC = CitizensAPI.getNPCRegistry().getById(id);
            if (fromNPC != null)
                from = fromNPC.getBukkitEntity();
        } catch (NumberFormatException e) {
            from = Bukkit.getPlayerExact(args.getString(1));
            firstWasPlayer = true;
        }
        try {
            int id = args.getInteger(2);
            NPC toNPC = CitizensAPI.getNPCRegistry().getById(id);
            if (toNPC != null)
                to = toNPC.getBukkitEntity();
        } catch (NumberFormatException e) {
            if (!firstWasPlayer)
                to = Bukkit.getPlayerExact(args.getString(2));
        }
        if (from == null)
            throw new CommandException(Messages.FROM_ENTITY_NOT_FOUND);
        if (to == null)
            throw new CommandException(Messages.TO_ENTITY_NOT_FOUND);
        from.teleport(to);
        Messaging.sendTr(sender, Messages.TPTO_SUCCESS);
    }

    @Command(
            aliases = { "npc" },
            usage = "vulnerable (-t)",
            desc = "Toggles an NPC's vulnerability",
            modifiers = { "vulnerable" },
            min = 1,
            max = 1,
            flags = "t",
            permission = "citizens.npc.vulnerable")
    public void vulnerable(CommandContext args, CommandSender sender, NPC npc) {
        boolean vulnerable = !npc.data().get(NPC.DEFAULT_PROTECTED_METADATA, true);
        if (args.hasFlag('t')) {
            npc.data().set(NPC.DEFAULT_PROTECTED_METADATA, vulnerable);
        } else {
            npc.data().setPersistent(NPC.DEFAULT_PROTECTED_METADATA, vulnerable);
        }
        String key = vulnerable ? Messages.VULNERABLE_STOPPED : Messages.VULNERABLE_SET;
        Messaging.sendTr(sender, key, npc.getName());
    }
}
