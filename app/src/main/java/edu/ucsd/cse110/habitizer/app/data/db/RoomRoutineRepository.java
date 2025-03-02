package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineRepository;
import edu.ucsd.cse110.observables.MutableSubject;

public class RoomRoutineRepository implements RoutineRepository {
    private final RoutineDao routineDao;

    public RoomRoutineRepository(RoutineDao routineDao) {
        this.routineDao = routineDao;
    }

    @Override
    public MutableSubject<Routine> find(int id) {
        LiveData<RoutineEntity> entityLiveData = routineDao.findById(id);
        LiveData<Routine> routineLiveData = Transformations.map(entityLiveData, entity -> {
            return (entity == null) ? null : entity.toDomain();
        });
        return new LiveDataSubjectAdapter<>(routineLiveData);
    }

    @Override
    public MutableSubject<List<Routine>> findAll() {
        LiveData<List<RoutineEntity>> entitiesLiveData = routineDao.findAll();
        LiveData<List<Routine>> routinesLiveData = Transformations.map(entitiesLiveData, entities ->
                entities.stream()
                        .map(RoutineEntity::toDomain)
                        .collect(Collectors.toList())
        );
        return new LiveDataSubjectAdapter<>(routinesLiveData);
    }

    @Override
    public void save(Routine routine) {
        routineDao.insert(RoutineEntity.fromDomain(routine));
    }
}
