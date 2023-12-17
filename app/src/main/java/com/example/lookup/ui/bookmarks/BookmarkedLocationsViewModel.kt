package com.example.lookup.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lookup.data.repositories.bookmarks.BookmarksRepository
import com.example.lookup.domain.bookmarks.BookmarkedLocation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkedLocationsViewModel @Inject constructor(
    private val bookmarksRepository: BookmarksRepository
) : ViewModel() {

    private val _bookmarksListStream = MutableStateFlow<List<BookmarkedLocation>>(emptyList())
    val bookmarksListStream = _bookmarksListStream as StateFlow<List<BookmarkedLocation>>


    init {
        bookmarksRepository.getBookmarksRepositoryStream().onEach { bookmarkedLocations ->
            _bookmarksListStream.update { bookmarkedLocations }
        }.launchIn(viewModelScope)
    }

    fun deleteBookmarks(bookmarksToBeDeleted: List<BookmarkedLocation>) {
        viewModelScope.launch {
            bookmarksToBeDeleted.forEach {
                bookmarksRepository.deleteLocationFromBookmarks(it)
            }
        }
    }
}