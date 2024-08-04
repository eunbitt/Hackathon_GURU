package com.example.hackathon_guru

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScheduleViewModel : ViewModel() {
    private val _schedules = MutableLiveData<List<Schedule>>(listOf()) // 기본값을 빈 리스트로 설정
    val schedules: LiveData<List<Schedule>> get() = _schedules

    fun addSchedule(newSchedule: Schedule) {
        val updatedList = _schedules.value?.toMutableList() ?: mutableListOf()
        updatedList.add(newSchedule)
        _schedules.value = updatedList
    }

    fun updateSchedule(updatedSchedule: Schedule, position: Int) {
        val updatedList = _schedules.value?.toMutableList() ?: mutableListOf()
        updatedList[position] = updatedSchedule
        _schedules.value = updatedList
    }

    fun deleteSchedule(position: Int) {
        val updatedList = _schedules.value?.toMutableList() ?: mutableListOf()
        updatedList.removeAt(position)
        _schedules.value = updatedList
    }

    // 초기화 메서드
    fun initializeDefaultSchedule() {
        // 기본 일정 설정 예제 (필요에 따라 수정)
        _schedules.value = listOf(
            Schedule("일정을 추가하세요", "", "", ""),
            // 여기에 더 많은 기본 일정 추가
        )
    }
}
