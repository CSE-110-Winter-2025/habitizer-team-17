package edu.ucsd.cse110.observables;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class PlainMutableSubject<T> implements MutableSubject<T> {
    private final AtomicReference<Optional<T>> value = new AtomicReference<>(Optional.empty());
    private final ConcurrentLinkedQueue<Observer<T>> observers = new ConcurrentLinkedQueue<>();

    public PlainMutableSubject() {
    }

    public PlainMutableSubject(T initialValue) {
        this.value.getAndSet(Optional.of(initialValue));
    }

    protected void notifyObservers() {
        T newValue = value.get().orElse(null);
        observers.forEach(observer -> observer.onChanged(newValue));
    }

    @Override
    @Nullable
    public T getValue() {
        return value.get().orElse(null);
    }

  @Override
    public void setValue(T newValue) {
        value.set(Optional.ofNullable(newValue));
        notifyObservers();
    }



    @Override
    public boolean hasObservers() {
        return !observers.isEmpty();
    }

    @Override
    public boolean isInitialized() {
        return value.get().isPresent();
    }

    @Override
    public Observer<T> observe(@NonNull Observer<T> observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            if (isInitialized()) observer.onChanged(getValue());
        }
        return observer;
    }

    @Override
    public void removeObserver(@NonNull Observer<T> observer) {
        observers.removeIf(o -> o.equals(observer));
    }

    @Override
    public void removeObservers() {
        observers.clear();
    }

    @Override
    @VisibleForTesting
    public List<Observer<T>> getObservers() {
        return List.copyOf(observers);
    }
}
