package com.example.medplusadmin.utils

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.medplusadmin.domain.models.Medicine
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

/**
 * Collects a flow safely within the Fragment's lifecycle.
 */
inline fun Fragment.collectFlowSafely(
    handler: CoroutineExceptionHandler,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend () -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch(handler) {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(state) {
            block()
        }
    }
}

/*Show a Toast from a Fragment.*/
fun Fragment.showToast(message: String) {
    context?.showToast(message)
}

/*Show a Toast from any Context.*/
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/*Convert a content URI to a ByteArray safely.*/
fun uriToByteArray(context: Context?, uri: Uri): ByteArray {
    if (context == null) return ByteArray(0)

    return context.contentResolver.openInputStream(uri)?.use { inputStream ->
        inputStream.readBytes()
    } ?: ByteArray(0)
}


