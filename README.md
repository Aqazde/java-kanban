# Task Management System

## Introduction
This project is a task management system developed as part of the **"Java Backend" course at Yandex**. It provides a robust backend implementation for managing tasks, epics, and subtasks through a RESTful HTTP server. The system supports task creation, updates, deletion, prioritization, and history tracking, with data persistence to a CSV file. The project demonstrates proficiency in Java, HTTP server development, JSON serialization, and unit testing.

## Project Description
The Task Management System is a Java-based application that allows users to manage tasks, epics, and subtasks via HTTP requests. It uses the `com.sun.net.httpserver.HttpServer` for handling HTTP requests and Gson for JSON serialization/deserialization. Tasks are stored in memory or persisted to a CSV file using the `FileBackedTaskManager`. The system ensures no time conflicts between tasks and automatically updates epic statuses based on their subtasks.

## Features
- **Task Management**: Create, update, retrieve, and delete tasks, epics, and subtasks.
- **Time Management**: Assign start times and durations to tasks/subtasks, with validation to prevent time overlaps.
- **Epic-Subtask Relationship**: Epics aggregate subtasks, with automatic status and time updates based on subtask changes.
- **History Tracking**: Maintain a history of viewed tasks, accessible via the `/history` endpoint.
- **Prioritized Tasks**: Retrieve tasks sorted by start time using the `/prioritized` endpoint.
- **File Persistence**: Save and load tasks to/from a CSV file using `FileBackedTaskManager`.
- **RESTful API**: Expose endpoints for CRUD operations on tasks, epics, and subtasks.
- **Comprehensive Testing**: Unit tests for task management logic and HTTP handlers using JUnit.

## Requirements
- Java 11 or higher
- Gson library (`com.google.code.gson:gson:2.10.1`)
- JUnit 5 for testing
- Maven or Gradle for dependency management (optional, if using a build tool)

## Setup Instructions
1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd task-management-system