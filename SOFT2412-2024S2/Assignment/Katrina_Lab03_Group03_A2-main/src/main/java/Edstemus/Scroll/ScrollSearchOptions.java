package Edstemus.Scroll;

import Edstemus.GUI.SearchField;

import java.util.HashMap;
import java.util.Map;

public class ScrollSearchOptions {
    private Map<SearchField, Object> criteria = new HashMap<>();

    public void addCriteria(SearchField field, Object value) {
        criteria.put(field, value);
    }

    public Object getCriteria(SearchField field) {
        return criteria.get(field);
    }

    public boolean hasCriteria(SearchField field) {
        return criteria.containsKey(field);
    }

    public Map<SearchField, Object> getAllCriteria() {
        return criteria;
    }
}
