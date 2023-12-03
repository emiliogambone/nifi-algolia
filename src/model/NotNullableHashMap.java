package processors.ingestor.model;

import java.lang.reflect.Field;
import java.util.HashMap;

public class NotNullableHashMap<K, V> extends HashMap<K, V> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    @Override
    public V put(K key, V value) {
        return value != null && !isFullOfNulls(value) ? super.put(key, value) : null;
    }

    public boolean isFullOfNulls(Object o) {
        boolean allNull = true;
        try {
            for (Field f : o.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                allNull &= f.get(o) == null;
            }
        } catch (IllegalAccessException ignored) {
            return true;
        }

        return allNull;
    }

}
