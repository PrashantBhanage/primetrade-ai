// Path: /home/prrrssshhh/PROJECTS/primetrade.AI/src/main/java/com/primetrade/api/service/TaskService.java
package com.primetrade.api.service;

import com.primetrade.api.dto.TaskRequest;
import com.primetrade.api.exception.ResourceNotFoundException;
import com.primetrade.api.model.Task;
import com.primetrade.api.model.User;
import com.primetrade.api.repository.TaskRepository;
import com.primetrade.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public List<Task> getAllTasks(String loggedInEmail) {
        User loggedInUser = findUserByEmail(loggedInEmail);

        if (loggedInUser.getRole() == User.Role.ADMIN) {
            return taskRepository.findAllByOrderByCreatedAtDesc();
        }

        return taskRepository.findTasksForUserOrdered(loggedInUser.getId());
    }

    public Task createTask(String loggedInEmail, TaskRequest request) {
        User loggedInUser = findUserByEmail(loggedInEmail);

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setUser(loggedInUser);

        if (request.getStatus() == null) {
            // doing this here instead of in model to keep it explicit
            task.setStatus(Task.Status.TODO);
        } else {
            task.setStatus(request.getStatus());
        }

        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, String loggedInEmail, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        User loggedInUser = findUserByEmail(loggedInEmail);
        ensureCanManageTask(loggedInUser, task);

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            task.setTitle(request.getTitle().trim());
        }

        task.setDescription(request.getDescription());

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId, String loggedInEmail) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        User loggedInUser = findUserByEmail(loggedInEmail);
        ensureCanManageTask(loggedInUser, task);

        taskRepository.delete(task);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private void ensureCanManageTask(User loggedInUser, Task task) {
        if (loggedInUser == null || task == null || task.getUser() == null) {
            throw new AccessDeniedException("You are not allowed to perform this action");
        }

        boolean isAdmin = loggedInUser.getRole() == User.Role.ADMIN;
        boolean isOwner = task.getUser().getId().equals(loggedInUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to modify this task");
        }
    }
}
