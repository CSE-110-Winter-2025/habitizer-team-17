package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import edu.ucsd.cse110.observables.MutableSubject;
import edu.ucsd.cse110.observables.Observer;
import edu.ucsd.cse110.observables.PlainMutableSubject;

public class LiveDataSubjectAdapter<T> implements MutableSubject<T> {
    private final PlainMutableSubject<T> subject = new PlainMutableSubject<>();

    public LiveDataSubjectAdapter(LiveData<T> liveData) {
        // Observe LiveData forever. Every time it changes, update the subject.
        liveData.observeForever(value -> subject.setValue(value));
    }

    // --- Subject<T> and MutableSubject<T> methods ---

    @Override
    @Nullable
    public T getValue() {
        return subject.getValue();
    }

    @Override
    public boolean hasObservers() {
        return subject.hasObservers();
    }

    @Override
    public boolean isInitialized() {
        return subject.isInitialized();
    }

    @Override
    public Observer<T> observe(Observer<T> observer) {
        return subject.observe(observer);
    }

    @Override
    public void removeObserver(Observer<T> observer) {
        subject.removeObserver(observer);
    }

    @Override
    public void removeObservers() {
        subject.removeObservers();
    }

    @Override
    public java.util.List<Observer<T>> getObservers() {
        return subject.getObservers();
    }

    @Override
    public void setValue(T newValue) {
        // This lets external code also set the subject’s value if needed.
        subject.setValue(newValue);
    }
}
