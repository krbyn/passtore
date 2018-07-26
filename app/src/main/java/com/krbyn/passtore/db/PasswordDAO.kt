package com.krbyn.passtore.db

import com.krbyn.passtore.pass.Password
import com.krbyn.passtore.pass.SimplePassword

interface PasswordDAO {
    companion object {
        const val DB_NAME = "PasstoreDBName"
    }
    /**
     * Create a new password
     * @param pass [Password] to create
     * @return the same [Password] with real [Password.id]
     */
    fun insertPassword(pass: Password): Password

    /**
     * Update the [Password] by [Password.id]
     * @param pass [Password] to update
     * @return true if updated successfully, false otherwise
     */
    fun updatePassword(pass: Password): Boolean

    /**
     * Delete password using [id]
     * @param id ID of the password to be deleted
     * @return true if password exists and deleted, false otherwise
     */
    fun deletePassword(id: Long): Boolean

    /**
     * Get password by [Password.id]
     * @param id of the password to get
     * @return [Password] with the id, null if no password is found
     */
    fun getPassword(id: Long): Password?

    /**
     * Get all passwords display form (only [Password.id], [Password.name] and [Password.type]
     * @param [sort] parameter to sort by: [SortBy.values]
     * @return List of [SimplePassword] sorted by the [sort] parameter
     */
    fun getAllPasswordNames(sort: SortBy = SortBy.ID): List<SimplePassword>

    /**
     * Enum to provide the sort criteria of the [getAllPasswordNames] method
     */
    enum class SortBy {
        /**
         * Sort by [Password.id]
         */
        ID,
        /**
         * Sort by [Password.name]
         */
        NAME,
        /**
         * Sort by [Password.type]
         */
        TYPE,
        /**
         * Sort by last updated time
         */
        LAST_UPDATE
    }
}