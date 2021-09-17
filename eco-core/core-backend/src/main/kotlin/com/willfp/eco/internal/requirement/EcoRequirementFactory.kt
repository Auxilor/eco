package com.willfp.eco.internal.requirement

import com.willfp.eco.core.requirement.Requirement
import com.willfp.eco.core.requirement.RequirementFactory
import com.willfp.eco.internal.requirement.requirements.*

class EcoRequirementFactory: RequirementFactory {
    override fun create(name: String): Requirement {
        return when (name.lowercase()) {
            "has-permission" -> RequirementHasPermission()
            "placeholder-equals" -> RequirementPlaceholderEquals()
            "placeholder-greater-than" -> RequirementPlaceholderGreaterThan()
            "placeholder-less-than" -> RequirementPlaceholderLessThan()
            else -> RequirementTrue()
        }
    }
}