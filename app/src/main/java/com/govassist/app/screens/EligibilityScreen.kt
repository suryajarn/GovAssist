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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.govassist.app.components.GovAssistButton
import com.govassist.app.components.GovAssistOutlinedButton
import com.govassist.app.components.PlaceholderNotice
import com.govassist.app.components.ScreenHeader
import com.govassist.app.components.SelectableOptionCard
import com.govassist.app.eligibility.CshcEligibilityChecker
import com.govassist.app.eligibility.EligibilityInput
import com.govassist.app.eligibility.EligibilityResult
import com.govassist.app.eligibility.HouseholdType
import com.govassist.app.ui.theme.AccentOrange
import com.govassist.app.ui.theme.ErrorRed
import com.govassist.app.ui.theme.NavyPrimary
import com.govassist.app.ui.theme.SuccessGreen
import com.govassist.app.ui.theme.SurfaceLightGrey
import com.govassist.app.ui.theme.SurfaceWhite
import com.govassist.app.ui.theme.TextSecondary

// Step indices. Steps 1-6 are questions; 0 is the intro and 7 is the result.
private const val STEP_INTRO = 0
private const val STEP_AGE = 1
private const val STEP_RESIDENCY = 2
private const val STEP_INCOME_SUPPORT = 3
private const val STEP_HOUSEHOLD = 4
private const val STEP_CHILDREN = 5
private const val STEP_INCOME = 6
private const val STEP_RESULT = 7
private const val TOTAL_QUESTIONS = 6

@Composable
fun EligibilityScreen(
    onBack: () -> Unit,
    onContinueToApplicationGuide: () -> Unit = {}
) {
    var step by remember { mutableStateOf(STEP_INTRO) }
    var answers by remember { mutableStateOf(EligibilityInput()) }
    var result by remember { mutableStateOf<EligibilityResult?>(null) }

    // Free-text field states, kept separate from the typed answer so the user
    // can type an incomplete number without it being force-parsed every keystroke.
    var ageText by remember { mutableStateOf("") }
    var childrenText by remember { mutableStateOf("0") }
    var incomeText by remember { mutableStateOf("") }

    fun goBack() {
        when (step) {
            STEP_INTRO -> onBack()
            STEP_RESULT -> step = STEP_INCOME
            else -> step -= 1
        }
    }

    Scaffold(containerColor = SurfaceWhite) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader(title = "Check CSHC Eligibility", onBack = { goBack() })

            if (step in STEP_AGE..STEP_INCOME) {
                QuestionProgress(questionNumber = step, total = TOTAL_QUESTIONS)
                Spacer(modifier = Modifier.height(16.dp))
            }

            when (step) {
                STEP_INTRO -> IntroStep(onStart = { step = STEP_AGE })

                STEP_AGE -> AgeStep(
                    ageText = ageText,
                    onAgeTextChange = { ageText = it },
                    onContinue = {
                        answers = answers.copy(age = ageText.toIntOrNull())
                        step = STEP_RESIDENCY
                    }
                )

                STEP_RESIDENCY -> YesNoStep(
                    question = "Are you an Australian resident, and currently in Australia?",
                    helper = "This includes being physically present in Australia when you lodge your claim.",
                    onAnswer = { yes ->
                        answers = answers.copy(isAustralianResidentPresentInAustralia = yes)
                        step = STEP_INCOME_SUPPORT
                    }
                )

                STEP_INCOME_SUPPORT -> YesNoStep(
                    question = "Do you currently receive an income support payment or a " +
                            "Department of Veterans' Affairs pension?",
                    helper = "For example, the Age Pension, JobSeeker Payment, or a DVA service pension.",
                    onAnswer = { yes ->
                        answers = answers.copy(receivesIncomeSupportPayment = yes)
                        step = STEP_HOUSEHOLD
                    }
                )

                STEP_HOUSEHOLD -> HouseholdStep(
                    selected = answers.householdType,
                    onSelect = { type ->
                        answers = answers.copy(householdType = type)
                        step = STEP_CHILDREN
                    }
                )

                STEP_CHILDREN -> ChildrenStep(
                    childrenText = childrenText,
                    onChildrenTextChange = { childrenText = it },
                    onContinue = {
                        answers = answers.copy(dependentChildren = childrenText.toIntOrNull() ?: 0)
                        step = STEP_INCOME
                    }
                )

                STEP_INCOME -> IncomeStep(
                    householdType = answers.householdType,
                    incomeText = incomeText,
                    onIncomeTextChange = { incomeText = it },
                    onContinue = {
                        val finalAnswers = answers.copy(
                            annualAdjustedTaxableIncome = incomeText.toDoubleOrNull()
                        )
                        answers = finalAnswers
                        result = CshcEligibilityChecker.check(finalAnswers)
                        step = STEP_RESULT
                    }
                )

                STEP_RESULT -> result?.let {
                    ResultStep(
                        result = it,
                        onStartAgain = {
                            step = STEP_INTRO
                            answers = EligibilityInput()
                            ageText = ""
                            childrenText = "0"
                            incomeText = ""
                            result = null
                        },
                        onContinueToApplicationGuide = onContinueToApplicationGuide
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun QuestionProgress(questionNumber: Int, total: Int) {
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
    val criteria = listOf(
        "You have reached Age Pension age",
        "You meet residency requirements",
        "You are not already receiving an income-support payment",
        "Your income is under the applicable CSHC income test threshold"
    )

    Text(
        text = "GovAssist will ask you six short questions and tell you which of the " +
                "criteria below you meet.",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 20.dp)
    )

    Text(
        text = "What GovAssist will check",
        style = MaterialTheme.typography.titleLarge,
        color = NavyPrimary,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    criteria.forEach { criterion ->
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = NavyPrimary,
                modifier = Modifier.padding(top = 2.dp, end = 12.dp)
            )
            Text(text = criterion, style = MaterialTheme.typography.bodyLarge)
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    GovAssistButton(
        text = "Start eligibility check",
        onClick = onStart,
        icon = Icons.Filled.FactCheck,
        contentDescription = "Start eligibility check. Answer six questions to see if you qualify."
    )
}

@Composable
private fun AgeStep(
    ageText: String,
    onAgeTextChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Text(
        text = "What is your age?",
        style = MaterialTheme.typography.headlineMedium,
        color = NavyPrimary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = "Age Pension age is currently ${CshcEligibilityChecker.AGE_PENSION_AGE}.",
        style = MaterialTheme.typography.bodyLarge,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 20.dp)
    )

    OutlinedTextField(
        value = ageText,
        onValueChange = { new -> if (new.length <= 3 && new.all { it.isDigit() }) onAgeTextChange(new) },
        label = { Text("Your age in years") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Your age in years" }
    )

    Spacer(modifier = Modifier.height(20.dp))

    GovAssistButton(
        text = "Continue",
        onClick = onContinue,
        contentDescription = "Continue to the next question"
    )
}

@Composable
private fun YesNoStep(
    question: String,
    helper: String,
    onAnswer: (Boolean) -> Unit
) {
    Text(
        text = question,
        style = MaterialTheme.typography.headlineMedium,
        color = NavyPrimary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = helper,
        style = MaterialTheme.typography.bodyLarge,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 20.dp)
    )

    GovAssistButton(
        text = "Yes",
        onClick = { onAnswer(true) },
        contentDescription = "Yes, answering \"$question\""
    )
    Spacer(modifier = Modifier.height(14.dp))
    GovAssistOutlinedButton(
        text = "No",
        onClick = { onAnswer(false) },
        contentDescription = "No, answering \"$question\""
    )
}

@Composable
private fun HouseholdStep(
    selected: HouseholdType,
    onSelect: (HouseholdType) -> Unit
) {
    Text(
        text = "Which best describes your situation?",
        style = MaterialTheme.typography.headlineMedium,
        color = NavyPrimary,
        modifier = Modifier.padding(bottom = 20.dp)
    )

    HouseholdType.entries.forEach { type ->
        SelectableOptionCard(
            label = type.displayLabel,
            selected = selected == type,
            onClick = { onSelect(type) }
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun ChildrenStep(
    childrenText: String,
    onChildrenTextChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Text(
        text = "How many dependent children are in your care?",
        style = MaterialTheme.typography.headlineMedium,
        color = NavyPrimary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = "Enter 0 if this doesn't apply to you. Each dependent child raises your income limit.",
        style = MaterialTheme.typography.bodyLarge,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 20.dp)
    )

    OutlinedTextField(
        value = childrenText,
        onValueChange = { new -> if (new.length <= 2 && new.all { it.isDigit() }) onChildrenTextChange(new) },
        label = { Text("Number of dependent children") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Number of dependent children" }
    )

    Spacer(modifier = Modifier.height(20.dp))

    GovAssistButton(
        text = "Continue",
        onClick = onContinue,
        contentDescription = "Continue to the next question"
    )
}

@Composable
private fun IncomeStep(
    householdType: HouseholdType,
    incomeText: String,
    onIncomeTextChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Text(
        text = "What is your annual adjusted taxable income?",
        style = MaterialTheme.typography.headlineMedium,
        color = NavyPrimary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
        text = if (householdType == HouseholdType.SINGLE) {
            "Enter your own income in dollars."
        } else {
            "Enter your and your partner's combined income in dollars."
        },
        style = MaterialTheme.typography.bodyLarge,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = 20.dp)
    )

    OutlinedTextField(
        value = incomeText,
        onValueChange = { new -> if (new.all { it.isDigit() || it == '.' }) onIncomeTextChange(new) },
        label = { Text("Annual income (dollars)") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Annual adjusted taxable income in dollars" }
    )

    Spacer(modifier = Modifier.height(20.dp))

    GovAssistButton(
        text = "See my result",
        onClick = onContinue,
        contentDescription = "See my eligibility result"
    )
}

@Composable
private fun ResultStep(
    result: EligibilityResult,
    onStartAgain: () -> Unit,
    onContinueToApplicationGuide: () -> Unit
) {
    val bannerColor = if (result.overallEligible) SuccessGreen else ErrorRed
    val bannerText = if (result.overallEligible) {
        "You likely qualify for the CSHC"
    } else {
        "You likely don't qualify for the CSHC right now"
    }
    val bannerIcon = if (result.overallEligible) Icons.Filled.CheckCircle else Icons.Filled.Cancel

    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = bannerColor),
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = bannerText }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = bannerIcon,
                contentDescription = null,
                tint = SurfaceWhite,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = bannerText,
                style = MaterialTheme.typography.titleLarge,
                color = SurfaceWhite
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
        text = "How each criterion was assessed",
        style = MaterialTheme.typography.titleLarge,
        color = NavyPrimary,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    result.criteria.forEach { criterion ->
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = if (criterion.met) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                contentDescription = if (criterion.met) "Met" else "Not met",
                tint = if (criterion.met) SuccessGreen else ErrorRed,
                modifier = Modifier.padding(top = 2.dp, end = 12.dp)
            )
            Column {
                Text(text = criterion.label, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = criterion.detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }

    PlaceholderNotice(
        text = "This is an estimate based on income limits published by Services Australia " +
                "for the period from 20 September 2025 to 19 September 2026. These limits are " +
                "indexed every 20 September and may have changed. This does not guarantee a " +
                "real application will be approved. Always confirm current figures and your " +
                "own eligibility with Services Australia."
    )

    Spacer(modifier = Modifier.height(20.dp))

    if (result.overallEligible) {
        GovAssistButton(
            text = "Continue to application guide",
            onClick = onContinueToApplicationGuide,
            contentDescription = "Continue to the CSHC application guide"
        )
        Spacer(modifier = Modifier.height(14.dp))
    }

    GovAssistOutlinedButton(
        text = "Start again",
        onClick = onStartAgain,
        contentDescription = "Start the eligibility check again"
    )
}
