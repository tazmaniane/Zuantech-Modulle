package id.zuantech.appmodule.common

sealed class UiState {
    object Loading : UiState()
    object Success : UiState()
    class Error(val message: String) : UiState()
    class Failed(val code: Int, val message: String) : UiState()
}

sealed class UiState2 {
    object Loading : UiState2()
    class Success(val message: String) : UiState2()
    class Error(val message: String) : UiState2()
    class Failed(val code: Int, val message: String) : UiState2()
}

sealed class ResourceState<out T> {
    object Loading : ResourceState<Nothing>()
    data class Fetched<T>(val item: T) : ResourceState<T>()
    class Error(val message: String) : ResourceState<Nothing>()
}