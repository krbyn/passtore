package com.krbyn.passtore.async

import com.krbyn.passtore.pass.SimplePassword

class AllPasswordResult {
    val passwords: List<SimplePassword>
    val error: String
    val reason: Throwable?
    val failed: Boolean
    val calceled: Boolean

    constructor(passwords: List<SimplePassword>) {
        this.passwords = passwords
        this.error = ""
        this.reason = null
        this.failed = false
        this.calceled = false
    }

    constructor(error: String, reason: Throwable? = null) {
        this.passwords = emptyList()
        this.error = error
        this.reason = reason
        this.failed = true
        this.calceled = false
    }

    constructor() {
        this.passwords = emptyList()
        this.error = ""
        this.reason = null
        this.failed = false
        this.calceled = true
    }
}