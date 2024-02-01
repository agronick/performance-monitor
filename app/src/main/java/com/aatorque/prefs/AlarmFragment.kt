package com.aatorque.prefs

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aatorque.datastore.Coloring
import com.aatorque.datastore.Display
import com.aatorque.datastore.Operation
import com.aatorque.datastore.Screen
import com.aatorque.datastore.UserPreferenceOrBuilder
import com.aatorque.stats.R
import com.aatorque.ui.AppTheme
import com.rarepebble.colorpicker.ColorPickerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

val Operation.op: String
    get() {
        return this.valueDescriptor.options.getExtension(com.aatorque.datastore.UserPrefs.op)
    }

@Parcelize
data class FormData(
    var operation: Operation = Operation.GT,
    var value: Double = 0.0,
    var color: Int = Color(255, 0, 0, 255).toArgb(),
) : Parcelable {
    fun toColoring(): Coloring {
        return Coloring.newBuilder()
            .setColor(color)
            .setOperation(operation)
            .setValue(value)
            .build()
    }

    companion object {
        fun fromColoring(coloring: Coloring): FormData {
            return FormData(
                value=coloring.value,
                operation=coloring.operation,
                color=coloring.color,
            )
        }
    }
}


class StateModel : ViewModel() {

    val uiState = mutableStateListOf<FormData>()
    fun prependItem(it: FormData) {
        uiState.add(it)
        uiState.sortWith(
            compareBy({
                when (it.operation) {
                    Operation.EQ -> 0
                    Operation.GT, Operation.GTE -> 1
                    Operation.LTE, Operation.LT -> 2
                    Operation.UNRECOGNIZED -> 3
                }
            }, {
                when (it.operation) {
                    Operation.GT, Operation.GTE -> -it.value
                    else -> it.value
                }
            })
        )
    }

    fun delete(current: FormData) {
        uiState.remove(current)
    }
}

class AlarmFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = ComposeView(requireContext())
        view.apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MainView(requireArguments())
            }
        }
        return view
    }
}


@Composable
fun NewItemDialog(
    onConfirmation: (FormData) -> Unit,
    onDismissRequest: () -> Unit,
    update: FormData? = null
) {
    val state = rememberSaveable {
        mutableStateOf(update ?: FormData())
    }
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = stringResource(R.string.add_item),
            )
        },
        text = {
            OperationForm(state)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(state.value)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}


@Composable
fun OperationForm(state: MutableState<FormData>) {
    val expanded = remember { mutableStateOf(false) }

    val firstColWidth = Modifier.fillMaxWidth(0.25f)
    Column {
        Row {
            Column(
                modifier = firstColWidth
            ) {
                Text(text = stringResource(id = R.string.condition))
                Button(
                    onClick = {
                        expanded.value = !expanded.value
                    }) {
                    Text(text = state.value.operation.op)
                }
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false },
                ) {
                    Operation.entries.forEach { s ->
                        if (s != Operation.UNRECOGNIZED) {
                            DropdownMenuItem(onClick = {
                                expanded.value = false
                                state.value = state.value.copy(operation = s)
                            }, text = {
                                Text(text = s.op)
                            })
                        }
                    }
                }
            }
            Column {
                Text(text = stringResource(id = R.string.value))
                NumberField(value = state.value.value, onNumberChange = {
                    it?.let {
                        state.value = state.value.copy(
                            value = it.toDouble()
                        )
                    }
                })
            }
        }
        Row {
            Column(
                modifier = firstColWidth
            ) {
                Text(text = stringResource(id = R.string.color))
            }
            Column {
                AndroidView(factory = { context ->
                    ColorPickerView(context).apply {
                        showHex(false)
                        setCurrentColor(state.value.color)
                        addColorObserver {
                            state.value = state.value.copy(
                                color = it.color
                            )
                        }
                    }
                })
            }
        }
    }
}

@Composable
fun NumberField(
    value: Number?,
    onNumberChange: (Number?) -> Unit,
) {
    val number = remember { mutableStateOf(value) }
    val textValue = remember {
        number.value = value
        mutableStateOf(value?.toDouble()?.let {
            if (it == 0.0) {
                ""
            } else if (it % 1.0 == 0.0) {
                it.toInt().toString()
            } else {
                it.toString()
            }
        } ?: "")
    }

    val numberRegex = remember { "[-]?[\\d]*[.]?[\\d]*".toRegex() }
    // for no negative numbers use "[\d]*[.]?[\d]*"

    TextField(
        value = textValue.value,
        onValueChange = {
            if (numberRegex.matches(it)) {
                textValue.value = it
                number.value = it.toDoubleOrNull()
                onNumberChange(number.value)
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun MainView(
    arguments: Bundle,
    model: StateModel = viewModel(),
    darkTheme: Boolean = isSystemInDarkTheme(),
) {
    val openAlertDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val isClock = arguments.getBoolean("isClock")
    val screen = arguments.getInt("screen")
    val index = arguments.getInt("index")

    fun getCurrentDisplay(pref: UserPreferenceOrBuilder): Pair<Display, (Int, Display.Builder) -> Screen.Builder> {
        val screenObj = pref.getScreens(screen).toBuilder()
        return if (isClock) {
            Pair(screenObj.getGauges(index), screenObj::setGauges)
        } else {
            Pair(screenObj.getDisplays(index), screenObj::setDisplays)
        }
    }


    LaunchedEffect(Unit) {
        context.dataStore.data.map {
            getCurrentDisplay(it).first.alarmsList
        }.collect {
            model.uiState.clear()
            model.uiState.addAll(it.map(FormData::fromColoring))
        }
    }
    DisposableEffect(Unit) {
        onDispose {
             CoroutineScope(Dispatchers.IO).launch {
                context.dataStore.updateData { currentSettings ->
                    return@updateData currentSettings.toBuilder().also { set1 ->
                        val toUpdate = getCurrentDisplay(set1)
                        val display = toUpdate.first.toBuilder()
                        display.clearAlarms()
                        display.addAllAlarms(
                            model.uiState.map(FormData::toColoring)
                        )
                        set1.setScreens(screen, toUpdate.second(index, display))
                    }.build()
                }
            }
        }
    }

    AppTheme(
        darkTheme
    ) {
        when {
            // ...
            openAlertDialog.value -> {
                NewItemDialog(
                    onConfirmation = {
                        model.prependItem(it)
                        openAlertDialog.value = false
                        println("Confirmation registered") // Add logic here to handle confirmation.
                    },
                    onDismissRequest = { openAlertDialog.value = false },
                )
            }
        }
        Column {
            Row {
                Button(
                    onClick = {
                        openAlertDialog.value = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = stringResource(R.string.add_item),
                    )
                    Text(text = stringResource(R.string.add_item))
                }
            }
            Spacer(modifier = Modifier.height(3.dp))
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.color_instruct),

                    modifier = Modifier
                        .padding(10.dp, 10.dp)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            VerticalReorderList()
        }
    }
}

@Composable
fun VerticalReorderList(model: StateModel = viewModel()) {
    val openAlertDialog = remember { mutableStateOf<FormData?>(null) }

    when {
        openAlertDialog.value != null -> {
            val willRemove = openAlertDialog.value!!
            NewItemDialog(
                update=willRemove.copy(),
                onDismissRequest = { openAlertDialog.value = null },
                onConfirmation = {
                    model.delete(willRemove)
                    model.prependItem(it)
                    openAlertDialog.value = null
                },
            )
        }
    }
    Column{
        model.uiState.forEach { current ->
            ListItem(
                headlineContent = {
                    Text("value ${current.operation.op} ${current.value}")
                },
                leadingContent = {
                    TextButton(
                        onClick = {
                            openAlertDialog.value = current
                        },
                        modifier = Modifier
                            .background(
                                Color(current.color)
                            )
                            .border(
                                BorderStroke(1.dp, Color.Black)
                            )
                            .height(50.dp)
                            .width(50.dp)
                    ) {

                    }
                },

                trailingContent = {
                    Button(onClick = {
                        model.delete(current)
                    }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = Icons.Filled.Delete.toString(),
                        )
                    }
                },
                shadowElevation=3.dp,
                tonalElevation=1.dp,
            )
        }
    }
}
