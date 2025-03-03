package edu.ucsd.cse110.habitizer.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

@Entity(tableName = "routines")
public class RoutineEntity {
    private static final String TAG = "RoutineEntity";

    @PrimaryKey(autoGenerate = true)
    public Integer id;

    @NonNull
    @ColumnInfo(name = "name")
    public String name;

    // Store tasks as a JSON string
    @NonNull
    @ColumnInfo(name = "tasks_json")
    public String tasksJson;

    @ColumnInfo(name = "goal_time")
    public int goalTime;

    @ColumnInfo(name = "sort_order")
    public int sortOrder;

    public RoutineEntity(@NonNull String name, @NonNull String tasksJson, int goalTime, int sortOrder) {
        this.name = name;
        this.tasksJson = tasksJson;
        this.goalTime = goalTime;
        this.sortOrder = sortOrder;
    }

    // Convert from domain Routine to RoutineEntity using org.json for conversion
    public static RoutineEntity fromDomain(@NonNull Routine routine) {
        String tasksJson = tasksToJson(routine.tasks());
        RoutineEntity entity = new RoutineEntity(routine.name(), tasksJson, routine.goalTime(), routine.sortOrder());
        entity.id = routine.id(); // Set ID or keep as null for new routines
        Log.d(TAG, "fromDomain: id=" + entity.id + ", name=" + entity.name);
        return entity;
    }

    // Convert this entity back to a domain Routine
    public Routine toDomain() {
        List<Task> tasks = tasksFromJson(tasksJson);
        Log.d(TAG, "toDomain: id=" + id + ", name=" + name + ", tasks=" + tasks.size());
        return new Routine(id, name, tasks, goalTime, sortOrder);
    }

    // Helper: Convert a list of Task objects to a JSON string.
    private static String tasksToJson(List<Task> tasks) {
        JSONArray jsonArray = new JSONArray();
        for (Task task : tasks) {
            try {
                JSONObject jsonObject = new JSONObject();
                if (task.id() != null) {
                    jsonObject.put("id", task.id());
                } else {
                    jsonObject.put("id", JSONObject.NULL);
                }
                jsonObject.put("name", task.name());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                Log.e("RoutineEntity", "Error creating JSON for task: " + e.getMessage());
            }
        }
        return jsonArray.toString();
    }

    // Helper: Convert a JSON string back into a list of Task objects.
    private static List<Task> tasksFromJson(String json) {
        List<Task> tasks = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Integer id = jsonObject.has("id") && !jsonObject.isNull("id")
                        ? jsonObject.getInt("id")
                        : null;
                String name = jsonObject.getString("name");
                tasks.add(new Task(id, name));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON to tasks: " + e.getMessage(), e);
        }
        return tasks;
    }

    // Add this public static method to RoutineEntity
    public static List<Task> fromJson(String json) throws Exception {
        List<Task> tasks = new ArrayList<>();
        org.json.JSONArray jsonArray = new org.json.JSONArray(json);

        for (int i = 0; i < jsonArray.length(); i++) {
            org.json.JSONObject obj = jsonArray.getJSONObject(i);
            Integer id = null;
            if (obj.has("id") && !obj.isNull("id")) {
                id = obj.getInt("id");
            }
            String name = obj.getString("name");
            tasks.add(new Task(id, name));
        }

        return tasks;
    }
}