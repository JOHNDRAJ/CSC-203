import java.io.*;
import java.util.*;
import java.util.stream.Stream;

/** Contains functionality for loading a world from a text file. */
public class WorldParser {

    /** Used to store a dimensions for instantiating a world. */
    private static class WorldDimensions {
        int numRows;
        int numCols;
    }

    /** Creates a 'World' from a text file. */
    public static World createFromFile(String filePath, ImageLibrary imageLibrary) {
        World world = null;

        try (FileReader reader = new FileReader(filePath)) {
            world = load(reader, imageLibrary);
        } catch (IOException e) {
            System.err.printf(
                    "Unable to load world from file '%s'%n",
                    filePath
            );
        }

        return world;
    }

    /** Creates a 'World' from a string. */
    public static World createFromString(String worldString, ImageLibrary imageLibrary) {
        return load(new StringReader(worldString), imageLibrary);
    }

    /**
     * Loads a world by parsing the given source, erasing any current state.
     *
     * @param reader The data source.
     * @param imageLibrary Image data to use for world entities.
     */
    private static World load(Reader reader, ImageLibrary imageLibrary){
        // Initialize Temporary Data Structures
        WorldDimensions worldDimensions = new WorldDimensions(); // x: numCols, y: numRows
        List<Background[]> backgroundRows = new LinkedList<>();
        Set<Entity> potentialEntities = new HashSet<>();

        // Parse the file
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                    parseLoadLine(line, imageLibrary, worldDimensions, backgroundRows, potentialEntities);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        // Instantiate the world
        World world;
        if (worldDimensions.numRows > 0 && worldDimensions.numCols > 0) {
            world = new World(worldDimensions.numRows, worldDimensions.numCols);
        } else {
            throw new IllegalArgumentException("World dimension is non-positive");
        }

        // Construct the background grid
        for (int y = 0; y < backgroundRows.size(); y++) {
            Background[] backgroundRow = backgroundRows.get(y);
            for (int x = 0; x < backgroundRow.length; x++) {
                world.setBackgroundCell(new Point(x, y), backgroundRow[x]);
            }
        }

        // Construct the occupancy grid and entity set
        for (Entity entity : potentialEntities) {
            Point position = entity.getPosition();
            world.addEntity(entity);
        }

        // Return the successfully constructed world
        return world;
    }


    /** Called to parse each line in the save file. */
    private static void parseLoadLine(String line, ImageLibrary imageLibrary, WorldDimensions worldDimensions, List<Background[]> backgroundRows, Set<Entity> entities) {
        // Clean Up the line
        line = line.strip();

        // Handle Comments
        if (line.startsWith("#")) return;

        // Format the line
        String[] args = line.strip().split(":\\s+");
        if (args.length > 0) {
            switch(args[0]) {
                case "Rows":
                    worldDimensions.numRows = parseRows(args[1]);
                    break;
                case "Cols":
                    worldDimensions.numCols = parseCols(args[1]);
                    break;
                case "Background":
                    backgroundRows.add(parseBackground(args[1], imageLibrary, worldDimensions.numCols));
                    break;
                case "Entity":
                    entities.add(parseEntity(args[1], imageLibrary));
                    break;
            }
        }
    }

    /** Parses lines containing world row size information. */
    private static int parseRows(String parameters) {
        return Integer.parseInt(parameters.strip());
    }

    /** Parses lines containing world column size information. */
    private static int parseCols(String parameters) {
        return Integer.parseInt(parameters.strip());
    }

    /** Parses lines containing background information. */
    private static Background[] parseBackground(String parameters, ImageLibrary imageLibrary, int numCols) {
        // Clean and format parameters
        String[] args = parameters.strip().split("\\s");

        // Parse the parameters
        return Stream.of(args)
                .map(key -> key.isBlank() ? null : new Background(key, imageLibrary.get(key), 0))
                .limit(numCols)
                .toArray(Background[]::new);
    }

    /** Parses lines containing entity information. */
    private static Entity parseEntity(String parameters, ImageLibrary imageLibrary) {
        String[] args = parameters.strip().split("\\s");

        if (args.length >= Entity.ENTITY_PROPERTY_COLUMN_COUNT) {
            // Get general properties
            String[] properties = Arrays.copyOfRange(
                    args,
                    0,
                    Entity.ENTITY_PROPERTY_COLUMN_COUNT
            );

            // Parse general properties
            String key = properties[Entity.ENTITY_PROPERTY_KEY_INDEX];
            String id = properties[Entity.ENTITY_PROPERTY_ID_INDEX];
            Point position = new Point(
                    Integer.parseInt(properties[Entity.ENTITY_PROPERTY_POSITION_X_INDEX]),
                    Integer.parseInt(properties[Entity.ENTITY_PROPERTY_POSITION_Y_INDEX])
            );

            // Get specific properties
            String[] specificProperties = Arrays.copyOfRange(
                    args,
                    Entity.ENTITY_PROPERTY_COLUMN_COUNT,
                    args.length
            );

            // Parse specific properties
            return switch (key) {
                case Entity.DUDE_KEY -> parseDude(specificProperties, id, position, imageLibrary);
                case Entity.FAIRY_KEY -> parseFairy(specificProperties, id, position, imageLibrary);
                case Entity.HOUSE_KEY -> parseHouse(specificProperties, id, position, imageLibrary);
                case Entity.MUSHROOM_KEY -> parseMushroom(specificProperties, id, position, imageLibrary);
                case Entity.WATER_KEY -> parseWater(specificProperties, id, position, imageLibrary);
                case Entity.SAPLING_KEY -> parseSapling(specificProperties, id, position, imageLibrary);
                case Entity.STUMP_KEY -> parseStump(specificProperties, position, id, imageLibrary);
                case Entity.TREE_KEY -> parseTree(specificProperties, id, position, imageLibrary);
                default -> throw new IllegalArgumentException(String.format("Unexpected entity key: %s", key));
            };
        } else {
            throw new IllegalArgumentException("Entity command parameters must be formatted as: [key] [id] [x] [y] ...");
        }
    }

    /** Parses a line of Dude data. */
    private static Entity parseDude(String[] properties, String id, Point position, ImageLibrary imageLibrary) {
        if (properties.length == Entity.DUDE_PARSE_PROPERTY_COUNT) {

            // Modify to use a constructor from your class hierarchy
            // Ensure the order of all passed arguments match the desired parameters
            return Entity.createDude(
                    id,
                    position,
                    imageLibrary.get(Entity.DUDE_KEY),
                    Double.parseDouble(properties[Entity.DUDE_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX]),
                    Double.parseDouble(properties[Entity.DUDE_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX]),
                    0,
                    Integer.parseInt(properties[Entity.DUDE_PARSE_PROPERTY_RESOURCE_LIMIT_INDEX])
            );

        } else {
            throw new IllegalArgumentException(String.format(
                    "%s requires %d properties when parsing, got %d",
                    Entity.DUDE_KEY,
                    Entity.DUDE_PARSE_PROPERTY_COUNT,
                    properties.length
            ));
        }
    }

    /** Parses a line of Fairy data. */
    private static Entity parseFairy(String[] properties, String id, Point position, ImageLibrary imageLibrary) {
        if (properties.length == Entity.FAIRY_PARSE_PROPERTY_COUNT) {

            // Modify to use a constructor from your class hierarchy
            // Ensure the order of all passed arguments match the desired parameters
            return Entity.createFairy(
                    id,
                    position,
                    imageLibrary.get(Entity.FAIRY_KEY),
                    Double.parseDouble(properties[Entity.FAIRY_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX]),
                    Double.parseDouble(properties[Entity.FAIRY_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX])
            );

        } else {
            throw new IllegalArgumentException(String.format(
                    "%s requires %d properties when parsing, got %d",
                    Entity.FAIRY_KEY,
                    Entity.FAIRY_PARSE_PROPERTY_COUNT,
                    properties.length
            ));
        }
    }

    /** Parses a line of Mushroom data. */
    private static Entity parseMushroom(String[] properties, String id, Point position, ImageLibrary imageLibrary) {
        if (properties.length == Entity.MUSHROOM_PARSE_PROPERTY_COUNT) {

            // Modify to use a constructor from your class hierarchy
            // Ensure the order of all passed arguments match the desired parameters
            return Entity.createMushroom(
                    id,
                    position,
                    imageLibrary.get(Entity.MUSHROOM_KEY),
                    Double.parseDouble(properties[Entity.MUSHROOM_PARSE_BEHAVIOR_PERIOD_INDEX])
            );

        } else {
            throw new IllegalArgumentException(String.format(
                    "%s requires %d properties when parsing, got %d",
                    Entity.MUSHROOM_KEY,
                    Entity.MUSHROOM_PARSE_PROPERTY_COUNT,
                    properties.length
            ));
        }
    }

    /** Parses a line of House data. */
    private static Entity parseHouse(String[] properties, String id, Point position, ImageLibrary imageLibrary) {
        if (properties.length == Entity.HOUSE_PARSE_PROPERTY_COUNT) {

            // Modify to use a constructor from your class hierarchy
            // Ensure the order of all passed arguments match the desired parameters
            return Entity.createHouse(
                    id,
                    position,
                    imageLibrary.get(Entity.HOUSE_KEY)
            );

        } else {
            throw new IllegalArgumentException(String.format(
                    "%s requires %d properties when parsing, got %d",
                    Entity.HOUSE_KEY,
                    Entity.HOUSE_PARSE_PROPERTY_COUNT,
                    properties.length
            ));
        }
    }

    private static Entity parseSapling(String[] properties, String id, Point position, ImageLibrary imageLibrary) {
        if (properties.length == Entity.SAPLING_PARSE_PROPERTY_COUNT) {

            // Modify this to use a constructor from your class hierarchy
            // Ensure the order of all passed arguments match the desired parameters
            return Entity.createSapling(
                    id,
                    position,
                    imageLibrary.get(Entity.SAPLING_KEY)
            );

        } else {
            throw new IllegalArgumentException(String.format(
                    "%s requires %d properties when parsing, got %d",
                    Entity.SAPLING_KEY,
                    Entity.SAPLING_PARSE_PROPERTY_COUNT,
                    properties.length
            ));
        }
    }

    private static Entity parseStump(String[] properties, Point pt, String id, ImageLibrary imageLibrary) {
        if (properties.length == Entity.STUMP_PARSE_PROPERTY_COUNT) {

            // Modify this to use a constructor from your class hierarchy
            // Ensure the order of all passed arguments match the desired parameters
            return Entity.createStump(
                    id,
                    pt,
                    imageLibrary.get(Entity.STUMP_KEY)
            );

        } else {
            throw new IllegalArgumentException(String.format(
                    "%s requires %d properties when parsing, got %d",
                    Entity.STUMP_KEY,
                    Entity.STUMP_PARSE_PROPERTY_COUNT,
                    properties.length
            ));
        }
    }

    private static Entity parseTree(String[] properties, String id, Point position, ImageLibrary imageLibrary) {
        if (properties.length == Entity.TREE_PARSE_PROPERTY_COUNT) {

            // Modify to use a constructor from your class hierarchy
            // Ensure the order of all passed arguments match the desired parameters
            return Entity.createTree(
                    id,
                    position,
                    imageLibrary.get(Entity.TREE_KEY),
                    Double.parseDouble(properties[Entity.TREE_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX]),
                    Double.parseDouble(properties[Entity.TREE_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX]),
                    Integer.parseInt(properties[Entity.TREE_PARSE_PROPERTY_HEALTH_INDEX])
            );

        } else {
            throw new IllegalArgumentException(String.format(
                    "%s requires %d properties when parsing, got %d",
                    Entity.TREE_KEY,
                    Entity.TREE_PARSE_PROPERTY_COUNT,
                    properties.length
            ));
        }
    }

    private static Entity parseWater(String[] properties, String id, Point position, ImageLibrary imageLibrary) {
        if (properties.length == Entity.WATER_PARSE_PROPERTY_COUNT) {

            // Modify to use a constructor from your class hierarchy
            // Ensure the order of all passed arguments match the desired parameters
            return Entity.createWater(
                    id,
                    position,
                    imageLibrary.get(Entity.WATER_KEY)
            );

        } else {
            throw new IllegalArgumentException(String.format(
                    "%s requires %d properties when parsing, got %d",
                    Entity.WATER_KEY,
                    Entity.WATER_PARSE_PROPERTY_COUNT,
                    properties.length
            ));
        }
    }
}
