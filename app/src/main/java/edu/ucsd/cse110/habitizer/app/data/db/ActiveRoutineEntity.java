package edu.ucsd.cse110.habitizer.app.data.db;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.ucsd.cse110.habitizer.lib.domain.ActiveRoutine;
import edu.ucsd.cse110.habitizer.lib.domain.ActiveTask;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

@Entity(tableName = "active_routines")
public class ActiveRoutineEntity {
    private static final String TAG = "ActiveRoutineEntity";

    @PrimaryKey
    @ColumnInfo(name = "routine_id")
    public Integer routineId;
    @ColumnInfo(name = "previous_task_end_time")
    public long previousTaskEndTime;

    @ColumnInfo(name = "active_tasks_json")
    public String activeTasksJson;

    ActiveRoutineEntity(Integer routineId, long previousTaskEndTime, String activeTasksJson) {
        this.routineId = routineId;
        this.previousTaskEndTime = previousTaskEndTime;
        this.activeTasksJson = activeTasksJson;
    }

    public static ActiveRoutineEntity fromActiveRoutine(ActiveRoutine activeRoutine) {
        String activeTasksJson = activeTasksToJson(activeRoutine.activeTasks());
        return new ActiveRoutineEntity(activeRoutine.routine().id(),
                activeRoutine.previousTaskEndTime(),
                activeTasksJson);
    }

    public @NonNull ActiveRoutine toActiveRoutine(Routine routine) {
        var activeTasks = activeTasksFromJson(activeTasksJson);
        return new ActiveRoutine(routine, activeTasks, previousTaskEndTime);
    }

    private static String activeTasksToJson(List<ActiveTask> activeTasks) {
        JSONArray jsonArray = new JSONArray();
        for (ActiveTask activeTask : activeTasks) {
            try {
                JSONObject jsonObject = new JSONObject();
                JSONObject taskObject = new JSONObject();
                if (activeTask.task().id() != null) {
                    taskObject.put("id", activeTask.task().id());
                } else {
                    taskObject.put("id", JSONObject.NULL);
                }
                taskObject.put("name", activeTask.task().name());
                jsonObject.put("task", taskObject);
                jsonObject.put("checked", activeTask.checked());
                jsonObject.put("checkedElapsedTime", activeTask.checkedElapsedTime());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                Log.e(TAG, "Error creating JSON for task: " + e.getMessage());
            }
        }
        return jsonArray.toString();
    }

    private static List<ActiveTask> activeTasksFromJson(String json) {
        List<ActiveTask> activeTasks = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject taskObject = jsonObject.getJSONObject("task");
                Integer id = null;
                if (taskObject.has("id") && !taskObject.isNull("id")) {
                    id = taskObject.getInt("id");
                }
                String name = taskObject.getString("name");
                Task task = new Task(id, name);
                boolean checked = jsonObject.getBoolean("checked");
                long checkedElapsedTime = jsonObject.getLong("checkedElapsedTime");
                activeTasks.add(new ActiveTask(task, checked, checkedElapsedTime));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON to tasks: " + e.getMessage(), e);
        }
        return activeTasks;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ActiveRoutineEntity that = (ActiveRoutineEntity) o;
        return previousTaskEndTime == that.previousTaskEndTime && Objects.equals(routineId,
                that.routineId) && Objects.equals(activeTasksJson, that.activeTasksJson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(routineId, previousTaskEndTime, activeTasksJson);
    }
}
