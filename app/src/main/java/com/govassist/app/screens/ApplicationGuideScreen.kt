package com.govassist.app.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.govassist.app.application.AuState
import com.govassist.app.application.CshcApplicationData
import com.govassist.app.application.CshcApplicationValidator
import com.govassist.app.components.GovAssistButton
import com.govassist.app.components.GovAssistOutlinedButton
import com.govassist.app.components.PlaceholderNotice
import com.govassist.app.components.ScreenHeader
import com.govassist.app.components.SelectableOptionCard
import com.govassist.app.eligibility.CshcEligibilityChecker
import com.govassist.app.ui.theme.AccentOrange
import com.govassist.app.ui.theme.ErrorRed
import com.govassist.app.ui.theme.NavyPrimary
import com.govassist.app.ui.theme.SuccessGreen
import com.govassist.app.ui.theme.SurfaceLightGrey
import com.govassist.app.ui.theme.SurfaceWhite
import com.govassist.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private const val STEP_INTRO = 0
private const val STEP_NAME = 1
private const val STEP_DOB = 2
private const val STEP_CRN = 3
private const val STEP_STREET = 4
private const val STEP_SUBURB = 5
private const val STEP_STATE = 6
private const val STEP_POSTCODE = 7
private const val STEP_PHONE = 8
private const val STEP_REVIEW = 9
private const val STEP_DONE = 10
private const val TOTAL_QUESTIONS = 8

@Composable
fun ApplicationGuideScreen(onBack: () -> Unit) {
    var step by remember { mutableStateOf(STEP_INTRO) }
    var form by remember { mutableStateOf(CshcApplicationData()) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    fun showMessage(message: String) {
        coroutineScope.launch { snackbarHostState.showSnackbar(message) }
    }

    fun goBack() {
        when (step) {
            STEP_INTRO -> onBack()
            STEP_DONE -> step = STEP_REVIEW
            else -> step -= 1
        }
    }

    fun resetAll() {
        step = STEP_INTRO
        form = CshcApplicationData()
    }

    Scaffold(
        containerColor = SurfaceWhite,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader(title = "CSHC Application Guide", onBack = { goBack() })

            if (step in STEP_NAME..STEP_PHONE) {
                QuestionProgressBar(questionNumber = step, total = TOTAL_QUESTIONS)
                Spacer(modifier = Modifier.height(16.dp))
            }

            when (step) {
                STEP_INTRO -> IntroStep(onStart = { step = STEP_NAME })

                STEP_NAME -> TextFieldStep(
                    title = "What is the applicant's full name?",
                    helper = "Enter it exactly as it appears on official documents.",
                    value = form.fullName,
                    onValueChange = { form = form.copy(fullName = it) },
                    validate = CshcApplicationValidator::validateFullName,
                    onContinue = { step = STEP_DOB },
                    fieldLabel = "Full name",
                    contentDescriptionLabel = "Applicant's full name"
                )

                STEP_DOB -> DobStep(
                    value = form.dateOfBirth,
                    onValueChange = { form = form.copy(dateOfBirth = it) },
                    onContinue = { step = STEP_CRN }
                )

                STEP_CRN -> TextFieldStep(
                    title = "What is the applicant's Centrelink Reference Number (CRN)?",
                    helper = "This is on any letter from Centrelink, 9 digits followed by 1 letter.",
                    value = form.crn,
                    onValueChange = { form = form.copy(crn = it.uppercase()) },
                    validate = CshcApplicationValidator::validateCrn,
                    onContinue = { step = STEP_STREET },
                    fieldLabel = "CRN",
                    contentDescriptionLabel = "Centrelink Reference Number"
                )

                STEP_STREET -> TextFieldStep(
                    title = "What is the applicant's street address?",
                    helper = "For example, 12 Smith Street.",
                    value = form.streetAddress,
                    onValueChange = { form = form.copy(streetAddress = it) },
                    validate = CshcApplicationValidator::validateStreetAddress,
                    onContinue = { step = STEP_SUBURB },
                    fieldLabel = "Street address",
                    contentDescriptionLabel = "Street address"
                )

                STEP_SUBURB -> TextFieldStep(
                    title = "What suburb or town is that in?",
                    helper = null,
                    value = form.suburb,
                    onValueChange = { form = form.copy(suburb = it) },
                    validate = CshcApplicationValidator::validateSuburb,
                    onContinue = { step = STEP_STATE },
                    fieldLabel = "Suburb",
                    contentDescriptionLabel = "Suburb"
                )

                STEP_STATE -> StateStep(
                    selected = form.state,
                    onSelect = {
                        form = form.copy(state = it)
                        step = STEP_POSTCODE
                    }
                )

                STEP_POSTCODE -> TextFieldStep(
                    title = "What is the postcode?",
                    helper = "4 digits, for example 3000.",
                    value = form.postcode,
                    onValueChange = { new -> if (new.length <= 4) form = form.copy(postcode = new) },
                    validate = CshcApplicationValidator::validatePostcode,
                    onContinue = { step = STEP_PHONE },
                    fieldLabel = "Postcode",
                    contentDescriptionLabel = "Postcode",
                    keyboardType = KeyboardType.Number
                )

                STEP_PHONE -> TextFieldStep(
                    title = "What is the best contact phone number?",
                    helper = "For example, 0412 345 678.",
                    value = form.phone,
                    onValueChange = { form = form.copy(phone = it) },
                    validate = CshcApplicationValidator::validatePhone,
                    onContinue = { step = STEP_REVIEW },
                    fieldLabel = "Phone number",
                    contentDescriptionLabel = "Contact phone number",
                    keyboardType = KeyboardType.Phone
                )

                STEP_REVIEW -> ReviewStep(
                    form = form,
                    onConfirm = { step = STEP_DONE },
                    onStartAgain = { resetAll() }
                )

                STEP_DONE -> DoneStep(
                    onGuideMeThroughMyGov = {
                        showMessage("Voice-guided myGov walkthroughs are coming soon.")
                    },
                    onStartAgain = { resetAll() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun QuestionProgressBar(questionNumber: Int, total: Int) {
    Column {
        Text(
            text = "Question $questionNumber of $total",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        LinearProgressIndicator(
            progress = { questionNumber / total.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = AccentOrange,
            trackColor = SurfaceLightGrey
        )
    }
}

@Composable
private fun IntroStep(onStart: () -> Unit) {
    Text(
        text = "GovAssist will ask for the details needed on the CSHC application, one " +
                "at a time, and check each one as you go. At the end you'll see a full " +
                "summary before anything is treated as final.",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 20.dp)
    )

    Text(
        text = "You'll be asked for",
        style = MaterialTheme.typography.titleLarge,
        color = NavyPrimary,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    listOf(
        "Full name",
        "Date of birth",
        "Centrelink Reference Number (CRN)",
        "Home address",
        "Contact phone number"
    ).forEach { item ->
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = NavyPrimary,
                modifier = Modifier.padding(top = 2.dp, end = 12.dp)
            )
            Text(text = item, style = MaterialTheme.typography.bodyLarge)
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    PlaceholderNotice(
        text = "Nothing you enter here is saved or sent anywhere in this prototype. " +
                "It only exists on this screen for this session."
    )

    Spacer(modifier = Modifier.height(20.dp))

    GovAssistButton(
        text = "Start application guide",
        onClick = onStart,
        icon = Icons.Filled.EditNote,
        contentDescription = "Start the CSHC application guide"
    )
}

@Composable
private fun TextFieldStep(
    title: String,
    helper: String?,
    value: String,
    onValueChange: (String) -> Unit,
    validate: (String) -> String?,
    onContinue: () -> Unit,
    fieldLabel: String,
    contentDescriptionLabel: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val error = validate(value)
    val showError = value.isNotBlank() && error != null

    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        color = NavyPrimary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    if (helper != null) {
        Text(
            text = helper,
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 20.dp)
        )
    } else {
        Spacer(modifier = Modifier.height(12.dp))
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(fieldLabel) },
        singleLine = true,
        isError = showError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = contentDescriptionLabel }
    )
    if (showError) {
        Text(
            text = error ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = ErrorRed,
            modifier = Modifier.padding(top = 6.dp)
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    GovAssistButton(
        text = "Continue",
        onClick = onContinue,
        enabled = error == null,
        contentDescription = "Continue to the next question"
    )
}

@Composable
private fun DobStep(
    value: String,
    onValueChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    val error = CshcApplicationValidator.validateDateOfBirth(value)
    val showError = value.isNotBlank() && error != null
    val ageWarning = if (error == null) {
        CshcApplicationValidator.ageWarningIfAny(value, CshcEligibilityChecker.AGE_PENSION_AGE)
    } else {
        null
    }

    Text(
        text = "What is the applicant's date of birth?",
        style = MaterialTheme.typography.headlineMedium,
        color = NavyPrimary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = "Enter it as DD/MM/YYYY, for example 05/03/1955.",
        style = MaterialTheme.typography.bodyLarge,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 20.dp)
    )

    OutlinedTextField(
        value = value,
        onValueChange = { new -> if (new.length <= 10) onValueChange(new) },
        label = { Text("Date of birth (DD/MM/YYYY)") },
        singleLine = true,
        isError = showError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Date of birth, day month year" }
    )
    if (showError) {
        Text(
            text = error ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = ErrorRed,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
    if (ageWarning != null) {
        Text(
            text = ageWarning,
            style = MaterialTheme.typography.bodyMedium,
            color = AccentOrange,
            modifier = Modifier.padding(top = 6.dp)
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    GovAssistButton(
        text = "Continue",
        onClick = onContinue,
        enabled = error == null,
        contentDescription = "Continue to the next question"
    )
}

@Composable
private fun StateStep(
    selected: AuState?,
    onSelect: (AuState) -> Unit
) {
    Text(
        text = "Which state or territory is that address in?",
        style = MaterialTheme.typography.headlineMedium,
        color = NavyPrimary,
        modifier = Modifier.padding(bottom = 20.dp)
    )

    AuState.entries.forEach { state ->
        SelectableOptionCard(
            label = state.label,
            selected = selected == state,
            onClick = { onSelect(state) }
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun ReviewStep(
    form: CshcApplicationData,
    onConfirm: () -> Unit,
    onStartAgain: () -> Unit
) {
    Text(
        text = "Check everything below carefully",
        style = MaterialTheme.typography.headlineMedium,
        color = NavyPrimary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = "Nothing has been submitted anywhere yet. Review each answer before continuing.",
        style = MaterialTheme.typography.bodyLarge,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 20.dp)
    )

    val summaryRows = listOf(
        "Full name" to form.fullName,
        "Date of birth" to form.dateOfBirth,
        "CRN" to form.crn,
        "Street address" to form.streetAddress,
        "Suburb" to form.suburb,
        "State" to (form.state?.label ?: ""),
        "Postcode" to form.postcode,
        "Phone number" to form.phone
    )

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLightGrey),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)) {
            summaryRows.forEach { (label, fieldValue) ->
                Column(modifier = Modifier.padding(vertical = 10.dp)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Text(
                        text = fieldValue.ifBlank { "Not provided" },
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    GovAssistButton(
        text = "This all looks correct",
        onClick = onConfirm,
        contentDescription = "Confirm all details are correct and continue"
    )

    Spacer(modifier = Modifier.height(14.dp))

    GovAssistOutlinedButton(
        text = "Start again",
        onClick = onStartAgain,
        contentDescription = "Start the application guide again"
    )
}

@Composable
private fun DoneStep(
    onGuideMeThroughMyGov: () -> Unit,
    onStartAgain: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = SuccessGreen),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Your CSHC application draft is ready" }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = SurfaceWhite,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = "Your CSHC application draft is ready",
                style = MaterialTheme.typography.titleLarge,
                color = SurfaceWhite
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "Next, GovAssist will talk you through entering these details into your " +
                "own myGov session, step by step, reading what's on screen and telling you " +
                "what to do next. You'll confirm each step yourself before anything is " +
                "entered or submitted.",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 20.dp)
    )

    GovAssistOutlinedButton(
        text = "Guide me through myGov",
        onClick = onGuideMeThroughMyGov,
        icon = Icons.Filled.RecordVoiceOver,
        contentDescription = "Guide me through entering this into myGov"
    )

    Spacer(modifier = Modifier.height(14.dp))

    GovAssistOutlinedButton(
        text = "Start a new application guide",
        onClick = onStartAgain,
        contentDescription = "Start the application guide again from the beginning"
    )
}
