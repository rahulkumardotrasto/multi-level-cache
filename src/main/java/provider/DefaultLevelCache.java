package provider;

import lombok.NonNull;
import lombok.AllArgsConstructor;

import model.LevelCacheData;
import model.ReadResponse;
import model.WriteResponse;

import  java.util.Collections;
import  java.util.List;

@AllArgsConstructor
public class DefaultLevelCache<Key, Value> implements ILevelCache<Key, Value> {
    private final LevelCacheData levelCachaData;
    private final CacheProvider<Key, Value> cacheProvider;

    @NonNull
    private final ILevelCache<Key, Value> next;

    @NonNull
    public WriteResponse set(Key key, Value value) {
        Double currTime = 0.0;
        Value curLevelValue = cacheProvider.get(key);
        currTime += levelCachaData.getReadTime();
        if (!value.equals(curLevelValue)) {
            cacheProvider.set(key, value);
            currTime += levelCachaData.getWriteTime();
        }

        currTime += next.set(key, value).getTimeTaken();
        return new WriteResponse(currTime);
    }

    @NonNull
    public ReadResponse<Value> get(Key key) {
        Double curTime = 0.0;
        Value curLevelValue = cacheProvider.get(key);
        curTime += levelCachaData.getReadTime();

        if(curLevelValue == null){
            ReadResponse<Value> nextResponse = next.get(key);
            curTime += nextResponse.getTotalTime();
            curLevelValue = nextResponse.getValue();
            if (curLevelValue != null){
                cacheProvider.set(key, curLevelValue);
                curTime += levelCachaData.getWriteTime();
            }
        }

        return new ReadResponse<>(curLevelValue, curTime);
    }

    @NonNull
    public List<Double> getUsages() {
        final List<Double>  usages;
        if(next == null){
            usages = Collections.emptyList();
        } else {
            usages = next.getUsages();
        }

        usages.add(0, cacheProvider.getCurrentUsage());
        return usages;
    }

}
