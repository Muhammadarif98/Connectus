import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.connectus.mvvm.ChatAppViewModel

class ChatAppViewModelFactory(private val application: Application) : ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatAppViewModel::class.java)) {
            Log.d("ChatAppViewModelFactory", "Создание экземпляра ChatAppViewModel")
            return ChatAppViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
