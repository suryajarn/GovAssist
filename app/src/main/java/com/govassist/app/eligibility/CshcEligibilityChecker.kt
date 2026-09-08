package com.govassist.app.eligibility

/**
 * Local, rules-based CSHC eligibility check.
 *
 * This mirrors what the backend's Eligibility Agent is designed to do (see the
 * requirements and backend decisions docs), so the app can demonstrate a real
 * eligibility outcome today. When the real agent is wired in, only this file
 * should need to change; [com.govassist.app.screens.EligibilityScreen] talks
 * to it through [check] and doesn't know or care how the answer was worked out.
 *
 * IMPORTANT: the income thresholds below are set by Services Australia and are
 * indexed every 20 September. The figures here are the ones published for the
 * 20 September 2025 to 19 September 2026 period. They WILL go out of date and
 * must be re-checked against servicesaustralia.gov.au before this is used for
 * anything beyond a prototype demo.
 */
object CshcEligibilityChecker {

    // Age Pension age is currently a flat 67 for everyone reaching it now.
    const val AGE_PENSION_AGE = 67

    // Annual adjusted taxable income limits, current as described above.
    const val SINGLE_INCOME_LIMIT = 101_105.0
    const val COUPLE_COMBINED_INCOME_LIMIT = 161_768.0
    const val COUPLE_SEPARATED_INCOME_LIMIT = 202_210.0
    const val PER_DEPENDENT_CHILD_ADDITION = 639.60

    fun check(input: EligibilityInput): EligibilityResult {
        val criteria = mutableListOf<EligibilityCriterionResult>()

        val ageMet = (input.age ?: 0) >= AGE_PENSION_AGE
        criteria += EligibilityCriterionResult(
            label = "Age Pension age",
            met = ageMet,
            detail = if (ageMet) {
                "You meet the minimum age of $AGE_PENSION_AGE."
            } else {
                "You need to be at least $AGE_PENSION_AGE. You entered ${input.age ?: "no age"}."
            }
        )

        val residencyMet = input.isAustralianResidentPresentInAustralia == true
        criteria += EligibilityCriterionResult(
            label = "Residency",
            met = residencyMet,
            detail = if (residencyMet) {
                "You meet the residency requirement."
            } else {
                "You need to be an Australian resident and present in Australia when you apply."
            }
        )

        val notOnIncomeSupportMet = input.receivesIncomeSupportPayment == false
        criteria += EligibilityCriterionResult(
            label = "Not already receiving an income support payment",
            met = notOnIncomeSupportMet,
            detail = if (notOnIncomeSupportMet) {
                "You are not currently receiving an income support payment or DVA pension."
            } else {
                "The CSHC is only for people who are not already receiving an income support payment or a Department of Veterans' Affairs pension."
            }
        )

        val threshold = incomeThresholdFor(input.householdType, input.dependentChildren)
        val income = input.annualAdjustedTaxableIncome ?: Double.MAX_VALUE
        val incomeMet = income < threshold
        criteria += EligibilityCriterionResult(
            label = "Income test",
            met = incomeMet,
            detail = if (incomeMet) {
                "Your income is under the applicable limit of ${formatCurrency(threshold)}."
            } else {
                "Your income needs to be under ${formatCurrency(threshold)} for your household type. " +
                    "You entered ${formatCurrency(income)}."
            }
        )

        return EligibilityResult(
            overallEligible = criteria.all { it.met },
            criteria = criteria,
            incomeThresholdApplied = threshold
        )
    }

    private fun incomeThresholdFor(householdType: HouseholdType, dependentChildren: Int): Double {
        val base = when (householdType) {
            HouseholdType.SINGLE -> SINGLE_INCOME_LIMIT
            HouseholdType.COUPLE -> COUPLE_COMBINED_INCOME_LIMIT
            HouseholdType.COUPLE_SEPARATED -> COUPLE_SEPARATED_INCOME_LIMIT
        }
        return base + (dependentChildren.coerceAtLeast(0) * PER_DEPENDENT_CHILD_ADDITION)
    }

    private fun formatCurrency(amount: Double): String {
        val rounded = Math.round(amount)
        val withCommas = "%,d".format(rounded)
        return "$$withCommas"
    }
}

enum class HouseholdType(val displayLabel: String) {
    SINGLE("Single"),
    COUPLE("Couple, living together"),
    COUPLE_SEPARATED("Couple, separated by illness, respite care, or prison")
}

/** Everything the checker needs to know. All fields are collected one at a time in the UI. */
data class EligibilityInput(
    val age: Int? = null,
    val isAustralianResidentPresentInAustralia: Boolean? = null,
    val receivesIncomeSupportPayment: Boolean? = null,
    val householdType: HouseholdType = HouseholdType.SINGLE,
    val dependentChildren: Int = 0,
    val annualAdjustedTaxableIncome: Double? = null
)

data class EligibilityCriterionResult(
    val label: String,
    val met: Boolean,
    val detail: String
)

data class EligibilityResult(
    val overallEligible: Boolean,
    val criteria: List<EligibilityCriterionResult>,
    val incomeThresholdApplied: Double
)
