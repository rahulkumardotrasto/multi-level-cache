package policy;

public class LRUEvictionPolicy<Key> implements EvictionPolicy<Key> {
    public void keyAccessed(Object o){

    }

    public Key evictKey(){
        return null;
    }
}
