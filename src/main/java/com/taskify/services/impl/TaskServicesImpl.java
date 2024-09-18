package com.taskify.services.impl;

import com.taskify.constants.ActionType;
import com.taskify.constants.PriorityType;
import com.taskify.constants.ResourceType;
import com.taskify.dtos.FunctionDto;
import com.taskify.dtos.TaskDto;
import com.taskify.exceptions.ResourceNotFoundException;
import com.taskify.externals.email.EmailServices;
import com.taskify.models.CustomerModel;
import com.taskify.models.TaskModel;
import com.taskify.models.TaskifyTimelineModel;
import com.taskify.models.UserModel;
import com.taskify.models.prototypes.TaskPrototypeModel;
import com.taskify.repositories.CustomerRepository;
import com.taskify.repositories.TaskRepository;
import com.taskify.repositories.TaskifyTimelineRepository;
import com.taskify.repositories.UserRepository;
import com.taskify.repositories.prototypes.TaskPrototypeRepository;
import com.taskify.services.FunctionServices;
import com.taskify.services.TaskServices;
import com.taskify.utils.MonthlyStats;
import com.taskify.utils.PageResponse;
import com.taskify.utils.TaskStats;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TaskServicesImpl implements TaskServices {

    private static final int PAGE_SIZE = 25;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskPrototypeRepository taskPrototypeRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private FunctionServices functionServices;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TaskifyTimelineRepository taskifyTimelineRepository;

    @Autowired
    private EmailServices emailServices;

    @Override
    public TaskDto createTask(TaskDto taskDto) {
        System.out.println("assigned to userid: " + taskDto.getAssignedToUserId());
        TaskModel taskModel = this.modelMapper.map(taskDto, TaskModel.class);
        taskModel.setCreatedDate(LocalDateTime.now());
        taskModel.setClosedByUser(null);
        taskModel.setClosed(false);
        taskModel.setClosedDate(null);

        // Check for the customer
        CustomerModel foundCustomer = this.customerRepository.findById(taskDto.getCustomerId()).orElseThrow(
                () -> new IllegalArgumentException("No customer exist for id: " + taskDto.getCustomerId()));

        taskModel.setCustomer(foundCustomer);

        // Retrieve the task_prototype
        TaskPrototypeModel foundTaskPrototype = this.taskPrototypeRepository.findById(taskDto.getTaskPrototypeId())
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "No task_prototype exist for id: " + taskDto.getTaskPrototypeId()));
        taskModel.setTaskPrototype(foundTaskPrototype);

        // Check for the created user
        UserModel foundCreatedByUser = this.userRepository.findById(taskDto.getCreatedByUserId()).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + taskDto.getCreatedByUserId()));
        taskModel.setCreatedByUser(foundCreatedByUser);

        // Check for the assigned user
        UserModel foundAssignedUser = this.userRepository.findById(taskDto.getAssignedToUserId()).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + taskDto.getAssignedToUserId()));
        taskModel.setAssignedToUser(foundAssignedUser);

        // Check for priority type
        if (!taskModel.getPriorityType().equalsIgnoreCase(PriorityType.NORMAL.name()) &&
                !taskModel.getPriorityType().equalsIgnoreCase(PriorityType.MEDIUM.name()) &&
                !taskModel.getPriorityType().equalsIgnoreCase(PriorityType.HIGH.name())) {
            throw new IllegalArgumentException("Invalid priority type...");
        }

        taskModel.setTaskAbbreviation(this.taskAbbreviation(foundTaskPrototype, taskModel));

        // Create the task
        taskModel = this.taskRepository.save(taskModel);

        // Log the action
        TaskifyTimelineModel taskifyTimelineModel = new TaskifyTimelineModel(
                null,
                foundCreatedByUser,
                ResourceType.TASK.name(),
                ActionType.CREATE.name(),
                taskModel.getCreatedDate(),
                taskModel.getTaskAbbreviation(),
                null,
                null,
                null);
        this.taskifyTimelineRepository.save(taskifyTimelineModel);

        // TODO: Notify the user
        String subject = "Task Assignment Notification from Taskify Software";
        String body = generateCreateTaskEmail(taskModel, taskModel.getAssignedToUser());
        this.emailServices.sendSimpleMessage(taskModel.getAssignedToUser().getEmail(), subject, body);

        return this.taskModelToDto(taskModel);
    }

    private String generateCreateTaskEmail(TaskModel taskModel, UserModel userModel) {
        String body = "\nDear " + userModel.getName() + ",\n\n" +
                "We hope this message finds you well. This is to inform you that you have been assigned a task via Taskify Software. The details of the task are as follows:\n\n"
                +
                "Task Type: " + taskModel.getTaskPrototype().getTaskType() + "\n" +
                "Task Priority: " + taskModel.getPriorityType() + "\n\n" +
                "You will be responsible for this task. Please feel free to reach out if you have any specific instructions or requirements for this task.\n\n"
                +
                "Looking forward to your guidance and support throughout the completion of this task.\n\n" +
                "Best regards,\n" +
                "Taskify Software\n";
        ;

        return body;
    }

    @Override
    public PageResponse<TaskDto> getAllTasks(int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<TaskModel> pageTask = this.taskRepository.findAll(pageable);

        List<TaskModel> taskModels = pageTask.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTask.getTotalPages(),
                pageTask.getTotalElements(),
                this.taskModelsToDtos(taskModels));
    }

    @Override
    public PageResponse<TaskDto> getTasksByPrototypeId(int pageNumber, Long taskPrototypeId) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        TaskPrototypeModel foundTaskPrototype = this.taskPrototypeRepository.findById(taskPrototypeId).orElseThrow(
                () -> new IllegalArgumentException("No task_prototype exist for id: " + taskPrototypeId));

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<TaskModel> pageTask = this.taskRepository.findByTaskPrototype(pageable, foundTaskPrototype);

        List<TaskModel> taskModels = pageTask.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTask.getTotalPages(),
                pageTask.getTotalElements(),
                this.taskModelsToDtos(taskModels));
    }

    @Override
    public TaskDto getTasksById(Long id) {
        TaskModel foundTask = this.taskRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No task exist for id: " + id));

        return this.taskModelToDto(foundTask);
    }

    @Override
    public PageResponse<TaskDto> getTasksByPriority(int pageNumber, String priorityType) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        // Check for priority type
        if (priorityType.equalsIgnoreCase(PriorityType.NORMAL.name()) &&
                priorityType.equalsIgnoreCase(PriorityType.MEDIUM.name()) &&
                priorityType.equalsIgnoreCase(PriorityType.HIGH.name())) {
            throw new IllegalArgumentException("Invalid priority type");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<TaskModel> pageTask = this.taskRepository.findByPriorityType(pageable, priorityType);

        List<TaskModel> taskModels = pageTask.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTask.getTotalPages(),
                pageTask.getTotalElements(),
                this.taskModelsToDtos(taskModels));
    }

    @Override
    public PageResponse<TaskDto> getTasksByCreatedUser(int pageNumber, Long createdByUserId) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        UserModel foundCreatedByUser = this.userRepository.findById(createdByUserId).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + createdByUserId));

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<TaskModel> pageTask = this.taskRepository.findByCreatedByUser(pageable, foundCreatedByUser);

        List<TaskModel> taskModels = pageTask.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTask.getTotalPages(),
                pageTask.getTotalElements(),
                this.taskModelsToDtos(taskModels));
    }

    @Override
    public PageResponse<TaskDto> getTasksByAssignedUser(int pageNumber, Long assignedByUserId) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        UserModel foundAssignedByUser = this.userRepository.findById(assignedByUserId).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + assignedByUserId));

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<TaskModel> pageTask = this.taskRepository.findByAssignedToUser(pageable, foundAssignedByUser);

        List<TaskModel> taskModels = pageTask.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTask.getTotalPages(),
                pageTask.getTotalElements(),
                this.taskModelsToDtos(taskModels));
    }

    @Override
    public PageResponse<TaskDto> getTasksByCreatedDate(int pageNumber, Date createdDate) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<TaskModel> pageTask = this.taskRepository.findByCreatedDate(pageable, createdDate);

        List<TaskModel> taskModels = pageTask.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTask.getTotalPages(),
                pageTask.getTotalElements(),
                this.taskModelsToDtos(taskModels));
    }

    @Override
    public PageResponse<TaskDto> getTasksByClosedDate(int pageNumber, Date closedDate) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<TaskModel> pageTask = this.taskRepository.findByClosedDate(pageable, closedDate);

        List<TaskModel> taskModels = pageTask.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTask.getTotalPages(),
                pageTask.getTotalElements(),
                this.taskModelsToDtos(taskModels));
    }

    @Override
    public PageResponse<TaskDto> getTasksByIsClosed(int pageNumber, boolean isClosed) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<TaskModel> pageTask = this.taskRepository.findByIsClosed(pageable, isClosed);

        List<TaskModel> taskModels = pageTask.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTask.getTotalPages(),
                pageTask.getTotalElements(),
                this.taskModelsToDtos(taskModels));
    }

    @Override
    public PageResponse<TaskDto> getTasksByClosedByUserId(int pageNumber, Long closedByUserId) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        UserModel foundClosedByUser = this.userRepository.findById(closedByUserId).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + closedByUserId));

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<TaskModel> pageTask = this.taskRepository.findByClosedByUser(pageable, foundClosedByUser);

        List<TaskModel> taskModels = pageTask.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTask.getTotalPages(),
                pageTask.getTotalElements(),
                this.taskModelsToDtos(taskModels));
    }

    @Override
    public TaskDto updateTask(TaskDto givenTaskDto, Long userId) {
        // System.out.println("\n\ngivenTaskDto: " + givenTaskDto);
        TaskModel foundTask = this.taskRepository.findById(givenTaskDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException("No task exist for id: " + givenTaskDto.getId()));

        UserModel assignedByUser = this.userRepository.findById(givenTaskDto.getAssignedToUserId()).orElseThrow(
                () -> new ResourceNotFoundException("No user exist for id: " + givenTaskDto.getAssignedToUserId()));

        UserModel foundUser = this.userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("No user exist for id: " + userId));

        // Check for priority type
        if (!givenTaskDto.getPriorityType().equalsIgnoreCase(PriorityType.NORMAL.name()) &&
                !givenTaskDto.getPriorityType().equalsIgnoreCase(PriorityType.MEDIUM.name()) &&
                !givenTaskDto.getPriorityType().equalsIgnoreCase(PriorityType.HIGH.name())) {
            throw new IllegalArgumentException("Invalid priority type...");
        }

        foundTask.setPriorityType(givenTaskDto.getPriorityType());

        TaskPrototypeModel foundTaskPrototypeModel = this.taskPrototypeRepository
                .findById(givenTaskDto.getTaskPrototypeId()).orElseThrow(
                        () -> new IllegalArgumentException(
                                "No task_prototype exist for id: " + givenTaskDto.getTaskPrototypeId()));

        foundTask.setTaskPrototype(foundTaskPrototypeModel);
        foundTask.setAssignedToUser(assignedByUser);

        foundTask.setTaskAbbreviation(this.taskAbbreviation(foundTaskPrototypeModel, foundTask));

        foundTask = this.taskRepository.save(foundTask);

        // Notift=y the use if got forwarded
        if (foundTask.getAssignedToUser().getId() != assignedByUser.getId()) {
            this.generateForwardTaskEmailBody(foundTask.getCreatedByUser(), foundTask, assignedByUser);
        }

        // Log the action
        TaskifyTimelineModel taskifyTimelineModel = new TaskifyTimelineModel(
                null,
                foundUser,
                ResourceType.TASK.name(),
                ActionType.UPDATE.name(),
                LocalDateTime.now(),
                foundTask.getTaskAbbreviation(),
                null,
                null,
                null);
        this.taskifyTimelineRepository.save(taskifyTimelineModel);

        return this.taskModelToDto(foundTask);
    }

    private String generateForwardTaskEmailBody(UserModel createdUser, TaskModel taskModel, UserModel assignedUser) {
        String body = "Dear " + assignedUser.getName() + ",\n\n" +
                "We hope this message finds you well.\n\n" +
                "We are pleased to inform you that a task has been forwarded to you on Taskify. Below are the details of the task and function:\n\n"
                +
                "Task Type     : " + taskModel.getTaskPrototype().getTaskType() + "\n" +
                "Task Priorrity: " + taskModel.getPriorityType() + "\n" +
                "Created By    : " + taskModel.getCreatedByUser().getName() + "\n\n" +
                "Please log in to your Taskify account to review the task details and manage your workflow. Should you have any questions or require further information, feel free to reach out.\n\n"
                +
                "Thank you for your attention and cooperation.\n\n" +
                "Best regards,\n\n" +
                "Taskify Software";

        return body;
    }

    @Override
    public TaskDto getTaskByAbbreviation(String taskAbbreviation) {
        TaskModel taskModel = this.taskRepository.findByTaskAbbreviation(taskAbbreviation).orElseThrow(
                () -> new ResourceNotFoundException("No task exist for task_abbreviation: " + taskAbbreviation));

        return this.taskModelToDto(taskModel);
    }

    @Override
    public TaskDto closeTask(TaskDto givenTaskDto) {
        TaskModel foundTask = this.taskRepository.findById(givenTaskDto.getId()).orElseThrow(
                () -> new ResourceNotFoundException("No task exist for id: " + givenTaskDto.getId()));

        // Check whether the task is eligible for closing
        List<FunctionDto> functionDtos = this.functionServices.getFunctionsByTaskId(foundTask.getId());
        for (FunctionDto functionDto : functionDtos) {
            if (!functionDto.getIsClosed()) {
                throw new IllegalArgumentException(
                        "The given task can't be closed. Please close all the functions first inorder to close the task!");
            }
        }

        UserModel closedByUser = this.userRepository.findById(givenTaskDto.getClosedByUserId()).orElseThrow(
                () -> new IllegalArgumentException("No user exist for id: " + givenTaskDto.getClosedByUserId()));

        // Close the task
        foundTask.setClosed(true);
        foundTask.setClosedDate(LocalDateTime.now());
        foundTask.setClosedByUser(closedByUser);

        // Save the changes
        this.taskRepository.save(foundTask);

        // Log the action
        TaskifyTimelineModel taskifyTimelineModel = new TaskifyTimelineModel(
                null,
                closedByUser,
                ResourceType.TASK.name(),
                ActionType.CLOSED.name(),
                foundTask.getClosedDate(),
                foundTask.getTaskAbbreviation(),
                null,
                null,
                null);
        this.taskifyTimelineRepository.save(taskifyTimelineModel);

        // Notify the user
        this.generateCloseTaskEmailBody(foundTask, closedByUser);

        return this.taskModelToDto(foundTask);
    }

    private String generateCloseTaskEmailBody(TaskModel taskModel, UserModel closedByUser) {
        StringBuilder body = new StringBuilder();
        body.append("Dear Team,\n\n")
                .append("We would like to inform you that the following task has been successfully closed:\n\n")
                .append("Task Type     : ").append(taskModel.getTaskPrototype().getTaskType()).append("\n")
                .append("Task Priority : ").append(taskModel.getPriorityType()).append("\n")
                .append("Closed By     : ").append(closedByUser.getName()).append("\n")
                .append("Closed Date   : ").append(taskModel.getClosedDate()).append("\n\n")
                .append("Please review any related tasks or follow-ups in Taskify. If you have any questions or need further clarification, feel free to reach out.\n\n")
                .append("Thank you for your attention.\n\n")
                .append("Best regards,\n\n")
                .append("Taskify Software");

        return body.toString();
    }

    @Override
    public List<TaskDto> getTasksByCustomer(Long customerId) {
        CustomerModel foundCustomer = this.customerRepository.findById(customerId).orElseThrow(
                () -> new IllegalArgumentException("No customer exist for id: " + customerId));

        List<TaskModel> taskModels = this.taskRepository.findByCustomer(foundCustomer);

        return this.taskModelsToDtos(taskModels);
    }

    @Override
    public boolean deleteTask(Long id, Long userId) {
        TaskDto foundTask = this.getTasksById(id);

        UserModel foundUser = this.userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("No user exist for id: " + userId));

        // Delete the functions
        for (FunctionDto functionDto : foundTask.getFunctions()) {
            this.functionServices.deleteFunction(functionDto.getId(), userId);
        }

        // Delete the task
        this.taskRepository.deleteById(id);

        // Log the action
        TaskifyTimelineModel taskifyTimelineModel = new TaskifyTimelineModel(
                null,
                foundUser,
                ResourceType.TASK.name(),
                ActionType.DELETE.name(),
                LocalDateTime.now(),
                foundTask.getTaskAbbreviation(),
                null,
                null,
                null);
        this.taskifyTimelineRepository.save(taskifyTimelineModel);

        // TODO: Notify the user

        return true; // Success!
    }

    private TaskDto taskModelToDto(TaskModel taskModel) {
        if (taskModel == null) {
            return null;
        }

        TaskDto taskDto = this.modelMapper.map(taskModel, TaskDto.class);
        taskDto.setTaskPrototypeId(taskModel.getTaskPrototype().getId());
        taskDto.setCreatedByUserId(taskModel.getCreatedByUser().getId());
        taskDto.setIsClosed(taskModel.isClosed());
        if (taskModel.getAssignedToUser() != null) {
            taskDto.setAssignedToUserId(taskModel.getAssignedToUser().getId());
        }
        if (taskModel.getClosedByUser() != null) {
            taskDto.setClosedByUserId(taskModel.getClosedByUser().getId());
        }
        taskDto.setCustomerId(taskModel.getCustomer().getId());

        taskDto.setFunctions(this.functionServices.getFunctionsByTaskId(taskDto.getId()));

        return taskDto;
    }

    private List<TaskDto> taskModelsToDtos(List<TaskModel> taskModels) {
        List<TaskDto> taskDtos = new ArrayList<>();
        if (taskModels != null) {
            for (TaskModel taskModel : taskModels) {
                taskDtos.add(this.taskModelToDto(taskModel));
            }
        }

        return taskDtos;
    }

    @Override
    public Long getTotalTasksByYearAndMonth(int year, int month) {
        List<TaskModel> taskModels = this.taskRepository.findTasksByYearAndMonth(year, month);

        return (long) taskModels.size();
    }

    @Override
    public TaskStats totalStats() {
        TaskStats taskStats = new TaskStats();
        taskStats.setCustomers(this.customerRepository.count());

        taskStats.setTasks(this.taskRepository.count());

        PageResponse<TaskDto> tPageResponseN = this.getTasksByPrototypeId(1, 2L);
        Long newPumpTask = tPageResponseN.getTotalRecords();
        taskStats.setNewPumpTask(newPumpTask);

        taskStats.setOverdueTasks(this.getOverdueTasks(1).getTotalRecords());

        PageResponse<TaskDto> tPageResponseS = this.getTasksByPrototypeId(1, 3L);
        Long serviceTask = tPageResponseN.getTotalRecords();
        taskStats.setServiceTask(serviceTask);

        return taskStats;
    }

    @Override
    public MonthlyStats monthlyStats() {
        MonthlyStats monthlyStats = new MonthlyStats();

        LocalDate currentDate = LocalDate.now(); // Get current date
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();

        System.out.println("year: " + year + ", month: " + month);

        List<TaskModel> taskModels = this.taskRepository.findTasksByYearAndMonth(year, month);

        monthlyStats.setTasks((long) taskModels.size());
        System.out.println("monthlyStats.getTasks(): " + monthlyStats.getTasks());

        List<TaskModel> highPriorityTasks = taskModels.stream()
                .filter(t -> t.getPriorityType().equals(PriorityType.HIGH.name())).toList();

        monthlyStats.setHighPriority((long) highPriorityTasks.size());

        List<TaskModel> pendingTasks = taskModels.stream()
                .filter(t -> t.isClosed() == false).toList();
        monthlyStats.setPending((long) pendingTasks.size());


        List<TaskModel> completedTasks = taskModels.stream()
                .filter(t -> t.isClosed() == true).toList();
        monthlyStats.setCompleted((long) completedTasks.size());

        return monthlyStats;
    }

    @Override
    public PageResponse<TaskDto> getOverdueTasks(int pageNumber) {
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page should always be greater than 0.");
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "id"));

        Page<TaskModel> pageTask = this.taskRepository.findTasksWithDueFunctionModelsBeforeNowAndNotClosed(pageable);

        List<TaskModel> taskModels = pageTask.getContent();

        return new PageResponse<>(
                pageNumber,
                PAGE_SIZE,
                pageTask.getTotalPages(),
                pageTask.getTotalElements(),
                this.taskModelsToDtos(taskModels));
    }

    private String taskAbbreviation(TaskPrototypeModel taskPrototypeModel, TaskModel taskModel) {
        // Get the first character of the task type
        String taskTypeFirstCharacter = taskPrototypeModel.getTaskType().substring(0, 1);

        // Use LocalDate from LocalDateTime
        LocalDate createdDate = taskModel.getCreatedDate().toLocalDate();

        // Get the last two digits of the year
        int yearLastTwoDigits = createdDate.getYear() % 100;

        // Get the month value (already 1-based, no need to add 1)
        String month = String.format("%02d", createdDate.getMonthValue());

        List<TaskModel> taskModels = this.taskRepository.findTasksByYearAndMonth(createdDate.getYear(),
                createdDate.getMonthValue());

        String taskCount = "";
        String taskAbbreviation = "";
        for (TaskModel t : taskModels) {
            if (taskModel.getId() != null && t.getId().equals(taskModel.getId())) {
                taskCount = t.getTaskAbbreviation().substring(5);
                taskAbbreviation = taskTypeFirstCharacter + t.getTaskAbbreviation().substring(1);
            }
        }

        if (taskModels.size() > 0) {
            if (taskCount.equals("")) { // New task
                int count = Integer.valueOf(taskModels.get(0).getTaskAbbreviation().substring(5));
                taskCount = String.format("%03d", ++count);
            }
        } else {
            taskCount = String.format("%03d", 1);
        }

        taskAbbreviation = taskTypeFirstCharacter + yearLastTwoDigits + month + taskCount;

        // System.out.println("taskCount: " + taskCount);

        // System.out.println("taskAbbreviation: " + taskAbbreviation);

        return taskAbbreviation;
    }

}
