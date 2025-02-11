package edu.ucsd.cse110.habitizer.lib.domain;

import java.util.List;

import edu.ucsd.cse110.habitizer.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.observables.MutableSubject;

public class ActiveTaskRepository {
    private final InMemoryDataSource dataSource;

    public ActiveTaskRepository(InMemoryDataSource dataSource){
        this.dataSource = dataSource;
    }

    public Integer count() {
        return dataSource.getActiveTasks().size();
    }

    public MutableSubject<ActiveTask> find (int id) {

        return dataSource.getActiveTaskSubject(id);
    }

    public void save(ActiveTask activeTask) {

        dataSource.putActiveTask(activeTask);
    }

}
