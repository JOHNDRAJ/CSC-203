import java.util.*;
import processing.core.PImage;

/** Represents an entity that exists in the virtual world. */
public class Entity {
    /**
     * Enumerated type defining different kinds of entities in the world.
     * Specific values are assigned to the entity's 'kind' instance variable at initialization.
     * Each kind represents a distinct kind of entity with unique properties and behaviors.
     */
    public enum EntityKind {
        DUDE,
        FAIRY,
        HOUSE,
        MUSHROOM,
        SAPLING,
        STUMP,
        TREE,
        WATER
    }

    // Constant string identifiers for the corresponding type of entity.
    // Used to identify entities in the save file and retrieve image information.
    public static final String DUDE_KEY = "dude";
    public static final String FAIRY_KEY = "fairy";
    public static final String HOUSE_KEY = "house";
    public static final String MUSHROOM_KEY = "mushroom";
    public static final String SAPLING_KEY = "sapling";
    public static final String STUMP_KEY = "stump";
    public static final String TREE_KEY = "tree";
    public static final String WATER_KEY = "water";

    // Constant save file column positions for properties required by all entities.
    public static final int ENTITY_PROPERTY_KEY_INDEX = 0;
    public static final int ENTITY_PROPERTY_ID_INDEX = 1;
    public static final int ENTITY_PROPERTY_POSITION_X_INDEX = 2;
    public static final int ENTITY_PROPERTY_POSITION_Y_INDEX = 3;
    public static final int ENTITY_PROPERTY_COLUMN_COUNT = 4;

    // Constant save file column positions for properties corresponding to speicific entity types.
    // Do not use these values directly in any constructors or 'create' methods.
    public static final int DUDE_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX = 0;
    public static final int DUDE_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX = 1;
    public static final int DUDE_PARSE_PROPERTY_RESOURCE_LIMIT_INDEX = 2;
    public static final int DUDE_PARSE_PROPERTY_COUNT = 3;

    public static final int FAIRY_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX = 0;
    public static final int FAIRY_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX = 1;
    public static final int FAIRY_PARSE_PROPERTY_COUNT = 2;

    public static final int HOUSE_PARSE_PROPERTY_COUNT = 0;

    public static final int MUSHROOM_PARSE_BEHAVIOR_PERIOD_INDEX = 0;
    public static final int MUSHROOM_PARSE_PROPERTY_COUNT = 1;

    public static final int STUMP_PARSE_PROPERTY_COUNT = 0;

    public static final int SAPLING_PARSE_PROPERTY_COUNT = 0;

    public static final int TREE_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX = 0;
    public static final int TREE_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX = 1;
    public static final int TREE_PARSE_PROPERTY_HEALTH_INDEX = 2;
    public static final int TREE_PARSE_PROPERTY_COUNT = 3;

    // Constant limits and default values for specific entity types.
    public static final int SAPLING_HEALTH_LIMIT = 5;
    public static final double SAPLING_BEHAVIOR_PERIOD = 2.0;
    public static final double SAPLING_ANIMATION_PERIOD = 0.01; // Very small to react to health changes

    public static final double TREE_RANDOM_BEHAVIOR_PERIOD_MIN = 0.01;
    public static final double TREE_RANDOM_BEHAVIOR_PERIOD_MAX = 0.10;
    public static final double TREE_RANDOM_ANIMATION_PERIOD_MIN = 0.1;
    public static final double TREE_RANDOM_ANIMATION_PERIOD_MAX = 1.0;
    public static final int TREE_RANDOM_HEALTH_MIN = 1;
    public static final int TREE_RANDOM_HEALTH_MAX = 3;

    public static final int WATER_PARSE_PROPERTY_COUNT = 0;

    /** Determines instance logic and categorization. */
    private final EntityKind kind;

    /** Entity's identifier that often includes the corresponding 'key' constant. */
    private String id;

    /** Entity's x/y position in the world. */
    private Point position;

    /** Entity's inanimate (singular) or animation (multiple) images. */
    private List<PImage> images;

    /** Index of the element from 'images' used to draw the entity. */
    private int imageIndex;

    /** Positive (non-zero) time delay between the entity's animations. */
    private double animationPeriod;

    /** Positive (non-zero) time delay between the entity's behaviors. */
    private double behaviorPeriod;

    /** Entity's current health level. */
    private int health;

    /** Number of resources collected by the entity. */
    private int resourceCount;

    /** Total number of resources the entity may hold. */
    private int resourceLimit;

    /**
     * Constructs an Entity with specified characteristics.
     * In the base program, this is not called directly.
     * Instead, the encapsulated 'create' methods are used to create specific types of entities.
     *
     * @param kind            The entity's type that determines instance logic and categorization.
     * @param id              The entity's identifier.
     * @param position        The entity's x/y position in the world.
     * @param images          The entity's inanimate (singular) or animation (multiple) images.
     * @param animationPeriod The positive (non-zero) time delay between the entity's animations.
     * @param behaviorPeriod  The positive (non-zero) time delay between the entity's behaviors.
     * @param health          The entity's current health level.
     * @param resourceCount   The number of resources held by the entity.
     * @param resourceLimit   The total number of resources the entity may hold.
     */
    public Entity(EntityKind kind, String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod, int health, int resourceCount, int resourceLimit) {
        this.kind = kind;
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.animationPeriod = animationPeriod;
        this.behaviorPeriod = behaviorPeriod;
        this.health = health;
        this.resourceCount = resourceCount;
        this.resourceLimit = resourceLimit;
    }

    /**
     * Returns a new 'Dude' type entity.
     * Constructor arguments provide hints to data necessary for a subclass.
     *
     * @param id              A unique string identifier for the entity.
     *                        Typically, the constant 'Dude' key, but can be anything.
     * @param position        The entity's x/y position in the world.
     * @param images          A list of images that represent an entity and its possible animation.
     * @param animationPeriod The time between when an entity's animation is scheduled and executed.
     * @param behaviorPeriod  The time between when an entity's activity is scheduled and executed.
     * @param resourceCount   The number of resources held by the entity.
     * @param resourceLimit   The total number of resources the entity can hold.
     *
     * @return A new Entity object configured as a 'Dude'.
     */
    public static Entity createDude(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod, int resourceCount, int resourceLimit) {
        return new Entity(EntityKind.DUDE, id, position, images, animationPeriod, behaviorPeriod, 0, resourceCount, resourceLimit);
    }

    /**
     * Creates an Entity representing a(n) 'Fairy'.
     * The parameters provide a hint to data relevant for a(n) 'Fairy' class.
     *
     * @param id              A unique string identifier for the entity.
     *                        Typically, the constant 'Fairy' key, but can be anything.
     * @param position        The entity's x/y position in the world.
     * @param images          A list of images that represent an entity and its possible animation.
     * @param animationPeriod The time between when an entity's animation is scheduled and executed.
     * @param behaviorPeriod  The time between when an entity's activity is scheduled and executed.
     *
     * @return A new Entity object configured as a 'Fairy'.
     */
    public static Entity createFairy(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod) {
        return new Entity(EntityKind.FAIRY, id, position, images, animationPeriod, behaviorPeriod, 0, 0, 0);
    }

    /**
     * Creates an Entity representing a(n) 'House'.
     * The parameters provide a hint to data relevant for a 'House' class.
     *
     * @param id A unique string identifier for the entity.
     *           Typically, the constant 'House' key, but can be anything.
     * @param position The entity's x/y position in the world.
     * @param images A list of images that represent an entity and its possible animation.
     *
     * @return A new Entity object configured as a 'House'.
     */
    public static Entity createHouse(String id, Point position, List<PImage> images) {
        return new Entity(EntityKind.HOUSE, id, position, images, 0.0, 0.0, 0, 0, 0);
    }

    /**
     * Creates an Entity representing a(n) 'Mushroom'.
     * The parameters provide a hint to data relevant for a 'Mushroom' class.
     *
     * @param id A unique string identifier for the entity.
     *           Typically, the constant 'House' key, but can be anything.
     * @param position The entity's x/y position in the world.
     * @param images A list of images that represent an entity and its possible animation.
     *
     * @return A new Entity object configured as a 'Mushroom'.
     */
    public static Entity createMushroom(String id, Point position, List<PImage> images, double behaviorPeriod) {
        return new Entity(EntityKind.MUSHROOM, id, position, images, 0.0, behaviorPeriod, 0, 0, 0);
    }

    /**
     * Creates an Entity representing a(n) 'Water'.
     * The parameters provide a hint to data relevant for a 'Water' class.
     *
     * @param id A unique string identifier for the entity.
     *           Typically, the constant 'Water' key, but can be anything.
     * @param position The entity's x/y position in the world.
     * @param images A list of images that represent an entity and its possible animation.
     *
     * @return A new Entity object configured as a 'Water'.
     */
    public static Entity createWater(String id, Point position, List<PImage> images) {
        return new Entity(EntityKind.WATER, id, position, images, 0.0, 0.0, 0, 0, 0);
    }

    /**
     * Creates an Entity representing a(n) 'Sapling'.
     * The parameters provide a hint to data relevant for a 'Sapling' class.
     *
     * @param id A unique string identifier for the entity.
     *           Typically, the constant 'Sapling' key, but can be anything.
     * @param position The entity's x/y position in the world.
     * @param images A list of images that represent an entity and its possible animation.
     *
     * @return A new Entity object configured as a 'Sapling'.
     */
    public static Entity createSapling(String id, Point position, List<PImage> images) {
        return new Entity(EntityKind.SAPLING, id, position, images, SAPLING_ANIMATION_PERIOD, SAPLING_BEHAVIOR_PERIOD, 0, 0, 0);
    }

    /**
     * Creates an Entity representing a(n) 'Stump'.
     * The parameters provide a hint to data relevant for a 'Stump' class.
     *
     * @param id A unique string identifier for the entity.
     *           Typically, the 'Stump' key, but can be anything.
     * @param position The entity's x/y position in the world.
     * @param images A list of images that represent an entity and its possible animation.
     *
     * @return A new Entity object configured as a 'Stump'.
     */
    public static Entity createStump(String id, Point position, List<PImage> images) {
        return new Entity(EntityKind.STUMP, id, position, images, 0.0, 0.0, 0, 0, 0);
    }

    /**
     * Creates an Entity representing a(n) 'Tree'.
     * The parameters provide a hint to data relevant for a 'Tree' class.
     *
     * @param id              A unique string identifier for the entity.
     *                        Typically, the 'Tree' key, but can be anything.
     * @param position        The entity's x/y position in the world.
     * @param images          A list of images that represent an entity and its possible animation.
     * @param animationPeriod The time between when an entity's animation is scheduled and executed.
     * @param behaviorPeriod  The time between when an entity's activity is scheduled and executed.
     * @return A new Entity object configured as a 'Tree'.
     */
    public static Entity createTree(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod, int health) {
        return new Entity(EntityKind.TREE, id, position, images, animationPeriod, behaviorPeriod, health, 0, 0);
    }

    /** Helper method for testing. Preserve this functionality for all kinds of entities. */
    public String log(){
        if (id.isEmpty()) {
            return null;
        } else {
            return String.format("%s %d %d %d", id, position.x, position.y, imageIndex);
        }
    }

    /** Called when an animation action occurs. */
    public void updateImage() {
        switch(kind) {
            case DUDE, FAIRY, TREE:
                imageIndex = imageIndex + 1;
                break;
            case SAPLING:
                if (health <= 0) {
                    imageIndex = 0;
                } else if (health < SAPLING_HEALTH_LIMIT) {
                    imageIndex = images.size() * health / SAPLING_HEALTH_LIMIT;
                } else {
                    imageIndex = images.size() - 1;
                }
                break;
            default:
                throw new UnsupportedOperationException(String.format("updateImage not supported for %s", kind));
        }
    }

    /** Called to begin animation and/or behavior for an entity. */
    public void scheduleActions(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        switch (kind) {
            case MUSHROOM:
                scheduleBehavior(scheduler, world, imageLibrary);
                break;
            case DUDE, FAIRY, SAPLING, TREE:
                scheduleAnimation(scheduler, world, imageLibrary);
                scheduleBehavior(scheduler, world, imageLibrary);
                break;
            default:
                // Do nothing for other entities
        }
    }

    /** Begins all animation updates for the entity. */
    public void scheduleAnimation(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduler.scheduleEvent(this, Action.createAnimation(this, 0), animationPeriod);
    }

    /** Schedules a single behavior update for the entity. */
    public void scheduleBehavior(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduler.scheduleEvent(this, Action.createBehavior(this, world, imageLibrary), behaviorPeriod);
    }

    /** Performs the entity's behavior logic. */
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        switch(kind) {
            case DUDE:
                executeDudeActivity(world, imageLibrary, scheduler);
                break;
            case FAIRY:
                executeFairyActivity(world, imageLibrary, scheduler);
                break;
            case MUSHROOM:
                executeMushroomActivity(world, imageLibrary, scheduler);
                break;
            case SAPLING:
                executeSaplingActivity(world, imageLibrary, scheduler);
                break;
            case TREE:
                executeTreeActivity(world, imageLibrary, scheduler);
                break;
            default:
                throw new UnsupportedOperationException(String.format("executeActivity not supported for %s", kind));
        }
    }

    /** Executes Dude specific Logic. */
    public void executeDudeActivity(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        Optional<Entity> dudeTarget = findDudeTarget(world);
        if (dudeTarget.isEmpty() || !moveToDude(world, dudeTarget.get(), scheduler) || !transformDude(world, scheduler, imageLibrary)) {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
    }

    /** Returns the (optional) entity a Dude will path toward. */
    public Optional<Entity> findDudeTarget(World world) {
        List<EntityKind> potentialTargets;

        if (resourceCount == resourceLimit) {
            potentialTargets = List.of(EntityKind.HOUSE);
        } else {
            potentialTargets = List.of(EntityKind.TREE, EntityKind.SAPLING);
        }

        return world.findNearest(position, potentialTargets);
    }

    /** Attempts to move the Dude toward a target, returning True if already adjacent to it. */
    public boolean moveToDude(World world, Entity target, EventScheduler scheduler) {
        if (position.adjacentTo(target.position)) {
            if (target.kind == EntityKind.TREE || target.kind == EntityKind.SAPLING) {
                target.health = target.health - 1;
            }
            return true;
        } else {
            Point nextPos = nextPositionDude(world, target.position);

            if (!position.equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }

            return false;
        }
    }

    /** Determines a Dude's next position when moving. */
    public Point nextPositionDude(World world, Point destination) {
        // Differences between the destination and current position along each axis
        int deltaX = destination.x - position.x;
        int deltaY = destination.y - position.y;

        // Positions one step toward the destination along each axis
        Point horizontalPosition = new Point(position.x + Integer.signum(deltaX), position.y);
        Point verticalPosition = new Point(position.x, position.y + Integer.signum(deltaY));

        // Assumes all destinations are within bounds of the world
        // If this is not the case, also check 'World.inBounds()'
        boolean horizontalOccupied = world.isOccupied(horizontalPosition) && world.getOccupant(horizontalPosition).get().kind != EntityKind.STUMP;
        boolean verticalOccupied = world.isOccupied(verticalPosition) && world.getOccupant(verticalPosition).get().kind != EntityKind.STUMP;

        // Move along the farther direction, preferring horizontal
        if (Math.abs(deltaX) >= Math.abs(deltaY)) {
            if (!horizontalOccupied) {
                return horizontalPosition;
            } else if (!verticalOccupied) {
                return verticalPosition;
            }
        } else {
            if (!verticalOccupied) {
                return verticalPosition;
            } else if (!horizontalOccupied) {
                return horizontalPosition;
            }
        }

        return getPosition();
    }

    /** Changes the Dude's graphics. */
    public boolean transformDude(World world, EventScheduler scheduler, ImageLibrary imageLibrary) {
        if (resourceCount < resourceLimit) {
            resourceCount += 1;
            if (resourceCount == resourceLimit) {
                Entity dude = createDude(id, position, imageLibrary.get(DUDE_KEY + "_carry"), animationPeriod, behaviorPeriod, resourceCount, resourceLimit);

                world.removeEntity(scheduler, this);

                world.addEntity(dude);
                dude.scheduleActions(scheduler, world, imageLibrary);

                return true;
            }
        } else {
            Entity dude = createDude(id, position, imageLibrary.get(DUDE_KEY), animationPeriod, behaviorPeriod, 0, resourceLimit);

            world.removeEntity(scheduler, this);

            world.addEntity(dude);
            dude.scheduleActions(scheduler, world, imageLibrary);

            return true;
        }

        return false;
    }

    /** Executes Fairy specific Logic. */
    public void executeFairyActivity(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        Optional<Entity> fairyTarget = world.findNearest(getPosition(), new ArrayList<>(List.of(EntityKind.STUMP)));

        if (fairyTarget.isPresent()) {
            Point tgtPos = fairyTarget.get().getPosition();

            if (moveToFairy(world, fairyTarget.get(), scheduler)) {
                Entity sapling = createSapling(SAPLING_KEY + "_" + fairyTarget.get().id, tgtPos, imageLibrary.get(SAPLING_KEY));

                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageLibrary);
            }
        }

        scheduleBehavior(scheduler, world, imageLibrary);
    }

    /** Attempts to move the Fairy toward a target, returning True if already adjacent to it. */
    public boolean moveToFairy(World world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacentTo(target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = nextPositionFairy(world, target.getPosition());
            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    /** Determines a Fairy's next position when moving. */
    public Point nextPositionFairy(World world, Point destination) {
        // Differences between the destination and current position along each axis
        int deltaX = destination.x - position.x;
        int deltaY = destination.y - position.y;

        // Positions one step toward the destination along each axis
        Point horizontalPosition = new Point(position.x + Integer.signum(deltaX), position.y);
        Point verticalPosition = new Point(position.x, position.y + Integer.signum(deltaY));

        // Assumes all destinations are within bounds of the world
        // If this is not the case, also check 'World.withinBounds()'
        boolean horizontalOccupied = world.isOccupied(horizontalPosition);
        boolean verticalOccupied = world.isOccupied(verticalPosition);

        // Move along the farther direction, preferring horizontal
        if (Math.abs(deltaX) >= Math.abs(deltaY)) {
            if (!horizontalOccupied) {
                return horizontalPosition;
            } else if (!verticalOccupied) {
                return verticalPosition;
            }
        } else {
            if (!verticalOccupied) {
                return verticalPosition;
            } else if (!horizontalOccupied) {
                return horizontalPosition;
            }
        }

        return getPosition();
    }

    /** Executes Mushroom specific Logic. */
    public void executeMushroomActivity(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        List<Point> adjacentPositions = new ArrayList<>(List.of(
                new Point(position.x - 1, position.y),
                new Point(position.x + 1, position.y),
                new Point(position.x, position.y - 1),
                new Point(position.x, position.y + 1)
        ));
        Collections.shuffle(adjacentPositions);

        List<Point> mushroomBackgroundPositions = new ArrayList<>();
        List<Point> mushroomEntityPositions = new ArrayList<>();
        for (Point adjacentPosition : adjacentPositions) {
            if (world.inBounds(adjacentPosition) && !world.isOccupied(adjacentPosition) && world.hasBackground(adjacentPosition)) {
                Background bg = world.getBackgroundCell(adjacentPosition);
                if (bg.getId().equals("grass")) {
                    mushroomBackgroundPositions.add(adjacentPosition);
                } else if (bg.getId().equals("grass_mushrooms")) {
                    mushroomEntityPositions.add(adjacentPosition);
                }
            }
        }

        if (!mushroomBackgroundPositions.isEmpty()) {
            Point position = mushroomBackgroundPositions.get(0);

            Background background = new Background("grass_mushrooms", imageLibrary.get("grass_mushrooms"), 0);
            world.setBackgroundCell(position, background);
        } else if (!mushroomEntityPositions.isEmpty()) {
            Point position = mushroomEntityPositions.get(0);

            Entity mushroom = createMushroom(MUSHROOM_KEY, position, imageLibrary.get(MUSHROOM_KEY), behaviorPeriod * 4.0);

            world.addEntity(mushroom);
            mushroom.scheduleActions(scheduler, world, imageLibrary);
        }

        scheduleBehavior(scheduler, world, imageLibrary);
    }

    /** Executes Sapling specific Logic. */
    public void executeSaplingActivity(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        health = health + 1;
        if (!transformSapling(world, scheduler, imageLibrary)) {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
    }

    /** Checks the Sapling's health and transforms accordingly, returning true if successful. */
    public boolean transformSapling(World world, EventScheduler scheduler, ImageLibrary imageLibrary) {
        if (health <= 0) {
            Entity stump = createStump(STUMP_KEY + "_" + id, position, imageLibrary.get(STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        } else if (health >= SAPLING_HEALTH_LIMIT) {
            Entity tree = createTree(
                    TREE_KEY + "_" + id,
                    position,
                    imageLibrary.get(TREE_KEY),
                    NumberUtil.getRandomDouble(TREE_RANDOM_ANIMATION_PERIOD_MIN, TREE_RANDOM_ANIMATION_PERIOD_MAX), NumberUtil.getRandomDouble(TREE_RANDOM_BEHAVIOR_PERIOD_MIN, TREE_RANDOM_BEHAVIOR_PERIOD_MAX),
                    NumberUtil.getRandomInt(TREE_RANDOM_HEALTH_MIN, TREE_RANDOM_HEALTH_MAX)
            );

            world.removeEntity(scheduler, this);

            world.addEntity(tree);
            tree.scheduleActions(scheduler, world, imageLibrary);

            return true;
        }

        return false;
    }

    /** Executes Tree specific Logic. */
    public void executeTreeActivity(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        if (!transformTree(world, scheduler, imageLibrary)) {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
    }

    /** Checks the Tree's health and transforms accordingly, returning true if successful. */
    public boolean transformTree(World world, EventScheduler scheduler, ImageLibrary imageLibrary) {
        if (health <= 0) {
            Entity stump = createStump(STUMP_KEY + "_" + id, position, imageLibrary.get(STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        }

        return false;
    }

    public double getAnimationPeriod() {
        switch (kind) {
            case DUDE, FAIRY, SAPLING, TREE:
                return animationPeriod;
            default:
                throw new UnsupportedOperationException(String.format("getAnimationPeriod not supported for %s", kind));
        }
    }

    public EntityKind getKind() {
        return kind;
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public List<PImage> getImages() {
        return images;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
