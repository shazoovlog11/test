package com.example.todo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todo.data.TodoDao
import com.example.todo.data.TodoItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(private val todoDao: TodoDao) : ViewModel() {

    val todoItems: StateFlow<List<TodoItem>> = todoDao.getAllItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addItem(title: String, description: String = "") {
        if (title.isBlank()) return
        viewModelScope.launch {
            todoDao.insertItem(TodoItem(title = title, description = description))
        }
    }

    fun toggleComplete(item: TodoItem) {
        viewModelScope.launch {
            todoDao.updateItem(item.copy(isCompleted = !item.isCompleted))
        }
    }

    fun deleteItem(item: TodoItem) {
        viewModelScope.launch {
            todoDao.deleteItem(item)
        }
    }

    fun clearCompleted() {
        viewModelScope.launch {
            todoDao.deleteCompletedItems()
        }
    }
}

class TodoViewModelFactory(private val todoDao: TodoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TodoViewModel(todoDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
