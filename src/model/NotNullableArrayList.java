package processors.ingestor.model;

import java.util.ArrayList;
import java.util.Objects;

public class NotNullableArrayList<E> extends ArrayList<E> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean add(E e) {
        if ((Objects.isNull(e))) {
            return false;
        }
        return super.add(e);
    }

}
