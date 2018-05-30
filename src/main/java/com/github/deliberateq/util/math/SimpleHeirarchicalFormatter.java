package com.github.deliberateq.util.math;

public interface SimpleHeirarchicalFormatter {
    
    void header(String s, boolean collapsed);

    void blockStart();

    void item(Object object);

    void link(String s, String id, Object object, String action);

    void image(String s, String id, Object object, String action);

    void blockFinish();

}
