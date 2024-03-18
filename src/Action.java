/** A scheduled action to be carried out by a specific entity. */
public class Action {
    /**
     * Enumerated type defining different kinds of actions that entities take in the world.
     * Specific values are assigned to the action's 'kind' instance variable at initialization.
     * There are two types of actions: animations (image updates) and behaviors (logic updates).
     */
    public enum ActionKind {
        ANIMATION,
        BEHAVIOR
    }

    /** Type that determines instance logic and categorization. */
    private final ActionKind kind;
    /** Entity enacting the action. */
    private final Entity entity;
    /** World in which the action occurs. */
    private final World world;
    /** Image data that may be used by the action. */
    private final ImageLibrary imageLibrary;
    /** Number of animation repeats. A zero indicates indefinite repeats. */
    private int repeatCount;

    /**
     * Constructs an Action object with specified characteristics.
     * In the base program, this is not called directly.
     * Instead, the encapsulated 'create' method are used to create specific kinds.
     *
     * @param kind The type that determines instance logic and categorization.
     * @param entity The entity enacting the action.
     * @param world The world in which the action occurs.
     * @param imageLibrary The image data that may be used by the action.
     * @param repeatCount The number of animation repeats. A zero indicates indefinite repeats.
     */
    public Action(ActionKind kind, Entity entity, World world, ImageLibrary imageLibrary, int repeatCount) {
        this.kind = kind;
        this.entity = entity;
        this.world = world;
        this.imageLibrary = imageLibrary;
        this.repeatCount = repeatCount;
    }

    /**
     * Returns a new 'Behavior' type action.
     * Constructor arguments provide hints to data necessary for a subclass.
     *
     * @param entity The entity enacting the behavior.
     * @param world The world in which the behavior occurs.
     * @param imageLibrary The image data that may be used by the behavior.
     *
     * @return A new Action object configured as a(n) 'Behavior'.
     */
    public static Action createBehavior(Entity entity, World world, ImageLibrary imageLibrary) {
        return new Action(ActionKind.BEHAVIOR, entity, world, imageLibrary, 0);
    }

    /**
     * Returns a new 'Animation' type action.
     * Constructor arguments provide hints to data necessary for a subclass.
     *
     * @param entity The entity that the animation is applied to.
     * @param repeatCount The number of animation repeats. A zero indicates indefinite repeats.
     *
     * @return A new Action object configured as a(n) 'Animation'.
     */
    public static Action createAnimation(Entity entity, int repeatCount) {
        return new Action(ActionKind.ANIMATION, entity, null, null, repeatCount);
    }

    /** Called when the action's scheduled time occurs. */
    public void execute(EventScheduler scheduler) {
        switch (kind) {
            case ANIMATION:
                executeAnimation(scheduler);
                break;
            case BEHAVIOR:
                executeBehavior(scheduler);
                break;
            default:
                throw new UnsupportedOperationException(String.format("executeAction not supported for %s", kind));
        }
    }

    /** Performs 'Behavior' specific logic. */
    public void executeBehavior(EventScheduler scheduler) {
        entity.executeBehavior(world, imageLibrary, scheduler);
    }

    /** Performs 'Animation' specific logic. */
    public void executeAnimation(EventScheduler scheduler) {
        entity.updateImage();

        if (repeatCount != 1) {
            scheduler.scheduleEvent(entity, createAnimation(this.entity, Math.max(this.repeatCount - 1, 0)), entity.getAnimationPeriod());
        }
    }
}
