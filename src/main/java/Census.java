import exceptionHandler.CensusException;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Implement the two methods below. We expect this class to be stateless and thread safe.
 */
public class Census {
    /**
     * Number of cores in the current machine.
     */
    private static final int CORES = Runtime.getRuntime().availableProcessors();

    /**
     * Output format expected by our tests.
     */
    public static final String OUTPUT_FORMAT = "%d:%d=%d"; // Position:Age=Total
    /**
     * Factory for iterators.
     */
    private final Function<String, Census.AgeInputIterator> iteratorFactory;

    /**
     * Creates a new Census calculator.
     *
     * @param iteratorFactory factory for the iterators.
     */
    public Census(Function<String, Census.AgeInputIterator> iteratorFactory) {
        this.iteratorFactory = iteratorFactory;
    }

    /**
     * Given one region name, call {@link #iteratorFactory} to get an iterator for this region and return
     * the 3 most common ages in the format specified by {@link #OUTPUT_FORMAT}.
     */
    public String[] top3Ages(String region) {
        Map<Integer,Integer> ageCountMap= new ConcurrentHashMap<>();

        if(region != null && !region.isEmpty()){
            addingRegionAgeAndCountMapToValues(region,ageCountMap);
            return top3AgeOutputCreate.apply(ageCountMap);
        }
        return new String[]{};
    }

    /**
     * Given a list of region names, call {@link #iteratorFactory} to get an iterator for each region and return
     * the 3 most common ages across all regions in the format specified by {@link #OUTPUT_FORMAT}.
     * We expect you to make use of all cores in the machine, specified by {@link #CORES).
     */
    public String[] top3Ages(List<String> regionNames) {

        if(regionNames!=null && !regionNames.isEmpty()){
            Map<Integer,Integer> ageCountMap= new ConcurrentHashMap <>();

//            regionNames.forEach(region->{
//
//                if(region != null && !region.isEmpty() && !region.equalsIgnoreCase("empty") && !region.equalsIgnoreCase("invalid") ){
//                        addingRegionAgeAndCountMapToValues(region,ageCountMap);
//                    }
//            });
            regionNames.parallelStream()
                    .filter(region->region != null && !region.isEmpty() && !region.equalsIgnoreCase("empty") && !region.equalsIgnoreCase("invalid") )
                    .forEach( region -> addingRegionAgeAndCountMapToValues(region,ageCountMap));


            return ageCountMap.isEmpty()? new String[]{}:top3AgeOutputCreate.apply(ageCountMap);
        }

        throw new UnsupportedOperationException();
    }


    /**
     * Implementations of this interface will return ages on call to {@link Iterator#next()}. They may open resources
     * when being instantiated created.
     */
    public interface AgeInputIterator extends Iterator<Integer>, Closeable {
    }


    /**
     * Updates the ageCountMap with age counts from the specified region.
     * @param region the region to process
     * @param ageCountMap a map to store age counts
    * */
    private void addingRegionAgeAndCountMapToValues(String region, Map<Integer, Integer> ageCountMap) {

        try (AgeInputIterator ageInputIterator = iteratorFactory.apply(region)) {
            ageInputIterator.forEachRemaining(age ->{
                    if(age>=0){
                        ageCountMap.merge(age,1, Integer::sum);
                    }
            });
        } catch (IOException e) {
            throw new CensusException("Iterator hasn't been closed.");

        }
        // I newly add without this ones topThreeAgeCounter keep counting
        finally {
            topThreeAgeCounter=new AtomicInteger(1);
        }
    }


    /**
     * Counter to keep track of the position
     * **/
    private AtomicInteger topThreeAgeCounter= new AtomicInteger(1);

    /**
     * Processes the ageCountMap to find the top 3 most common ages.
     * The map is sorted in descending order of the counts (values).
     * The first 3 entries are formatted according to OUTPUT_FORMAT,
     * which includes their position, age, and count, and returned as a String array.
     */
    private final Function<Map<Integer,Integer>,String[]> top3AgeOutputCreate = ageCountMap->ageCountMap.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())).limit(3)
            .map(entry-> String.format(OUTPUT_FORMAT, topThreeAgeCounter.getAndIncrement(), entry.getKey(), entry.getValue())) .toArray(String[]::new);

}
