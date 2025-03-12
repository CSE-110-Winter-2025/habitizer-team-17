package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.lifecycle.Transformations;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import edu.ucsd.cse110.habitizer.lib.domain.CustomTimer;
import edu.ucsd.cse110.habitizer.lib.domain.CustomTimerRepository;
import edu.ucsd.cse110.observables.MutableSubject;

public class RoomCustomTimerRepository implements CustomTimerRepository {
    CustomTimerDao customTimerDao;

    private final Executor executor = Executors.newSingleThreadExecutor();

    public RoomCustomTimerRepository(CustomTimerDao customTimerDao) {
        this.customTimerDao = customTimerDao;
    }

    @Override
    public MutableSubject<CustomTimer> find() {
        var customTimerEntityAsLiveData = customTimerDao.findAsLiveData();
        var customTimerAsLiveData = Transformations.map(customTimerEntityAsLiveData,
                customTimerEntity -> {
                    if (customTimerEntity == null) return null;
                    return customTimerEntity.toCustomTimer();
                });
        return new LiveDataSubjectAdapter<>(customTimerAsLiveData);
    }

    @Override
    public void save(CustomTimer customTimer) {
        executor.execute(() -> customTimerDao.replace(CustomTimerEntity.fromCustomTimer(customTimer)));
    }

    @Override
    public void delete() {
        executor.execute(() -> customTimerDao.delete());
    }
}
