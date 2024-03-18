import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy {

    /**
     * Return a list containing a single point representing the next step toward a goal
     * If the start is within reach of the goal, the returned list is empty.
     *
     * @param start the point to begin the search from
     * @param end the point to search for a point within reach of
     * @param canPassThrough a function that returns true if the given point is traversable
     * @param withinReach a function that returns true if both points are within reach of each other
     * @param potentialNeighbors a function that returns the neighbors of a given point, as a stream
     */
    public List<Point> computePath(
            Point start,
            Point end,
            Predicate<Point> canPassThrough,
            BiPredicate<Point, Point> withinReach,
            Function<Point, Stream<Point>> potentialNeighbors
    ) {
        if (withinReach.test(start, end)) {
            return new ArrayList<>();
        }

        List<Point> openset = new ArrayList<>();
        List<Point> closedset =  new ArrayList<>();
        Map<Point, Point> camefrom = new HashMap<>();
        Map<Point, Integer> gmap = new HashMap<>();
        Map<Point, Integer> hmap = new HashMap<>();
        Map<Point, Integer> fmap = new HashMap<>();

        Point currentPoint = start;
        camefrom.put(start, null);
        gmap.put(start, 0);
        fmap.put(start, start.manhattanDistanceTo(end));

        while (!withinReach.test(currentPoint, end)) {

            List<Point> opensetNeighbors = potentialNeighbors.apply(currentPoint)
                    .filter(canPassThrough)
                    .filter(x -> !closedset.contains(x))
                    .toList();
            //put neighbors into gmap and fmap
            for (Point point : opensetNeighbors) {
                int newgscore = gmap.get(currentPoint) + 1;
                if (gmap.containsKey(point)) {
                    if (newgscore < gmap.get(point)) {
                        camefrom.put(point, currentPoint);
                        gmap.put(point, newgscore);
                        fmap.put(point, newgscore + point.manhattanDistanceTo(end));
                    }
                } else {
                    camefrom.put(point, currentPoint);
                    gmap.put(point, newgscore);
                    fmap.put(point, newgscore + point.manhattanDistanceTo(end));
                    openset.add(point);
                }
            }
            closedset.add(currentPoint);
            openset.remove(currentPoint);
            if (openset.isEmpty()){
                return openset;
            }
            Point lowest = openset.getFirst();
            for (Point p : openset){
                if (fmap.get(p) < fmap.get(lowest)){
                    lowest = p;
                }
            }
            currentPoint = lowest;

        }
        List<Point> path = new ArrayList<>();
        while (currentPoint != start){
            path.add(0, currentPoint);
            currentPoint = camefrom.get(currentPoint);
        }
        return path;
    }
}
