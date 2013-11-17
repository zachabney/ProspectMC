package net.citizensnpcs.api.npc;

import net.citizensnpcs.api.ai.GoalController;
import net.citizensnpcs.api.ai.Navigator;
import net.citizensnpcs.api.ai.speech.SpeechController;
import net.citizensnpcs.api.astar.Agent;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitFactory;
import net.citizensnpcs.api.util.DataKey;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 * Represents an NPC with optional {@link Trait}s.
 */
public interface NPC extends Agent, Cloneable {

    /**
     * Adds a trait to this NPC. This will use the {@link TraitFactory} defined
     * for this NPC to construct and attach a trait using
     * {@link #addTrait(Trait)}.
     *
     * @param trait
     *            The class of the trait to add
     */
    public void addTrait(Class<? extends Trait> trait);

    /**
     * Adds a trait to this NPC.
     *
     * @param trait
     *            Trait to add
     */
    public void addTrait(Trait trait);

    /**
     * @return A clone of the NPC. May not be an exact copy depending on the
     *         {@link Trait}s installed.
     */
    public NPC clone();

    /**
     * @return The metadata store of this NPC.
     */
    public MetadataStore data();

    /**
     * Despawns this NPC. This is equivalent to calling
     * {@link #despawn(DespawnReason)} with {@link DespawnReason#PLUGIN}.
     *
     * @return Whether this NPC was able to despawn
     */
    public boolean despawn();

    /**
     * Despawns this NPC.
     *
     * @param reason
     *            The reason for despawning, for use in {@link NPCDespawnEvent}
     * @return Whether this NPC was able to despawn
     */
    boolean despawn(DespawnReason reason);

    /**
     * Permanently removes this NPC and all data about it from the registry it's
     * attached to.
     */
    public void destroy();

    /**
     * Faces a given {@link Location} if the NPC is spawned.
     */
    public void faceLocation(Location location);

    /**
     * Gets the Bukkit entity associated with this NPC. This may be
     * <code>null</code> if {@link #isSpawned()} is false.
     *
     * @return Entity associated with this NPC
     */
    public LivingEntity getBukkitEntity();

    /**
     * Gets the default {@link GoalController} of this NPC.
     *
     * @return Default goal controller
     */
    public GoalController getDefaultGoalController();

    /**
     * Gets the default {@link SpeechController} of this NPC.
     *
     * @return Default speech controller
     */
    public SpeechController getDefaultSpeechController();

    /**
     * Gets the full name of this NPC.
     *
     * @return Full name of this NPC
     */
    public String getFullName();

    /**
     * Gets the unique ID of this NPC. This is not guaranteed to be globally
     * unique across server sessions.
     *
     * @return ID of this NPC
     */
    public int getId();

    /**
     * Gets the name of this NPC with color codes stripped.
     *
     * @return Stripped name of this NPC
     */
    public String getName();

    /**
     * @return The {@link Navigator} of this NPC.
     */
    public Navigator getNavigator();

    /**
     * If the NPC is not spawned, then this method will return the last known
     * location, or null if it has never been spawned. Otherwise, it is
     * equivalent to calling <code>npc.getBukkitEntity().getLocation()</code>.
     *
     * @return The stored location, or <code>null</code> if none was found.
     */
    public Location getStoredLocation();

    /**
     * Gets a trait from the given class. If the NPC does not currently have the
     * trait then it will be created and attached using {@link #addTrait(Class)}
     * .
     *
     * @param trait
     *            Trait to get
     * @return Trait with the given name
     */
    public <T extends Trait> T getTrait(Class<T> trait);

    /**
     * Returns the currently attached {@link Trait}s
     *
     * @return An Iterable of the current traits
     */
    public Iterable<Trait> getTraits();

    /**
     * Checks if this NPC has the given trait.
     *
     * @param trait
     *            Trait to check
     * @return Whether this NPC has the given trait
     */
    public boolean hasTrait(Class<? extends Trait> trait);

    public boolean isProtected();

    /**
     * Gets whether this NPC is currently spawned.
     *
     * @return Whether this NPC is spawned
     */
    public boolean isSpawned();

    /**
     * Loads the {@link NPC} from the given {@link DataKey}. This reloads all
     * traits, respawns the NPC and sets it up for execution. Should not be
     * called often.
     *
     * @param key
     *            The root data key
     */
    public void load(DataKey key);

    /**
     * Removes a trait from this NPC.
     *
     * @param trait
     *            Trait to remove
     */
    public void removeTrait(Class<? extends Trait> trait);

    /**
     * Saves the {@link NPC} to the given {@link DataKey}. This includes all
     * metadata, traits, and spawn information that will allow it to respawn at
     * a later time via {@link #load(DataKey)}.
     *
     * @param key
     *            The root data key
     */
    public void save(DataKey key);

    /**
     * Sets the name of this NPC.
     *
     * @param name
     *            Name to give this NPC
     */
    public void setName(String name);

    /**
     * A helper method for using {@link #DEFAULT_PROTECTED_METADATA} to set the
     * NPC as protected or not protected from damage/entity target events.
     * Equivalent to
     * <code>npc.data().set(NPC.DEFAULT_PROTECTED_METADATA, isProtected);</code>
     *
     * @param isProtected
     *            Whether the NPC should be protected
     */
    public void setProtected(boolean isProtected);

    /**
     * Attempts to spawn this NPC.
     *
     * @param location
     *            Location to spawn this NPC
     * @return Whether this NPC was able to spawn at the location
     */
    public boolean spawn(Location location);

    /**
     * An alternative to {{@link #getBukkitEntity().getLocation()} that
     * teleports passengers as well.
     *
     * @param location
     *            The destination location
     * @param cause
     *            The cause for teleporting
     */
    public void teleport(Location location, TeleportCause cause);

    public static final String DEFAULT_PROTECTED_METADATA = "protected";
    public static final String LEASH_PROTECTED_METADATA = "protected-leash";
    public static final String RESPAWN_DELAY_METADATA = "respawn-delay";
    public static final String TARGETABLE_METADATA = "protected-target";
}