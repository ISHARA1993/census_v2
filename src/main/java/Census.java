import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
//        In the example below, the top three are ages 10, 15 and 12
//        return new String[]{
//                String.format(OUTPUT_FORMAT, 1, 10, 38),
//                String.format(OUTPUT_FORMAT, 2, 15, 35),
//                String.format(OUTPUT_FORMAT, 3, 12, 30)
//        };

        //ageCountMap key age and Value count of age
        Map<Integer,Integer> ageCountMap= new HashMap<>();
        int count=0;
        if(region != null && !region.isEmpty()){
            if(region.matches(OUTPUT_FORMAT)){
                try (AgeInputIterator ageInputIterator = iteratorFactory.apply(region)) {
                    ageInputIterator.forEachRemaining(age -> ageCountMap.merge(age,1,(countOne,countTwo)->(countOne+countTwo)));


                } catch (IOException e) {
                    throw new RuntimeException("Iterator hasn't been closed."+e);
                }

                //ageCountMap sorted descending order , set get first 3 convert to OUTPUT_FORMAT
                 return ageCountMap.entrySet().stream()
                         .sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue())).limit(3)
                                 .map(entry-> String.format(OUTPUT_FORMAT, count+1, entry.getKey(), entry.getValue())) .toArray(String[]::new);

            }
            else{
                //wrong region format Exception
                return new String[]{"1:1=1"};
            }


        }
        return new String[]{};


        //throw new UnsupportedOperationException();
    }

    /**
     * Given a list of region names, call {@link #iteratorFactory} to get an iterator for each region and return
     * the 3 most common ages across all regions in the format specified by {@link #OUTPUT_FORMAT}.
     * We expect you to make use of all cores in the machine, specified by {@link #CORES).
     */
    public String[] top3Ages(List<String> regionNames) {

//        In the example below, the top three are ages 10, 15 and 12
//        return new String[]{
//                String.format(OUTPUT_FORMAT, 1, 10, 38),
//                String.format(OUTPUT_FORMAT, 2, 15, 35),
//                String.format(OUTPUT_FORMAT, 3, 12, 30)
//        };

        throw new UnsupportedOperationException();
    }


    /**
     * Implementations of this interface will return ages on call to {@link Iterator#next()}. They may open resources
     * when being instantiated created.
     */
    public interface AgeInputIterator extends Iterator<Integer>, Closeable {
    }
}
