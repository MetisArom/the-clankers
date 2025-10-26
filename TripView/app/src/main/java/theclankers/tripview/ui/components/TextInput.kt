package theclankers.tripview.ui.components

// Used in the edit profile section and also the sign up flow, also for creating trips and answering form questions
// Takes as input prefill text
// onSubmit
// Label as input as well, could be left blank and the text won't show up

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun FormInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String = "",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = "",
    maxLines: Int = 1,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onSubmit: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Show label if provided
        if (label.isNotBlank()) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                if (placeholder.isNotBlank()) {
                    Text(text = placeholder)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            maxLines = maxLines,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = { onSubmit() },
                onNext = { onSubmit() },
                onGo = { onSubmit() },
                onSend = { onSubmit() }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Show error message if there is an error
        if (isError && errorMessage.isNotBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

// Variant for multiline text input (like descriptions or notes)
@Composable
fun FormTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String = "",
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = "",
    minLines: Int = 3,
    maxLines: Int = 6
) {
    FormInput(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        isError = isError,
        errorMessage = errorMessage,
        maxLines = maxLines,
        singleLine = false,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Default
    )
}

