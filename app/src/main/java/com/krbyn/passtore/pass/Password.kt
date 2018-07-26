package com.krbyn.passtore.pass

data class Password(var id: Long = -1L, val name: String, val type: String, val fields: List<PassField>)