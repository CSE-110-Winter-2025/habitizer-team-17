package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutine;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutineRepository;
import edu.ucsd.cse110.observables.MutableSubject;

public class RoomActiveRoutineRepository implements ActiveRoutineRepository {
    private final ActiveRoutineDao activeRoutineDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public RoomActiveRoutineRepository(ActiveRoutineDao activeRoutineDao) {
        this.activeRoutineDao = activeRoutineDao;
    }

    @Override
    public MutableSubject<ActiveRoutine> find() {
        var activeRoutineWithRoutineAsLiveData = activeRoutineDao.findAsLiveData();
        var activeRoutine = Transformations.map(activeRoutineWithRoutineAsLiveData,
                activeRoutineWithRoutine -> {
                    var firstEntry = activeRoutineWithRoutine.entrySet().stream().findFirst();
                    if (firstEntry.isEmpty()) {
                        return null;
                    }
                    var activeRoutineEntity = firstEntry.get().getKey();
                    var routineEntity = firstEntry.get().getValue();
                    return activeRoutineEntity.toActiveRoutine(routineEntity.toDomain());
                });
        return new LiveDataSubjectAdapter<>(activeRoutine);
    }

    @Override
    public void save(ActiveRoutine activeRoutine) {
        executor.execute(() -> activeRoutineDao.replace(ActiveRoutineEntity.fromActiveRoutine(activeRoutine)));
    }

    @Override
    public void delete() {
        executor.execute(activeRoutineDao::delete);
    }
}
