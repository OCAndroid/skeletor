package skeletor.sample.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import skeletor.sample.list.JourneyViewModel
import skeletor.sample.list.repository.JourneyRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: JourneyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(JourneyViewModel::class.java) -> JourneyViewModel(repository)
                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}